package com.akkafun.product.service

trait StorageServiceComponent {

  def storageService: StorageService

  trait StorageService {
    def addStorage()
  }

}

trait DefaultStorageServiceComponent extends StorageServiceComponent {

  def storageService: StorageService = new DefaultStorageService

  class DefaultStorageService extends StorageService {
    override def addStorage(): Unit = {
      println("addStorage")
    }
  }

}
