package com.example.hoop_locater.dto.hoop

data class Hoop(
    val floorType: FloorType,
    val hoopCount: Int,
    val id: Int,
    val latitude: Double,
    val light: Light,
    val longitude: Double,
    val name: String
)