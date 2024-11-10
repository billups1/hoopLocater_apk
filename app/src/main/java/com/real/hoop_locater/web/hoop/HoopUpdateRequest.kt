package com.real.hoop_locater.web.hoop

data class HoopUpdateRequest(
    val id: Long,
    val name: String,
    val hoopCount: Int?,
    val floorType: String,
    val light: String,
    val freeState: String,
    val standardState: String
)