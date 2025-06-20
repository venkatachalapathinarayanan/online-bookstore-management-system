package com.bookstore.ordermanagement.dto

data class OrderItemRequestDTO(
    val bookId: Long,
    val quantity: Int,
    val price: Double
)

data class OrderRequestDTO(
    val userId: Long,
    val items: List<OrderItemRequestDTO>
)

data class OrderItemResponseDTO(
    val bookId: Long,
    val quantity: Int,
    val price: Double
)

data class OrderResponseDTO(
    val id: Long,
    val userId: Long,
    val items: List<OrderItemResponseDTO>,
    val status: String
)
