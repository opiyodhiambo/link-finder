package adventure

import akka.actor.Actor
import adventure.*
import akka.actor.Status
import akka.io.Tcp.Abort
import java.util.concurrent.Executor
import scala.concurrent.ExecutionContext
import akka.Done
import akka.actor.TypedActor.dispatcher
import akka.pattern.pipe
import akka.actor.{ActorRef, Props}

object Getter {
  case object Done
  case object Abort
}

class Getter(url: String, depth: Int) extends Actor { // takes the url to visit and the depth of the link
  implicit val executor: ExecutionContext = context.dispatcher.asInstanceOf[Executor with ExecutionContext]
  def client: WebClient = AsyncWebClient
  client get url pipeTo self // send message to the actor when future is complete

  def receive = {
    case body: String =>
      for (link <- AsyncWebClient.findLinks(body)) // get the links using the findLinks method in the WebClient class
        context.parent ! Check(
          link,
          depth
        ) // Check the links and send the message to the parent actor
      stop() // The actor then stops, a method defined below

    case _: Status.Failure => stop()

    case Abort             => stop()
  }

  def stop(): Unit = {
    context.parent ! Done
    context.stop(self)
  }
}
