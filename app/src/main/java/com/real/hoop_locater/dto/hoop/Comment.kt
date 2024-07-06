package com.real.hoop_locater.dto.hoop

import java.io.Serializable

data class Comment(
    val id: Long,
    val writer: String,
    val hoopId: Long,
    val content: String,
    val writeDate: String
) : Serializable