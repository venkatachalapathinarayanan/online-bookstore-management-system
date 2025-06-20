package com.bookstore.ordermanagement.service

import com.bookstore.ordermanagement.dto.*
import com.bookstore.ordermanagement.model.Cart
import com.bookstore.ordermanagement.model.CartItem
import com.bookstore.ordermanagement.repository.CartRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CartService(private val cartRepository: CartRepository) {
    @Transactional
    fun addToCart(request: AddToCartRequestDTO) {
        val cart = cartRepository.findByUserId(request.userId) ?: Cart(userId = request.userId)
        val existingItem = cart.items.find { it.bookId == request.bookId }
        if (existingItem != null) {
            existingItem.quantity += request.quantity
        } else {
            val newItem = CartItem(bookId = request.bookId, quantity = request.quantity, cart = cart)
            cart.items.add(newItem)
        }
        cartRepository.save(cart)
    }

    @Transactional
    fun removeFromCart(request: RemoveFromCartRequestDTO) {
        val cart = cartRepository.findByUserId(request.userId) ?: return
        cart.items.removeIf { it.bookId == request.bookId }
        cartRepository.save(cart)
    }

    fun viewCart(userId: Long): CartResponseDTO {
        val cart = cartRepository.findByUserId(userId)
        val items = cart?.items?.map { CartItemDTO(bookId = it.bookId, quantity = it.quantity) } ?: emptyList()
        return CartResponseDTO(userId = userId, items = items)
    }

    @Transactional
    fun clearCart(userId: Long) {
        cartRepository.deleteByUserId(userId)
    }
}

