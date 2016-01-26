package com.akkafun.product

import java.net.InetSocketAddress

import com.twitter.common.quantity.{Time, Amount}
import com.twitter.common.zookeeper.{ServerSetImpl, ZooKeeperClient}
import com.twitter.finagle.Service
import com.twitter.finagle.builder.ClientBuilder
import com.twitter.finagle.http._

/**
  * Created by Administrator on 2016/1/14.
  */
object ZkPortal {

  def main(args: Array[String]): Unit = {

    val client: Service[Request, Response] = ClientBuilder()
      .codec(Http())
      .hostConnectionLimit(10)
      .dest("zk!127.0.0.1:2181,127.0.0.1:2182,127.0.0.1:2183!/f/users")
      .build()

    val request = Request("/", ("userId", "1"), ("amount", "2"), ("productId", "3"), ("channel", "4"))
    client(request) onSuccess { response =>
      val responseString = response.contentString
      println("))) Received result for authorized request: " + responseString)
    } onFailure { error =>
      println("))) Unauthorized request errored (as desired): " + error.getClass.getName)
    } ensure {
      client.close()
    }
  }
}
