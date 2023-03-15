package com.poc.mongodb.db.repository

import com.poc.mongodb.db.model.InventoryLevel
import com.poc.mongodb.db.request.FacilityResponseVO
import com.poc.mongodb.db.request.SetOrAdjust
import org.springframework.data.mongodb.repository.Query
import java.util.*

interface InventoryLevelMongoTemplateRepo {

    fun upsertInventoryLevel(level: InventoryLevel, operation: SetOrAdjust) : Result<String>
    fun findByOrglistofSkuNLication(orgId: String, sku:List<String>, facilityId:List<String>): FacilityResponseVO?


}