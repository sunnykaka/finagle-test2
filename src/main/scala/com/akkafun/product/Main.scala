package com.akkafun.product

import java.net.InetSocketAddress

import com.akkafun.product.finagle.ProductController
import com.twitter.finagle.builder.{Server, ServerBuilder}
import com.twitter.finagle.http.{Http, Request, Response}

/**
  * Created by Administrator on 2016/1/14.
  */
object Main {

  def main(args: Array[String]): Unit = {

    val app = Application.init()

    val productController = new ProductController(app)

    val server: Server = ServerBuilder()
      .codec(Http())
      .bindTo(new InetSocketAddress(8082))
      .name("productserver")
      .build(new HttpServicePicker(productController.investService))


  }

}
