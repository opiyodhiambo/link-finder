package adventure
import adventure.*

class Controller extends Actor with ActorLogging {
  var cache = Set.empty[String]
  var children = Set.empty[String]
  def receive = {
    case Check => ???
    case Getter.Done => ???
  }
}
