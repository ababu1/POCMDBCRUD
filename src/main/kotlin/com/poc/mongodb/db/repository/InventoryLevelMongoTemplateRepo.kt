package com.poc.mongodb.db.repository

import com.poc.mongodb.db.model.InventoryLevel
import com.poc.mongodb.db.request.SetOrAdjust

interface InventoryLevelMongoTemplateRepo {

    fun upsertInventoryLevel(level: InventoryLevel, operation: SetOrAdjust) : Result<String>
}