package com.akkafun.product.service

import com.akkafun.product.User

trait UserServiceComponent {

  def userService: UserService

  trait UserService {
    def auth(name: String): Option[User]
  }

}

trait DefaultUserServiceComponent extends UserServiceComponent{

  def userService: UserService = new DefaultUserService

  class DefaultUserService extends UserService {
    override def auth(name: String): Option[User] = ???
  }

}
