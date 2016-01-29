package com.akkafun.product

import java.net.InetSocketAddress

import com.akkafun.product.finagle.ProductController
import com.twitter.finagle.Service
import com.twitter.finagle.builder.{ClientBuilder, Server, ServerBuilder}
import com.twitter.finagle.http._

/**
  * Created by Administrator on 2016/1/14.
  */
object Portal {

  def main(args: Array[String]): Unit = {

    val client: Service[Request, Response] = ClientBuilder()
      .codec(Http())
      .hosts(new InetSocketAddress(8082))
      .hostConnectionLimit(1)
      .build()

    val request = Request("/", ("userId", "1"), ("amount", "2000000"), ("productId", "3"), ("channel", "4"), ("__serviceName", "invest"))
    client(request) onSuccess { response =>
      println(s"response status: ${response.status}, response string: ${response.contentString} ")
    } onFailure { error =>
      println(s"found error: ${error.getMessage}, error class: ${error.getClass.getName}")
    } ensure {
      client.close()
    }
  }
}
