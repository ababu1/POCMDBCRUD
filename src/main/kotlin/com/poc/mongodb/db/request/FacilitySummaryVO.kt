package com.poc.mongodb.db.request

data class ItemSummaryVO(
    val sku:String,
    val availableQuantity:Int
)

data class FacilitySummaryVO (
    var facilityId: String,
    var items: List<ItemSummaryVO> = emptyList()
)

data class FacilityResponseVO (
    var org: String?,
    var facilitySummary: List<FacilitySummaryVO> = emptyList()
)

data class Items(
    val sku:String,
    val availableQuantity:Int
)
data class ItemSummaryVO1(
    var facilityId: String,
    val items:Items
)