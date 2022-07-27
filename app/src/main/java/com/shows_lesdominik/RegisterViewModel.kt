package com.shows_lesdominik

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterViewModel : ViewModel() {

    private val _registrationResultLiveData = MutableLiveData<Boolean>()
    val registrationResultLiveData: LiveData<Boolean> = _registrationResultLiveData

    fun onRegisterButtonClicked(email: String, password: String, passwordConfirmation: String) {
        val registerRequest = RegisterRequest(
            email = email,
            password = password,
            passwordConfirmation = passwordConfirmation
        )

        ApiModule.retrofit.register(registerRequest)
            .enqueue(object: Callback<LoginAndRegisterResponse> {
                override fun onResponse(call: Call<LoginAndRegisterResponse>, response: Response<LoginAndRegisterResponse>) {
                    _registrationResultLiveData.value = response.isSuccessful
                }

                override fun onFailure(call: Call<LoginAndRegisterResponse>, t: Throwable) {
                    _registrationResultLiveData.value = false
                }
            })
    }
}