package com.shows_lesdominik

import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterViewModel : ViewModel() {

    private val _registrationResultLiveData = MutableLiveData<Boolean>()
    val registrationResultLiveData: LiveData<Boolean> = _registrationResultLiveData

    private val _isRegisterButtonEnabledLiveData = MutableLiveData<Boolean>()
    val isRegisterButtonEnabledLiveData: LiveData<Boolean> = _isRegisterButtonEnabledLiveData

    private val _emailErrorLiveData = MutableLiveData<Int?>()
    val emailErrorLiveData: LiveData<Int?> = _emailErrorLiveData

    private val _passwordErrorLiveData = MutableLiveData<Int?>()
    val passwordErrorLiveData: LiveData<Int?> = _passwordErrorLiveData

    private val _repeatPasswordErrorLiveData = MutableLiveData<Int?>()
    val repeatPasswordErrorLiveData: LiveData<Int?> = _repeatPasswordErrorLiveData

    private var emailCorrect = false
    private var passwordCorrect = false
    private var passwordRepeatCorrect = false

    private var correctPassword: String = ""
    private var correctPasswordRepeat: String = ""

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

    fun emailValidation(email: String) {
        when {
            email.isEmpty() -> {
                _emailErrorLiveData.value = null
                emailCorrect = false
            }
            Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                _emailErrorLiveData.value = null
                emailCorrect = true
            }
            else -> {
                _emailErrorLiveData.value = R.string.invalid_email_address
                emailCorrect = false
            }
        }
        _isRegisterButtonEnabledLiveData.value = emailCorrect && passwordCorrect && passwordRepeatCorrect
    }

    fun passwordValidation(password: String) {
        when {
            password.isEmpty() -> {
                _passwordErrorLiveData.value = null
                passwordCorrect = false
            }
            password.length < 6 -> {
                _passwordErrorLiveData.value = R.string.password_length_message
                passwordCorrect = false
            }
            correctPasswordRepeat.isNotEmpty() && password != correctPasswordRepeat -> {
                _repeatPasswordErrorLiveData.value = R.string.passwords_not_matching
                passwordCorrect = true
                passwordRepeatCorrect = false
                correctPassword = password
            }
            else -> {
                correctPassword = password
                _passwordErrorLiveData.value = null
                passwordCorrect = true
            }
        }
        _isRegisterButtonEnabledLiveData.value = emailCorrect && passwordCorrect && passwordRepeatCorrect
    }

    fun repeatPasswordValidation(repeatPassword: String) {
        when {
            repeatPassword.isEmpty() -> {
                _repeatPasswordErrorLiveData.value = null
                passwordRepeatCorrect = false
            }
            repeatPassword != correctPassword -> {
                _repeatPasswordErrorLiveData.value = R.string.passwords_not_matching
                passwordRepeatCorrect = false
            }
            else -> {
                correctPasswordRepeat = repeatPassword
                _repeatPasswordErrorLiveData.value = null
                passwordRepeatCorrect = true
            }
        }
        _isRegisterButtonEnabledLiveData.value = emailCorrect && passwordCorrect && passwordRepeatCorrect
    }
}