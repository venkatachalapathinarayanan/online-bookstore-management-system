package com.bookstore.ordermanagement.repository

import com.bookstore.ordermanagement.model.Cart
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
interface CartRepository : JpaRepository<Cart, Long> {
    fun findByUserId(userId: Long): Cart?

    @Transactional
    @Modifying
    fun deleteByUserId(userId: Long)
}

