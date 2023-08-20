package adventure
import adventure.*
import akka.actor.{Actor, ActorLogging, ActorRef}

class Controller extends Actor with ActorLogging {
  var cache = Set.empty[String] // holds the cached results of the visited strings
  var children = Set.empty[ActorRef] //keeps track of all the child actors created 
  def receive = {
    case Check(url, depth) =>
      log.debug("{} checking {}", depth, url) // Log every check request at debug level
      if (!cache(url) && depth > 0) // if the cache already has the url, or has a max depth of zero, nothing is done
        children += context.actorOf(Props(new Getter(url, depth -1))) // otherwise, create a new getter  and tell it about the url to fetch, decreasing the depth by 1
      cache += url // append the visited url to the cache 
    case Getter.Done => 
      children -= sender // once the getter is done, we remove it from the children set 
      if (children.isEmpty) context.parent ! Result(cache) // Once no getter is there anymore, we know that the whole process is fin ished an we tell the parent the results
  }
}
