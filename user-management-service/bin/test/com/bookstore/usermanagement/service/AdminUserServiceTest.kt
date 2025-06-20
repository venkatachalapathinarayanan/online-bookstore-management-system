package com.bookstore.usermanagement.service

import com.bookstore.usermanagement.dto.UserRequestDTO
import com.bookstore.usermanagement.model.User
import com.bookstore.usermanagement.repository.UserRepository
import io.mockk.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.server.ResponseStatusException
import java.util.*

class AdminUserServiceTest {
    private val userRepository: UserRepository = mockk()
    private val passwordEncoder: PasswordEncoder = mockk()
    private lateinit var adminUserService: AdminUserService

    @BeforeEach
    fun setUp() {
        adminUserService = AdminUserService(userRepository, passwordEncoder)
        clearAllMocks()
    }

    @Test
    fun `addAdmin should create new admin user successfully`() {
        // Arrange
        val request = UserRequestDTO(
            userName = "admin1",
            fullName = "Admin User",
            email = "admin@example.com",
            password = "adminpass123",
            phoneNumber = "1234567890",
            address = "123 Admin St"
        )
        val encodedPassword = "encodedAdminPass123"
        val savedAdmin = User(
            id = 1L,
            userName = "admin1",
            fullName = "Admin User",
            email = "admin@example.com",
            password = encodedPassword,
            phoneNumber = "1234567890",
            address = "123 Admin St",
            role = "ADMIN"
        )

        every { userRepository.existsByUserName("admin1") } returns false
        every { passwordEncoder.encode("adminpass123") } returns encodedPassword
        every { userRepository.save(any()) } returns savedAdmin

        // Act
        val result = adminUserService.addAdmin(request)

        // Assert
        assertEquals(1L, result.id)
        assertEquals("admin1", result.userName)
        assertEquals("Admin User", result.fullName)
        assertEquals("ADMIN", result.role)

        verify { userRepository.existsByUserName("admin1") }
        verify { passwordEncoder.encode("adminpass123") }
        verify { userRepository.save(any()) }
    }

    @Test
    fun `addAdmin should throw exception when username already exists`() {
        // Arrange
        val request = UserRequestDTO(
            userName = "existingadmin",
            fullName = "Admin User",
            email = "admin@example.com",
            password = "adminpass123",
            phoneNumber = "1234567890",
            address = "123 Admin St"
        )

        every { userRepository.existsByUserName("existingadmin") } returns true

        // Act & Assert
        val exception = assertThrows<ResponseStatusException> {
            adminUserService.addAdmin(request)
        }
        assertTrue(exception.reason!!.contains("Username already exists"))

        verify { userRepository.existsByUserName("existingadmin") }
        verify(exactly = 0) { userRepository.save(any()) }
    }

    @Test
    fun `getAdmin should return admin user when found`() {
        // Arrange
        val adminId = 1L
        val adminUser = User(
            id = adminId,
            userName = "admin1",
            fullName = "Admin User",
            email = "admin@example.com",
            password = "password",
            phoneNumber = "1234567890",
            address = "123 Admin St",
            role = "ADMIN"
        )

        every { userRepository.findById(adminId) } returns Optional.of(adminUser)

        // Act
        val result = adminUserService.getAdmin(adminId)

        // Assert
        assertEquals(adminId, result.id)
        assertEquals("admin1", result.userName)
        assertEquals("ADMIN", result.role)

        verify { userRepository.findById(adminId) }
    }

    @Test
    fun `getAdmin should throw exception when admin not found`() {
        // Arrange
        val adminId = 999L
        every { userRepository.findById(adminId) } returns Optional.empty()

        // Act & Assert
        assertThrows<ResponseStatusException> {
            adminUserService.getAdmin(adminId)
        }

        verify { userRepository.findById(adminId) }
    }

    @Test
    fun `getAdmin should throw exception when user is not admin`() {
        // Arrange
        val userId = 1L
        val regularUser = User(
            id = userId,
            userName = "user1",
            fullName = "Regular User",
            email = "user@example.com",
            password = "password",
            phoneNumber = "1234567890",
            address = "123 User St",
            role = "USERS"
        )

        every { userRepository.findById(userId) } returns Optional.of(regularUser)

        // Act & Assert
        assertThrows<ResponseStatusException> {
            adminUserService.getAdmin(userId)
        }

        verify { userRepository.findById(userId) }
    }

    @Test
    fun `listAdmins should return only admin users`() {
        // Arrange
        val users = listOf(
            User(1L, "admin1", "Admin One", "admin1@test.com", "pass", "123", "addr1", "ADMIN"),
            User(2L, "user1", "User One", "user1@test.com", "pass", "456", "addr2", "USERS"),
            User(3L, "admin2", "Admin Two", "admin2@test.com", "pass", "789", "addr3", "ADMIN")
        )

        every { userRepository.findAll() } returns users

        // Act
        val result = adminUserService.listAdmins()

        // Assert
        assertEquals(2, result.size)
        assertEquals("admin1", result[0].userName)
        assertEquals("admin2", result[1].userName)
        assertTrue(result.all { it.role == "ADMIN" })

        verify { userRepository.findAll() }
    }

