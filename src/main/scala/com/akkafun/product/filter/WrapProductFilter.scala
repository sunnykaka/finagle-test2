package com.akkafun.product.filter

import com.akkafun.product.service.ProductServiceComponent
import com.akkafun.product.{Products, User}
import com.twitter.finagle.http.{Status, Version, Request, Response}
import com.twitter.finagle.{Filter, Service, SimpleFilter}
import com.twitter.util.Future

/**
  * Created by Administrator on 2016/1/28.
  */
class WrapProductFilter(productService: ProductServiceComponent#ProductService) extends Filter[Request, Response, ProductRequest, Response] {
  override def apply(request: Request, service: Service[ProductRequest, Response]): Future[Response] = {
    val productId = request.getLongParam("productId")
    productService.getById(productId).fold {
      val response = Response(Version.Http11, Status.BadRequest)
      response.contentString = s"unknown product id: $productId"
      Future.value(response)
    }(p => service(ProductRequest(request, p)))
  }
}

case class ProductRequest(request: Request, product: Products){

}