package com.bookstore.bookinventory.service

import com.bookstore.bookinventory.dto.BookResponseDTO
import com.bookstore.bookinventory.dto.BookSearchRequestDTO
import com.bookstore.bookinventory.model.Book
import com.bookstore.bookinventory.repository.BookInventoryRepository
import com.bookstore.bookinventory.repository.BookPriceRepository
import com.bookstore.bookinventory.repository.BookRepository
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service

@Service
class BookSearchService(
    private val bookRepository: BookRepository,
    private val bookPriceRepository: BookPriceRepository,
    private val bookInventoryRepository: BookInventoryRepository
) {
    private val logger = LoggerFactory.getLogger(BookSearchService::class.java)

    fun searchBooks(request: BookSearchRequestDTO): Page<BookResponseDTO> {
        val sort = if (request.sortDir.equals("desc", true)) Sort.by(request.sortBy).descending() else Sort.by(request.sortBy).ascending()
        val pageable: Pageable = PageRequest.of(request.page, request.size, sort)
        val books = bookRepository.findByIsDeletedFalse()
            .filter { book ->
                (request.title.isNullOrBlank() || book.title.contains(request.title, ignoreCase = true)) &&
                (request.author.isNullOrBlank() || book.author.contains(request.author, ignoreCase = true)) &&
                (request.genre.isNullOrBlank() || book.genre.equals(request.genre, ignoreCase = true)) &&
                (request.isbn.isNullOrBlank() || book.isbn.equals(request.isbn, ignoreCase = true))
            }
            .filter { book ->
                val price = bookPriceRepository.findByBookId(book.id)?.price ?: 0.0
                (request.minPrice == null || price >= request.minPrice) &&
                (request.maxPrice == null || price <= request.maxPrice)
            }
            .filter { book ->
                (request.genreFilter.isNullOrBlank() || book.genre.equals(request.genreFilter, ignoreCase = true))
            }
            .filter { book ->
                if (request.availableOnly == true) {
                    val quantity = bookInventoryRepository.findByBookId(book.id)?.quantity ?: 0
                    quantity > 0
                } else true
            }
        val sortedBooks = books.sortedWith(compareBy(
            if (request.sortDir.equals("desc", true)) {
                { book: Book ->
                    when (request.sortBy) {
                        "title" -> book.title
                        "author" -> book.author
                        "genre" -> book.genre
                        "isbn" -> book.isbn
                        else -> book.title
                    }
                }
            } else {
                { book: Book ->
                    when (request.sortBy) {
                        "title" -> book.title
                        "author" -> book.author
                        "genre" -> book.genre
                        "isbn" -> book.isbn
                        else -> book.title
                    }
                }
            }
        ))
        val start = pageable.offset.toInt()
        val end = (start + pageable.pageSize).coerceAtMost(sortedBooks.size)
        val pageContent = if (start <= end) sortedBooks.subList(start, end) else emptyList()
        val responseList = pageContent.map { book ->
            val price = bookPriceRepository.findByBookId(book.id)?.price ?: 0.0
            val quantity = bookInventoryRepository.findByBookId(book.id)?.quantity ?: 0
            BookResponseDTO(
                id = book.id,
                title = book.title,
                author = book.author,
                genre = book.genre,
                isbn = book.isbn,
                price = price,
                quantity = quantity,
                createdAt = book.createdAt,
                updatedAt = book.updatedAt,
                isDeleted = book.isDeleted
            )
        }
        return PageImpl(responseList, pageable, sortedBooks.size.toLong())
    }
}
