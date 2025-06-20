package com.bookstore.common.kafka

import com.bookstore.common.event.EventMessage
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.springframework.kafka.core.KafkaTemplate

class KafkaEventPublisherTest {
    private lateinit var kafkaTemplate: KafkaTemplate<String, EventMessage>
    private lateinit var publisher: KafkaEventPublisher

    @BeforeEach
    fun setUp() {
        kafkaTemplate = mock()
        publisher = KafkaEventPublisher(kafkaTemplate)
    }

    @Test
    fun `publish sends event to kafka`() {
        val event = EventMessage("type", mapOf("key" to "value"))
        val topic = "test-topic"
        publisher.publish(topic, event)
        verify(kafkaTemplate).send(topic, event)
    }
}

