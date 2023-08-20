package adventure
import akka.actor.Actor
import adventure.*
import akka.actor.ReceiveTimeout

class Main extends Actor {
  val receptionist = context.actorOf(Props[Receptionist], "receptionist")
  receptionist ! Get("http://www.google.com")
  context.setReceiveTimeout(10.seconds)
  def receive: Actor.Receive = {
    case Result(url, set) =>
      println(set.toVector.sorted.mkString(s"Results for '$url':\n", "\n", "\n"))
    case Failed(url) => 
      println(s"Failed to fetch '$url'\n")
    case ReceiveTimeout =>
      context.stop(self)
  }
}
