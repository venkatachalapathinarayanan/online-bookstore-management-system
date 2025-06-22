package com.bookstore.ordermanagement.service

import com.bookstore.common.event.EventMessage
import com.bookstore.common.kafka.KafkaEventPublisher
import com.bookstore.ordermanagement.dto.OrderRequestDTO
import com.bookstore.ordermanagement.dto.OrderResponseDTO
import com.bookstore.ordermanagement.dto.OrderItemRequestDTO
import com.bookstore.ordermanagement.dto.OrderItemResponseDTO
import com.bookstore.ordermanagement.model.Order
import com.bookstore.ordermanagement.model.OrderItem
import com.bookstore.ordermanagement.repository.OrderRepository
import com.bookstore.ordermanagement.repository.CartRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

@Service
class OrderService(
    private val orderRepository: OrderRepository,
    private val kafkaEventPublisher: KafkaEventPublisher,
    private val cartRepository: CartRepository,
    private val bookInventoryClient: BookInventoryClient
) {
    private val logger = LoggerFactory.getLogger(OrderService::class.java)

    @Transactional
    fun createOrder(request: OrderRequestDTO): OrderResponseDTO {
        // Validate that all items have prices
        request.items.forEach { item ->
            if (item.price <= BigDecimal.ZERO) {
                throw IllegalArgumentException("Invalid price for book ${item.bookId}: ${item.price}")
            }
        }
        
        val order = Order(
            userId = request.userId,
            items = request.items.map {
                OrderItem(
                    bookId = it.bookId,
                    quantity = it.quantity,
                    price = it.price
                )
            }.toMutableList()
        )
        order.items.forEach { it.order = order }
        val saved = orderRepository.save(order)
        logger.info("Order created successfully: {}", saved.id)
        
        // Publish OrderCreated event
        val event = EventMessage(
            eventType = "OrderCreated",
            payload = mapOf(
                "orderId" to saved.id,
                "userId" to saved.userId,
                "items" to saved.items.map { mapOf(
                    "bookId" to it.bookId,
                    "quantity" to it.quantity,
                    "price" to it.price
                ) }
            )
        )
        kafkaEventPublisher.publish("order-events", event)
        
        return OrderResponseDTO(
            id = saved.id,
            userId = saved.userId,
            items = saved.items.map { OrderItemResponseDTO(it.bookId, it.quantity, it.price) },
            status = saved.status
        )
    }

    @Transactional
    fun createOrderFromCart(userId: Long): OrderResponseDTO {
        val cart = cartRepository.findByUserId(userId) 
            ?: throw NoSuchElementException("Cart not found for user $userId")
        
        if (cart.items.isEmpty()) {
            throw IllegalStateException("Cart is empty")
        }
        
        // Fetch prices for all items in cart
        val bookIds = cart.items.map { it.bookId }
        val prices = bookInventoryClient.getBookPrices(bookIds)
            .block() // Convert Mono to blocking call for transaction
        
        if (prices == null || prices.isEmpty()) {
            throw IllegalStateException("Unable to fetch prices for books")
        }
        
        val order = Order(
            userId = userId,
            items = cart.items.map { cartItem ->
                val price = prices[cartItem.bookId] 
                    ?: throw IllegalStateException("Price not found for book ${cartItem.bookId}")
                
                OrderItem(
                    bookId = cartItem.bookId,
                    quantity = cartItem.quantity,
                    price = price
                )
            }.toMutableList()
        )
        order.items.forEach { it.order = order }
        
        val saved = orderRepository.save(order)
        cartRepository.deleteByUserId(userId)
        
        logger.info("Order created from cart for user {}: orderId={}, totalItems={}", 
            userId, saved.id, saved.items.size)
        
        // Publish OrderCreated event
        val event = EventMessage(
            eventType = "OrderCreated",
            payload = mapOf(
                "orderId" to saved.id,
                "userId" to saved.userId,
                "items" to saved.items.map { mapOf(
                    "bookId" to it.bookId,
                    "quantity" to it.quantity,
                    "price" to it.price
                ) }
            )
        )
        kafkaEventPublisher.publish("order-events", event)
        
        return OrderResponseDTO(
            id = saved.id,
            userId = saved.userId,
            items = saved.items.map { OrderItemResponseDTO(it.bookId, it.quantity, it.price) },
            status = saved.status
        )
    }

    fun getOrderStatus(orderId: Long): String {
        val order = orderRepository.findById(orderId).orElseThrow { NoSuchElementException("Order not found") }
        return order.status
    }

    fun getOrderHistory(userId: Long): List<OrderResponseDTO> {
        return orderRepository.findAll()
            .filter { it.userId == userId }
            .map { order ->
                OrderResponseDTO(
                    id = order.id,
                    userId = order.userId,
                    items = order.items.map { OrderItemResponseDTO(it.bookId, it.quantity, it.price) },
                    status = order.status
                )
            }
    }

    @Transactional
    fun confirmPayment(orderId: Long): OrderResponseDTO {
        val order = orderRepository.findById(orderId).orElseThrow { NoSuchElementException("Order not found") }
        val updatedOrder = order.copy(status = "PAID")
        val saved = orderRepository.save(updatedOrder)
        logger.info("Payment confirmed for order {}", saved.id)
        // Publish OrderPaid event
        val event = EventMessage(
            eventType = "OrderPaid",
            payload = mapOf(
                "orderId" to saved.id,
                "userId" to saved.userId,
                "items" to saved.items.map { mapOf(
                    "bookId" to it.bookId,
                    "quantity" to it.quantity,
                    "price" to it.price
                ) }
            )
        )
        kafkaEventPublisher.publish("order-events", event)
        return OrderResponseDTO(
            id = saved.id,
            userId = saved.userId,
            items = saved.items.map { OrderItemResponseDTO(it.bookId, it.quantity, it.price) },
            status = saved.status
        )
    }
}
