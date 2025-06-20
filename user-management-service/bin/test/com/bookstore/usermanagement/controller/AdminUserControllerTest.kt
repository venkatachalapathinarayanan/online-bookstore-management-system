package com.bookstore.usermanagement.controller

import com.bookstore.usermanagement.dto.UserRequestDTO
import com.bookstore.usermanagement.dto.UserResponseDTO
import com.bookstore.usermanagement.service.AdminUserService
import com.fasterxml.jackson.databind.ObjectMapper
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.server.ResponseStatusException

class AdminUserControllerTest {
    private lateinit var mockMvc: MockMvc
    private val adminUserService: AdminUserService = mockk()
    private val objectMapper = ObjectMapper()

    private lateinit var adminUserController: AdminUserController

    @BeforeEach
    fun setUp() {
        adminUserController = AdminUserController(adminUserService)
        mockMvc = MockMvcBuilders.standaloneSetup(adminUserController).build()
    }

    @Test
    fun `addAdmin should return 201 and admin response when admin created successfully`() {
        // Arrange
        val request = UserRequestDTO(
            userName = "admin1",
            fullName = "Admin User",
            email = "admin@example.com",
            password = "adminpass123",
            phoneNumber = "1234567890",
            address = "123 Admin St"
        )
        val response = UserResponseDTO(
            id = 1L,
            userName = "admin1",
            fullName = "Admin User",
            email = "admin@example.com",
            phoneNumber = "1234567890",
            address = "123 Admin St",
            role = "ADMIN"
        )

        every { adminUserService.addAdmin(request) } returns response

        // Act & Assert
        mockMvc.perform(
            post("/api/admins")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.userName").value("admin1"))
            .andExpect(jsonPath("$.role").value("ADMIN"))
    }

    @Test
    fun `addAdmin should return 409 when username already exists`() {
        // Arrange
        val request = UserRequestDTO(
            userName = "existingadmin",
            fullName = "Admin User",
            email = "admin@example.com",
            password = "adminpass123",
            phoneNumber = "1234567890",
            address = "123 Admin St"
        )

        every { adminUserService.addAdmin(request) } throws ResponseStatusException(HttpStatus.CONFLICT, "Username already exists")

        // Act & Assert
        mockMvc.perform(
            post("/api/admins")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isConflict)
    }

    @Test
    fun `getAdmin should return admin when found`() {
        // Arrange
        val adminId = 1L
        val response = UserResponseDTO(
            id = adminId,
            userName = "admin1",
            fullName = "Admin User",
            email = "admin@example.com",
            phoneNumber = "1234567890",
            address = "123 Admin St",
            role = "ADMIN"
        )

        every { adminUserService.getAdmin(adminId) } returns response

        // Act & Assert
        mockMvc.perform(get("/api/admins/{id}", adminId))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(adminId))
            .andExpect(jsonPath("$.userName").value("admin1"))
            .andExpect(jsonPath("$.role").value("ADMIN"))
    }

    @Test
    fun `getAdmin should return 404 when admin not found`() {
        // Arrange
        val adminId = 999L
        every { adminUserService.getAdmin(adminId) } throws ResponseStatusException(HttpStatus.NOT_FOUND, "Admin user not found")

        // Act & Assert
        mockMvc.perform(get("/api/admins/{id}", adminId))
            .andExpect(status().isNotFound)
    }

    @Test
    fun `listAdmins should return list of admin users`() {
        // Arrange
        val admins = listOf(
            UserResponseDTO(1L, "admin1", "Admin One", "admin1@test.com", "123", "addr1", "ADMIN"),
            UserResponseDTO(2L, "admin2", "Admin Two", "admin2@test.com", "456", "addr2", "ADMIN")
        )

        every { adminUserService.listAdmins() } returns admins

        // Act & Assert
        mockMvc.perform(get("/api/admins"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].userName").value("admin1"))
            .andExpect(jsonPath("$[0].role").value("ADMIN"))
            .andExpect(jsonPath("$[1].userName").value("admin2"))
            .andExpect(jsonPath("$[1].role").value("ADMIN"))
    }

    @Test
    fun `updateAdmin should return updated admin when successful`() {
        // Arrange
        val adminId = 1L
        val request = UserRequestDTO(
            userName = "updatedadmin",
            fullName = "Updated Admin",
            email = "updated@example.com",
            password = "newpassword",
            phoneNumber = "9876543210",
            address = "456 Updated St"
        )
        val response = UserResponseDTO(
            id = adminId,
            userName = "updatedadmin",
            fullName = "Updated Admin",
            email = "updated@example.com",
            phoneNumber = "9876543210",
            address = "456 Updated St",
            role = "ADMIN"
        )

        every { adminUserService.updateAdmin(adminId, request) } returns response

        // Act & Assert
        mockMvc.perform(
            put("/api/admins/{id}", adminId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(adminId))
            .andExpect(jsonPath("$.userName").value("updatedadmin"))
            .andExpect(jsonPath("$.role").value("ADMIN"))
    }

    @Test
    fun `deleteAdmin should return 204 when admin deleted successfully`() {
        // Arrange
        val adminId = 1L
        every { adminUserService.deleteAdmin(adminId) } just runs

        // Act & Assert
        mockMvc.perform(delete("/api/admins/{id}", adminId))
            .andExpect(status().isNoContent)
    }

    @Test
    fun `deleteAdmin should return 404 when admin not found`() {
        // Arrange
        val adminId = 999L
        every { adminUserService.deleteAdmin(adminId) } throws ResponseStatusException(HttpStatus.NOT_FOUND, "Admin user not found")

        // Act & Assert
        mockMvc.perform(delete("/api/admins/{id}", adminId))
            .andExpect(status().isNotFound)
    }
}
