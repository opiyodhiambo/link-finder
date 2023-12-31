package adventure
import akka.testkit.TestKit
import akka.actor.Props
import akka.testkit.ImplicitSender
import org.scalatest.wordspec.AnyWordSpecLike

object ReceptionistSpec {
  class FakeController extends Actor {
    import context.dispatcher

    def receive = { case Controller.Check(url, depth) =>
      context.system.scheduler.scheduleOnce(1.second, sender, Controller.Result(Set(url)))
    }
  }

  def fakeReceptionist: Props =
    Props(new Receptionist {
      override def controllerProps = Props[FakeController]
    })
}

class ReceptionistSpec
    extends TestKit(ActorSystem("ReceptionistSpec"))
    with WordSpecLike
    with BeforeAndAfterAll
    with ImplicitSender {
  import ReceptionistSpec._
  import Receptionist._

  override def afterAll(): Unit = {
    "A Receptionist" must {

      "reply with a result" in {
        val receptionist = system.actorOf(fakeReceptionist, "sendResult")
        receptionist ! Get("myURL")
        expectMsg(Result("myURL", Set("myURL")))
      }

      "reject request flood" in {
        val receptionist = system.actorOf(fakeReceptionist, "rejectFlood")
        for (i <- 1 to 5) receptionist ! Get(s"myURL$i")
        expectMsg(Failed("myURL5"))
        for (i in 1 <- 4) expectMsg(Results(s"myURL$i", Set(s"myURL$i")))

      }
    }
  }
}
