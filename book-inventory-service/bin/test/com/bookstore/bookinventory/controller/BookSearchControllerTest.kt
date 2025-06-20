package com.bookstore.bookinventory.controller

import com.bookstore.bookinventory.dto.BookSearchRequestDTO
import com.bookstore.bookinventory.dto.BookResponseDTO
import com.bookstore.bookinventory.service.BookSearchService
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.data.domain.PageImpl
import org.springframework.http.ResponseEntity
import java.time.LocalDateTime

class BookSearchControllerTest {
    private val bookSearchService: BookSearchService = mockk()
    private lateinit var bookSearchController: BookSearchController

    @BeforeEach
    fun setUp() {
        bookSearchController = BookSearchController(bookSearchService)
    }

    @Test
    fun `searchBooks returns paged result`() {
        val request = BookSearchRequestDTO(page = 0, size = 10, sortBy = "title", sortDir = "asc")
        val response = PageImpl(listOf(BookResponseDTO(1L, "title", "author", "genre", "isbn", 10.0, 5, LocalDateTime.of(2024, 1, 1, 0, 0), LocalDateTime.of(2024, 1, 2, 0, 0), false)))
        every { bookSearchService.searchBooks(request) } returns response
        val result: ResponseEntity<*> = bookSearchController.searchBooks(request)
        assertEquals(response, result.body)
    }
}

