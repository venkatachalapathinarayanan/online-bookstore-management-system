package com.bookstore.usermanagement.service

import com.bookstore.common.event.EventMessage
import com.bookstore.common.kafka.KafkaEventPublisher
import com.bookstore.usermanagement.dto.UserRequestDTO
import com.bookstore.usermanagement.dto.UserResponseDTO
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

class UserServiceTest {
    private val userRepository: UserRepository = mockk()
    private val passwordEncoder: PasswordEncoder = mockk()
    private val kafkaEventPublisher: KafkaEventPublisher = mockk()
    private lateinit var userService: UserService

    @BeforeEach
    fun setUp() {
        userService = UserService(userRepository, passwordEncoder, kafkaEventPublisher)
        clearAllMocks()
    }

    @Test
    fun `addUser should create new user successfully`() {
        // Arrange
        val request = UserRequestDTO(
            userName = "testuser",
            fullName = "Test User",
            email = "test@example.com",
            password = "password123",
            phoneNumber = "1234567890",
            address = "123 Test St"
        )
        val encodedPassword = "encodedPassword123"
        val savedUser = User(
            id = 1L,
            userName = "testuser",
            fullName = "Test User",
            email = "test@example.com",
            password = encodedPassword,
            phoneNumber = "1234567890",
            address = "123 Test St",
            role = "USERS"
        )

        every { userRepository.existsByUserName("testuser") } returns false
        every { passwordEncoder.encode("password123") } returns encodedPassword
        every { userRepository.save(any()) } returns savedUser
        every { kafkaEventPublisher.publish(any(), any()) } just Runs

        // Act
        val result = userService.addUser(request)

        // Assert
        assertEquals(1L, result.id)
        assertEquals("testuser", result.userName)
        assertEquals("Test User", result.fullName)
        assertEquals("test@example.com", result.email)
        assertEquals("USERS", result.role)

        verify { userRepository.existsByUserName("testuser") }
        verify { passwordEncoder.encode("password123") }
        verify { userRepository.save(any()) }
        verify { kafkaEventPublisher.publish("user-events", any<EventMessage>()) }
    }

    @Test
    fun `addUser should throw exception when username already exists`() {
        // Arrange
        val request = UserRequestDTO(
            userName = "existinguser",
            fullName = "Test User",
            email = "test@example.com",
            password = "password123",
            phoneNumber = "1234567890",
            address = "123 Test St"
        )

        every { userRepository.existsByUserName("existinguser") } returns true

        // Act & Assert
        val exception = assertThrows<IllegalArgumentException> {
            userService.addUser(request)
        }
        assertEquals("Username already exists", exception.message)

        verify { userRepository.existsByUserName("existinguser") }
        verify(exactly = 0) { userRepository.save(any()) }
        verify(exactly = 0) { kafkaEventPublisher.publish(any(), any()) }
    }

    @Test
    fun `getUser should return user when found`() {
        // Arrange
        val userId = 1L
        val user = User(
            id = userId,
            userName = "testuser",
            fullName = "Test User",
            email = "test@example.com",
            password = "password",
            phoneNumber = "1234567890",
            address = "123 Test St",
            role = "USERS"
        )

        every { userRepository.findById(userId) } returns Optional.of(user)

        // Act
        val result = userService.getUser(userId)

        // Assert
        assertEquals(userId, result.id)
        assertEquals("testuser", result.userName)
        assertEquals("Test User", result.fullName)

        verify { userRepository.findById(userId) }
    }

    @Test
    fun `getUser should throw exception when user not found`() {
        // Arrange
        val userId = 999L
        every { userRepository.findById(userId) } returns Optional.empty()

        // Act & Assert
        assertThrows<ResponseStatusException> {
            userService.getUser(userId)
        }

        verify { userRepository.findById(userId) }
    }

    @Test
    fun `listUsers should return all users`() {
        // Arrange
        val users = listOf(
            User(1L, "user1", "User One", "user1@test.com", "pass", "123", "addr1", "USERS"),
            User(2L, "user2", "User Two", "user2@test.com", "pass", "456", "addr2", "USERS")
        )

        every { userRepository.findAll() } returns users

        // Act
        val result = userService.listUsers()

        // Assert
        assertEquals(2, result.size)
        assertEquals("user1", result[0].userName)
        assertEquals("user2", result[1].userName)

        verify { userRepository.findAll() }
    }

