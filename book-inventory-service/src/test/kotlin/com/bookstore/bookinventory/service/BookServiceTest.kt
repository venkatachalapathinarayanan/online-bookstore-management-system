package com.bookstore.bookinventory.service

import com.bookstore.bookinventory.dto.BookRequestDTO
import com.bookstore.bookinventory.dto.BookResponseDTO
import com.bookstore.bookinventory.model.*
import com.bookstore.bookinventory.repository.BookInventoryRepository
import com.bookstore.bookinventory.repository.BookPriceRepository
import com.bookstore.bookinventory.repository.BookRepository
import com.bookstore.bookinventory.repository.InventoryLogRepository
import io.mockk.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.LocalDateTime

class BookServiceTest {
    private val bookRepository: BookRepository = mockk(relaxed = true)
    private val bookPriceRepository: BookPriceRepository = mockk(relaxed = true)
    private val bookInventoryRepository: BookInventoryRepository = mockk(relaxed = true)
    private val inventoryLogRepository: InventoryLogRepository = mockk(relaxed = true)
    private lateinit var bookService: BookService

    @BeforeEach
    fun setUp() {
        clearAllMocks()
        bookService = BookService(bookRepository, bookPriceRepository, bookInventoryRepository, inventoryLogRepository)
    }

    @Test
    fun `createBook returns BookResponseDTO`() {
        val request = BookRequestDTO("title", "author", "genre", "isbn", BigDecimal("10.0"), 5)
        val book = Book(1L, "title", "author", "genre", "isbn", LocalDateTime.now(), LocalDateTime.now(), false)
        val bookPrice = BookPrice(1L, 1L, BigDecimal("10.0"))
        val bookInventory = BookInventory(1L, 1L, 5)
        
        val bookSlot = slot<Book>()
        val priceSlot = slot<BookPrice>()
        val inventorySlot = slot<BookInventory>()
        val logSlot = slot<InventoryLog>()
        
        every { bookRepository.existsByIsbnAndIsDeletedFalse(any()) } returns false
        every { bookRepository.save(capture(bookSlot)) } returns book
        every { bookPriceRepository.save(capture(priceSlot)) } returns bookPrice
        every { bookInventoryRepository.save(capture(inventorySlot)) } returns bookInventory
        every { inventoryLogRepository.save(capture(logSlot)) } returns mockk()
        every { bookPriceRepository.findByBookId(1L) } returns bookPrice
        every { bookInventoryRepository.findByBookId(1L) } returns bookInventory

        val result = bookService.createBook(request)
        
        assertEquals("title", result.title)
        assertEquals("author", result.author)
        assertEquals(BigDecimal("10.0"), result.price)
        assertEquals(5, result.quantity)
        
        assertEquals(request.title, bookSlot.captured.title)
        assertEquals(request.author, bookSlot.captured.author)
        assertEquals(request.genre, bookSlot.captured.genre)
        assertEquals(request.isbn, bookSlot.captured.isbn)
        
        assertEquals(book.id, priceSlot.captured.bookId)
        assertEquals(request.price, priceSlot.captured.price)
        
        assertEquals(book.id, inventorySlot.captured.bookId)
        assertEquals(request.quantity, inventorySlot.captured.quantity)
        
        assertEquals(book.id, logSlot.captured.bookId)
        assertEquals(InventoryAction.CREATE, logSlot.captured.action)
        assertEquals(request.quantity, logSlot.captured.quantity)
    }

    @Test
    fun `getBook returns BookResponseDTO`() {
        val book = Book(1L, "title", "author", "genre", "isbn", LocalDateTime.now(), LocalDateTime.now(), false)
        val bookPrice = BookPrice(1L, 1L, BigDecimal("10.0"))
        val bookInventory = BookInventory(1L, 1L, 5)
        
        every { bookRepository.findByIdAndIsDeletedFalse(1L) } returns book
        every { bookPriceRepository.findByBookId(1L) } returns bookPrice
        every { bookInventoryRepository.findByBookId(1L) } returns bookInventory

        val result = bookService.getBook(1L)
        assertEquals(1L, result.id)
        assertEquals(BigDecimal("10.0"), result.price)
        assertEquals(5, result.quantity)
    }

