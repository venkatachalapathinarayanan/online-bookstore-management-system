package com.bookstore.bookinventory.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive
import jakarta.validation.constraints.Size
import java.time.LocalDateTime

// Request DTO for creating/updating a book
data class BookRequestDTO(
    @field:NotBlank(message = "Title is required")
    @field:Size(max = 255, message = "Title must be less than 255 characters")
    val title: String?,

    @field:NotBlank(message = "Author is required")
    @field:Size(max = 255, message = "Author must be less than 255 characters")
    val author: String?,

    @field:NotBlank(message = "Genre is required")
    @field:Size(max = 100, message = "Genre must be less than 100 characters")
    val genre: String?,

    @field:NotBlank(message = "ISBN is required")
    @field:Size(max = 20, message = "ISBN must be less than 20 characters")
    val isbn: String?,

    @field:NotNull(message = "Price is required")
    @field:Positive(message = "Price must be positive")
    val price: Double?,

    @field:NotNull(message = "Quantity is required")
    @field:Positive(message = "Quantity must be positive")
    val quantity: Int?
)

// Response DTO for returning book details
data class BookResponseDTO(
    val id: Long,
    val title: String,
    val author: String,
    val genre: String,
    val isbn: String,
    val price: Double,
    val quantity: Int,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
    val isDeleted: Boolean
)

