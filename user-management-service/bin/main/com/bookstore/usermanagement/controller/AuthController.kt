package com.bookstore.usermanagement.controller

import com.bookstore.usermanagement.dto.LoginRequestDTO
import com.bookstore.usermanagement.dto.LoginResponseDTO
import com.bookstore.usermanagement.repository.UserRepository
import com.bookstore.usermanagement.security.JwtUtil
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException

@Tag(name = "Authentication", description = "Endpoints for user authentication and authorization")
@RestController
@RequestMapping("/api/auth")
class AuthController(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtUtil: JwtUtil
) {

    @Operation(
        summary = "User login", 
        description = "Authenticates a user with username and password, returns JWT token for subsequent API calls"
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Login successful, JWT token returned"),
            ApiResponse(responseCode = "401", description = "Invalid username or password"),
            ApiResponse(responseCode = "400", description = "Invalid request format")
        ]
    )
    @PostMapping("/login")
    fun login(
        @Parameter(description = "User login credentials", required = true)
        @Valid @RequestBody loginRequest: LoginRequestDTO
    ): LoginResponseDTO {
        val username = loginRequest.username 
            ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Username is required")
            
        val user = userRepository.findByUserName(username)
            .orElseThrow { 
                ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid username or password") 
            }

        if (!passwordEncoder.matches(loginRequest.password, user.password)) {
            throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid username or password")
        }

        val token = jwtUtil.generateToken(user.userName, listOf(user.role))
        return LoginResponseDTO(token = token)
    }
}

