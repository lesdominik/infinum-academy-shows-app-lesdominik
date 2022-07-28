package com.shows_lesdominik

import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginViewModel : ViewModel() {

    private val _loginResultLiveData = MutableLiveData<Boolean>()
    val loginResultLiveData: LiveData<Boolean> = _loginResultLiveData

    private lateinit var sharedPreferences: SharedPreferences

    fun onLoginButtonClicked(email: String, password: String) {
        val loginRequest = LoginRequest(
            email = email,
            password = password
        )

        ApiModule.retrofit.signIn(loginRequest)
            .enqueue(object: Callback<LoginAndRegisterResponse> {
                override fun onResponse(call: Call<LoginAndRegisterResponse>, response: Response<LoginAndRegisterResponse>) {
                    _loginResultLiveData.value = response.isSuccessful
                    sharedPreferences.edit {
                        putString("ACCESS_TOKEN", response.headers()["access-token"])
                    }
                }

                override fun onFailure(call: Call<LoginAndRegisterResponse>, t: Throwable) {
                    _loginResultLiveData.value = false
                }

            })
    }
}