package com.bookstore.usermanagement.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

// DTO for creating/updating a user
data class UserRequestDTO(
    @field:NotBlank
    val userName: String,

    @field:NotBlank
    val fullName: String,

    @field:NotBlank
    @field:Email
    val email: String,

    @field:NotBlank
    val password: String,

    @field:NotBlank
    val phoneNumber: String,

    @field:NotBlank
    val address: String,
)

// DTO for returning user details
data class UserResponseDTO(
    val id: Long,
    val userName: String,
    val fullName: String,
    val email: String,
    val phoneNumber: String,
    val address: String,
    val role: String
)

