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
class InventoryItemsController(@Autowired val inventoryItemService: InventoryItemService) {

    @PostMapping("/create")
    fun addInventoryItem(@RequestBody orgInventoryItem: OrgInventoryItems) : ResponseEntity<String> {
        inventoryItemService.addInventoryItem(orgInventoryItem)
        return ResponseEntity.status(HttpStatus.CREATED).build()
    }

    @GetMapping("/ALL")
    fun getAllInventory():ResponseEntity<List<OrgInventoryItems>>
            = ResponseEntity.ok(inventoryItemService.getAllInventoryItems())

    @GetMapping("/{sku}")
    fun getInventoryItemBySku(@PathVariable sku:String) : ResponseEntity<List<OrgInventoryItems>>
            = ResponseEntity.ok(inventoryItemService.getInventoryItemBySku(sku))

    @GetMapping("/ById/{id}")
    fun getInventoryItemById(@PathVariable id:String) : ResponseEntity<InventoryItem>
            = ResponseEntity.ok(inventoryItemService.findById(id))
    @GetMapping("/{org_id}")
    fun getInventoryItemByOrgId(@PathVariable org_id:String) : ResponseEntity<OrgInventoryItems>
            = ResponseEntity.ok(inventoryItemService.getInventoryItemByOrgId(org_id))

    @DeleteMapping("/{id}")
    fun deleteInventoryByOrgId(@PathVariable id:String):ResponseEntity<String> {
        inventoryItemService.deleteInventoryItem(id)
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build()
    }

    @PatchMapping("/update")
    fun updateInventorybyOrgId(@RequestBody orgInventoryItem: OrgInventoryItems): ResponseEntity<String> {
        inventoryItemService.updateInventoryItem(orgInventoryItem)
        return ResponseEntity.ok().build()
    }

}