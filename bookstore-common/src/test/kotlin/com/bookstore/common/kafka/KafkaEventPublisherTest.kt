package com.bookstore.common.kafka

import com.bookstore.common.event.EventMessage
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.support.SendResult
import java.util.concurrent.CompletableFuture

class KafkaEventPublisherTest {
    private lateinit var kafkaTemplate: KafkaTemplate<String, EventMessage>
    private lateinit var publisher: KafkaEventPublisher

    @BeforeEach
    fun setUp() {
        kafkaTemplate = mockk()
        publisher = KafkaEventPublisher(kafkaTemplate)
    }

    @Test
    fun `publish sends event to kafka`() {
        val event = EventMessage("type", mapOf("key" to "value"))
        val topic = "test-topic"
        val mockResult = mockk<SendResult<String, EventMessage>>()
        val future = CompletableFuture.completedFuture(mockResult)
        
        every { kafkaTemplate.send(topic, event) } returns future
        
        publisher.publish(topic, event)
        
        verify { kafkaTemplate.send(topic, event) }
    }
}

