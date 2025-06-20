package com.bookstore.ordermanagement.controller

import com.bookstore.ordermanagement.dto.*
import com.bookstore.ordermanagement.service.CartService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@Tag(name = "Cart", description = "Endpoints for managing the shopping cart")
@RestController
@RequestMapping("/cart")
class CartController(private val cartService: CartService) {
    @Operation(summary = "Add item to cart", description = "Adds a book to the user's cart.")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Item added to cart"),
            ApiResponse(responseCode = "400", description = "Invalid request data")
        ]
    )
    @PostMapping("/add")
    fun addToCart(
        @Parameter(description = "Add to cart request payload", required = true)
        @RequestBody request: AddToCartRequestDTO
    ): ResponseEntity<Void> {
        cartService.addToCart(request)
        return ResponseEntity.ok().build()
    }

    @Operation(summary = "Remove item from cart", description = "Removes a book from the user's cart.")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Item removed from cart"),
            ApiResponse(responseCode = "400", description = "Invalid request data")
        ]
    )
    @PostMapping("/remove")
    fun removeFromCart(
        @Parameter(description = "Remove from cart request payload", required = true)
        @RequestBody request: RemoveFromCartRequestDTO
    ): ResponseEntity<Void> {
        cartService.removeFromCart(request)
        return ResponseEntity.ok().build()
    }

    @Operation(summary = "View cart", description = "Retrieves the current contents of the user's cart.")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Cart contents returned"),
            ApiResponse(responseCode = "404", description = "Cart not found")
        ]
    )
    @GetMapping("/{userId}")
    fun viewCart(
        @Parameter(description = "ID of the user", required = true)
        @PathVariable userId: Long
    ): ResponseEntity<CartResponseDTO> {
        val response = cartService.viewCart(userId)
        return ResponseEntity.ok(response)
    }

    @Operation(summary = "Clear cart", description = "Removes all items from the user's cart.")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Cart cleared"),
            ApiResponse(responseCode = "404", description = "Cart not found")
        ]
    )
    @DeleteMapping("/{userId}")
    fun clearCart(
        @Parameter(description = "ID of the user", required = true)
        @PathVariable userId: Long
    ): ResponseEntity<Void> {
        cartService.clearCart(userId)
        return ResponseEntity.ok().build()
    }
}

