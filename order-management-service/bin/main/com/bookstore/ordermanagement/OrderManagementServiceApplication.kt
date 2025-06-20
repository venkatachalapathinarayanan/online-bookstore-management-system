package com.bookstore.ordermanagement

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan

@SpringBootApplication
@ComponentScan(basePackages = ["com.bookstore.ordermanagement", "com.bookstore.common"])
class OrderManagementServiceApplication

fun main(args: Array<String>) {
    runApplication<OrderManagementServiceApplication>(*args)
}

