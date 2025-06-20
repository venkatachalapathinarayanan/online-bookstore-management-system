package com.bookstore.usermanagement.controller

import com.bookstore.usermanagement.dto.UserRequestDTO
import com.bookstore.usermanagement.dto.UserResponseDTO
import com.bookstore.usermanagement.service.AdminUserService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@Tag(name = "Admin Management", description = "Endpoints for managing admin users (requires ADMIN privileges)")
@RestController
@RequestMapping("/api/admins")
@SecurityRequirement(name = "bearerAuth")
class AdminUserController(private val adminUserService: AdminUserService) {

    @Operation(summary = "Create admin user", description = "Creates a new admin user with ADMIN role")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "201", description = "Admin user created successfully"),
            ApiResponse(responseCode = "400", description = "Invalid request data"),
            ApiResponse(responseCode = "401", description = "Unauthorized - authentication required"),
            ApiResponse(responseCode = "403", description = "Forbidden - admin privileges required"),
            ApiResponse(responseCode = "409", description = "Username or email already exists")
        ]
    )
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun addAdmin(
        @Parameter(description = "Admin user registration data", required = true)
        @Valid @RequestBody request: UserRequestDTO
    ): UserResponseDTO = adminUserService.addAdmin(request)

    @Operation(summary = "Get admin user by ID", description = "Retrieves admin user details by their unique ID")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Admin user found and returned"),
            ApiResponse(responseCode = "401", description = "Unauthorized - authentication required"),
            ApiResponse(responseCode = "403", description = "Forbidden - admin privileges required"),
            ApiResponse(responseCode = "404", description = "Admin user not found")
        ]
    )
    @GetMapping("/{id}")
    fun getAdmin(
        @Parameter(description = "Admin user ID", required = true, example = "1")
        @PathVariable id: Long
    ): UserResponseDTO = adminUserService.getAdmin(id)

    @Operation(summary = "List all admin users", description = "Retrieves a list of all admin users")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "List of admin users returned"),
            ApiResponse(responseCode = "401", description = "Unauthorized - authentication required"),
            ApiResponse(responseCode = "403", description = "Forbidden - admin privileges required")
        ]
    )
    @GetMapping
    fun listAdmins(): List<UserResponseDTO> = adminUserService.listAdmins()

    @Operation(summary = "Update admin user", description = "Updates admin user information by ID")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Admin user updated successfully"),
            ApiResponse(responseCode = "400", description = "Invalid request data"),
            ApiResponse(responseCode = "401", description = "Unauthorized - authentication required"),
            ApiResponse(responseCode = "403", description = "Forbidden - admin privileges required"),
            ApiResponse(responseCode = "404", description = "Admin user not found"),
            ApiResponse(responseCode = "409", description = "Username or email already exists")
        ]
    )
    @PutMapping("/{id}")
    fun updateAdmin(
        @Parameter(description = "Admin user ID", required = true, example = "1")
        @PathVariable id: Long,
        @Parameter(description = "Updated admin user data", required = true)
        @Valid @RequestBody request: UserRequestDTO
    ): UserResponseDTO = adminUserService.updateAdmin(id, request)

    @Operation(summary = "Delete admin user", description = "Removes an admin user from the system by ID")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "204", description = "Admin user deleted successfully"),
            ApiResponse(responseCode = "401", description = "Unauthorized - authentication required"),
            ApiResponse(responseCode = "403", description = "Forbidden - admin privileges required"),
            ApiResponse(responseCode = "404", description = "Admin user not found")
        ]
    )
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteAdmin(
        @Parameter(description = "Admin user ID", required = true, example = "1")
        @PathVariable id: Long
    ) = adminUserService.deleteAdmin(id)
}

