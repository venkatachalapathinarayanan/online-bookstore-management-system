package com.bookstore.ordermanagement.dto

data class AddToCartRequestDTO(
    val userId: Long,
    val bookId: Long,
    val quantity: Int
)

data class RemoveFromCartRequestDTO(
    val userId: Long,
    val bookId: Long
)

data class CartItemDTO(
    val bookId: Long,
    val quantity: Int
)

data class CartResponseDTO(
    val userId: Long,
    val items: List<CartItemDTO>
)

