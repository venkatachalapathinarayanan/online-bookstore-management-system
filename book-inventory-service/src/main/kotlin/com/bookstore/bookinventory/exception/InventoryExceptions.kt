package com.bookstore.bookinventory.exception

class BookNotFoundException(message: String = "Book not found") : RuntimeException(message)

class InventoryNotFoundException(message: String = "Inventory not found for book") : RuntimeException(message)

class InsufficientStockException(message: String = "Not enough stock to decrease") : RuntimeException(message)

