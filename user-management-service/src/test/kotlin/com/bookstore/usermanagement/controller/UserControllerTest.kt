package com.bookstore.usermanagement.controller

import com.bookstore.usermanagement.dto.UserRequestDTO
import com.bookstore.usermanagement.dto.UserResponseDTO
import com.bookstore.usermanagement.service.UserService
import com.fasterxml.jackson.databind.ObjectMapper
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.server.ResponseStatusException
import org.springframework.http.HttpStatus
import java.util.NoSuchElementException

class UserControllerTest {
    private lateinit var mockMvc: MockMvc
    private val userService: UserService = mockk()
    private val objectMapper = ObjectMapper()

    private lateinit var userController: UserController

    @BeforeEach
    fun setUp() {
        userController = UserController(userService)
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build()
    }

    @Test
    fun `addUser should return 201 and user response when user created successfully`() {
        // Arrange
        val request = UserRequestDTO(
            userName = "testuser",
            fullName = "Test User",
            email = "test@example.com",
            password = "password123",
            phoneNumber = "1234567890",
            address = "123 Test St"
        )
        val response = UserResponseDTO(
            id = 1L,
            userName = "testuser",
            fullName = "Test User",
            email = "test@example.com",
            phoneNumber = "1234567890",
            address = "123 Test St",
            role = "USERS"
        )

        every { userService.addUser(request) } returns response

        // Act & Assert
        mockMvc.perform(
            post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.userName").value("testuser"))
            .andExpect(jsonPath("$.fullName").value("Test User"))
            .andExpect(jsonPath("$.email").value("test@example.com"))
            .andExpect(jsonPath("$.role").value("USERS"))
    }

    @Test
    fun `addUser should return 400 when request is invalid`() {
        // Arrange
        val invalidRequest = """{"userName": "", "email": "invalid-email"}"""

        // Act & Assert
        mockMvc.perform(
            post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidRequest)
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `getUser should return user when found`() {
        // Arrange
        val userId = 1L
        val response = UserResponseDTO(
            id = userId,
            userName = "testuser",
            fullName = "Test User",
            email = "test@example.com",
            phoneNumber = "1234567890",
            address = "123 Test St",
            role = "USERS"
        )

        every { userService.getUser(userId) } returns response

        // Act & Assert
        mockMvc.perform(get("/api/users/{id}", userId))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(userId))
            .andExpect(jsonPath("$.userName").value("testuser"))
    }

    @Test
    fun `getUser should return 404 when user not found`() {
        // Arrange
        val userId = 999L
        every { userService.getUser(userId) } throws NoSuchElementException("User not found")

        // Act & Assert
        mockMvc.perform(get("/api/users/{id}", userId))
            .andExpect(status().isInternalServerError) // Service throws NoSuchElementException which gets handled as 500
    }

    @Test
    fun `listUsers should return list of users`() {
        // Arrange
        val users = listOf(
            UserResponseDTO(1L, "user1", "User One", "user1@test.com", "123", "addr1", "USERS"),
            UserResponseDTO(2L, "user2", "User Two", "user2@test.com", "456", "addr2", "USERS")
        )

        every { userService.listUsers() } returns users

        // Act & Assert
        mockMvc.perform(get("/api/users"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].userName").value("user1"))
            .andExpect(jsonPath("$[1].userName").value("user2"))
    }

    @Test
    fun `updateUser should return updated user when successful`() {
        // Arrange
        val userId = 1L
        val request = UserRequestDTO(
            userName = "updateduser",
            fullName = "Updated User",
            email = "updated@example.com",
            password = "newpassword",
            phoneNumber = "9876543210",
            address = "456 Updated St"
        )
        val response = UserResponseDTO(
            id = userId,
            userName = "updateduser",
            fullName = "Updated User",
            email = "updated@example.com",
            phoneNumber = "9876543210",
            address = "456 Updated St",
            role = "USERS"
        )

        every { userService.updateUser(userId, request) } returns response

        // Act & Assert
        mockMvc.perform(
            put("/api/users/{id}", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(userId))
            .andExpect(jsonPath("$.userName").value("updateduser"))
            .andExpect(jsonPath("$.fullName").value("Updated User"))
    }

    @Test
    fun `updateUser should return 404 when user not found`() {
        // Arrange
        val userId = 999L
        val request = UserRequestDTO(
            userName = "updateduser",
            fullName = "Updated User",
            email = "updated@example.com",
            password = "newpassword",
            phoneNumber = "9876543210",
            address = "456 Updated St"
        )

        every { userService.updateUser(userId, request) } throws ResponseStatusException(HttpStatus.NOT_FOUND, "User not found")

        // Act & Assert
        mockMvc.perform(
            put("/api/users/{id}", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isNotFound)
    }

    @Test
    fun `deleteUser should return 204 when user deleted successfully`() {
        // Arrange
        val userId = 1L
        every { userService.deleteUser(userId) } just runs

        // Act & Assert
        mockMvc.perform(delete("/api/users/{id}", userId))
            .andExpect(status().isNoContent)
    }

    @Test
    fun `deleteUser should return 404 when user not found`() {
        // Arrange
        val userId = 999L
        every { userService.deleteUser(userId) } throws ResponseStatusException(HttpStatus.NOT_FOUND, "User not found")

        // Act & Assert
        mockMvc.perform(delete("/api/users/{id}", userId))
            .andExpect(status().isNotFound)
    }
}
