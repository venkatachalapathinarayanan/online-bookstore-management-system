package com.bookstore.usermanagement.controller

import com.bookstore.usermanagement.dto.UserRequestDTO
import com.bookstore.usermanagement.dto.UserResponseDTO
import com.bookstore.usermanagement.service.UserService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@Tag(name = "Users", description = "Endpoints for managing regular users")
@RestController
@RequestMapping("/api/users")
class UserController(private val userService: UserService) {

    @Operation(summary = "Create a new user", description = "Registers a new user in the system with USER role")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "201", description = "User created successfully"),
            ApiResponse(responseCode = "400", description = "Invalid request data"),
            ApiResponse(responseCode = "409", description = "Username or email already exists")
        ]
    )
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun addUser(
        @Parameter(description = "User registration data", required = true)
        @Valid @RequestBody request: UserRequestDTO
    ): UserResponseDTO = userService.addUser(request)

    @Operation(summary = "Get user by ID", description = "Retrieves user details by their unique ID")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "User found and returned"),
            ApiResponse(responseCode = "404", description = "User not found")
        ]
    )
    @GetMapping("/{id}")
    fun getUser(
        @Parameter(description = "User ID", required = true, example = "1")
        @PathVariable id: Long
    ): UserResponseDTO = userService.getUser(id)

    @Operation(summary = "List all users", description = "Retrieves a list of all registered users")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "List of users returned")
        ]
    )
    @GetMapping
    fun listUsers(): List<UserResponseDTO> = userService.listUsers()

    @Operation(summary = "Update user", description = "Updates user information by ID")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "User updated successfully"),
            ApiResponse(responseCode = "400", description = "Invalid request data"),
            ApiResponse(responseCode = "404", description = "User not found"),
            ApiResponse(responseCode = "409", description = "Username or email already exists")
        ]
    )
    @PutMapping("/{id}")
    fun updateUser(
        @Parameter(description = "User ID", required = true, example = "1")
        @PathVariable id: Long,
        @Parameter(description = "Updated user data", required = true)
        @Valid @RequestBody request: UserRequestDTO
    ): UserResponseDTO = userService.updateUser(id, request)

    @Operation(summary = "Delete user", description = "Removes a user from the system by ID")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "204", description = "User deleted successfully"),
            ApiResponse(responseCode = "404", description = "User not found")
        ]
    )
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteUser(
        @Parameter(description = "User ID", required = true, example = "1")
        @PathVariable id: Long
    ) = userService.deleteUser(id)
}

