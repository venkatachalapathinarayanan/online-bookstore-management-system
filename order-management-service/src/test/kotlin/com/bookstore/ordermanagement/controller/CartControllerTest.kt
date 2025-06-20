package com.bookstore.ordermanagement.controller

import com.bookstore.ordermanagement.dto.AddToCartRequestDTO
import com.bookstore.ordermanagement.dto.RemoveFromCartRequestDTO
import com.bookstore.ordermanagement.dto.CartResponseDTO
import com.bookstore.ordermanagement.service.CartService
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.ResponseEntity

class CartControllerTest {
    private val cartService: CartService = mockk()
    private lateinit var cartController: CartController

    @BeforeEach
    fun setUp() {
        cartController = CartController(cartService)
    }

    @Test
    fun `addToCart returns ok`() {
        val request = AddToCartRequestDTO(1L, 2L, 3)
        every { cartService.addToCart(request) } returns Unit
        val result = cartController.addToCart(request)
        assertEquals(ResponseEntity.ok().build<Void>(), result)
    }

    @Test
    fun `removeFromCart returns ok`() {
        val request = RemoveFromCartRequestDTO(1L, 2L)
        every { cartService.removeFromCart(request) } returns Unit
        val result = cartController.removeFromCart(request)
        assertEquals(ResponseEntity.ok().build<Void>(), result)
    }

    @Test
    fun `viewCart returns cart response`() {
        val response = CartResponseDTO(1L, emptyList())
        every { cartService.viewCart(1L) } returns response
        val result = cartController.viewCart(1L)
        assertEquals(response, result.body)
    }

    @Test
    fun `clearCart returns ok`() {
        every { cartService.clearCart(1L) } returns Unit
        val result = cartController.clearCart(1L)
        assertEquals(ResponseEntity.ok().build<Void>(), result)
    }
}

