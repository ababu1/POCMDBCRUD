package com.poc.mongodb.db.repository

import com.poc.mongodb.db.model.InventoryItem

interface InventoryItemMongoTemplateRepo {

    fun upsertInventoryItem(item: InventoryItem): Result<String>
}