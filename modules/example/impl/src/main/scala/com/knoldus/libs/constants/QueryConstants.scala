package com.knoldus.libs.constants

  object QueryConstants {

    val CREATE_TABLE = """CREATE TABLE IF NOT EXISTS customerdatabase.customer (
                         |id text PRIMARY KEY, name text, email text)"""

    // Get product from database by product id.
    val GET_PRODUCT = "SELECT * FROM customer WHERE id =?"

    // Get all the products from database.
    val GET_ALL_PRODUCTS = "SELECT * FROM customer"

    val INSERT_PRODUCT = "INSERT INTO customerdatabase.customer (id, name, email) VALUES (?, ?, ?)"

    val DELETE_PRODUCT = "DELETE FROM customer where id = ?"
  }


