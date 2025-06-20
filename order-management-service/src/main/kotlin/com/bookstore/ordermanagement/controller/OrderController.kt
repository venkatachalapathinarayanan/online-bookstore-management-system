package com.bookstore.ordermanagement.controller

import com.bookstore.ordermanagement.dto.OrderRequestDTO
import com.bookstore.ordermanagement.dto.OrderResponseDTO
import com.bookstore.ordermanagement.service.OrderService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@Tag(name = "Orders", description = "Endpoints for managing orders")
@RestController
@RequestMapping("/orders")
class OrderController(private val orderService: OrderService) {
    @Operation(summary = "Create a new order", description = "Creates a new order for a user.")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Order created successfully"),
            ApiResponse(responseCode = "400", description = "Invalid request data"),
            ApiResponse(responseCode = "403", description = "Forbidden - insufficient permissions")
        ]
    )
    @PostMapping
    fun createOrder(
        @Parameter(description = "Order request payload", required = true)
        @RequestBody request: OrderRequestDTO
    ): ResponseEntity<OrderResponseDTO> {
        val response = orderService.createOrder(request)
        return ResponseEntity.ok(response)
    }

    @Operation(summary = "Get order status", description = "Retrieves the status of a specific order.")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Order status returned"),
            ApiResponse(responseCode = "404", description = "Order not found")
        ]
    )
    @GetMapping("/{orderId}/status")
    fun getOrderStatus(
        @Parameter(description = "ID of the order", required = true)
        @PathVariable orderId: Long
    ): ResponseEntity<String> {
        val status = orderService.getOrderStatus(orderId)
        return ResponseEntity.ok(status)
    }

    @Operation(summary = "Get order history for a user", description = "Retrieves all orders placed by a specific user.")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Order history returned"),
            ApiResponse(responseCode = "404", description = "User or orders not found")
        ]
    )
    @GetMapping("/user/{userId}")
    fun getOrderHistory(
        @Parameter(description = "ID of the user", required = true)
        @PathVariable userId: Long
    ): ResponseEntity<List<OrderResponseDTO>> {
        val orders = orderService.getOrderHistory(userId)
        return ResponseEntity.ok(orders)
    }

    @Operation(summary = "Confirm payment for an order", description = "Confirms payment for a specific order.")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Payment confirmed"),
            ApiResponse(responseCode = "404", description = "Order not found")
        ]
    )
    @PostMapping("/{orderId}/confirm-payment")
    fun confirmPayment(
        @Parameter(description = "ID of the order", required = true)
        @PathVariable orderId: Long
    ): ResponseEntity<OrderResponseDTO> {
        val response = orderService.confirmPayment(orderId)
        return ResponseEntity.ok(response)
    }

    @Operation(summary = "Create order from cart", description = "Creates an order from the user's cart.")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Order created from cart"),
            ApiResponse(responseCode = "404", description = "User or cart not found")
        ]
    )
    @PostMapping("/from-cart/{userId}")
    fun createOrderFromCart(
        @Parameter(description = "ID of the user", required = true)
        @PathVariable userId: Long
    ): ResponseEntity<OrderResponseDTO> {
        val response = orderService.createOrderFromCart(userId)
        return ResponseEntity.ok(response)
    }
}
