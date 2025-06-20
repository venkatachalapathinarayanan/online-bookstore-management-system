package com.bookstore.bookinventory.dto

import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.PositiveOrZero

// Request DTO for updating inventory

data class InventoryUpdateRequestDTO(
    @field:NotNull(message = "Book ID is required")
    val bookId: Long?,
    @field:NotNull(message = "Quantity is required")
    @field:PositiveOrZero(message = "Quantity must be zero or positive")
    val quantity: Int?
)

data class InventoryDecreaseRequestDTO(
    @field:NotNull(message = "Book ID is required")
    val bookId: Long?,
    @field:NotNull(message = "Decrease amount is required")
    @field:PositiveOrZero(message = "Decrease amount must be zero or positive")
    val decreaseBy: Int?
)

// Response DTO for inventory status

data class InventoryStatusDTO(
    val bookId: Long,
    val title: String,
    val quantity: Int
)

