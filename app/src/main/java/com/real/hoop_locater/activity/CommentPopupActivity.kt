package com.real.hoop_locater.activity

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.real.hoop_locater.BuildConfig
import com.real.hoop_locater.R
import com.real.hoop_locater.adapter.CommentRVAdapter
import com.real.hoop_locater.app.App
import com.real.hoop_locater.app.App.Companion.prefs
import com.real.hoop_locater.app.App.Companion.retrofitService
import com.real.hoop_locater.app.AuthApp
import com.real.hoop_locater.databinding.ActivityCommentPopupBinding
import com.real.hoop_locater.dto.ResponseDto
import com.real.hoop_locater.dto.auth.User
import com.real.hoop_locater.dto.hoop.Comment
import com.real.hoop_locater.dto.hoop.Hoop
import com.real.hoop_locater.dto.page.Page
import com.real.hoop_locater.web.RetrofitService
import com.real.hoop_locater.web.comment.CommentCreateRequest
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class CommentPopupActivity : AppCompatActivity() {

    lateinit var binding: ActivityCommentPopupBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_comment_popup)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding = ActivityCommentPopupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val displayMetrics = applicationContext.resources.displayMetrics
        window.attributes.width = (displayMetrics.widthPixels * 0.91).toInt()

        val hoop = intent.getSerializableExtra("hoop") as Hoop

        binding.commentTitle.text = "<" + hoop.name + "> 추가정보"

        // 내 id 입력하기
        val accessToken = prefs.getAccessToken()
        var alertDialog = AlertDialog.Builder(this)
        if (accessToken != null && accessToken != "") {
            retrofitService.myInfo().enqueue(object : Callback<ResponseDto<User>> {
                override fun onResponse(
                    call: Call<ResponseDto<User>>,
                    response: Response<ResponseDto<User>>
                ) {
                    if (response.body()?.data?.loginId != null) {
                        binding.writerView.text = response.body()?.data?.nickName.toString()
                    } else {
                        binding.writerView.text = App.prefs.getViewAnonymousId()
                        prefs.deleteAccessToken()
                        alertDialog.setMessage("로그아웃 되었습니다. 다시 로그인해 주세요.")
                            .setPositiveButton("확인") { dialog, which ->
                            }
                            .show()
                    }
                }
                override fun onFailure(call: Call<ResponseDto<User>>, t: Throwable) {
                    binding.writerView.text = App.prefs.getViewAnonymousId()
                    prefs.deleteAccessToken()
                    alertDialog.setMessage("로그아웃 되었습니다. 다시 로그인해 주세요.")
                        .setPositiveButton("확인") { dialog, which ->
                        }
                        .show()
                }
            })
        } else {
            binding.writerView.text = App.prefs.getViewAnonymousId()
        }

        binding.commentCountView.text = "댓글 수 " + hoop.commentCount + "개"

        // 해당 농구장의 댓글 리스트 불러오기
        val recyclerView = findViewById<RecyclerView>(R.id.commentRv)
        setCommentList(hoop, recyclerView)

        // 댓글쓰기
        binding.writeBtn.setOnClickListener {

            if (binding.commentInput.text.trim().toString().length == 0) {
                Toast.makeText(this, "댓글 내용을 입력해 주세요.", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            AlertDialog.Builder(this)
                .setMessage("댓글을 입력하시겠습니까? 입력한 댓글은 변경할 수 없습니다.")
                .setPositiveButton("확인") { dialog, which ->
                    retrofitService.createComment(
                        CommentCreateRequest(
                            hoop.id,
                            binding.commentInput.text.toString()
                        )
                    ).enqueue(object : Callback<ResponseDto<Comment>> {
                        override fun onResponse(
                            call: Call<ResponseDto<Comment>>,
                            response: Response<ResponseDto<Comment>>
                        ) {
                            setCommentList(hoop, recyclerView)
                            binding.commentInput.setText(null)
                        }

                        override fun onFailure(call: Call<ResponseDto<Comment>>, t: Throwable) {
                            Toast.makeText(
                                this@CommentPopupActivity,
                                "댓글 작성에 실패했습니다. 잠시 후 다시 시도해 주세요.",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    })
                }
                .setNegativeButton("취소") { dialog, which ->

                }
                .show()

        }

        // 뒤로가기
        binding.backBtn.setOnClickListener {
            this.finish()
        }

    }

    private fun setCommentList(
        hoop: Hoop,
        recyclerView: RecyclerView
    ) {
        retrofitService.getCommentList(hoop.id)
            .enqueue(object : Callback<ResponseDto<Page<Comment>>> {
                override fun onResponse(
                    call: Call<ResponseDto<Page<Comment>>>,
                    response: Response<ResponseDto<Page<Comment>>>
                ) {
                    if (response.body()?.data?.totalElements == 0) {
                        binding.commentRvLayout.visibility = View.GONE
                        binding.noCommentLayout.visibility = View.VISIBLE
                    } else {
                        binding.noCommentLayout.visibility = View.GONE
                        binding.commentRvLayout.visibility = View.VISIBLE
                        recyclerView.adapter =
                            CommentRVAdapter(baseContext, response.body()!!.data!!.content)
                        recyclerView.layoutManager = LinearLayoutManager(this@CommentPopupActivity)
                        binding.commentCountView.text =
                            "댓글 수 " + response.body()!!.data!!.totalElements + "개"
                    }
                }

                override fun onFailure(call: Call<ResponseDto<Page<Comment>>>, t: Throwable) {
                    Toast.makeText(
                        this@CommentPopupActivity,
                        "댓글 불러오기에 실패했습니다. 잠시 후 다시 시도해 주세요.",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
    }
}