    @Test
    fun `listBooks returns list of BookResponseDTO`() {
        val book = Book(1L, "title", "author", "genre", "isbn", LocalDateTime.now(), LocalDateTime.now(), false)
        val bookPrice = BookPrice(1L, 1L, BigDecimal("10.0"))
        val bookInventory = BookInventory(1L, 1L, 5)
        
        every { bookRepository.findByIsDeletedFalse() } returns listOf(book)
        every { bookPriceRepository.findByBookId(1L) } returns bookPrice
        every { bookInventoryRepository.findByBookId(1L) } returns bookInventory

        val result = bookService.listBooks()
        assertEquals(1, result.size)
        assertEquals(BigDecimal("10.0"), result[0].price)
        assertEquals(5, result[0].quantity)
    }

    @Test
    fun `updateBook returns updated BookResponseDTO`() {
        val request = BookRequestDTO("title", "author", "genre", "isbn", BigDecimal("10.0"), 5)
        val book = Book(1L, "title", "author", "genre", "isbn", LocalDateTime.now(), LocalDateTime.now(), false)
        val bookPrice = BookPrice(1L, 1L, BigDecimal("10.0"))
        val bookInventory = BookInventory(1L, 1L, 5)
        
        val bookSlot = slot<Book>()
        val priceSlot = slot<BookPrice>()
        val logSlot = slot<InventoryLog>()
        
        every { bookRepository.findByIdAndIsDeletedFalse(1L) } returns book
        every { bookRepository.save(capture(bookSlot)) } returns book
        every { bookPriceRepository.findByBookId(1L) } returns bookPrice
        every { bookPriceRepository.save(capture(priceSlot)) } returns bookPrice
        every { bookInventoryRepository.findByBookId(1L) } returns bookInventory
        every { inventoryLogRepository.save(capture(logSlot)) } returns mockk()

        val result = bookService.updateBook(1L, request)
        
        assertEquals("title", result.title)
        assertEquals(BigDecimal("10.0"), result.price)
        assertEquals(5, result.quantity)
        
        assertEquals(book.id, bookSlot.captured.id)
        assertEquals(request.title, bookSlot.captured.title)
        assertEquals(request.author, bookSlot.captured.author)
        assertEquals(request.genre, bookSlot.captured.genre)
        assertEquals(request.isbn, bookSlot.captured.isbn)
        
        assertEquals(book.id, priceSlot.captured.bookId)
        assertEquals(request.price, priceSlot.captured.price)
        
        assertEquals(book.id, logSlot.captured.bookId)
        assertEquals(InventoryAction.UPDATE, logSlot.captured.action)
    }

    @Test
    fun `softDeleteBook marks book as deleted`() {
        val book = Book(1L, "title", "author", "genre", "isbn", LocalDateTime.now(), LocalDateTime.now(), false)
        val updatedBook = book.copy(isDeleted = true)
        
        val bookSlot = slot<Book>()
        val logSlot = slot<InventoryLog>()
        
        every { bookRepository.findByIdAndIsDeletedFalse(1L) } returns book
        every { bookRepository.save(capture(bookSlot)) } returns updatedBook
        every { inventoryLogRepository.save(capture(logSlot)) } returns mockk()

        bookService.softDeleteBook(1L)
        
        assertEquals(book.id, bookSlot.captured.id)
        assertEquals(true, bookSlot.captured.isDeleted)
        
        assertEquals(book.id, logSlot.captured.bookId)
        assertEquals(InventoryAction.SOFT_DELETE, logSlot.captured.action)
        assertEquals(0, logSlot.captured.quantity)
    }
}

