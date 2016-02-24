package com.akkafun.product

import java.net.InetSocketAddress

import com.akkafun.product.finagle.ProductController
import com.twitter.finagle.Http
import com.twitter.finagle.builder.{Server, ServerBuilder}
import com.twitter.finagle.http.{Request, Response}
import com.twitter.finagle.stats.DefaultStatsReceiver
import com.twitter.finagle.tracing.ConsoleTracer
import com.twitter.finagle.zipkin.thrift.ZipkinTracer
import com.twitter.util.Await

/**
  * Created by Administrator on 2016/1/14.
  */
object Main {

  def main(args: Array[String]): Unit = {

    val useZk = true

    val app = Application.init(useZk)

    val productController = new ProductController(app)

    val server = Http.server.
      withLabel("productserver").
      withTracer(ZipkinTracer.mk("192.168.99.100", 9410, DefaultStatsReceiver, 1.0f))
    if(useZk) {
      Await.result(server.serveAndAnnounce(
        "zk!127.0.0.1:2181,127.0.0.1:2182,127.0.0.1:2183!/f/products!0",
        new InetSocketAddress(8082),
        new HttpServicePicker(productController.investService)))

    } else {
      Await.result(server.serve(
        new InetSocketAddress(8082),
        new HttpServicePicker(productController.investService)))
    }

//    val server: Server = ServerBuilder()
//      .codec(Http().enableTracing(enable = true))
//      .bindTo(new InetSocketAddress(8082))
//      .name("productserver")
//      .tracer(ZipkinTracer.mk("192.168.99.100", 9410, DefaultStatsReceiver, 1.0f))
//      .build(new HttpServicePicker(productController.investService))


  }

}
