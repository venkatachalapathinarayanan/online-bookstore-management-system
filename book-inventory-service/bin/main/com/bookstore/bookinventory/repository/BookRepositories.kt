package com.bookstore.bookinventory.repository

import com.bookstore.bookinventory.model.*
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface BookRepository : JpaRepository<Book, Long> {
    fun findByIsDeletedFalse(): List<Book>
    fun findByIdAndIsDeletedFalse(id: Long): Book?
    fun existsByIsbnAndIsDeletedFalse(isbn: String): Boolean
}

@Repository
interface BookPriceRepository : JpaRepository<BookPrice, Long> {
    fun findByBookId(bookId: Long): BookPrice?
}

@Repository
interface BookInventoryRepository : JpaRepository<BookInventory, Long> {
    fun findByBookId(bookId: Long): BookInventory?
}

@Repository
interface InventoryLogRepository : JpaRepository<InventoryLog, Long> {
    fun findByBookId(bookId: Long): List<InventoryLog>
}

