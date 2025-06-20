package com.bookstore.usermanagement.controller

import com.bookstore.usermanagement.dto.LoginRequestDTO
import com.bookstore.usermanagement.model.User
import com.bookstore.usermanagement.repository.UserRepository
import com.bookstore.usermanagement.security.JwtUtil
import com.fasterxml.jackson.databind.ObjectMapper
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import java.util.*

class AuthControllerTest {
    private lateinit var mockMvc: MockMvc
    private val userRepository: UserRepository = mockk()
    private val passwordEncoder: PasswordEncoder = mockk()
    private val jwtUtil: JwtUtil = mockk()
    private val objectMapper = ObjectMapper()

    private lateinit var authController: AuthController

    @BeforeEach
    fun setUp() {
        authController = AuthController(userRepository, passwordEncoder, jwtUtil)
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build()
    }

    @Test
    fun `login should return JWT token when credentials are valid`() {
        // Arrange
        val loginRequest = LoginRequestDTO("testuser", "password123")
        val user = User(
            id = 1L,
            userName = "testuser",
            fullName = "Test User",
            email = "test@example.com",
            password = "encodedPassword",
            phoneNumber = "1234567890",
            address = "123 Test St",
            role = "USERS"
        )
        val expectedToken = "jwt.token.here"

        every { userRepository.findByUserName("testuser") } returns Optional.of(user)
        every { passwordEncoder.matches("password123", "encodedPassword") } returns true
        every { jwtUtil.generateToken("testuser", listOf("USERS")) } returns expectedToken

        // Act & Assert
        mockMvc.perform(
            post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.token").value(expectedToken))
    }

    @Test
    fun `login should return 401 when user not found`() {
        // Arrange
        val loginRequest = LoginRequestDTO("nonexistentuser", "password123")

        every { userRepository.findByUserName("nonexistentuser") } returns Optional.empty()

        // Act & Assert
        mockMvc.perform(
            post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest))
        )
            .andExpect(status().isUnauthorized)
    }

    @Test
    fun `login should return 401 when password is incorrect`() {
        // Arrange
        val loginRequest = LoginRequestDTO("testuser", "wrongpassword")
        val user = User(
            id = 1L,
            userName = "testuser",
            fullName = "Test User",
            email = "test@example.com",
            password = "encodedPassword",
            phoneNumber = "1234567890",
            address = "123 Test St",
            role = "USERS"
        )

        every { userRepository.findByUserName("testuser") } returns Optional.of(user)
        every { passwordEncoder.matches("wrongpassword", "encodedPassword") } returns false

        // Act & Assert
        mockMvc.perform(
            post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest))
        )
            .andExpect(status().isUnauthorized)
    }

    @Test
    fun `login should return 400 when request body is invalid`() {
        // Act & Assert
        mockMvc.perform(
            post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\": \"\", \"password\": \"\"}")
        )
            .andExpect(status().isBadRequest)
    }
}
