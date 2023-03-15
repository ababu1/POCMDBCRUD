package com.poc.mongodb.db.service

import com.poc.mongodb.db.model.InventoryItem
import com.poc.mongodb.db.model.InventoryLevel
import com.poc.mongodb.db.model.ItemDetail
import com.poc.mongodb.db.repository.InventoryItemMongoTemplateRepo
import com.poc.mongodb.db.repository.InventoryItemRepo
import com.poc.mongodb.db.repository.InventoryLevelMongoTemplateRepo
import com.poc.mongodb.db.repository.InventoryLevelRepo
import com.poc.mongodb.db.request.FacilityResponseVO
import com.poc.mongodb.db.request.FacilitySummaryVO
import com.poc.mongodb.db.request.InventoryItemsPayLoad
import com.poc.mongodb.db.request.ItemSummaryVO
import com.poc.mongodb.db.request.SetOrAdjust
import org.springframework.stereotype.Service

@Service
class InventoryItemService(
    val inventoryItemRepo: InventoryItemRepo,
    val inventoryItemMongoTemplateRepo: InventoryItemMongoTemplateRepo,
    val inventoryLevelMongoTemplateRepo: InventoryLevelMongoTemplateRepo,
    val inventoryLevelRepo: InventoryLevelRepo
) {

    fun upsertInventoryItem(payLoad: InventoryItemsPayLoad, operation: SetOrAdjust): InventoryItemsPayLoad {

        payLoad.inventoryItems.forEach { f ->
            inventoryItemMongoTemplateRepo.upsertInventoryItem(InventoryItem(sku = f.sku,
                orgId = payLoad.org.id,
            ))
            f.inventoryLevels.forEach { l ->
                inventoryLevelMongoTemplateRepo.upsertInventoryLevel(
                    InventoryLevel(
                        count = l.count,
                        facilityId = l.facilityId,
                        effectiveTs = f.effectiveTs,
                        itemDetail = ItemDetail(f.sku, payLoad.org.id)
                    ),
                    operation
                )
            }
        }
        return payLoad
    }

    fun getInventoryItemByOrg(org:String, sku:List<String>, facilityId:List<String>): FacilityResponseVO? {

        val facilityResponse = inventoryLevelMongoTemplateRepo.findByOrglistofSkuNLication(org,sku,facilityId)

        return facilityResponse
    }

}