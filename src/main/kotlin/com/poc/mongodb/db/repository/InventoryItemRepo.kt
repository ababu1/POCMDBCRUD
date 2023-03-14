package com.poc.mongodb.db.repository

import com.poc.mongodb.db.model.InventoryItem
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository


@Repository
interface InventoryItemRepo: MongoRepository<InventoryItem, String>