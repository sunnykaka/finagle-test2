package com.akkafun.product.finagle


import com.akkafun.product.service.ProductService
import com.twitter.finagle.Service
import com.twitter.finagle.http.{Status, Version, Response, Request}
import com.twitter.util.Future

/**
  * Created by Administrator on 2016/1/14.
  */
object ProductController {

  val productService = new ProductService

  class InvestService extends Service[Request, Response] {
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
