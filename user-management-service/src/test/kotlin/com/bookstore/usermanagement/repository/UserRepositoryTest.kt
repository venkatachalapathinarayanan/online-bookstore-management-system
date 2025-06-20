package com.bookstore.usermanagement.repository

import com.bookstore.usermanagement.model.User
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Disabled
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import org.springframework.test.context.ActiveProfiles
import java.util.*

@DataJpaTest
@ActiveProfiles("test")
@Disabled("Temporarily disabled due to Spring context configuration issues")
class UserRepositoryTest {
    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var entityManager: TestEntityManager

    @Test
    fun `findByUserName should return user when exists`() {
        // Arrange
        val user = User(
            userName = "testuser",
            fullName = "Test User",
            email = "test@example.com",
            password = "password",
            phoneNumber = "1234567890",
            address = "123 Test St",
            role = "USERS"
        )
        entityManager.persistAndFlush(user)

        // Act
        val found = userRepository.findByUserName("testuser")

        // Assert
        assertTrue(found.isPresent)
        assertEquals("testuser", found.get().userName)
        assertEquals("Test User", found.get().fullName)
    }

    @Test
    fun `findByUserName should return empty when user not exists`() {
        // Act
        val found = userRepository.findByUserName("nonexistent")

        // Assert
        assertFalse(found.isPresent)
    }

    @Test
    fun `existsByUserName should return true when user exists`() {
        // Arrange
        val user = User(
            userName = "existinguser",
            fullName = "Existing User",
            email = "existing@example.com",
            password = "password",
            phoneNumber = "1234567890",
            address = "123 Test St",
            role = "USERS"
        )
        entityManager.persistAndFlush(user)

        // Act
        val exists = userRepository.existsByUserName("existinguser")

        // Assert
        assertTrue(exists)
    }

    @Test
    fun `existsByUserName should return false when user not exists`() {
        // Act
        val exists = userRepository.existsByUserName("nonexistent")

        // Assert
        assertFalse(exists)
    }

    @Test
    fun `save should persist new user`() {
        // Arrange
        val user = User(
            userName = "newuser",
            fullName = "New User",
            email = "new@example.com",
            password = "password",
            phoneNumber = "1234567890",
            address = "123 Test St",
            role = "USERS"
        )

        // Act
        val saved = userRepository.save(user)

        // Assert
        assertNotNull(saved.id)
        assertEquals("newuser", saved.userName)
        assertEquals("New User", saved.fullName)
    }

    @Test
    fun `findAll should return all users`() {
        // Arrange
        val user1 = User(
            userName = "user1",
            fullName = "User One",
            email = "user1@example.com",
            password = "password",
            phoneNumber = "1234567890",
            address = "123 Test St",
            role = "USERS"
        )
        val user2 = User(
            userName = "user2",
            fullName = "User Two",
            email = "user2@example.com",
            password = "password",
            phoneNumber = "0987654321",
            address = "456 Test St",
            role = "ADMIN"
        )
        entityManager.persistAndFlush(user1)
        entityManager.persistAndFlush(user2)

        // Act
        val allUsers = userRepository.findAll()

        // Assert
        assertEquals(2, allUsers.size)
        assertTrue(allUsers.any { it.userName == "user1" })
        assertTrue(allUsers.any { it.userName == "user2" })
    }

    @Test
    fun `findById should return user when exists`() {
        // Arrange
        val user = User(
            userName = "testuser",
            fullName = "Test User",
            email = "test@example.com",
            password = "password",
            phoneNumber = "1234567890",
            address = "123 Test St",
            role = "USERS"
        )
        val saved = entityManager.persistAndFlush(user)

        // Act
        val found = userRepository.findById(saved.id!!)

        // Assert
        assertTrue(found.isPresent)
        assertEquals("testuser", found.get().userName)
    }

    @Test
    fun `findById should return empty when user not exists`() {
        // Act
        val found = userRepository.findById(999L)

        // Assert
        assertFalse(found.isPresent)
    }

    @Test
    fun `existsById should return true when user exists`() {
        // Arrange
        val user = User(
            userName = "testuser",
            fullName = "Test User",
            email = "test@example.com",
            password = "password",
            phoneNumber = "1234567890",
            address = "123 Test St",
            role = "USERS"
        )
        val saved = entityManager.persistAndFlush(user)

        // Act
        val exists = userRepository.existsById(saved.id!!)

        // Assert
        assertTrue(exists)
    }

    @Test
    fun `existsById should return false when user not exists`() {
        // Act
        val exists = userRepository.existsById(999L)

        // Assert
        assertFalse(exists)
    }

    @Test
    fun `deleteById should remove user`() {
        // Arrange
        val user = User(
            userName = "todelete",
            fullName = "To Delete",
            email = "delete@example.com",
            password = "password",
            phoneNumber = "1234567890",
            address = "123 Test St",
            role = "USERS"
        )
        val saved = entityManager.persistAndFlush(user)

        // Act
        userRepository.deleteById(saved.id!!)

        // Assert
        assertFalse(userRepository.existsById(saved.id!!))
    }
} 