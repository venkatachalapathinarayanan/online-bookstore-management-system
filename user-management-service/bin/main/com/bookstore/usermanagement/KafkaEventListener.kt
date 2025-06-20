package com.bookstore.usermanagement

import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue

@Component
class KafkaEventListener {
    private val logger = LoggerFactory.getLogger(KafkaEventListener::class.java)
    private val objectMapper = jacksonObjectMapper()

    @KafkaListener(topics = ["order-events", "inventory-events"], groupId = "user-management-group")
    fun handleEvents(message: String) {
        try {
            val event: Map<String, Any> = objectMapper.readValue(message)
            val eventType = event["eventType"]
            logger.info("Received event: $eventType, payload: ${event["payload"]}")
            // Add business logic here for OrderCreated, InventoryUpdated, etc.
        } catch (ex: Exception) {
            logger.error("Failed to process event", ex)
        }
    }
}

