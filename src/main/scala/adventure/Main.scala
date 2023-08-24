package adventure
import akka.actor.{Actor, Props}
import adventure.*
import akka.actor.ReceiveTimeout
import concurrent.duration.DurationInt
import adventure.WebClient

class Main extends Actor {
  val receptionist = context.actorOf(Props[Receptionist], "receptionist")

  receptionist ! Get("http://www.google.com")
  receptionist ! Get("http://www.google.com/1")
  receptionist ! Get("http://www.google.com/2")
  receptionist ! Get("http://www.google.com/3")
  receptionist ! Get("http://www.google.com/4")

  context.setReceiveTimeout(10.seconds)
  def receive: Actor.Receive = {
    case Result(cache) =>
      println(cache.toVector.sorted.mkString("Results:\n", "\n", "\n"))

    case Failed(url) =>
      println(s"Failed to fetch '$url'\n")

    case ReceiveTimeout =>
      context.stop(self)
  }

  // override def postStop(): Unit = {
  //   WebClient.shutdown()
  
}
