 package adventure
 import akka.testkit.TestKit
import akka.actor.Props

 object ReceptionistSpec {
   class FakeController extends Actor {
     def receive = {
       case Controller.Check(url, depth) =>
         Thread.sleep(1000)
         sender ! Controller.Result(Set(url))
     }
   }

   def fakeReceptionist: Props = 
     Props(new Receptionist {
       override def controllerProps = Props[FakeController]
     })
 }
