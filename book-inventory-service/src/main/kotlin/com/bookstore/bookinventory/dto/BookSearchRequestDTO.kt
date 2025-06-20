package com.bookstore.bookinventory.dto

data class BookSearchRequestDTO(
    val title: String? = null,
    val author: String? = null,
    val genre: String? = null,
    val isbn: String? = null,
    val minPrice: Double? = null,
    val maxPrice: Double? = null,
    val genreFilter: String? = null,
    val availableOnly: Boolean? = null,
    val page: Int = 0,
    val size: Int = 10,
    val sortBy: String = "title",
    val sortDir: String = "asc"
)

