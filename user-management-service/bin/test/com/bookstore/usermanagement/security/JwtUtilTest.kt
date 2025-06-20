package com.bookstore.usermanagement.security

import io.jsonwebtoken.ExpiredJwtException
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.test.util.ReflectionTestUtils
import java.util.*

class JwtUtilTest {
    private lateinit var jwtUtil: JwtUtil
    private val secret = "testSecretKeyForJwtTokenGenerationAndValidation"
    private val expiration = 3600L // 1 hour

    @BeforeEach
    fun setUp() {
        jwtUtil = JwtUtil(secret, expiration)
    }

    @Test
    fun `generateToken should create valid JWT token`() {
        // Arrange
        val username = "testuser"
        val roles = listOf("USERS", "ADMIN")

        // Act
        val token = jwtUtil.generateToken(username, roles)

        // Assert
        assertNotNull(token)
        assertTrue(token.split(".").size == 3) // JWT has 3 parts
        assertEquals(username, jwtUtil.extractUsername(token))
        assertEquals(roles, jwtUtil.extractRoles(token))
    }

    @Test
    fun `extractUsername should return correct username from token`() {
        // Arrange
        val username = "testuser"
        val roles = listOf("USERS")
        val token = jwtUtil.generateToken(username, roles)

        // Act
        val extractedUsername = jwtUtil.extractUsername(token)

        // Assert
        assertEquals(username, extractedUsername)
    }

    @Test
    fun `extractRoles should return correct roles from token`() {
        // Arrange
        val username = "testuser"
        val roles = listOf("USERS", "ADMIN")
        val token = jwtUtil.generateToken(username, roles)

        // Act
        val extractedRoles = jwtUtil.extractRoles(token)

        // Assert
        assertEquals(roles, extractedRoles)
    }

    @Test
    fun `extractRoles should return empty list when no roles in token`() {
        // Arrange
        val username = "testuser"
        val roles = emptyList<String>()
        val token = jwtUtil.generateToken(username, roles)

        // Act
        val extractedRoles = jwtUtil.extractRoles(token)

        // Assert
        assertTrue(extractedRoles.isEmpty())
    }

    @Test
    fun `validateToken should return true for valid token`() {
        // Arrange
        val username = "testuser"
        val roles = listOf("USERS")
        val token = jwtUtil.generateToken(username, roles)

        // Act
        val isValid = jwtUtil.validateToken(token, username)

        // Assert
        assertTrue(isValid)
    }

    @Test
    fun `validateToken should return false for different username`() {
        // Arrange
        val username = "testuser"
        val roles = listOf("USERS")
        val token = jwtUtil.generateToken(username, roles)

        // Act
        val isValid = jwtUtil.validateToken(token, "differentuser")

        // Assert
        assertFalse(isValid)
    }

    @Test
    fun `validateToken should throw exception for expired token`() {
        // Arrange
        val username = "testuser"
        val roles = listOf("USERS")
        
        // Create JwtUtil with very short expiration (1 millisecond)
        val shortExpirationJwtUtil = JwtUtil(secret, 1L)
        val token = shortExpirationJwtUtil.generateToken(username, roles)

        // Wait to ensure token expires
        Thread.sleep(2000)

        // Act & Assert
        assertThrows<ExpiredJwtException> {
            shortExpirationJwtUtil.validateToken(token, username)
        }
    }

    @Test
    fun `extractUsername should throw exception for invalid token`() {
        // Arrange
        val invalidToken = "invalid.token.here"

        // Act & Assert
        assertThrows<Exception> {
            jwtUtil.extractUsername(invalidToken)
        }
    }

    @Test
    fun `extractRoles should throw exception for invalid token`() {
        // Arrange
        val invalidToken = "invalid.token.here"

        // Act & Assert
        assertThrows<Exception> {
            jwtUtil.extractRoles(invalidToken)
        }
    }

    @Test
    fun `validateToken should throw exception for invalid token`() {
        // Arrange
        val invalidToken = "invalid.token.here"

        // Act & Assert
        assertThrows<Exception> {
            jwtUtil.validateToken(invalidToken, "testuser")
        }
    }

    @Test
    fun `generateToken should create tokens with same content but different timestamps`() {
        // Arrange
        val username = "testuser"
        val roles = listOf("USERS")

        // Act
        val token1 = jwtUtil.generateToken(username, roles)
        Thread.sleep(1000) // Ensure different timestamp
        val token2 = jwtUtil.generateToken(username, roles)

        // Assert
        assertNotEquals(token1, token2) // Tokens should be different due to timestamp
        assertEquals(username, jwtUtil.extractUsername(token1))
        assertEquals(username, jwtUtil.extractUsername(token2))
        assertEquals(roles, jwtUtil.extractRoles(token1))
        assertEquals(roles, jwtUtil.extractRoles(token2))
    }
} 