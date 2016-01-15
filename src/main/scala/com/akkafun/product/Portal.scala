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
      .hosts(new InetSocketAddress(8081))
      .hostConnectionLimit(1)
      .build()

    val request = Request("/", ("userId", "1"), ("amount", "2"), ("productId", "3"), ("channel", "4"))
    client(request) onSuccess { response =>
      val responseString = response.contentString
      println("))) Received result for authorized request: " + responseString)
    } onFailure { error =>
      println("))) Unauthorized request errored (as desired): " + error.getClass.getName)
    } ensure {
      client.close()
    }
  }
}
