class Getter(url: String, depth: Int) extends Actor {
  implicit val exec = context.dispatcher
  val future = WebClient.get(url)
  future onComplete {
    case Success(body) => self ! body 
    case Failure(err) => self ! Status.Failure(err)
  }
}
