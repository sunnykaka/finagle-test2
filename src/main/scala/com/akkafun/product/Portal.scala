package com.akkafun.product

import com.twitter.finagle.Http
import com.twitter.finagle.http.{Http => _, _}
import com.twitter.finagle.stats.DefaultStatsReceiver
import com.twitter.finagle.zipkin.thrift.ZipkinTracer
import com.twitter.util.Await

/**
  * Created by Administrator on 2016/1/14.
  */
object Portal {

  def main(args: Array[String]): Unit = {

    val useZk = true

    val dest = if(useZk) "zk!127.0.0.1:2181,127.0.0.1:2182,127.0.0.1:2183!/f/products" else "127.0.0.1:8082"

    val client = Http.client.
      withSessionPool.maxSize(10).
      withLabel("product-product-client").
      withTracer(ZipkinTracer.mk("192.168.99.100", 9410, DefaultStatsReceiver, 1.0f)).
      newService(dest)

    val request = Request("/", ("userId", "1"), ("amount", "2000000"), ("productId", "3"), ("channel", "4"), ("__serviceName", "invest"))
    Await.ready(client(request)) onSuccess { response =>
      println(s"response status: ${response.status}, response string: ${response.contentString} ")
    } onFailure { error =>
      println(s"found error: ${error.getMessage}, error class: ${error.getClass.getName}")
    } ensure {
      client.close()
    }

    //    val client: Service[Request, Response] = ClientBuilder()
    //      .codec(Http().enableTracing(enable = true))
    //      .hosts(new InetSocketAddress(8082))
    //      .hostConnectionLimit(1)
    //      .tracer(ZipkinTracer.mk("192.168.99.100", 9410, DefaultStatsReceiver, 1.0f))
    ////      .tracer(ConsoleTracer)
    //      .build()


  }
}
