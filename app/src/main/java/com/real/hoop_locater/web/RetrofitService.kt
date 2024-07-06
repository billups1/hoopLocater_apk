package com.real.hoop_locater.web

import com.real.hoop_locater.dto.ResponseDto
import com.real.hoop_locater.dto.hoop.Comment
import com.real.hoop_locater.dto.hoop.Hoop
import com.real.hoop_locater.web.hoop.HoopCreateRequest
import com.real.hoop_locater.dto.hoop.HoopList
import com.real.hoop_locater.dto.page.Page
import com.real.hoop_locater.web.comment.CommentCreateRequest
import com.real.hoop_locater.web.comment.CommentDeleteRequest
import com.real.hoop_locater.web.report.ReportCreateRequest
import com.real.hoop_locater.web.hoop.HoopUpdateRequest
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface RetrofitService {

    // 농구장
    @GET("api/v1/hoop/all")
    fun getHoopList(): Call<ResponseDto<HoopList>>

    @GET("api/v1/hoop/{hoopId}")
    fun getHoop(@Path(value = "hoopId") hoopId: Long): Call<ResponseDto<Hoop>>

    @POST("api/v1/hoop/create")
    fun createHoop(@Body hoopCreateRequest: HoopCreateRequest): Call<ResponseDto<Hoop>>

    @PUT("api/v1/hoop/update")
    fun updateHoop(@Body hoopUpdateRequest: HoopUpdateRequest): Call<ResponseDto<Hoop>>

    // 신고
    @POST("api/v1/report/create")
    fun reportHoop(@Body reportCreateRequest: ReportCreateRequest): Call<ResponseDto<Int>>


    // 댓글
    @GET("api/v1/comment/{hoopId}")
    fun getCommentList(@Path(value = "hoopId") hoopId: Long): Call<ResponseDto<Page<Comment>>>

    @POST("api/v1/comment")
    fun createComment(@Body commentCreateRequest: CommentCreateRequest): Call<ResponseDto<Comment>>

    @DELETE("api/v1/comment")
    fun deleteComment(@Body commentDeleteRequest: CommentDeleteRequest): Call<ResponseDto<String>>

}