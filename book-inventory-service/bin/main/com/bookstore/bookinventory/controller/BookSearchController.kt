package com.bookstore.bookinventory.controller

import com.bookstore.bookinventory.dto.BookResponseDTO
import com.bookstore.bookinventory.dto.BookSearchRequestDTO
import com.bookstore.bookinventory.service.BookSearchService
import org.springframework.data.domain.Page
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.security.access.prepost.PreAuthorize

@RestController
@RequestMapping("/api/books/search")
class BookSearchController(private val bookSearchService: BookSearchService) {

    @PostMapping
    @PreAuthorize("hasAnyAuthority('USERS', 'ADMIN', 'SUPERADMIN')")
    fun searchBooks(@RequestBody request: BookSearchRequestDTO): ResponseEntity<Page<BookResponseDTO>> =
        ResponseEntity.ok(bookSearchService.searchBooks(request))
}

