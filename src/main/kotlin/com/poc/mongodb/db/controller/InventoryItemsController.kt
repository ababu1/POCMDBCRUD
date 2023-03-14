package com.poc.mongodb.db.controller


import com.poc.mongodb.db.model.InventoryItem
import com.poc.mongodb.db.model.OrgInventoryItems
import com.poc.mongodb.db.service.InventoryItemService
import com.poc.mongodb.db.service.InventoryItemsService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/orginventoryitems")
class InventoryItemsController(@Autowired val inventoryItemsService: InventoryItemsService) {

    @PostMapping("/publishInventoryItem")
    fun pushMessageToEH(@RequestBody orgInventoryItem: OrgInventoryItems) : ResponseEntity<String> {
        inventoryItemsService.publishEvents(orgInventoryItem)
        return ResponseEntity.status(HttpStatus.CREATED).build()
    }

    @GetMapping("/consumeInventoryItem")
    fun consumeMessageFromEH() : ResponseEntity<String> {
        inventoryItemsService.consumeEvents()
        return ResponseEntity.status(HttpStatus.OK).build()
    }

    @PostMapping("/create")
    fun addInventoryItem(@RequestBody orgInventoryItem: OrgInventoryItems) : ResponseEntity<String> {
        inventoryItemsService.addInventoryItem(orgInventoryItem)
        return ResponseEntity.status(HttpStatus.CREATED).build()
    }


}