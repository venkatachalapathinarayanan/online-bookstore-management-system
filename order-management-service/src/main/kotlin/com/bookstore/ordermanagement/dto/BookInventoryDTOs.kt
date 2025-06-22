package com.bookstore.ordermanagement.dto

import java.math.BigDecimal

data class BookPriceRequest(
    val bookIds: List<Long>
)

data class BookPriceResponse(
    val prices: Map<Long, BigDecimal>
)

data class BookResponse(
    val id: Long,
    val title: String,
    val author: String,
    val price: BigDecimal,
    val quantity: Int
) 