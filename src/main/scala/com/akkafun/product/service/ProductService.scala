package com.akkafun.product.service

import java.net.InetSocketAddress
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger

import com.twitter.finagle.Service
import com.twitter.finagle.builder.ClientBuilder
import com.twitter.finagle.http._
import com.twitter.util.Future

/**
  * Created by Administrator on 2016/1/14.
  */
class ProductService {

  val map = new ConcurrentHashMap[Long, Invest]()
  val idIncrementer = new AtomicInteger()

  def invest(userId: Long, amount: Long, productId: Long, channel: String): Future[Either[Exception, Option[Long]]] = {
    println(s"调用invest, userId: $userId, amount: $amount, productId: $productId, channel: $channel")
    val id = idIncrementer.incrementAndGet()

    val client: Service[Request, Response] = ClientBuilder()
      .codec(Http())
      .hosts(new InetSocketAddress(8080))
      .hostConnectionLimit(1)
      .build()

    val request = Request("/", ("userId", userId.toString), ("tradeId", "5"), ("balance", amount.toString), ("remark", "6"))
    client(request) flatMap { resp =>

      if(resp.getStatusCode() == Status.Ok.code && resp.getContentString() == "true") {
        val invest = new Invest(id, userId, amount, productId, channel)
        map.put(id, invest)
        Future.value(Right(Some(id.toLong)))
      } else {
        Future.value(Right(None))
      }

    } rescue {
      case e: Exception => Future.value(Left(e))
    } ensure {
      client.close()
    }

  }

  case class Invest(id: Long, userId: Long, amount: Long, productId: Long, channel: String)

}
