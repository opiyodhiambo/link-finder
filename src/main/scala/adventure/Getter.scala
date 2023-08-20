import org.asynchttpclient.AsyncHttpClient

val client = new AsyncHttpClient 
def get (url: String): String = {
  val response = client.prepareGet(url).execute().get
  if (response.getStatusCode < 400)
    response.getResponseBodyExcerpt(131072)
  else throw BadStatus(response.getStatusCode)
}
