package com.example.hoop_locater.dto.hoop

import java.io.Serializable

data class FloorType(
    val key: String,
    val krName: String,
    val order: Int
) : Serializable