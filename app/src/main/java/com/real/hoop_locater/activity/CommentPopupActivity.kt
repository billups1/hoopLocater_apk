package com.real.hoop_locater.activity

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.real.hoop_locater.BuildConfig
import com.real.hoop_locater.R
import com.real.hoop_locater.adapter.CommentRVAdapter
import com.real.hoop_locater.databinding.ActivityCommentPopupBinding
import com.real.hoop_locater.dto.ResponseDto
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

        binding.commentTitle.text = "<"+hoop.name+"> 의 추가정보"
        binding.writerView.text = getSharedPreferences("sp1", MODE_PRIVATE).getString("anonymousLogin", null)
        binding.commentCountView.text = "댓글 수 " + hoop.commentCount + "개"

        val retrofit = Retrofit.Builder().baseUrl(BuildConfig.API_URL)
            .addConverterFactory(GsonConverterFactory.create()).build()
        val service = retrofit.create(RetrofitService::class.java)

        val recyclerView = findViewById<RecyclerView>(R.id.commentRv)

        setCommentList(service, hoop, recyclerView)

        binding.writeBtn.setOnClickListener {
            service.createComment(
                CommentCreateRequest(
                    getSharedPreferences("sp1", MODE_PRIVATE).getString("anonymousLogin", null),
                    hoop.id,
                    binding.commentInput.text.toString()
                )
            ).enqueue(object : Callback<ResponseDto<Comment>> {
                override fun onResponse(
                    call: Call<ResponseDto<Comment>>,
                    response: Response<ResponseDto<Comment>>
                ) {
                    setCommentList(service, hoop, recyclerView)
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

        binding.backBtn.setOnClickListener {
            this.finish()
        }

    }

    private fun setCommentList(
        service: RetrofitService,
        hoop: Hoop,
        recyclerView: RecyclerView
    ) {
        service.getCommentList(hoop.id)
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
                        recyclerView.adapter = CommentRVAdapter(baseContext, response.body()!!.data!!.content)
                        recyclerView.layoutManager = LinearLayoutManager(this@CommentPopupActivity)
                        binding.commentCountView.text = "댓글 수 " + response.body()!!.data!!.totalElements + "개"
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