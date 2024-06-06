package com.example.hoop_locater.dto.hoop

import java.io.Serializable

data class Light(
    val key: String,
    val krName: String,
    val order: Int
) : Serializable