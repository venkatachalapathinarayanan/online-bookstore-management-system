package com.bookstore.ordermanagement.dto

import java.math.BigDecimal

data class OrderItemRequestDTO(
    val bookId: Long,
    val quantity: Int,
    val price: BigDecimal
)

data class OrderRequestDTO(
    val userId: Long,
    val items: List<OrderItemRequestDTO>
)

data class OrderItemResponseDTO(
    val bookId: Long,
    val quantity: Int,
    val price: BigDecimal
)

data class OrderResponseDTO(
    val id: Long,
    val userId: Long,
    val items: List<OrderItemResponseDTO>,
    val status: String
)
