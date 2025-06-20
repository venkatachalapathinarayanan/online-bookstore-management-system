package com.bookstore.ordermanagement.repository

import com.bookstore.ordermanagement.model.Order
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface OrderRepository : JpaRepository<Order, Long> {
    fun findAllByUserId(userId: Long): List<Order>
}
