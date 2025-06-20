package com.bookstore.bookinventory.exception

class BookAlreadyExistsException(message: String = "Book with this ISBN already exists") : RuntimeException(message)

