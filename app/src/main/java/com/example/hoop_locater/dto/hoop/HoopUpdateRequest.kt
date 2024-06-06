package com.example.hoop_locater.dto.hoop

data class HoopUpdateRequest(
    val id: Int,
    val name: String,
    val hoopCount: Int?,
    val floorType: String,
    val light: String
)