package com.bookstore.bookinventory.service

import com.bookstore.bookinventory.dto.InventoryUpdateRequestDTO
import com.bookstore.bookinventory.dto.InventoryDecreaseRequestDTO
import com.bookstore.bookinventory.dto.InventoryStatusDTO
import com.bookstore.bookinventory.exception.BookNotFoundException
import com.bookstore.bookinventory.exception.InventoryNotFoundException
import com.bookstore.bookinventory.exception.InsufficientStockException
import com.bookstore.bookinventory.model.InventoryAction
import com.bookstore.bookinventory.model.InventoryLog
import com.bookstore.bookinventory.repository.BookInventoryRepository
import com.bookstore.bookinventory.repository.BookRepository
import com.bookstore.bookinventory.repository.InventoryLogRepository
import com.bookstore.common.kafka.KafkaEventPublisher
import jakarta.transaction.Transactional
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class InventoryService(
    private val bookRepository: BookRepository,
    private val bookInventoryRepository: BookInventoryRepository,
    private val inventoryLogRepository: InventoryLogRepository,
    private val kafkaEventPublisher: KafkaEventPublisher // Inject publisher
) {
    private val logger = LoggerFactory.getLogger(InventoryService::class.java)

    @Transactional
    fun updateInventory(request: InventoryUpdateRequestDTO) {
        val bookId = request.bookId ?: throw IllegalArgumentException("Book ID is required")
        val quantity = request.quantity ?: throw IllegalArgumentException("Quantity is required")
        val book = bookRepository.findByIdAndIsDeletedFalse(bookId)
            ?: throw BookNotFoundException()
        val inventory = bookInventoryRepository.findByBookId(bookId)
        if (inventory != null) {
            bookInventoryRepository.save(inventory.copy(quantity = quantity))
        } else {
            bookInventoryRepository.save(com.bookstore.bookinventory.model.BookInventory(bookId = bookId, quantity = quantity))
        }
        inventoryLogRepository.save(
            InventoryLog(
                bookId = bookId,
                action = InventoryAction.UPDATE,
                quantity = quantity,
                timestamp = LocalDateTime.now()
            )
        )
        logger.info("Inventory updated for bookId {}: new quantity {}", bookId, quantity)
        // Publish inventory updated event
        val event = com.bookstore.common.event.EventMessage(
            eventType = "InventoryUpdated",
            payload = mapOf(
                "bookId" to bookId,
                "quantity" to quantity
            )
        )
        kafkaEventPublisher.publish("inventory-events", event)
    }

    @Transactional
    fun decreaseInventory(request: InventoryDecreaseRequestDTO) {
        val bookId = request.bookId ?: throw IllegalArgumentException("Book ID is required")
        val decreaseBy = request.decreaseBy ?: throw IllegalArgumentException("Decrease amount is required")
        val book = bookRepository.findByIdAndIsDeletedFalse(bookId)
            ?: throw BookNotFoundException()
        val inventory = bookInventoryRepository.findByBookId(bookId)
            ?: throw InventoryNotFoundException()
        if (inventory.quantity < decreaseBy) {
            throw InsufficientStockException()
        }
        val newQuantity = inventory.quantity - decreaseBy
        bookInventoryRepository.save(inventory.copy(quantity = newQuantity))
        inventoryLogRepository.save(
            InventoryLog(
                bookId = bookId,
                action = InventoryAction.UPDATE,
                quantity = newQuantity,
                timestamp = LocalDateTime.now()
            )
        )
        logger.info("Inventory decreased for bookId {}: decreased by {}, new quantity {}", bookId, decreaseBy, newQuantity)
    }

    fun getInventoryStatus(bookId: Long): InventoryStatusDTO {
        val book = bookRepository.findByIdAndIsDeletedFalse(bookId)
            ?: throw BookNotFoundException()
        val inventory = bookInventoryRepository.findByBookId(bookId)
        return InventoryStatusDTO(
            bookId = bookId,
            title = book.title,
            quantity = inventory?.quantity ?: 0
        )
    }

    fun filterBooksByStock(minStock: Int): List<InventoryStatusDTO> {
        val books = bookRepository.findByIsDeletedFalse()
        return books.mapNotNull { book ->
            val inventory = bookInventoryRepository.findByBookId(book.id)
            if (inventory != null && inventory.quantity >= minStock) {
                InventoryStatusDTO(bookId = book.id, title = book.title, quantity = inventory.quantity)
            } else null
        }
    }

    fun listLowOrOutOfStockBooks(threshold: Int = 5): List<InventoryStatusDTO> {
        val books = bookRepository.findByIsDeletedFalse()
        return books.mapNotNull { book ->
            val inventory = bookInventoryRepository.findByBookId(book.id)
            if (inventory != null && inventory.quantity <= threshold) {
                InventoryStatusDTO(bookId = book.id, title = book.title, quantity = inventory.quantity)
            } else if (inventory == null) {
                InventoryStatusDTO(bookId = book.id, title = book.title, quantity = 0)
            } else null
        }
    }
}