    @Test
    fun `updateUser should update existing user successfully`() {
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
        val existingUser = User(
            id = userId,
            userName = "olduser",
            fullName = "Old User",
            email = "old@example.com",
            password = "oldpassword",
            phoneNumber = "1234567890",
            address = "123 Old St",
            role = "USERS"
        )
        val encodedPassword = "encodedNewPassword"
        val updatedUser = existingUser.copy(
            userName = "updateduser",
            fullName = "Updated User",
            email = "updated@example.com",
            password = encodedPassword,
            phoneNumber = "9876543210",
            address = "456 Updated St"
        )

        every { userRepository.findById(userId) } returns Optional.of(existingUser)
        every { userRepository.existsByUserName("updateduser") } returns false
        every { passwordEncoder.encode("newpassword") } returns encodedPassword
        every { userRepository.save(any()) } returns updatedUser

        // Act
        val result = userService.updateUser(userId, request)

        // Assert
        assertEquals(userId, result.id)
        assertEquals("updateduser", result.userName)
        assertEquals("Updated User", result.fullName)

        verify { userRepository.findById(userId) }
        verify { userRepository.existsByUserName("updateduser") }
        verify { passwordEncoder.encode("newpassword") }
        verify { userRepository.save(any()) }
    }

    @Test
    fun `updateUser should throw exception when user not found`() {
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

        every { userRepository.findById(userId) } returns Optional.empty()

        // Act & Assert
        assertThrows<ResponseStatusException> {
            userService.updateUser(userId, request)
        }

        verify { userRepository.findById(userId) }
        verify(exactly = 0) { userRepository.save(any()) }
    }

    @Test
    fun `updateUser should throw exception when new username already exists`() {
        // Arrange
        val userId = 1L
        val request = UserRequestDTO(
            userName = "existinguser",
            fullName = "Updated User",
            email = "updated@example.com",
            password = "newpassword",
            phoneNumber = "9876543210",
            address = "456 Updated St"
        )
        val existingUser = User(
            id = userId,
            userName = "olduser",
            fullName = "Old User",
            email = "old@example.com",
            password = "oldpassword",
            phoneNumber = "1234567890",
            address = "123 Old St",
            role = "USERS"
        )

        every { userRepository.findById(userId) } returns Optional.of(existingUser)
        every { userRepository.existsByUserName("existinguser") } returns true

        // Act & Assert
        assertThrows<ResponseStatusException> {
            userService.updateUser(userId, request)
        }

        verify { userRepository.findById(userId) }
        verify { userRepository.existsByUserName("existinguser") }
        verify(exactly = 0) { userRepository.save(any()) }
    }

    @Test
    fun `deleteUser should delete existing user successfully`() {
        // Arrange
        val userId = 1L
        every { userRepository.existsById(userId) } returns true
        every { userRepository.deleteById(userId) } just Runs

        // Act
        userService.deleteUser(userId)

        // Assert
        verify { userRepository.existsById(userId) }
        verify { userRepository.deleteById(userId) }
    }

    @Test
    fun `deleteUser should throw exception when user not found`() {
        // Arrange
        val userId = 999L
        every { userRepository.existsById(userId) } returns false

        // Act & Assert
        assertThrows<ResponseStatusException> {
            userService.deleteUser(userId)
        }

        verify { userRepository.existsById(userId) }
        verify(exactly = 0) { userRepository.deleteById(any()) }
    }

    @Test
    fun `getAllUsers should return list of usernames`() {
        // Arrange
        val users = listOf(
            User(1L, "user1", "User One", "user1@test.com", "pass", "123", "addr1", "USERS"),
            User(2L, "user2", "User Two", "user2@test.com", "pass", "456", "addr2", "USERS")
        )

        every { userRepository.findAll() } returns users

        // Act
        val result = userService.getAllUsers()

        // Assert
        assertEquals(listOf("user1", "user2"), result)
        verify { userRepository.findAll() }
    }
}

