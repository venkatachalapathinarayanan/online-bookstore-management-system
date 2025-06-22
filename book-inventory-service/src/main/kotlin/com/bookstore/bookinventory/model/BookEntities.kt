package com.bookstore.bookinventory.model

import jakarta.persistence.*
import java.math.BigDecimal
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
    @Column(name = "created_at")
    val createdAt: LocalDateTime = LocalDateTime.now(),
    @Column(name = "updated_at")
    val updatedAt: LocalDateTime = LocalDateTime.now(),
    @Column(name = "is_deleted")
    val isDeleted: Boolean = false
)

@Entity
@Table(name = "books_price")
data class BookPrice(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    @Column(name = "book_id")
    val bookId: Long,
    @Column(precision = 10, scale = 2)
    val price: BigDecimal
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

