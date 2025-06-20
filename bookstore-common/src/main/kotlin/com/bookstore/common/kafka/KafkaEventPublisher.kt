package com.bookstore.common.kafka

import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component
import com.bookstore.common.event.EventMessage

@Component
class KafkaEventPublisher(
    private val kafkaTemplate: KafkaTemplate<String, EventMessage>
) {
    fun publish(topic: String, event: EventMessage) {
        kafkaTemplate.send(topic, event)
    }
}

