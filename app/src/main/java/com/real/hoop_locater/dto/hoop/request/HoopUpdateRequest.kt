package com.real.hoop_locater.dto.hoop.request

data class HoopUpdateRequest(
    val id: Int,
    val name: String,
    val hoopCount: Int?,
    val floorType: String,
    val light: String,
    val freeState: String,
    val standardState: String,
    val loginId : String?
)