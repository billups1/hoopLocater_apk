package com.real.hoop_locater.web.hoop

data class HoopCreateRequest(
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val hoopCount: Int,
    val floorType: String,
    val light: String,
    val freeState: String,
    val standardState: String,
    val loginId : String?
)