package com.bookstore.ordermanagement.service

import com.bookstore.ordermanagement.dto.AddToCartRequestDTO
import com.bookstore.ordermanagement.dto.RemoveFromCartRequestDTO
import com.bookstore.ordermanagement.dto.CartResponseDTO
import com.bookstore.ordermanagement.model.Cart
import com.bookstore.ordermanagement.model.CartItem
import com.bookstore.ordermanagement.repository.CartRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class CartServiceTest {
    private val cartRepository: CartRepository = mockk(relaxed = true)
    private lateinit var cartService: CartService

    @BeforeEach
    fun setUp() {
        cartService = CartService(cartRepository)
    }

    @Test
    fun `addToCart calls repository`() {
        val request = AddToCartRequestDTO(1L, 2L, 3)
        val cart = mockk<Cart>(relaxed = true)
        every { cartRepository.findByUserId(1L) } returns null
        every { cartRepository.save(any()) } returns cart
        
        cartService.addToCart(request)
        
        verify { cartRepository.save(any()) }
    }

    @Test
    fun `removeFromCart calls repository`() {
        val request = RemoveFromCartRequestDTO(1L, 2L)
        val cart = mockk<Cart>(relaxed = true)
        every { cartRepository.findByUserId(1L) } returns cart
        every { cartRepository.save(any()) } returns cart
        
        cartService.removeFromCart(request)
        
        verify { cartRepository.save(any()) }
    }

    @Test
    fun `viewCart returns cart response`() {
        val cartItem = CartItem(1L, 2L, 3)
        val cart = mockk<Cart>(relaxed = true) {
            every { userId } returns 1L
            every { items } returns mutableListOf(cartItem)
        }
        every { cartRepository.findByUserId(1L) } returns cart
        
        val result = cartService.viewCart(1L)
        
        assertEquals(1L, result.userId)
        assertEquals(1, result.items.size)
        assertEquals(2L, result.items[0].bookId)
        assertEquals(3, result.items[0].quantity)
    }

    @Test
    fun `clearCart calls repository`() {
        every { cartRepository.deleteByUserId(1L) } returns Unit
        
        cartService.clearCart(1L)
        
        verify { cartRepository.deleteByUserId(1L) }
    }
}

