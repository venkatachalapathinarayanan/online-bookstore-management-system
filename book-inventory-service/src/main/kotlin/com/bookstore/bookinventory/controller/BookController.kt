package com.bookstore.bookinventory.controller

import com.bookstore.bookinventory.dto.BookRequestDTO
import com.bookstore.bookinventory.dto.BookResponseDTO
import com.bookstore.bookinventory.dto.BookPriceRequestDTO
import com.bookstore.bookinventory.dto.BookPriceResponseDTO
import com.bookstore.bookinventory.service.BookService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@Tag(name = "Books", description = "Endpoints for managing books in the inventory")
@RestController
@RequestMapping("/api/books")
class BookController(private val bookService: BookService) {
    @Operation(summary = "Create a new book", description = "Adds a new book to the inventory.")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "201", description = "Book created successfully"),
            ApiResponse(responseCode = "400", description = "Invalid request data")
        ]
    )
    @PostMapping
    fun createBook(
        @Parameter(description = "Book request payload", required = true)
        @Valid @RequestBody request: BookRequestDTO
    ): ResponseEntity<BookResponseDTO> =
        ResponseEntity.status(HttpStatus.CREATED).body(bookService.createBook(request))

    @Operation(summary = "Get book by ID", description = "Retrieves a book by its unique ID.")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Book found"),
            ApiResponse(responseCode = "404", description = "Book not found")
        ]
    )
    @GetMapping("/{id}")
    fun getBook(
        @Parameter(description = "ID of the book", required = true)
        @PathVariable id: Long
    ): ResponseEntity<BookResponseDTO> =
        ResponseEntity.ok(bookService.getBook(id))

    @Operation(summary = "Get prices for multiple books", description = "Retrieves prices for a list of book IDs")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Prices retrieved successfully"),
            ApiResponse(responseCode = "400", description = "Invalid request data")
        ]
    )
    @PostMapping("/prices")
    fun getBookPrices(
        @Parameter(description = "List of book IDs", required = true)
        @RequestBody request: BookPriceRequestDTO
    ): ResponseEntity<BookPriceResponseDTO> {
        val prices = bookService.getBookPrices(request.bookIds)
        return ResponseEntity.ok(BookPriceResponseDTO(prices))
    }

    @Operation(summary = "List all books", description = "Retrieves a list of all books in the inventory.")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "List of books returned")
        ]
    )
    @GetMapping
    fun listBooks(): ResponseEntity<List<BookResponseDTO>> =
        ResponseEntity.ok(bookService.listBooks())

    @Operation(summary = "Update a book", description = "Updates the details of an existing book.")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Book updated successfully"),
            ApiResponse(responseCode = "404", description = "Book not found")
        ]
    )
    @PutMapping("/{id}")
    fun updateBook(
        @Parameter(description = "ID of the book", required = true)
        @PathVariable id: Long,
        @Parameter(description = "Book request payload", required = true)
        @Valid @RequestBody request: BookRequestDTO
    ): ResponseEntity<BookResponseDTO> =
        ResponseEntity.ok(bookService.updateBook(id, request))

    @Operation(summary = "Soft delete a book", description = "Marks a book as deleted without removing it from the database.")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "204", description = "Book soft deleted"),
            ApiResponse(responseCode = "404", description = "Book not found")
        ]
    )
    @DeleteMapping("/{id}")
    fun softDeleteBook(
        @Parameter(description = "ID of the book", required = true)
        @PathVariable id: Long
    ): ResponseEntity<Void> {
        bookService.softDeleteBook(id)
        return ResponseEntity.noContent().build()
    }
}

