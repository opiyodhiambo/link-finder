  package adventure

  private val client = new AsyncHttpClient // an instance used to make asynchronous HTTP requests 
  def get (url: String)(implicit exec: Executor): Future[String] = {
    val f = client.prepareGet(url).execute(); // prepareGet prepares the requests, while execute sends the request
    val p = Promise[String]() // Adapting it (f) into a scala future
    f.addListener(new Runnable { // Listener added to the ListenableFuture and will be runned when the future is completed.
      def run = { // a method describing what will happen when the future is complete 
        val response = f.get // f.get will not be blocked because the future has been completed
        if (response.getStatusCode < 400) 
          p.success(response.getResponseBodyExcerpt(131072)) // If the status code is less than 400 (indicating success), it completes the promise
        else p.failure(BadStatus(response.getStatusCode)) // Otherwise, the promise fails with s BadStatsCode
      }
    }, exec) // An executor is needed since the task (runnable) will be run asynchronously
    p.future // Getting a future from a promise

}
