package com.poc.mongodb.db.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection="inventoryitem")
data class InventoryItem (
    @Id
    val id : String? = null,
    var sku: String,
    var effectiveTs: String,
    var orgid: String
)
