package com.real.hoop_locater.dto

import java.io.Serializable

data class ResponseDto<T>(
    val code: String,
    val data: T,
    val message: String
) : Serializable