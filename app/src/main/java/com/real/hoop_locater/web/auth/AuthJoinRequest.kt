package com.real.hoop_locater.web.auth

data class AuthJoinRequest(
    val loginId: String,
    val nickName: String,
    val password: String,
    val passwordRecheck: String
)