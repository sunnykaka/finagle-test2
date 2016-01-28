package com.akkafun.product

import com.twitter.finagle.{ServiceFactory, ServiceProxy, Service}
import com.twitter.finagle.http._
import com.twitter.util.{Closable, Time, Future}


/**
  * Created by Administrator on 2016/1/28.
  */
abstract class ServicePicker[-Req, +Resp](services: RouteService[Req, Resp]*) extends Service[Req, Resp] {

  val duplicateServiceNames = services.map(s => s.name).groupBy(identity).collect {case (x, List(_, _, _*)) => x}

  if(duplicateServiceNames.nonEmpty) {
    throw new RuntimeException(s"Find duplicate service name: $duplicateServiceNames, please check your configuration!")
  }

  val serviceMap = services.map(s => s.name -> s).toMap
  println(s"serviceMap: $serviceMap")

  override def close(deadline: Time) = Closable.all(services:_*).close(deadline)

}

case class HttpServicePicker(services: RouteService[Request, Response]*) extends ServicePicker[Request, Response](services:_*) {

  override def apply(request: Request): Future[Response] = {
    val serviceName = request.getParam("__serviceName", "")
    val a = serviceMap.getOrElse(serviceName, NilHttpRouteService)
    a.apply(request)
  }
}

case class RouteService[-Req, +Resp](name: String, target: Service[Req, Resp]) extends ServiceProxy[Req, Resp](target) {
}

object NilHttpRouteService extends RouteService[Request, Response]("", new Service[Request, Response] {
  override def apply(request: Request): Future[Response] = Future.exception(new IllegalArgumentException("unknown request service"))
})
