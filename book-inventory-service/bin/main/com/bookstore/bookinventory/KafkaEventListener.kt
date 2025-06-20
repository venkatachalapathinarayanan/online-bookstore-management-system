package com.bookstore.bookinventory

import com.bookstore.bookinventory.service.InventoryService
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component
import org.springframework.beans.factory.annotation.Autowired
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue

@Component
class KafkaEventListener @Autowired constructor(
    private val inventoryService: InventoryService
) {
    private val logger = LoggerFactory.getLogger(KafkaEventListener::class.java)
    private val objectMapper = jacksonObjectMapper()

    @KafkaListener(topics = ["order-events"], groupId = "book-inventory-group")
    fun handleOrderCreated(message: String) {
        try {
            val event: Map<String, Any> = objectMapper.readValue(message)
            if (event["eventType"] == "OrderCreated") {
                val payload = event["payload"] as Map<*, *>
                val items = payload["items"] as? List<Map<String, Any>>
                if (items != null) {
                    for (item in items) {
                        val bookId = (item["bookId"] as Number).toLong()
                        val quantity = (item["quantity"] as Number).toInt()
                        inventoryService.decreaseInventory(
                            com.bookstore.bookinventory.dto.InventoryDecreaseRequestDTO(
                                bookId = bookId,
                                decreaseBy = quantity
                            )
                        )
                        logger.info("Processed OrderCreated event for bookId=$bookId, quantity=$quantity")
                    }
                } else {
                    logger.warn("OrderCreated event payload missing items list: $payload")
                }
            }
        } catch (ex: Exception) {
            logger.error("Failed to process OrderCreated event", ex)
        }
    }
}

