package com.poc.mongodb.db.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.math.BigDecimal

@Document(collection="inventorylevel")
data class InventoryLevel (
    @Id
    val id : String? = null,
//    var sku: String,
//    var orgId: String,
    var count: BigDecimal,
    var locationId: BigDecimal,
    var orgSku: InventoryItem
)