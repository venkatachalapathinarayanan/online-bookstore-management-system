package com.bookstore.bookinventory

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan

@SpringBootApplication
@ComponentScan(basePackages = ["com.bookstore.bookinventory", "com.bookstore.common"])
class BookInventoryServiceApplication

fun main(args: Array<String>) {
    runApplication<BookInventoryServiceApplication>(*args)
}