    @Test
    fun `updateAdmin should update existing admin successfully`() {
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
        val existingAdmin = User(
            id = adminId,
            userName = "oldadmin",
            fullName = "Old Admin",
            email = "old@example.com",
            password = "oldpassword",
            phoneNumber = "1234567890",
            address = "123 Old St",
            role = "ADMIN"
        )
        val encodedPassword = "encodedNewPassword"
        val updatedAdmin = existingAdmin.copy(
            userName = "updatedadmin",
            fullName = "Updated Admin",
            email = "updated@example.com",
            password = encodedPassword,
            phoneNumber = "9876543210",
            address = "456 Updated St"
        )

        every { userRepository.findById(adminId) } returns Optional.of(existingAdmin)
        every { userRepository.existsByUserName("updatedadmin") } returns false
        every { passwordEncoder.encode("newpassword") } returns encodedPassword
        every { userRepository.save(any()) } returns updatedAdmin

        // Act
        val result = adminUserService.updateAdmin(adminId, request)

        // Assert
        assertEquals(adminId, result.id)
        assertEquals("updatedadmin", result.userName)
        assertEquals("Updated Admin", result.fullName)

        verify { userRepository.findById(adminId) }
        verify { userRepository.existsByUserName("updatedadmin") }
        verify { passwordEncoder.encode("newpassword") }
        verify { userRepository.save(any()) }
    }

    @Test
    fun `updateAdmin should throw exception when admin not found`() {
        // Arrange
        val adminId = 999L
        val request = UserRequestDTO(
            userName = "updatedadmin",
            fullName = "Updated Admin",
            email = "updated@example.com",
            password = "newpassword",
            phoneNumber = "9876543210",
            address = "456 Updated St"
        )

        every { userRepository.findById(adminId) } returns Optional.empty()

        // Act & Assert
        assertThrows<ResponseStatusException> {
            adminUserService.updateAdmin(adminId, request)
        }

        verify { userRepository.findById(adminId) }
        verify(exactly = 0) { userRepository.save(any()) }
    }

    @Test
    fun `updateAdmin should throw exception when user is not admin`() {
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
        val regularUser = User(
            id = userId,
            userName = "user1",
            fullName = "Regular User",
            email = "user@example.com",
            password = "password",
            phoneNumber = "1234567890",
            address = "123 User St",
            role = "USERS"
        )

        every { userRepository.findById(userId) } returns Optional.of(regularUser)

        // Act & Assert
        assertThrows<ResponseStatusException> {
            adminUserService.updateAdmin(userId, request)
        }

        verify { userRepository.findById(userId) }
        verify(exactly = 0) { userRepository.save(any()) }
    }

    @Test
    fun `deleteAdmin should delete existing admin successfully`() {
        // Arrange
        val adminId = 1L
        val adminUser = User(
            id = adminId,
            userName = "admin1",
            fullName = "Admin User",
            email = "admin@example.com",
            password = "password",
            phoneNumber = "1234567890",
            address = "123 Admin St",
            role = "ADMIN"
        )

        every { userRepository.findById(adminId) } returns Optional.of(adminUser)
        every { userRepository.deleteById(adminId) } just Runs

        // Act
        adminUserService.deleteAdmin(adminId)

        // Assert
        verify { userRepository.findById(adminId) }
        verify { userRepository.deleteById(adminId) }
    }

    @Test
    fun `deleteAdmin should throw exception when admin not found`() {
        // Arrange
        val adminId = 999L
        every { userRepository.findById(adminId) } returns Optional.empty()

        // Act & Assert
        assertThrows<ResponseStatusException> {
            adminUserService.deleteAdmin(adminId)
        }

        verify { userRepository.findById(adminId) }
        verify(exactly = 0) { userRepository.deleteById(any()) }
    }

    @Test
    fun `deleteAdmin should throw exception when user is not admin`() {
        // Arrange
        val userId = 1L
        val regularUser = User(
            id = userId,
            userName = "user1",
            fullName = "Regular User",
            email = "user@example.com",
            password = "password",
            phoneNumber = "1234567890",
            address = "123 User St",
            role = "USERS"
        )

        every { userRepository.findById(userId) } returns Optional.of(regularUser)

        // Act & Assert
        assertThrows<ResponseStatusException> {
            adminUserService.deleteAdmin(userId)
        }

        verify { userRepository.findById(userId) }
        verify(exactly = 0) { userRepository.deleteById(any()) }
    }
}
