package com.bookstore.common.event

data class EventMessage(
    val eventType: String,
    val payload: Map<String, Any?>
)

