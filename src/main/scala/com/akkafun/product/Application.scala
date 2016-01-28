package com.akkafun.product

import java.net.InetSocketAddress

import com.akkafun.product.filter.WrapProductFilter
import com.akkafun.product.service.{DefaultProductServiceComponent, DefaultUserServiceComponent, DefaultStorageServiceComponent}
import com.twitter.finagle.Service
import com.twitter.finagle.builder.ClientBuilder
import com.twitter.finagle.http.{Http, Response, Request}

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
        .codec(Http())
        .hosts(new InetSocketAddress(8081))
        .hostConnectionLimit(1)
        .build()
    }

    val storageComponent = new DefaultStorageServiceComponent{}
    val storageService = storageComponent.storageService

    val userComponent = new DefaultUserServiceComponent{}
    val userService = userComponent.userService

    val productServiceComponent = new DefaultProductServiceComponent with
      DefaultStorageServiceComponent with
      DefaultUserServiceComponent{

      override val userInvoker: Service[Request, Response] = self.userInvoker
    }
    val productService = productServiceComponent.productService

    val wrapProductFilter = new WrapProductFilter(productService)
  }

}

trait Application {
  val userInvoker: Service[Request, Response]

  val storageComponent: DefaultStorageServiceComponent
  val storageService: DefaultStorageServiceComponent#StorageService

  val userComponent: DefaultUserServiceComponent
  val userService: DefaultUserServiceComponent#UserService

  val productServiceComponent: DefaultProductServiceComponent
  val productService: DefaultProductServiceComponent#ProductService

  val wrapProductFilter: WrapProductFilter

}
