package com.real.hoop_locater.web.comment

data class CommentCreateRequest(
    val hoopId: Long,
    val content: String
)