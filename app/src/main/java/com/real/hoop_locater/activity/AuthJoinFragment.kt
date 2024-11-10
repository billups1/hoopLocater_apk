package com.real.hoop_locater.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.real.hoop_locater.BuildConfig.API_URL
import com.real.hoop_locater.R
import com.real.hoop_locater.app.App.Companion.retrofitService
import com.real.hoop_locater.databinding.FragmentAuthJoinBinding
import com.real.hoop_locater.dto.ResponseDto
import com.real.hoop_locater.dto.auth.User
import com.real.hoop_locater.app.AuthInterceptor
import com.real.hoop_locater.web.RetrofitService
import com.real.hoop_locater.web.auth.AuthJoinRequest
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.regex.Pattern

class AuthJoinFragment : Fragment() {

    lateinit var binding: FragmentAuthJoinBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentAuthJoinBinding.inflate(inflater, container, false)

        var builder = OkHttpClient().newBuilder()
        var okHttpClient = builder
            .addInterceptor(AuthInterceptor())
            .build()

        // 로그인아이디 중복체크 버튼
        var duplicationChockOkId = ""
        binding.idDuplicationCheckBtnJoin.setOnClickListener {

            var loginId = binding.loginidInputJoin.text.toString().trim()

            if (loginId.length == 0) {
                Toast.makeText(activity, "아이디를 입력해 주시기 바랍니다.", Toast.LENGTH_LONG).show()
                binding.idDuplicationInfoFailJoin.visibility = View.GONE
                binding.idDuplicationInfoOkJoin.visibility = View.GONE
                return@setOnClickListener
            }

            retrofitService.loginIdDuplicationCheck(loginId)
                .enqueue(object : Callback<ResponseDto<Boolean>> {
                    override fun onResponse(
                        call: Call<ResponseDto<Boolean>>,
                        response: Response<ResponseDto<Boolean>>
                    ) {
                        if (response.body()?.code.equals("success")) {
                            if (response.body()?.data == false) {
                                duplicationChockOkId = loginId
                                binding.idDuplicationInfoFailJoin.visibility = View.GONE
                                binding.idDuplicationInfoOkJoin.visibility = View.VISIBLE
                            } else {
                                binding.idDuplicationInfoFailJoin.visibility = View.VISIBLE
                                binding.idDuplicationInfoOkJoin.visibility = View.GONE
                            }
                        }
                    }

                    override fun onFailure(call: Call<ResponseDto<Boolean>>, t: Throwable) {
                        Toast.makeText(activity, "요청에 실패했습니다.", Toast.LENGTH_LONG).show()
                    }
                })
        }

        // 닉네임 중복체크 버튼
        var duplicationChockOkNickName = ""
        binding.nickNameDuplicationCheckBtnJoin.setOnClickListener {

            var nickname = binding.nickNameInputJoin.text.toString().trim()

            if (nickname.length == 0) {
                Toast.makeText(activity, "닉네임을 입력해 주시기 바랍니다.", Toast.LENGTH_LONG).show()
                binding.nickNameDuplicationInfoFailJoin.visibility = View.GONE
                binding.nickNameDuplicationInfoOkJoin.visibility = View.GONE
                return@setOnClickListener
            }

            retrofitService.nickNameDuplicationCheck(nickname)
                .enqueue(object : Callback<ResponseDto<Boolean>> {
                    override fun onResponse(
                        call: Call<ResponseDto<Boolean>>,
                        response: Response<ResponseDto<Boolean>>
                    ) {
                        if (response.body()?.code.equals("success")) {
                            if (response.body()?.data == false) {
                                duplicationChockOkNickName = nickname
                                binding.nickNameDuplicationInfoFailJoin.visibility = View.GONE
                                binding.nickNameDuplicationInfoOkJoin.visibility = View.VISIBLE
                            } else {
                                binding.nickNameDuplicationInfoFailJoin.visibility = View.VISIBLE
                                binding.nickNameDuplicationInfoOkJoin.visibility = View.GONE
                            }
                        }
                    }

                    override fun onFailure(call: Call<ResponseDto<Boolean>>, t: Throwable) {
                        Toast.makeText(activity, "요청에 실패했습니다.", Toast.LENGTH_LONG).show()
                    }
                })
        }

        // 회원가입 버튼
        binding.joinBtnJoin.setOnClickListener {

            var loginId = binding.loginidInputJoin.text.toString().trim()
            var nickName = binding.nickNameInputJoin.text.toString().trim()
            var password = binding.passwordInputJoin.text.toString().trim()
            var passwordRecheck = binding.passwordInputRecheckJoin.text.toString().trim()

            if (loginId.length == 0) {
                Toast.makeText(activity, "아이디를 입력해 주시기 바랍니다.", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            val loginIdPattern = "^[A-Za-z0-9]{3,12}\$" // 영문, 숫자 3 ~ 20자 패턴
            if (!Pattern.compile(loginIdPattern).matcher(loginId).find()) {
                Toast.makeText(
                    activity,
                    "아이디는 영문과 숫자를 포함한 3 ~ 20자로 지정해 주시기 바랍니다.",
                    Toast.LENGTH_LONG
                ).show()
                return@setOnClickListener
            }

            if (nickName.length == 0) {
                Toast.makeText(activity, "닉네임을 입력해 주시기 바랍니다.", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            if (password.length == 0 || passwordRecheck.length == 0) {
                Toast.makeText(activity, "비밀번호를 입력해 주시기 바랍니다.", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            if (!password.equals(passwordRecheck)) {
                Toast.makeText(activity, "비밀번호 재확인 정보가 다릅니다.", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            if (!loginId.equals(duplicationChockOkId)) {
                Toast.makeText(activity, "아이디 중복확인 체크를 해주시기 바랍니다.", Toast.LENGTH_LONG)
                    .show() // 아이디 인풋값과 중복체크된 아이디가 다를 때
                return@setOnClickListener
            }

            if (!nickName.equals(duplicationChockOkNickName)) {
                Toast.makeText(activity, "닉네임 중복확인 체크를 해주시기 바랍니다.", Toast.LENGTH_LONG)
                    .show() // 닉네임 인풋값과 중복체크된 닉네임이 다를 때
                return@setOnClickListener
            }

            // 회원가입 요청
            retrofitService.join(
                AuthJoinRequest(
                    binding.loginidInputJoin.text.toString(),
                    binding.nickNameInputJoin.text.toString(),
                    binding.passwordInputJoin.text.toString(),
                    binding.passwordInputRecheckJoin.text.toString()
                )
            ).enqueue(object : Callback<ResponseDto<User>> {
                override fun onResponse(
                    call: Call<ResponseDto<User>>,
                    response: Response<ResponseDto<User>>
                ) {
                    it.findNavController()
                        .navigate(R.id.action_authJoinFragment_to_authLoginFragment)
                }

                override fun onFailure(call: Call<ResponseDto<User>>, t: Throwable) {
                    Toast.makeText(activity, "요청에 실패했습니다.", Toast.LENGTH_LONG).show()
                    return
                }

            })


        }

        // 뒤로가기 버튼
        binding.backBtnJoin.setOnClickListener {
            it.findNavController().navigate(R.id.action_authJoinFragment_to_authLoginFragment)
        }

        return binding.root
    }

}