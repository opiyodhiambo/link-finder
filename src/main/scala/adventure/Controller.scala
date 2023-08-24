package adventure

import adventure.*
import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem}
import scala.concurrent.duration.*
import akka.util.Timeout 
import scala.concurrent.ExecutionContext
import akka.actor.Props


case class Check(url: String, depth: Int)

class Controller extends Actor with ActorLogging {
  implicit val executionContext: ExecutionContext = context.system.dispatcher 

  context.system.scheduler.scheduleOnce(
    10.seconds,
    self,
    Timeout
  )(executionContext) // Will be reset after every process has been completed

  var cache = Set.empty[String] // holds the cached results of the visited urls
  var children =
    Set.empty[ActorRef] // keeps track of all the child actors created

  def receive = {
    case Check(url, depth) =>
      log.debug(
        "{} checking {}",
        depth,
        url
      ) // Log every check request at debug level
      if (!cache(url) && depth > 0) // if the cache already has the url, or has a max depth of zero, nothing is done
        val newGetter = context.actorOf(Props(new Getter(url, depth -1)))
        children = children + newGetter// otherwise, create a new getter  and tell it about the url to fetch, decreasing the depth by 1
      cache += url // append the visited url to the cache

    case Getter.Done =>
      children -= sender // once the getter is done, we remove it from the children set
      if (children.isEmpty)
        context.parent ! Result(
          cache
        ) // Once no getter is there anymore, we know that the whole process is fin ished an we tell the parent the results

    case Timeout => children foreach (_ ! Getter.Abort)
  }
}

class Cache extends Actor {
  var cache = Map.empty[String, String]

  def receive: Actor.Receive = {
    case Get(url) =>
      if (cache contains url) sende ! cache(url)
      else{
        val client = sender 
        WebClient get url map (Result(sender, url, _)) pipeTo self
      }

    case Result(client, url, body) =>
      cache += url -> body
      client ! body

  }
}
