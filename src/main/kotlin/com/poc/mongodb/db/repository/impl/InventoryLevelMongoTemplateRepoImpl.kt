package com.poc.mongodb.db.repository.impl

import com.poc.mongodb.db.model.InventoryLevel
import com.poc.mongodb.db.repository.InventoryLevelMongoTemplateRepo
import com.poc.mongodb.db.request.SetOrAdjust
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.stereotype.Repository

@Repository
class InventoryLevelMongoTemplateRepoImpl : InventoryLevelMongoTemplateRepo {

    @Autowired
    private lateinit var mongoTemplate: MongoTemplate

    override fun upsertInventoryLevel(level: InventoryLevel, operation: SetOrAdjust): Result<String> {
        return runCatching {
            val updateDefinition: Update = if (operation == SetOrAdjust.SET) {
                Update().set("count", level.count)
            } else {
                Update().inc("count", level.count)
            }
            val query: Query = Query().addCriteria(Criteria.where("itemDetail.sku").`is`(level.itemDetail.sku))
                .addCriteria(Criteria.where("itemDetail.orgId").`is`(level.itemDetail.orgId))
                .addCriteria(Criteria.where("locationId").`is`(level.locationId))

            mongoTemplate.upsert(query, updateDefinition, InventoryLevel::class.java).upsertedId.toString()
        }
    }
}