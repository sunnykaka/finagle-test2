package com.akkafun.product

import java.net.InetSocketAddress

import com.akkafun.product.finagle.ProductController
import com.twitter.finagle.Service
import com.twitter.finagle.builder.{Server, ServerBuilder}
import com.twitter.finagle.http.{Http, Request, Response}
import com.twitter.util.{Throw, Return}

/**
  * Created by Administrator on 2016/1/14.
  */
object ZkMain {

  def main(args: Array[String]): Unit = {

    val s1: Service[Request, Response] = new ProductController.InvestService

    val server: Server = ServerBuilder()
      .codec(Http())
      .bindTo(new InetSocketAddress("127.0.0.1", 8082))
      .name("productserver")
      .build(s1)

    server.announce("zk!127.0.0.1:2181,127.0.0.1:2182,127.0.0.1:2183!/f/products!0").respond {
      case Return(a) => println(s"announcement: $a")
      case Throw(e) => println(s"error: $e")
    }

  }

}
