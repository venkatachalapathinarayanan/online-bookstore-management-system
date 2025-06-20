package com.bookstore.bookinventory.model

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "books")
data class Book(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    val title: String,
    val author: String,
    val genre: String,
    val isbn: String,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now(),
    val isDeleted: Boolean = false
)

@Entity
@Table(name = "books_price")
data class BookPrice(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    @Column(name = "book_id")
    val bookId: Long,
    val price: Double
)

@Entity
@Table(name = "books_inventory")
data class BookInventory(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    @Column(name = "book_id")
    val bookId: Long,
    val quantity: Int
)

enum class InventoryAction {
    CREATE, UPDATE, SOFT_DELETE
}

@Entity
@Table(name = "inventory_logs")
data class InventoryLog(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    @Column(name = "book_id")
    val bookId: Long,
    @Enumerated(EnumType.STRING)
    val action: InventoryAction,
    val quantity: Int,
    val timestamp: LocalDateTime = LocalDateTime.now()
)

