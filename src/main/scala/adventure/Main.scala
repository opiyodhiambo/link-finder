package adventure
import akka.actor.Actor
import adventure.*
import akka.actor.ReceiveTimeout

class Main extends Actor {
  val receptionist = context.actorOf(Props[Receptionist], "receptionist")

  receptionist ! Get("http://www.google.com")
  receptionist ! Get("http://www.google.com/1")
  receptionist ! Get("http://www.google.com/2")
  receptionist ! Get("http://www.google.com/3")
  receptionist ! Get("http://www.google.com/4")

  context.setReceiveTimeout(10.seconds)
  def receive: Actor.Receive = {
    case Result(url, set) =>
      println(set.toVector.sorted.mkString(s"Results for '$url':\n", "\n", "\n"))

    case Failed(url) =>
      println(s"Failed to fetch '$url'\n")

    case ReceiveTimeout =>
      context.stop(self)
  }

  override def postStop(): Unit = {
    WebClient.shutdown()
  }
}
