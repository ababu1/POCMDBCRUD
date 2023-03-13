package com.poc.mongodb.db.service

import com.poc.mongodb.db.model.InventoryItem
import com.poc.mongodb.db.model.InventoryLevel
import com.poc.mongodb.db.model.OrgInventoryItems
import com.poc.mongodb.db.repository.InventoryItemRepository
import com.poc.mongodb.db.repository.InventoryItemsRepository
import com.poc.mongodb.db.repository.InventoryLevelRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class InventoryItemService(
    val inventoryItemsRepository: InventoryItemsRepository,
    val inventoryItemRepository: InventoryItemRepository,
    val inventoryLevelRepository: InventoryLevelRepository
) {

    fun addInventoryItem(orgInventoryItem: OrgInventoryItems):OrgInventoryItems {

        orgInventoryItem.inventoryItems.forEach { f ->
            val item = inventoryItemRepository.save(InventoryItem(sku = f.sku,
                effectiveTs = f.effectiveTs,
                orgid =orgInventoryItem.org.id.toString(),
            ))

            f.inventoryLevels.forEach { l ->
                inventoryLevelRepository.save(
                    InventoryLevel(
//                        sku = f.sku,
//                        orgId =orgInventoryItem.org.id.toString(),
                        count = l.count,
                        locationId = l.locationId,
                        orgSku = item)
                )
            }
        }
        return orgInventoryItem
    }

    fun updateInventoryItem(orgInventoryItem: OrgInventoryItems) {
        var savedInventoryItem:OrgInventoryItems
        = orgInventoryItem.org.id?.let {
            inventoryItemsRepository.findByOrgId(it)
                .orElseThrow { throw RuntimeException("Cannot find inventory item by org id") }
        }!!
//        savedInventoryItem.id = orgInventoryItem.id
//        savedInventoryItem.org.id = orgInventoryItem.org.id
        savedInventoryItem.org.id = "TESTAD001"
        inventoryItemsRepository.save(savedInventoryItem)
    }

    fun getAllInventoryItems() : List<OrgInventoryItems> = inventoryItemsRepository.findAll()

    fun getInventoryItemBySku(sku:String):List<OrgInventoryItems> =inventoryItemsRepository.findBySku(sku).orElseThrow{ throw RuntimeException("Cannot find Inventory by Sku") }

    fun getInventoryItemByOrgId(org_id:String):OrgInventoryItems =inventoryItemsRepository.findByOrgId(org_id).orElseThrow{ throw RuntimeException("Cannot find Inventory by Org id") }

    fun deleteInventoryItem(id:String)=inventoryItemsRepository.deleteByOrgId(id)


}