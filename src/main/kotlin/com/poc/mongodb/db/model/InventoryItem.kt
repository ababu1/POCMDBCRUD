package com.poc.mongodb.db.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection="inventory_item")
data class InventoryItem (
    @Id
    val id : String? = null,
    var sku: String,
    var orgId: String
)
