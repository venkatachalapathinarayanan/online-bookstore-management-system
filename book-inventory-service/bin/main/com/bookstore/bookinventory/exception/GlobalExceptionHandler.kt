package com.bookstore.bookinventory.exception

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.MethodArgumentNotValidException

@ControllerAdvice
class GlobalExceptionHandler {
    @ExceptionHandler(BookNotFoundException::class)
    fun handleBookNotFound(ex: BookNotFoundException): ResponseEntity<Any> =
        ResponseEntity(mapOf("error" to ex.message), HttpStatus.NOT_FOUND)

    @ExceptionHandler(InventoryNotFoundException::class)
    fun handleInventoryNotFound(ex: InventoryNotFoundException): ResponseEntity<Any> =
        ResponseEntity(mapOf("error" to ex.message), HttpStatus.NOT_FOUND)

    @ExceptionHandler(InsufficientStockException::class)
    fun handleInsufficientStock(ex: InsufficientStockException): ResponseEntity<Any> =
        ResponseEntity(mapOf("error" to ex.message), HttpStatus.BAD_REQUEST)

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidation(ex: MethodArgumentNotValidException): ResponseEntity<Any> =
        ResponseEntity(mapOf("error" to "Validation failed", "details" to ex.bindingResult.allErrors.map { it.defaultMessage }), HttpStatus.BAD_REQUEST)

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgument(ex: IllegalArgumentException): ResponseEntity<Any> =
        ResponseEntity(mapOf("error" to (ex.message ?: "Invalid argument")), HttpStatus.BAD_REQUEST)

    @ExceptionHandler(Exception::class)
    fun handleOther(ex: Exception): ResponseEntity<Any> =
        ResponseEntity(mapOf("error" to (ex.message ?: "Internal server error")), HttpStatus.INTERNAL_SERVER_ERROR)
}
