package com.akkafun.product.service

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger

import com.akkafun.product.Trade
import com.akkafun.product.Trade.TradeType.TradeType

trait TradeServiceComponent {

  def tradeService: TradeService

  trait TradeService {
    def createTrade(userId: Long, productId: Long, amount: Long, tradeType: TradeType): Trade
  }

}

trait DefaultTradeServiceComponent extends TradeServiceComponent {

  def tradeService: TradeService = new DefaultTradeService

  val tradeMap = new ConcurrentHashMap[Long, Trade]()
  val idIncrementer = new AtomicInteger()

  class DefaultTradeService extends TradeService {
    override def createTrade(userId: Long, productId: Long, amount: Long, tradeType: TradeType): Trade = {
      val trade = Trade(idIncrementer.incrementAndGet(), userId, productId, tradeType)
      println(s"save trade: $trade")
      tradeMap.put(trade.id, trade)
      trade
    }
  }

}
