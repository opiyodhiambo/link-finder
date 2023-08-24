package adventure
import adventure.*
import akka.actor.Actor
import akka.actor.Props
import akka.actor.ActorRef

case class Get(url: String)

class Receptionist extends Actor {
  def receive: Actor.Receive = waiting

  val waiting: Receive = { case Get(url) =>
    context.become(runNext(Vector(Job(sender, url))))
  }

  def running(queue: Vector[Job]): Receive = {
    // upon Get(url) append that to queue and keep running
    case Get(url) =>
      context.become(enqueueJob(queue, Job(sender, url)))

    // Upon Controller.Results, shot ot tp the client and run that to the next job from queue if any
    case Controller.Result(links) =>
      val job = queue.head
      job.client ! Result(job.url, links)
      context.stop(sender)
      context.become(runNext(queue.tail))
  }

  case class Job(client: ActorRef, url: String)
  var reqNo = 0

  def runNext(queue: Vector[Job]): Receive = {
    reqNo += 1
    if (queue.isEmpty) waiting // when the job queue is empty, the receptionist waits
    else {
      val controller = context.actorOf(Props[Controller], s"c$reqNo") // Otherwise, we instantiate a new controller
      controller ! Controller.Check(queue.head.url, 2) // then delegate the task, which is to check the link
      running(queue) // then we transition into the running state
    }
  }

  def enqueueJob(queue: Vector[Job], job: Job): Receive = {
    if (queue.size > 3) {
      sender ! Failed(job.url)
      running(queue)
    } else running(queue :+ job)
  }
}
