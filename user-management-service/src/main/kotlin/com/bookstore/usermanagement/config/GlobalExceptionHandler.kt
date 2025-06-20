package com.bookstore.usermanagement.config

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.server.ResponseStatusException

@ControllerAdvice
class GlobalExceptionHandler {
    @ExceptionHandler(ResponseStatusException::class)
    fun handleResponseStatusException(ex: ResponseStatusException): ResponseEntity<Map<String, Any?>> {
        val body = mapOf(
            "status" to ex.statusCode.value(),
            "error" to ex.statusCode.toString(), // Use toString() for a reliable error string
            "message" to (ex.reason ?: "Unexpected error occurred")
        )
        return ResponseEntity(body, ex.statusCode)
    }

    @ExceptionHandler(Exception::class)
    fun handleGenericException(ex: Exception): ResponseEntity<Map<String, Any?>> {
        val body = mapOf(
            "status" to HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "error" to HttpStatus.INTERNAL_SERVER_ERROR.reasonPhrase,
            "message" to (ex.message ?: "Unexpected error occurred")
        )
        return ResponseEntity(body, HttpStatus.INTERNAL_SERVER_ERROR)
    }
}

