package com.akkafun.product.finagle


import com.akkafun.product.filter.ProductRequest
import com.akkafun.product.{Application, RouteService}
import com.twitter.finagle.Service
import com.twitter.finagle.http._
import com.twitter.util.Future

/**
  * Created by Administrator on 2016/1/14.
  */
class ProductController(app: Application) { self =>

  import app._

  class InvestService extends Service[ProductRequest, Response] {
    override def apply(pr: ProductRequest): Future[Response] = {
      val request = pr.request
      val userId = request.getLongParam("userId")
      val amount = request.getLongParam("amount")
      val channel = request.getParam("channel")

      productService.invest(userId, amount, pr.product, channel) map {
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

  val investService = new RouteService("invest", wrapProductFilter.andThen(new InvestService))

}
