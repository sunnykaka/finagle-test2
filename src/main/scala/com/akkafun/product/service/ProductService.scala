package com.akkafun.product.service

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger

import com.akkafun.product.{Products, Invest}
import com.twitter.finagle.Service
import com.twitter.finagle.http._
import com.twitter.util.Future

trait ProductServiceComponent {

  def productService: ProductService

  val userInvoker: Service[Request, Response]

  trait ProductService {

    def invest(userId: Long, amount: Long, product: Products, channel: String): Future[Either[Exception, Option[Long]]]

    def getById(id: Long): Option[Products]

  }

}

trait DefaultProductServiceComponent extends ProductServiceComponent {
  self: StorageServiceComponent with UserServiceComponent =>

  def productService: ProductService = new DefaultProductService

  class DefaultProductService extends ProductService {

    import Products._

    val investMap = new ConcurrentHashMap[Long, Invest]()
    val idIncrementer = new AtomicInteger()

    override def invest(userId: Long, amount: Long, product: Products, channel: String): Future[Either[Exception, Option[Long]]] = {
      println(s"调用invest, userId: $userId, amount: $amount, productId: ${product.id}")
      val id = idIncrementer.incrementAndGet()

      val request = Request("/", ("userId", userId.toString), ("tradeId", "5"), ("balance", amount.toString), ("remark", "6"))

//      storageService.addStorage()

      userInvoker(request) flatMap { resp =>

        if (resp.getStatusCode() == Status.Ok.code && resp.getContentString() == "true") {
          val invest = new Invest(id, userId, amount, product.id)
          investMap.put(id, invest)
          Future.value(Right(Some(id.toLong)))
        } else {
          Future.value(Right(None))
        }

      } rescue {
        case e: Exception => Future.value(Left(e))
      }

    }

    override def getById(id: Long): Option[Products] = productMap.get(id)
  }


}
