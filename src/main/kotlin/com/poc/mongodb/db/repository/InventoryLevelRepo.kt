package com.poc.mongodb.db.repository

import com.poc.mongodb.db.model.InventoryLevel
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface InventoryLevelRepo: MongoRepository<InventoryLevel, String>