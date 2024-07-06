package com.real.hoop_locater.dto.hoop

import java.io.Serializable

data class Hoop(
    val id: Long,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val hoopCount: Int,
    val floorType: FloorType,
    val light: Light,
    val freeState: FreeState,
    val standardState: StandardState,
    val commentCount: Int
) : Serializable