package com.bookstore.bookinventory.service

import com.bookstore.bookinventory.dto.BookResponseDTO
import com.bookstore.bookinventory.dto.BookSearchRequestDTO
import com.bookstore.bookinventory.model.Book
import com.bookstore.bookinventory.repository.BookInventoryRepository
import com.bookstore.bookinventory.repository.BookPriceRepository
import com.bookstore.bookinventory.repository.BookRepository
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.data.domain.Page
import java.math.BigDecimal
import java.time.LocalDateTime

class BookSearchServiceTest {
    private val bookRepository: BookRepository = mockk()
    private val bookPriceRepository: BookPriceRepository = mockk()
    private val bookInventoryRepository: BookInventoryRepository = mockk()
    private lateinit var bookSearchService: BookSearchService

    @BeforeEach
    fun setUp() {
        bookSearchService = BookSearchService(bookRepository, bookPriceRepository, bookInventoryRepository)
    }

    @Test
    fun `searchBooks returns correct price and quantity`() {
        val book = Book(
            id = 1L,
            title = "Test Book",
            author = "Author",
            genre = "Fiction",
            isbn = "1234567890",
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now(),
            isDeleted = false
        )
        every { bookRepository.findByIsDeletedFalse() } returns listOf(book)
        every { bookPriceRepository.findByBookId(1L)?.price } returns BigDecimal("99.99")
        every { bookInventoryRepository.findByBookId(1L)?.quantity } returns 10

        val request = BookSearchRequestDTO(page = 0, size = 10, sortBy = "title", sortDir = "asc")
        val result: Page<BookResponseDTO> = bookSearchService.searchBooks(request)
        assertEquals(1, result.totalElements)
        val dto = result.content.first()
        assertEquals(BigDecimal("99.99"), dto.price)
        assertEquals(10, dto.quantity)
    }
}

