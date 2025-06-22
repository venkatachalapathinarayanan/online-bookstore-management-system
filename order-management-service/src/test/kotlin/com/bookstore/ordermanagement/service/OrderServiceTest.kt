package com.bookstore.ordermanagement.service

import com.bookstore.common.kafka.KafkaEventPublisher
import com.bookstore.ordermanagement.dto.OrderItemRequestDTO
import com.bookstore.ordermanagement.dto.OrderRequestDTO
import com.bookstore.ordermanagement.model.Order
import com.bookstore.ordermanagement.model.OrderItem
import com.bookstore.ordermanagement.repository.CartRepository
import com.bookstore.ordermanagement.repository.OrderRepository
import io.mockk.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.LocalDateTime

class OrderServiceTest {
    private val orderRepository: OrderRepository = mockk(relaxed = true)
    private val cartRepository: CartRepository = mockk(relaxed = true)
    private val kafkaEventPublisher: KafkaEventPublisher = mockk(relaxed = true)
    private val bookInventoryClient: BookInventoryClient = mockk(relaxed = true)
    private lateinit var orderService: OrderService

    @BeforeEach
    fun setUp() {
        clearAllMocks()
        orderService = OrderService(orderRepository, kafkaEventPublisher, cartRepository, bookInventoryClient)
    }

    @Test
    fun `createOrder returns order response`() {
        val orderItem = OrderItemRequestDTO(2L, 3, BigDecimal("10.0"))
        val request = OrderRequestDTO(1L, listOf(orderItem))
        val order = mockk<Order>(relaxed = true) {
            every { id } returns 1L
            every { userId } returns request.userId
            every { items } returns mutableListOf(OrderItem(1L, 2L, 3, BigDecimal("10.0")))
            every { status } returns "CREATED"
            every { createdAt } returns LocalDateTime.now()
        }
        every { orderRepository.save(any()) } returns order
        every { cartRepository.findByUserId(request.userId) } returns null

        val result = orderService.createOrder(request)

        assertEquals(order.id, result.id)
        assertEquals(order.userId, result.userId)
        assertEquals(1, result.items.size)
        assertEquals(2L, result.items[0].bookId)
        assertEquals(3, result.items[0].quantity)
        assertEquals(BigDecimal("10.0"), result.items[0].price)
        assertEquals("CREATED", result.status)
        verify { orderRepository.save(any()) }
        verify { kafkaEventPublisher.publish(any(), any()) }
    }

    @Test
    fun `getOrderStatus returns status`() {
        val order = mockk<Order>(relaxed = true) {
            every { status } returns "CREATED"
        }
        every { orderRepository.findById(1L) } returns java.util.Optional.of(order)

        val result = orderService.getOrderStatus(1L)

        assertEquals("CREATED", result)
    }

    @Test
    fun `getOrderHistory returns list`() {
        val orderItem = OrderItem(1L, 2L, 3, BigDecimal("10.0"))
        val order = mockk<Order>(relaxed = true) {
            every { id } returns 1L
            every { userId } returns 1L
            every { items } returns mutableListOf(orderItem)
            every { status } returns "COMPLETED"
            every { createdAt } returns LocalDateTime.now()
        }
        every { orderRepository.findAll() } returns listOf(order)

        val result = orderService.getOrderHistory(1L)

        assertEquals(1, result.size)
        assertEquals(order.id, result[0].id)
        assertEquals(order.userId, result[0].userId)
        assertEquals(1, result[0].items.size)
        assertEquals(2L, result[0].items[0].bookId)
        assertEquals(3, result[0].items[0].quantity)
        assertEquals(BigDecimal("10.0"), result[0].items[0].price)
        assertEquals("COMPLETED", result[0].status)
    }

    @Test
    fun `confirmPayment returns order response`() {
        val orderItem = OrderItem(1L, 2L, 3, BigDecimal("10.0"))
        val order = mockk<Order>(relaxed = true) {
            every { id } returns 1L
            every { userId } returns 1L
            every { items } returns mutableListOf(orderItem)
            every { status } returns "CREATED"
            every { createdAt } returns LocalDateTime.now()
        }
        val paidOrder = mockk<Order>(relaxed = true) {
            every { id } returns 1L
            every { userId } returns 1L
            every { items } returns mutableListOf(orderItem)
            every { status } returns "PAID"
            every { createdAt } returns LocalDateTime.now()
        }
        
        every { orderRepository.findById(1L) } returns java.util.Optional.of(order)
        every { orderRepository.save(any()) } returns paidOrder

        val result = orderService.confirmPayment(1L)

        assertEquals(1L, result.id)
        assertEquals(1L, result.userId)
        assertEquals(1, result.items.size)
        assertEquals(2L, result.items[0].bookId)
        assertEquals(3, result.items[0].quantity)
        assertEquals(BigDecimal("10.0"), result.items[0].price)
        assertEquals("PAID", result.status)
        verify { orderRepository.save(any()) }
        verify { kafkaEventPublisher.publish(any(), any()) }
    }
}
