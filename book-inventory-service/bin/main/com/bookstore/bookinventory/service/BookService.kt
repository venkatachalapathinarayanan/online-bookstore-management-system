package com.bookstore.bookinventory.service

import com.bookstore.bookinventory.dto.BookRequestDTO
import com.bookstore.bookinventory.dto.BookResponseDTO
import com.bookstore.bookinventory.model.*
import com.bookstore.bookinventory.repository.*
import com.bookstore.bookinventory.exception.BookNotFoundException
import com.bookstore.bookinventory.exception.BookAlreadyExistsException
import jakarta.transaction.Transactional
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.security.access.prepost.PreAuthorize
import java.time.LocalDateTime

@Service
class BookService(
    private val bookRepository: BookRepository,
    private val bookPriceRepository: BookPriceRepository,
    private val bookInventoryRepository: BookInventoryRepository,
    private val inventoryLogRepository: InventoryLogRepository
) {
    private val logger = LoggerFactory.getLogger(BookService::class.java)

    @PreAuthorize("hasAnyAuthority('ADMIN', 'SUPERADMIN')")
    @Transactional
    fun createBook(request: BookRequestDTO): BookResponseDTO {
        logger.info("Creating book: {}", request.title)
        if (bookRepository.existsByIsbnAndIsDeletedFalse(request.isbn!!)) {
            logger.warn("Book with ISBN already exists: {}", request.isbn)
            throw BookAlreadyExistsException()
        }
        val now = LocalDateTime.now()
        val book = bookRepository.save(
            Book(
                title = request.title!!,
                author = request.author!!,
                genre = request.genre!!,
                isbn = request.isbn,
                createdAt = now,
                updatedAt = now,
                isDeleted = false
            )
        )
        bookPriceRepository.save(BookPrice(bookId = book.id, price = request.price!!))
        bookInventoryRepository.save(BookInventory(bookId = book.id, quantity = request.quantity!!))
        inventoryLogRepository.save(
            InventoryLog(
                bookId = book.id,
                action = InventoryAction.CREATE,
                quantity = request.quantity,
                timestamp = now
            )
        )
        logger.info("Book created successfully: {}", book.id)
        return toResponseDTO(book)
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'SUPERADMIN')")
    fun getBook(id: Long): BookResponseDTO {
        logger.info("Fetching book with id: {}", id)
        val book = bookRepository.findByIdAndIsDeletedFalse(id)
            ?: throw BookNotFoundException()
        return toResponseDTO(book)
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'SUPERADMIN')")
    fun listBooks(): List<BookResponseDTO> {
        logger.info("Listing all books")
        return bookRepository.findByIsDeletedFalse().map { toResponseDTO(it) }
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'SUPERADMIN')")
    @Transactional
    fun updateBook(id: Long, request: BookRequestDTO): BookResponseDTO {
        logger.info("Updating book with id: {}", id)
        val book = bookRepository.findByIdAndIsDeletedFalse(id)
            ?: throw BookNotFoundException()
        val now = LocalDateTime.now()
        val updatedBook = bookRepository.save(
            book.copy(
                title = request.title!!,
                author = request.author!!,
                genre = request.genre!!,
                isbn = request.isbn!!,
                updatedAt = now
            )
        )
        bookPriceRepository.findByBookId(id)?.let {
            bookPriceRepository.save(it.copy(price = request.price!!))
        } ?: bookPriceRepository.save(BookPrice(bookId = id, price = request.price!!))
        inventoryLogRepository.save(
            InventoryLog(
                bookId = id,
                action = InventoryAction.UPDATE,
                quantity = bookInventoryRepository.findByBookId(id)?.quantity ?: 0, // Log current quantity for audit
                timestamp = now
            )
        )
        logger.info("Book updated successfully: {}", id)
        return toResponseDTO(updatedBook)
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'SUPERADMIN')")
    @Transactional
    fun softDeleteBook(id: Long) {
        logger.info("Soft deleting book with id: {}", id)
        val book = bookRepository.findByIdAndIsDeletedFalse(id)
            ?: throw BookNotFoundException()
        bookRepository.save(book.copy(isDeleted = true, updatedAt = LocalDateTime.now()))
        inventoryLogRepository.save(
            InventoryLog(
                bookId = id,
                action = InventoryAction.SOFT_DELETE,
                quantity = 0,
                timestamp = LocalDateTime.now()
            )
        )
        logger.info("Book soft deleted: {}", id)
    }

    private fun toResponseDTO(book: Book): BookResponseDTO {
        val price = bookPriceRepository.findByBookId(book.id)?.price ?: 0.0
        val quantity = bookInventoryRepository.findByBookId(book.id)?.quantity ?: 0
        return BookResponseDTO(
            id = book.id,
            title = book.title,
            author = book.author,
            genre = book.genre,
            isbn = book.isbn,
            price = price,
            quantity = quantity,
            createdAt = book.createdAt,
            updatedAt = book.updatedAt,
            isDeleted = book.isDeleted
        )
    }
}

