package com.poc.mongodb.db.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.math.BigDecimal

@Document(collection="inventory_level")
data class InventoryLevel (
    @Id
    val id : String? = null,
    var count: Int,
    var facilityId: String,
    var effectiveTs: String?,
    var itemDetail: ItemDetail
)

data class ItemDetail (
    var sku: String,
    var orgId: String
)