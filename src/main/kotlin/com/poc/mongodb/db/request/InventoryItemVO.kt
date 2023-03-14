package com.poc.mongodb.db.request

import org.bson.types.ObjectId
import java.math.BigDecimal

enum class SetOrAdjust {
    SET, ADJUST, FAILURE
}

data class InventoryLevelVO(
    val id:ObjectId? = null,
    var count: Int,
    var locationId: String
)


data class InventoryItemVO(
    val id:ObjectId? = null,
    var sku: String,
    var effectiveTs: String,
    var inventoryLevels:List<InventoryLevelVO>
)

data class Org(
    var id: String
)

data class InventoryItemsPayLoad(
    var id:String? = null,
    val org: Org,
    var inventoryItems:List<InventoryItemVO>
)
