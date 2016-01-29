package com.akkafun.product.service

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger

import com.akkafun.product.Trade.TradeType
import com.akkafun.product.{Products, Invest}
import com.twitter.finagle.Service
import com.twitter.finagle.http._
import com.twitter.util.Future

trait ProductServiceComponent {

  def productService: ProductService

  val userInvoker: Service[Request, Response]

  trait ProductService {

    def invest(userId: Long, amount: Long, product: Products, channel: String): Future[Either[Exception, Long]]

    def getById(id: Long): Option[Products]

  }

}

trait DefaultProductServiceComponent extends ProductServiceComponent {
  self: TradeServiceComponent with UserServiceComponent =>

  def productService: ProductService = new DefaultProductService

  class DefaultProductService extends ProductService {

    import Products._

    val investMap = new ConcurrentHashMap[Long, Invest]()
    val idIncrementer = new AtomicInteger()

    override def invest(userId: Long, amount: Long, product: Products, channel: String): Future[Either[Exception, Long]] = {
      println(s"invest method, userId: $userId, amount: $amount, productId: ${product.id}")
      val id = idIncrementer.incrementAndGet()
      val trade = tradeService.createTrade(userId, product.id, amount, TradeType.Invest)

      val request = Request("/", ("userId", userId.toString), ("tradeId", trade.id.toString), ("balance", amount.toString), ("remark", "remark"))

//      storageService.addStorage()

      userInvoker(request) flatMap { resp =>
        println(s"user invoke response status: ${resp.status}, response string: ${resp.contentString} ")
        if (resp.getStatusCode() == Status.Ok.code && resp.getContentString() == "true") {
          val invest = new Invest(id, userId, amount, product.id)
          println(s"save invest: $invest")
          investMap.put(id, invest)
          Future.value(Right(id.toLong))
        } else {
          Future.value(Left(new RuntimeException(s"invoke user invest error, response status: ${resp.status}, response string: ${resp.contentString}")))
        }
      }

    }

    override def getById(id: Long): Option[Products] = productMap.get(id)
  }


}
