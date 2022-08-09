package com.shows_lesdominik

import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

private const val REMEMBER_ME_CHECKED = "REMEMBER_ME_CHECKED"
private const val USER_EMAIL = "USER_EMAIL"

class LoginViewModel : ViewModel() {

    private val _userEmail = MutableLiveData<String>()
    val userEmail: LiveData<String> = _userEmail

    fun getUserEmail(sharedPreferences: SharedPreferences) {
        val rememberMeChecked = sharedPreferences.getBoolean(REMEMBER_ME_CHECKED, false)
        if (rememberMeChecked) {
            _userEmail.value = sharedPreferences.getString(USER_EMAIL, "")
        }
    }

    fun setRememberMeChecked(sharedPreferences: SharedPreferences, isChecked: Boolean) {
        sharedPreferences.edit {
            putBoolean(REMEMBER_ME_CHECKED, isChecked)
        }
    }

    fun storeUserEmail(sharedPreferences: SharedPreferences, userEmail: String) {
        sharedPreferences.edit {
            putString(USER_EMAIL, userEmail)
        }
    }
}