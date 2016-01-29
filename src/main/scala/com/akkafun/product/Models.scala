package com.akkafun.product

import com.akkafun.product.Products.BorrowStatus
import com.akkafun.product.Products.BorrowStatus.BorrowStatus
import com.akkafun.product.Trade.TradeType.TradeType


case class User(id: Long,
                name: String,
                balance: Long)

object User {
  val userMap = Map(
    1L -> User(id = 1, name = "foo", balance = 2000000),
    2L -> User(id = 2, name = "Carrie", balance = 10000000000L),
    3L -> User(id = 3, name = "Geralt", balance = 20000L)
  )
}


case class Invest(id: Long,
                  userId: Long,
                  amount: Long,
                  productId: Long)

case class Products(id: Long,
                   deadline: Int,
                   borrowAmount: Long,
                   hasInvestAmount: Long = 0L,
                   borrowStatus: BorrowStatus = BorrowStatus.Waiting)

object Products {

  object BorrowStatus extends Enumeration() {
    type BorrowStatus = Value
    val Waiting = Value("Waiting")
    val Tendering = Value("Tendering")
    val Full = Value("Full")
  }

  val productMap = Map(
    1L -> Products(id = 1, deadline = 12, borrowAmount = 1000000L),
    2L -> Products(id = 2, deadline = 12, borrowAmount = 2000000L),
    3L -> Products(id = 3, deadline = 12, borrowAmount = 5000000L, hasInvestAmount = 5000000L, borrowStatus = BorrowStatus.Full)
  )
}

case class Trade(id: Long,
                 userId: Long,
                 productId: Long,
                 tradeType: TradeType)

object Trade {

  object TradeType extends Enumeration() {
    type TradeType = Value
    val Invest = Value("Invest")
    val Deposit = Value("Deposit")
  }

}
