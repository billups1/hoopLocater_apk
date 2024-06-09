package com.real.hoop_locater.dto.report.request

data class ReportCreateRequest(
    val hoopId: Int,
    val reason: String,
    val loginId : String?
)