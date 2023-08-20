package adventure

import akka.actor.Actor
class Receptionist extends Actor {
  def receive: Actor.Receive = waiting
  val waiting: Receive = {
    // Upon Get (url) start a traversal and become running
  }
  def running(queue: Vector[Job]): Receive = {
    //upon Get(url) append that to queue and keep running
    //Upon Controller.Results, shot ot tp the client and run that to the next job from queue if any
  }
}
