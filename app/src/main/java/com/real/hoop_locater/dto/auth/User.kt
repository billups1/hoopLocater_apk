package com.real.hoop_locater.dto.auth

import java.io.Serializable

data class User(
    val id: Long,
    val loginId: String,
    val nickName: String
) : Serializable