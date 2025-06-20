package com.bookstore.ordermanagement.controller

import com.bookstore.ordermanagement.dto.OrderRequestDTO
import com.bookstore.ordermanagement.dto.OrderResponseDTO
import com.bookstore.ordermanagement.service.OrderService
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.ResponseEntity

class OrderControllerTest {
    private val orderService: OrderService = mockk()
    private lateinit var orderController: OrderController

    @BeforeEach
    fun setUp() {
        orderController = OrderController(orderService)
    }

    @Test
    fun `createOrder returns order response`() {
        val request = OrderRequestDTO(1L, emptyList())
        val response = OrderResponseDTO(1L, 1L, emptyList(), "CREATED")
        every { orderService.createOrder(request) } returns response
        val result = orderController.createOrder(request)
        assertEquals(response, result.body)
    }

    @Test
    fun `getOrderStatus returns status`() {
        every { orderService.getOrderStatus(1L) } returns "CREATED"
        val result = orderController.getOrderStatus(1L)
        assertEquals("CREATED", result.body)
    }

    @Test
    fun `getOrderHistory returns list`() {
        val response = listOf(OrderResponseDTO(1L, 1L, emptyList(), "CREATED"))
        every { orderService.getOrderHistory(1L) } returns response
        val result = orderController.getOrderHistory(1L)
        assertEquals(response, result.body)
    }

    @Test
    fun `confirmPayment returns order response`() {
        val response = OrderResponseDTO(1L, 1L, emptyList(), "PAID")
        every { orderService.confirmPayment(1L) } returns response
        val result = orderController.confirmPayment(1L)
        assertEquals(response, result.body)
    }

    @Test
    fun `createOrderFromCart returns order response`() {
        val response = OrderResponseDTO(1L, 1L, emptyList(), "CREATED")
        every { orderService.createOrderFromCart(1L) } returns response
        val result = orderController.createOrderFromCart(1L)
        assertEquals(response, result.body)
    }
}

