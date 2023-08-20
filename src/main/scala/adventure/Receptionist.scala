package adventure
import adventure.*

import akka.actor.Actor
import akka.actor.Props
class Receptionist extends Actor {
  def receive: Actor.Receive = waiting
  val waiting: Receive = {
    // Upon Get (url) start a traversal and become running
  }
  def running(queue: Vector[Job]): Receive = {
    //upon Get(url) append that to queue and keep running
    //Upon Controller.Results, shot ot tp the client and run that to the next job from queue if any
  }
  case class Job(client: ActorRef, url: String)
  var reqNo = 0
  def runNext(queue: Vector[Job]): Receive = {
    reqNo += 1 
    if (queue.isEmpty) waiting
    else {
      val controller = context.actorOf(Props[Controller], s"c$reqNo")
      controller ! Controller.Check(queue.head.url, 2)
      running(queue)
    }
  }
}
