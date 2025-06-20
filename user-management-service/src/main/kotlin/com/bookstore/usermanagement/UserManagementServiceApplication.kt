package com.bookstore.usermanagement

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan

@SpringBootApplication
@ComponentScan(basePackages = ["com.bookstore.usermanagement", "com.bookstore.common"])
class UserManagementServiceApplication

fun main(args: Array<String>) {
    runApplication<UserManagementServiceApplication>(*args)
}

