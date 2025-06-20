// Title: InventoryStatusDTO
package com.bookstore.bookinventory.controller

import com.bookstore.bookinventory.dto.InventoryUpdateRequestDTO
import com.bookstore.bookinventory.dto.InventoryDecreaseRequestDTO
import com.bookstore.bookinventory.dto.InventoryStatusDTO
import com.bookstore.bookinventory.service.InventoryService
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.ResponseEntity

class InventoryControllerTest {
    private val inventoryService: InventoryService = mockk()
    private lateinit var inventoryController: InventoryController

    @BeforeEach
    fun setUp() {
        inventoryController = InventoryController(inventoryService)
    }

    @Test
    fun `updateInventory returns no content`() {
        val request = InventoryUpdateRequestDTO(1L, 10)
        every { inventoryService.updateInventory(request) } returns Unit
        val result = inventoryController.updateInventory(request)
        assertEquals(ResponseEntity.noContent().build<Void>(), result)
    }

    @Test
    fun `decreaseInventory returns no content`() {
        val request = InventoryDecreaseRequestDTO(1L, 2)
        every { inventoryService.decreaseInventory(request) } returns Unit
        val result = inventoryController.decreaseInventory(request)
        assertEquals(ResponseEntity.noContent().build<Void>(), result)
    }

    @Test
    fun `getInventoryStatus returns status`() {
        val response = InventoryStatusDTO(bookId = 1L, title = "Sample Book", quantity = 5)
        every { inventoryService.getInventoryStatus(1L) } returns response
        val result = inventoryController.getInventoryStatus(1L)
        assertEquals(response, result.body)
    }

    @Test
    fun `filterBooksByStock returns list`() {
        val response = listOf(InventoryStatusDTO(bookId = 1L, title = "Sample Book", quantity = 5))
        every { inventoryService.filterBooksByStock(3) } returns response
        val result = inventoryController.filterBooksByStock(3)
        assertEquals(response, result.body)
    }

    @Test
    fun `listLowOrOutOfStockBooks returns list`() {
        val response = listOf(InventoryStatusDTO(bookId = 1L, title = "Sample Book", quantity = 0))
        every { inventoryService.listLowOrOutOfStockBooks(5) } returns response
        val result = inventoryController.listLowOrOutOfStockBooks(5)
        assertEquals(response, result.body)
    }
}

