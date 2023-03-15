package com.poc.mongodb.db.controller

import com.poc.mongodb.db.request.FacilityResponseVO
import com.poc.mongodb.db.request.InventoryItemsPayLoad
import com.poc.mongodb.db.request.SetOrAdjust
import com.poc.mongodb.db.service.InventoryItemService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/orginventoryitems")
class InventoryItemsController(@Autowired val inventoryItemService: InventoryItemService) {

    @PostMapping("/create")
    fun upsertInventoryItem(@RequestBody inventoryItemsPayLoad: InventoryItemsPayLoad) : ResponseEntity<String> {
        inventoryItemService.upsertInventoryItem(inventoryItemsPayLoad, SetOrAdjust.SET)
        return ResponseEntity.status(HttpStatus.ACCEPTED).build()
    }

    @PostMapping("/adjust")
    fun adjustInventoryItem(@RequestBody inventoryItemsPayLoad: InventoryItemsPayLoad) : ResponseEntity<String> {
        inventoryItemService.upsertInventoryItem(inventoryItemsPayLoad, SetOrAdjust.ADJUST)
        return ResponseEntity.status(HttpStatus.ACCEPTED).build()
    }

    @GetMapping("/ByOrg/{org}/{sku}/{locationId}")
    fun getInventoryItemByOrg(@PathVariable org:String, @PathVariable sku:List<String>, @PathVariable locationId:List<String>) : ResponseEntity<FacilityResponseVO>
            = ResponseEntity.ok(inventoryItemService.getInventoryItemByOrg(org, sku,locationId))


}