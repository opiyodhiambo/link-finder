package adventure

import akka.actor.Actor
import adventure.*
import akka.actor.Status

class Getter(url: String, depth: Int) extends Actor { // takes the url to visit and the depth of the link
  implicit val exec = context.dispatcher
  val future = WebClient.get(
    url
  ) // Fetch the link using the WebClient and gives us back a future
  WebClient get url pipeTo self // send message to the actor when future is complete

  def receive = {
    case body: String =>
      for (
        link <- findLinks(body)
      ) // get the links using the findLinks method in the WebClient class
        context.parent ! Controller.Check(
          link,
          depth
        ) // Check the links and send the message to the parent actor
      stop() // The actor then stops, a method defined below
    case _: Status.Failure => stop()
  }
  def stop(): Unit = {
    context.parent ! Done
    context.stop(self)
  }
}
