package com.bookstore.ordermanagement.model

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "order_items")
data class OrderItem(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    val bookId: Long,
    val quantity: Int,
    val price: Double,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    var order: Order? = null
)

@Entity
@Table(name = "orders")
data class Order(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    val userId: Long,
    @OneToMany(mappedBy = "order", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.EAGER)
    val items: MutableList<OrderItem> = mutableListOf(),
    val status: String = "CREATED",
    val createdAt: LocalDateTime = LocalDateTime.now()
)
