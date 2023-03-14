package com.poc.mongodb.db.repository.impl

import com.poc.mongodb.db.model.InventoryItem
import com.poc.mongodb.db.repository.InventoryItemMongoTemplateRepo
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.stereotype.Repository

@Repository
class InventoryItemMongoTemplateRepoImpl : InventoryItemMongoTemplateRepo {

    @Autowired
    private lateinit var mongoTemplate: MongoTemplate

    override fun upsertInventoryItem(item: InventoryItem) : Result<String> {
        return runCatching {
            val query: Query = Query().addCriteria(Criteria.where("sku").`is`(item.sku))
                .addCriteria(Criteria.where("orgId").`is`(item.orgId))
            val updateDefinition: Update = Update().set("sku", item.sku)

            mongoTemplate.upsert(query, updateDefinition, InventoryItem::class.java).upsertedId.toString()
        }
    }
}