package com.akkafun.product

import java.net.InetSocketAddress

import com.akkafun.product.finagle.ProductController
import com.twitter.finagle.stats.DefaultStatsReceiver
import com.twitter.finagle.zipkin.thrift.ZipkinTracer
import com.twitter.finagle.{Http, Service}
import com.twitter.finagle.builder.{Server, ServerBuilder}
import com.twitter.finagle.http.{Request, Response}
import com.twitter.util.{Throw, Return}

/**
  * Created by Administrator on 2016/1/14.
  */
object ZkMain {

  def main(args: Array[String]): Unit = {

    val app = Application.init()

    val productController = new ProductController(app)

    val server = Http.server.
      withLabel("productserver").
      withTracer(ZipkinTracer.mk("192.168.99.100", 9410, DefaultStatsReceiver, 1.0f)).
      //      withTracer(ConsoleTracer).
      serveAndAnnounce(
        "zk!127.0.0.1:2181,127.0.0.1:2182,127.0.0.1:2183!/f/products!0",
        new InetSocketAddress(8082),
        new HttpServicePicker(productController.investService))


//    val server: Server = ServerBuilder()
//      .codec(Http())
//      .bindTo(new InetSocketAddress("127.0.0.1", 8082))
//      .name("productserver")
//      .build(new HttpServicePicker(productController.investService))
//
//    server.announce("zk!127.0.0.1:2181,127.0.0.1:2182,127.0.0.1:2183!/f/products!0").respond {
//      case Return(a) => println(s"announcement: $a")
//      case Throw(e) => println(s"error: $e")
//    }

  }

}
