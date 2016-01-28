package com.akkafun.product.service

trait CatalogServiceComponent {

  def catalogService: CatalogService

  trait CatalogService {
    def addCatalog()
  }

}

trait DefaultCatalogServiceComponent extends CatalogServiceComponent{

  def catalogService: CatalogService = new DefaultCatalogService

  class DefaultCatalogService extends CatalogService {
    override def addCatalog(): Unit = {
      println("addCatalog")
    }

  }

}
