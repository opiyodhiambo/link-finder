import akka.actor.Props
import akka.actor.ActorRef
import akka.actor.Actor
val firstLink = "http://www.adventure.info/1"

val bodies = Map(
  firstLink ->
    """"<html>
          <head><title>Page 1 </title></head>
          <body>
            <h1>A link</h1>
            <a href="http://adventure.info/2">click here</a>
          </body>
      </html>""".stripMargin
)

val links = Map(firstLink -> Seq("http://adventure.info/2"))
object FakeWebClient extends WebClient {
  def get(url: String)(implicit exec: Executor): Future[String] = 
    bodies get url martch {
      case None => Future.failed(BadStatus(404))
      case Some(body) => Future.successful(body)
    }
}

def fakeGetter(url: String, depth: Int): Props = 
  Props(new Getter(url, depth) {
    override def client = FakeWebClient
  })

class StepParent(child: Props, probe: ActorRef) extends Actor {
  context.actorOf(child, "child")\
  def receive: Actor.Receive = {
    case msg => probe.tell(msg, sender)
  }
}
