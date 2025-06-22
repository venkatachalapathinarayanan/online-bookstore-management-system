package com.bookstore.bookinventory.controller

import com.bookstore.bookinventory.dto.BookRequestDTO
import com.bookstore.bookinventory.dto.BookResponseDTO
import com.bookstore.bookinventory.service.BookService
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import java.math.BigDecimal
import java.time.LocalDateTime

class BookControllerTest {
    private val bookService: BookService = mockk()
    private lateinit var bookController: BookController

    @BeforeEach
    fun setUp() {
        bookController = BookController(bookService)
    }

    @Test
    fun `createBook returns created response`() {
        val request = BookRequestDTO("title", "author", "genre", "isbn", BigDecimal("10.0"), 5)
        val response = BookResponseDTO(
            1L,
            "title",
            "author",
            "genre",
            "isbn",
            BigDecimal("10.0"),
            5,
            LocalDateTime.of(2024, 1, 1, 0, 0),
            LocalDateTime.of(2024, 1, 2, 0, 0),
            false
        )
        every { bookService.createBook(request) } returns response
        val result = bookController.createBook(request)
        assertEquals(HttpStatus.CREATED, result.statusCode)
        assertEquals(response, result.body)
    }

    @Test
    fun `getBook returns book`() {
        val response = BookResponseDTO(
            1L,
            "title",
            "author",
            "genre",
            "isbn",
            BigDecimal("10.0"),
            5,
            LocalDateTime.of(2024, 1, 1, 0, 0),
            LocalDateTime.of(2024, 1, 2, 0, 0),
            false
        )
        every { bookService.getBook(1L) } returns response
        val result = bookController.getBook(1L)
        assertEquals(response, result.body)
    }

    @Test
    fun `listBooks returns list`() {
        val response = listOf(
            BookResponseDTO(
                1L,
                "title",
                "author",
                "genre",
                "isbn",
                BigDecimal("10.0"),
                5,
                LocalDateTime.of(2024, 1, 1, 0, 0),
                LocalDateTime.of(2024, 1, 2, 0, 0),
                false
            )
        )
        every { bookService.listBooks() } returns response
        val result = bookController.listBooks()
        assertEquals(response, result.body)
    }

    @Test
    fun `updateBook returns updated book`() {
        val request = BookRequestDTO("title", "author", "genre", "isbn", BigDecimal("10.0"), 5)
        val response = BookResponseDTO(
            1L,
            "title",
            "author",
            "genre",
            "isbn",
            BigDecimal("10.0"),
            5,
            LocalDateTime.of(2024, 1, 1, 0, 0),
            LocalDateTime.of(2024, 1, 2, 0, 0),
            false
        )
        every { bookService.updateBook(1L, request) } returns response
        val result = bookController.updateBook(1L, request)
        assertEquals(response, result.body)
    }

    @Test
    fun `softDeleteBook returns no content`() {
        every { bookService.softDeleteBook(1L) } returns Unit
        val result = bookController.softDeleteBook(1L)
        assertEquals(HttpStatus.NO_CONTENT, result.statusCode)
    }
}

