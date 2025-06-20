package com.bookstore.bookinventory.service

import com.bookstore.bookinventory.dto.InventoryUpdateRequestDTO
import com.bookstore.bookinventory.dto.InventoryDecreaseRequestDTO
import com.bookstore.bookinventory.dto.InventoryStatusDTO
import com.bookstore.bookinventory.repository.BookInventoryRepository
import com.bookstore.bookinventory.repository.BookRepository
import com.bookstore.bookinventory.repository.InventoryLogRepository
import com.bookstore.common.kafka.KafkaEventPublisher
import com.bookstore.bookinventory.model.Book
import com.bookstore.bookinventory.model.BookInventory
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class InventoryServiceTest {
    private val bookRepository: BookRepository = mockk(relaxed = true)
    private val bookInventoryRepository: BookInventoryRepository = mockk(relaxed = true)
    private val inventoryLogRepository: InventoryLogRepository = mockk(relaxed = true)
    private val kafkaEventPublisher: KafkaEventPublisher = mockk(relaxed = true)
    private lateinit var inventoryService: InventoryService

    @BeforeEach
    fun setUp() {
        inventoryService = InventoryService(bookRepository, bookInventoryRepository, inventoryLogRepository, kafkaEventPublisher)
    }

    @Test
    fun `updateInventory updates inventory`() {
        val request = InventoryUpdateRequestDTO(1L, 10)
        val now = java.time.LocalDateTime.now()
        val book = Book(1L, "Sample Book", "Author", "Genre", "ISBN", now, now, false)
        val inventory = BookInventory(1L, 1L, 5)
        every { bookRepository.findByIdAndIsDeletedFalse(1L) } returns book
        every { bookInventoryRepository.findByBookId(1L) } returns inventory
        every { bookInventoryRepository.save(any()) } returns inventory.copy(quantity = 10)
        every { inventoryLogRepository.save(any()) } returns mockk()
        inventoryService.updateInventory(request)
    }
}
