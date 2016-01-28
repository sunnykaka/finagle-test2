package com.akkafun.product.finagle


import java.net.InetSocketAddress

import com.akkafun.product.service._
import com.twitter.finagle.Service
import com.twitter.finagle.builder.ClientBuilder
import com.twitter.finagle.http._
import com.twitter.util.Future

/**
  * Created by Administrator on 2016/1/14.
  */
class ProductController(useZk: Boolean = false) { self =>

  val userInvoker: Service[Request, Response] = if(useZk) {
    ClientBuilder()
      .codec(Http())
      .hostConnectionLimit(10)
      .dest("zk!127.0.0.1:2181,127.0.0.1:2182,127.0.0.1:2183!/f/users")
      .build()
  } else {
    ClientBuilder()
      .codec(Http())
      .hosts(new InetSocketAddress(8081))
      .hostConnectionLimit(1)
      .build()
  }

  val storageService = new DefaultStorageServiceComponent{}.storageService
  val catalogService = new DefaultCatalogServiceComponent{}.catalogService
  val productServiceComponent = new DefaultProductServiceComponent with
    DefaultStorageServiceComponent with
    DefaultCatalogServiceComponent{

    override val userInvoker: Service[Request, Response] = self.userInvoker
  }
  val productService = productServiceComponent.productService


  object InvestService extends Service[Request, Response] {
    override def apply(request: Request): Future[Response] = {
      val userId = request.getLongParam("userId")
      val amount = request.getLongParam("amount")
      val productId = request.getLongParam("productId")
      val channel = request.getParam("channel")

      productService.invest(userId, amount, productId, channel) map {
        case Left(e) =>
          val response = Response(Version.Http11, Status.InternalServerError)
          response.contentString = e.getMessage
          response
        case Right(v) =>
          val response = Response(Version.Http11, Status.Ok)
          response.contentString = v.getOrElse(0L).toString
          response
      }
    }
  }


}
