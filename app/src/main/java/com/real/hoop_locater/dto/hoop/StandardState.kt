package com.real.hoop_locater.dto.hoop

import java.io.Serializable

data class StandardState(
    val key: String,
    val krName: String,
    val order: Int
) : Serializable