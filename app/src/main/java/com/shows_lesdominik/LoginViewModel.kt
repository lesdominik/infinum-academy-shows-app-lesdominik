package com.shows_lesdominik

import android.content.Context
import android.content.SharedPreferences
import android.util.Patterns
import androidx.core.content.edit
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private const val REMEMBER_ME_CHECKED = "REMEMBER_ME_CHECKED"
private const val USER_EMAIL = "USER_EMAIL"
private const val CLIENT = "CLIENT"
private const val ACCESS_TOKEN = "ACCESS_TOKEN"

class LoginViewModel : ViewModel() {

    private val _loginResultLiveData = MutableLiveData<Boolean>()
    val loginResultLiveData: LiveData<Boolean> = _loginResultLiveData

    private val _userEmailLiveData = MutableLiveData<String>()
    val userEmailLiveData: LiveData<String> = _userEmailLiveData

    private val _isLoginButtonEnabledLiveData = MutableLiveData<Boolean>()
    val isLoginButtonEnabledLiveData: LiveData<Boolean> = _isLoginButtonEnabledLiveData

    private var emailNotEmpty = false
    private var passwordNotEmpty = false


    fun getUserEmail(sharedPreferences: SharedPreferences) {
        val rememberMeChecked = sharedPreferences.getBoolean("REMEMBER_ME_CHECKED", false)
        if (rememberMeChecked) {
            _userEmailLiveData.value = sharedPreferences.getString("USER_EMAIL", "")
        } else {
            _userEmailLiveData.value = ""
        }
    }

    fun setRememberMeChecked(sharedPreferences: SharedPreferences, isChecked: Boolean) {
        sharedPreferences.edit {
            putBoolean(REMEMBER_ME_CHECKED, isChecked)
        }
    }

    fun onLoginButtonClicked(sharedPreferences: SharedPreferences, email: String, password: String, context: Context) {
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
                        putString(USER_EMAIL, response.body()?.user?.email)
                    }
                }

                override fun onFailure(call: Call<LoginAndRegisterResponse>, t: Throwable) {
                    _loginResultLiveData.value = false
                }

            })
    }

    fun emailValidation(email: String) {
        emailNotEmpty = Patterns.EMAIL_ADDRESS.matcher(email).matches()
        _isLoginButtonEnabledLiveData.value = emailNotEmpty && passwordNotEmpty
    }

    fun passwordValidation(password: String) {
        passwordNotEmpty = password.isNotEmpty()
        _isLoginButtonEnabledLiveData.value = emailNotEmpty && passwordNotEmpty
    }
}