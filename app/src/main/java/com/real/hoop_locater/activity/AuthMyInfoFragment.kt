package com.real.hoop_locater.activity

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.navigation.findNavController
import com.real.hoop_locater.R
import com.real.hoop_locater.app.App
import com.real.hoop_locater.app.App.Companion.retrofitService
import com.real.hoop_locater.databinding.FragmentAuthMyInfoBinding
import com.real.hoop_locater.dto.ResponseDto
import com.real.hoop_locater.dto.auth.User
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AuthMyInfoFragment : Fragment() {

    lateinit var binding: FragmentAuthMyInfoBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentAuthMyInfoBinding.inflate(inflater, container, false)

        retrofitService.myInfo().enqueue(object : Callback<ResponseDto<User>> {
            override fun onResponse(
                call: Call<ResponseDto<User>>,
                response: Response<ResponseDto<User>>
            ) {
                binding.idTextView.text = response.body()?.data?.loginId
                binding.nickNameTextView.text = response.body()?.data?.nickName
            }

            override fun onFailure(call: Call<ResponseDto<User>>, t: Throwable) {
                Toast.makeText(context, "요청에 실패했습니다.", Toast.LENGTH_LONG).show()
                return
            }
        })

//        binding.myHoopBtn.setOnClickListener {
//            Toast.makeText(context, "준비 중입니다.", Toast.LENGTH_LONG).show()
//            return@setOnClickListener
//        }

        // 메인화면으로 이동
        binding.moveToMainBtn.setOnClickListener {
            startActivity(Intent(activity, MainActivity::class.java))
            activity?.finish() // 현재 액티비티 종료하기
        }

        binding.logoutBtn.setOnClickListener {
            retrofitService.logout().enqueue(object : Callback<ResponseDto<Object>> {
                override fun onResponse(
                    call: Call<ResponseDto<Object>>,
                    response: Response<ResponseDto<Object>>
                ) {
                    App.prefs.deleteAccessToken()
                    it.findNavController().navigate(R.id.action_authMyInfoFragment_to_authLoginFragment)
                }

                override fun onFailure(call: Call<ResponseDto<Object>>, t: Throwable) {
                    Toast.makeText(context, "요청에 실패했습니다.", Toast.LENGTH_LONG).show()
                    return
                }
            })
        }

        return binding.root
    }
}