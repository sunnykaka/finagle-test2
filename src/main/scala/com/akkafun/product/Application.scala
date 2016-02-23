package com.akkafun.product

import java.net.InetSocketAddress

import com.akkafun.product.filter.WrapProductFilter
import com.akkafun.product.service.{DefaultProductServiceComponent, DefaultUserServiceComponent, DefaultTradeServiceComponent}
import com.twitter.finagle.Service
import com.twitter.finagle.builder.ClientBuilder
import com.twitter.finagle.http.{Http, Response, Request}
import com.twitter.finagle.stats.DefaultStatsReceiver
import com.twitter.finagle.zipkin.thrift.ZipkinTracer

/**
  * Created by Administrator on 2016/1/28.
  */
object Application {


  def init(useZk: Boolean = false): Application = new Application { self =>
    val userInvoker: Service[Request, Response] = if(useZk) {
      ClientBuilder()
        .codec(Http())
        .hostConnectionLimit(10)
        .dest("zk!127.0.0.1:2181,127.0.0.1:2182,127.0.0.1:2183!/f/users")
        .build()
    } else {
      ClientBuilder()
        .codec(Http().enableTracing(enable = true))
        .hosts(new InetSocketAddress(8081))
        .hostConnectionLimit(1)
        .tracer(ZipkinTracer.mk("192.168.99.100", 9410, DefaultStatsReceiver, 1.0f))
        .build()
    }

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
