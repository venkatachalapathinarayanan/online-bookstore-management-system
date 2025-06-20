package com.bookstore.usermanagement.exception

import com.bookstore.usermanagement.config.GlobalExceptionHandler
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull

class GlobalExceptionHandlerTest {
    private lateinit var globalExceptionHandler: GlobalExceptionHandler

    @BeforeEach
    fun setUp() {
        globalExceptionHandler = GlobalExceptionHandler()
    }

    @Test
    fun `handleResponseStatusException should return correct status and message`() {
        // Arrange
        val exception = ResponseStatusException(HttpStatus.NOT_FOUND, "User not found")

        // Act
        val response = globalExceptionHandler.handleResponseStatusException(exception)

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        assertNotNull(response.body)
        assertEquals("User not found", response.body!!["message"])
        assertEquals(404, response.body!!["status"])
    }

    @Test
    fun `handleResponseStatusException should handle conflict status`() {
        // Arrange
        val exception = ResponseStatusException(HttpStatus.CONFLICT, "Username already exists")

        // Act
        val response = globalExceptionHandler.handleResponseStatusException(exception)

        // Assert
        assertEquals(HttpStatus.CONFLICT, response.statusCode)
        assertNotNull(response.body)
        assertEquals("Username already exists", response.body!!["message"])
        assertEquals(409, response.body!!["status"])
    }

    @Test
    fun `handleGenericException should return 500 status`() {
        // Arrange
        val exception = RuntimeException("Unexpected error")

        // Act
        val response = globalExceptionHandler.handleGenericException(exception)

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.statusCode)
        assertNotNull(response.body)
        assertEquals("Unexpected error", response.body!!["message"])
        assertEquals(500, response.body!!["status"])
    }

    @Test
    fun `handleGenericException should handle null message`() {
        // Arrange
        val exception = RuntimeException(null as String?)

        // Act
        val response = globalExceptionHandler.handleGenericException(exception)

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.statusCode)
        assertNotNull(response.body)
        assertEquals("Unexpected error occurred", response.body!!["message"])
        assertEquals(500, response.body!!["status"])
    }
} 