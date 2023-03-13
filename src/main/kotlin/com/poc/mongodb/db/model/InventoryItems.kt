package com.poc.mongodb.db.model

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.math.BigDecimal


@Document("inventoryLevels")
data class InventoryLevels(
    @Id
    val id:ObjectId? = null,
    var count: BigDecimal,
    var locationId: BigDecimal
)


@Document("inventoryitem")
data class InventoryItems(
    @Id
    val id:ObjectId? = null,
    var sku: String,
    var effectiveTs: String,
    var inventoryLevels:List<InventoryLevels>
)

@Document("org")
data class Org(
    @Id
    var id: String? = null
)

@Document(collection="orginventoryitems")
data class OrgInventoryItems(
    @Id
    var id:String? = null,
    val org:Org,
    var inventoryItems:List<InventoryItems>
)
