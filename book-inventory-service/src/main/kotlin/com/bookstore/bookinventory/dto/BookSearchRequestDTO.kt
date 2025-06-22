package com.bookstore.bookinventory.dto

import java.math.BigDecimal

data class BookSearchRequestDTO(
    val title: String? = null,
    val author: String? = null,
    val genre: String? = null,
    val isbn: String? = null,
    val minPrice: BigDecimal? = null,
    val maxPrice: BigDecimal? = null,
    val genreFilter: String? = null,
    val availableOnly: Boolean? = null,
    val page: Int = 0,
    val size: Int = 10,
    val sortBy: String = "title",
    val sortDir: String = "asc"
)

