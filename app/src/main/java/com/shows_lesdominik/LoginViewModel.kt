package com.shows_lesdominik

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private const val ACCESS_TOKEN = "ACCESS_TOKEN"
private const val CLIENT = "CLIENT"

class LoginViewModel : ViewModel() {

    private val _loginResultLiveData = MutableLiveData<Boolean>()
    val loginResultLiveData: LiveData<Boolean> = _loginResultLiveData

    private lateinit var sharedPreferences: SharedPreferences

    fun onLoginButtonClicked(email: String, password: String, context: Context) {
        sharedPreferences = context.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)

        val loginRequest = LoginRequest(
            email = email,
            password = password
        )

        ApiModule.retrofit.signIn(loginRequest)
            .enqueue(object: Callback<LoginAndRegisterResponse> {
                override fun onResponse(call: Call<LoginAndRegisterResponse>, response: Response<LoginAndRegisterResponse>) {
                    _loginResultLiveData.value = response.isSuccessful
                    sharedPreferences.edit {
                        putString(ACCESS_TOKEN, response.headers()["access-token"])
                        putString(CLIENT, response.headers()["client"])
                    }
                }

                override fun onFailure(call: Call<LoginAndRegisterResponse>, t: Throwable) {
                    _loginResultLiveData.value = false
                }

            })
    }
}