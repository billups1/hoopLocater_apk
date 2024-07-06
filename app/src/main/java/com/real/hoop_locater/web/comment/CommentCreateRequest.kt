package com.real.hoop_locater.web.comment

data class CommentCreateRequest(
    val writer: String?,
    val hoopId: Long,
    val content: String
)