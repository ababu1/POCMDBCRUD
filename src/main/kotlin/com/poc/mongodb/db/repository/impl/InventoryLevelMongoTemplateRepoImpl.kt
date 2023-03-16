package com.poc.mongodb.db.repository.impl

import com.poc.mongodb.db.model.InventoryLevel
import com.poc.mongodb.db.repository.InventoryLevelMongoTemplateRepo
import com.poc.mongodb.db.request.FacilityResponseVO
import com.poc.mongodb.db.request.FacilitySummaryVO
import com.poc.mongodb.db.request.ItemSummaryVO
import com.poc.mongodb.db.request.SetOrAdjust
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Sort
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.aggregation.Aggregation
import org.springframework.data.mongodb.core.aggregation.Aggregation.group
import org.springframework.data.mongodb.core.aggregation.Aggregation.match
import org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation
import org.springframework.data.mongodb.core.aggregation.Aggregation.project
import org.springframework.data.mongodb.core.aggregation.Aggregation.sort
import org.springframework.data.mongodb.core.aggregation.AggregationResults
import org.springframework.data.mongodb.core.aggregation.SortOperation
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.stereotype.Repository
import java.util.*


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
                .addCriteria(Criteria.where("facilityId").`is`(level.facilityId))

            updateDefinition.set("effectiveTs", level.effectiveTs)
            mongoTemplate.upsert(query, updateDefinition, InventoryLevel::class.java).upsertedId.toString()
        }
    }

    override fun findByOrglistofSkuNLication(orgId: String, sku:List<String>, facilityId:List<String>) : FacilityResponseVO? {

         val query: Query = Query().addCriteria(Criteria.where("itemDetail.orgId").`is`(orgId))
                .addCriteria(Criteria.where("itemDetail.sku").`in`(sku))
                .addCriteria(Criteria.where("facilityId").`in`(facilityId))
                .addCriteria(Criteria.where("count").`gt`(0))

         query.with(Sort.by(Sort.Direction.ASC, "facilityId"))



         var rs = mongoTemplate.find(query, InventoryLevel::class.java )

        var facilityResponse : FacilityResponseVO = FacilityResponseVO(orgId)
        var facilitySummary = mutableListOf<FacilitySummaryVO>()
        var itemSummary = mutableListOf<ItemSummaryVO>()
        var facilityPrev : String = ""

        rs.forEach { f ->
            if (facilityPrev== "" || facilityPrev == f.facilityId) {
                val facilityloc = ItemSummaryVO( sku = f.itemDetail.sku, availableQuantity = f.count)
                //facilitySummary  = mutableListOf<FacilitySummary>()
                itemSummary.add(facilityloc)
            } else {
                facilitySummary.add(FacilitySummaryVO(facilityId = facilityPrev, items=itemSummary))
                itemSummary = mutableListOf<ItemSummaryVO>()
                val facilityloc = ItemSummaryVO( sku = f.itemDetail.sku, availableQuantity = f.count)
                itemSummary.add(facilityloc)
            }
            facilityPrev = f.facilityId

        }
        if (itemSummary.isNotEmpty()) {
            facilitySummary.add(FacilitySummaryVO(facilityId = facilityPrev, items=itemSummary))
        }
        facilityResponse.facilitySummary =facilitySummary


//        val groupByfacilityId = group("facilityId")
//            .addToSet("items").`as`("items")
//            .sum("count").`as`("availableQuantity")
//        val filterCount = match(Criteria("count").gt(0))
//        val sortByFacilityId: SortOperation = sort(Sort.by(Sort.Direction.ASC, "facilityId"))
//        val projectObject: ProjectionOperation = project( "items":[["$itemDetail.sku", "$count"]])
//            .and("facilityId").previousOperation()

//        val result: AggregationResults<FacilityResponseVO> = mongoTemplate.aggregate(Aggregation.newAggregation(
//            filterCount,
//            groupByfacilityId,
//            projectObject
//        ), InventoryLevel.class, FacilityResponseVO.class).getMappedResults()

//        val aggregation: Aggregation = newAggregation(
//            filterCount,
//            groupByfacilityId,
//            projectObject
//        )
//        val result: AggregationResults<FacilityResponseVO> = mongoTemplate.aggregate(
//            aggregation, "inventory_level", FacilityResponseVO::class.java
//        )

        return facilityResponse
        }

}