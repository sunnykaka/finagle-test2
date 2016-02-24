package com.akkafun.product

import com.twitter.finagle.stats.DefaultStatsReceiver
import com.twitter.finagle.zipkin.thrift.ZipkinTracer
import com.twitter.finagle.{Http, Service}
import com.twitter.finagle.builder.ClientBuilder
import com.twitter.finagle.http.{Http => _, _}
import com.twitter.util.Await

/**
  * Created by Administrator on 2016/1/14.
  */
object ZkPortal {

  def main(args: Array[String]): Unit = {

    val client = Http.client.
      withSessionPool.maxSize(10).
      withLabel("product-product-client").
      //      withTracer(ConsoleTracer).
      withTracer(ZipkinTracer.mk("192.168.99.100", 9410, DefaultStatsReceiver, 1.0f)).
      newService("zk!127.0.0.1:2181,127.0.0.1:2182,127.0.0.1:2183!/f/products")

    val request = Request("/", ("userId", "1"), ("amount", "2000000"), ("productId", "3"), ("channel", "4"), ("__serviceName", "invest"))
    Await.ready(client(request)) onSuccess { response =>
      println(s"response status: ${response.status}, response string: ${response.contentString} ")
    } onFailure { error =>
      println(s"found error: ${error.getMessage}, error class: ${error.getClass.getName}")
    } ensure {
      client.close()
    }


//    val client: Service[Request, Response] = ClientBuilder()
//      .codec(Http())
//      .hostConnectionLimit(10)
//      .dest("zk!127.0.0.1:2181,127.0.0.1:2182,127.0.0.1:2183!/f/products")
//      .build()
//
//    val request = Request("/", ("userId", "1"), ("amount", "2000000"), ("productId", "3"), ("channel", "4"), ("__serviceName", "invest"), ("withZk", "true"))
//    client(request) onSuccess { response =>
//      val responseString = response.contentString
//      println("))) Received result for authorized request: " + responseString)
//    } onFailure { error =>
//      println("))) Unauthorized request errored (as desired): " + error.getClass.getName)
//    } ensure {
//      client.close()
//    }
  }
}
