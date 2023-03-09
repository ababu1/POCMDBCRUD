package com.poc.mongodb.db.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.math.BigDecimal
import java.sql.Timestamp


//@Document("inventoryLevels")
data class InventoryLevels(
    var count: BigDecimal,
    var locationId: BigDecimal
)

//@Document("SKUItem")
//data class SKUItem(
//    var sku: String,
//    var effectiveTs: Timestamp
////    var inventoryLevels:List<InventoryLevels>
//)

//@Document("inventoryitem")
data class InventoryItems(
    var sku: String,
    var effectiveTs: String,
    var inventoryLevels:List<InventoryLevels>
)

//@Document("org")
data class Org(
    @Id
    var id:String? = null
)

@Document("orginventoryitems")
data class OrgInventoryItems(
    @Id
    var id:String? = null,
    val org:Org,
    var inventoryItems:List<InventoryItems>
)


//data class OrgInventoryItems1(
//    val org:Org,
//    var inventoryItems:List<InventoryItems>
//)
//
//
//@Document("orginventoryitem1")
//data class OrgInventoryItems(
//    val org:OrgInventoryItems1
//)


