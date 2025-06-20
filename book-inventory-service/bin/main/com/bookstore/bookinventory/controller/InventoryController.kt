package com.bookstore.bookinventory.controller

import com.bookstore.bookinventory.dto.InventoryUpdateRequestDTO
import com.bookstore.bookinventory.dto.InventoryDecreaseRequestDTO
import com.bookstore.bookinventory.dto.InventoryStatusDTO
import com.bookstore.bookinventory.service.InventoryService
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/inventory")
class InventoryController(private val inventoryService: InventoryService) {

    @PostMapping("/update")
    fun updateInventory(@Valid @RequestBody request: InventoryUpdateRequestDTO): ResponseEntity<Void> {
        inventoryService.updateInventory(request)
        return ResponseEntity.noContent().build()
    }

    @PostMapping("/decrease")
    fun decreaseInventory(@Valid @RequestBody request: InventoryDecreaseRequestDTO): ResponseEntity<Void> {
        inventoryService.decreaseInventory(request)
        return ResponseEntity.noContent().build()
    }

    @GetMapping("/status/{bookId}")
    fun getInventoryStatus(@PathVariable bookId: Long): ResponseEntity<InventoryStatusDTO> =
        ResponseEntity.ok(inventoryService.getInventoryStatus(bookId))

    @GetMapping("/filter")
    fun filterBooksByStock(@RequestParam minStock: Int): ResponseEntity<List<InventoryStatusDTO>> =
        ResponseEntity.ok(inventoryService.filterBooksByStock(minStock))

    @GetMapping("/low-stock")
    fun listLowOrOutOfStockBooks(@RequestParam(required = false, defaultValue = "5") threshold: Int): ResponseEntity<List<InventoryStatusDTO>> =
        ResponseEntity.ok(inventoryService.listLowOrOutOfStockBooks(threshold))
}

