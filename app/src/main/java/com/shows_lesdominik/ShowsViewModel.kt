package com.shows_lesdominik

import android.content.SharedPreferences
import android.net.Uri
import androidx.core.content.edit
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import model.Show

private const val URI = "URI"
private const val REMEMBER_ME_CHECKED = "REMEMBER_ME_CHECKED"

class ShowsViewModel : ViewModel() {

    private val shows = listOf(
        Show("theOffice","The Office", R.drawable.the_office),
        Show("strangerThings","Stranger Things", R.drawable.stranger_things),
        Show("krvNijeVoda","Krv nije voda", R.drawable.krv_nije_voda)
    )

    private val _showsLiveData = MutableLiveData<List<Show>>()
    val showsLiveData: LiveData<List<Show>> = _showsLiveData

    private val _latestTmpUri = MutableLiveData<Uri?>()
    val latestTmpUri: LiveData<Uri?> = _latestTmpUri

    init {
        _showsLiveData.value = shows
    }

    fun getLatestTempUri(sharedPreferences: SharedPreferences) {
        val rememberMe = sharedPreferences.getBoolean(REMEMBER_ME_CHECKED, false)
        if (!rememberMe) {
            sharedPreferences.edit {
                remove(URI)
            }
        }
        var getUriString = sharedPreferences.getString(URI, null)
        if (getUriString == null) {
            _latestTmpUri.value = null
        } else {
            _latestTmpUri.value = Uri.parse(getUriString)
        }
    }

    fun setRememberMeToFalse(sharedPreferences: SharedPreferences) {
        sharedPreferences.edit {
            putBoolean(REMEMBER_ME_CHECKED, false)
            remove(URI)
        }
    }

    fun storeImageUri(sharedPreferences: SharedPreferences, uri: Uri) {
        sharedPreferences.edit {
            putString(URI, uri.toString())
        }
    }
}