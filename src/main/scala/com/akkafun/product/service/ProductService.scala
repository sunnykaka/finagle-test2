package com.akkafun.product.service

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger

import com.twitter.finagle.Service
import com.twitter.finagle.http._
import com.twitter.util.Future

trait ProductServiceComponent {

  def productService: ProductService

  val userInvoker: Service[Request, Response]

  trait ProductService {

    def invest(userId: Long, amount: Long, productId: Long, channel: String): Future[Either[Exception, Option[Long]]]

  }

}

trait DefaultProductServiceComponent extends ProductServiceComponent {
  self: StorageServiceComponent with CatalogServiceComponent =>

  def productService: ProductService = new DefaultProductService

  class DefaultProductService extends ProductService {
    val map = new ConcurrentHashMap[Long, Invest]()
    val idIncrementer = new AtomicInteger()

    override def invest(userId: Long, amount: Long, productId: Long, channel: String): Future[Either[Exception, Option[Long]]] = {
      println(s"调用invest, userId: $userId, amount: $amount, productId: $productId, channel: $channel")
      val id = idIncrementer.incrementAndGet()

      val request = Request("/", ("userId", userId.toString), ("tradeId", "5"), ("balance", amount.toString), ("remark", "6"))

      storageService.addStorage()

      userInvoker(request) flatMap { resp =>

        if (resp.getStatusCode() == Status.Ok.code && resp.getContentString() == "true") {
          val invest = new Invest(id, userId, amount, productId, channel)
          map.put(id, invest)
          Future.value(Right(Some(id.toLong)))
        } else {
          Future.value(Right(None))
        }

      } rescue {
        case e: Exception => Future.value(Left(e))
      }

    }
  }


}


case class Invest(id: Long, userId: Long, amount: Long, productId: Long, channel: String)
