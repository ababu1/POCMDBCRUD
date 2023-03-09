package com.poc.mongodb.db.repository

import com.poc.mongodb.db.model.OrgInventoryItems
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.mongodb.repository.Query
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface InventoryItemsRepository: MongoRepository<OrgInventoryItems, String> {
    @Query("{'inventoryItems.sku':?0}")
    fun findBySku(sku:String): Optional<List<OrgInventoryItems>>
    @Query("{'org.id':?0}")
    fun findByOrgId(id:String): Optional<OrgInventoryItems>
    @Query("{'org.id':?0}", delete = true)
    fun deleteByOrgId(id:String): Optional<OrgInventoryItems>
}