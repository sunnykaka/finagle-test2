package com.akkafun.product

import com.akkafun.product.filter.WrapProductFilter
import com.akkafun.product.service.{DefaultProductServiceComponent, DefaultTradeServiceComponent, DefaultUserServiceComponent}
import com.twitter.finagle.http.{Request, Response}
import com.twitter.finagle.stats.DefaultStatsReceiver
import com.twitter.finagle.zipkin.thrift.ZipkinTracer
import com.twitter.finagle.{Http, Service}

/**
  * Created by Administrator on 2016/1/28.
  */
object Application {


  def init(useZk: Boolean = false): Application = new Application { self =>

    val dest = if(useZk) "zk!127.0.0.1:2181,127.0.0.1:2182,127.0.0.1:2183!/f/users" else "127.0.0.1:8081"

    val userInvoker: Service[Request, Response] = Http.client.
      withSessionPool.maxSize(10).
      withLabel("product-user-client").
      withTracer(ZipkinTracer.mk("192.168.99.100", 9410, DefaultStatsReceiver, 1.0f)).
      newService(dest)

    val tradeComponent = new DefaultTradeServiceComponent{}
    val tradeService = tradeComponent.tradeService

    val userComponent = new DefaultUserServiceComponent{}
    val userService = userComponent.userService

    val productServiceComponent = new DefaultProductServiceComponent with
      DefaultTradeServiceComponent with
      DefaultUserServiceComponent{

      override val userInvoker: Service[Request, Response] = self.userInvoker
    }
    val productService = productServiceComponent.productService

    val wrapProductFilter = new WrapProductFilter(productService)
  }

}

trait Application {
  val userInvoker: Service[Request, Response]

  val tradeComponent: DefaultTradeServiceComponent
  val tradeService: DefaultTradeServiceComponent#TradeService

  val userComponent: DefaultUserServiceComponent
  val userService: DefaultUserServiceComponent#UserService

  val productServiceComponent: DefaultProductServiceComponent
  val productService: DefaultProductServiceComponent#ProductService

  val wrapProductFilter: WrapProductFilter

}
