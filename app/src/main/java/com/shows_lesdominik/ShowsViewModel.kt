package com.shows_lesdominik

import android.content.SharedPreferences
import androidx.core.content.edit
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.io.File
import java.io.IOException
import java.util.concurrent.Executors
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private const val REMEMBER_ME_CHECKED = "REMEMBER_ME_CHECKED"

class ShowsViewModel(private val database: ShowsDatabase) : ViewModel() {

    private val _showsLiveData = MutableLiveData<List<Show>>()
    val showsLiveData: LiveData<List<Show>> = _showsLiveData

    private val _userLiveData = MutableLiveData<User>()
    val userLiveData: LiveData<User> = _userLiveData

    fun setRememberMeChecked(sharedPreferences: SharedPreferences) {
        sharedPreferences.edit {
            putBoolean(REMEMBER_ME_CHECKED, false)
        }
    }

    fun getShows() {
        ApiModule.retrofit.getShows()
            .enqueue(object: Callback<ShowsResponse> {
                override fun onResponse(call: Call<ShowsResponse>, response: Response<ShowsResponse>) {
                    if (response.isSuccessful) {
                        response.body()?.let { initDatabase(it.shows) }
                        _showsLiveData.value = response.body()?.shows
                    } else {
                        _showsLiveData.value = emptyList()
                    }
                }

                override fun onFailure(call: Call<ShowsResponse>, t: Throwable) {
                    _showsLiveData.value = emptyList()
                }

            })
    }

    private fun initDatabase(shows: List<Show>) {
        Executors.newSingleThreadExecutor().execute {
            database.showDao().insertShows(shows.map { show ->
                ShowEntity(show.id, show.averageRating, show.description, show.imageUrl, show.noOfReviews, show.title)
            })
        }
    }

    fun getShowsFromDatabase(): LiveData<List<ShowEntity>> {
        return database.showDao().getAllShows()
    }

    fun getUserInfo() {
        ApiModule.retrofit.getUserInfo()
            .enqueue(object: Callback<UserInfoResponse> {
                override fun onResponse(call: Call<UserInfoResponse>, response: Response<UserInfoResponse>) {
                    if (response.isSuccessful) {
                        _userLiveData.value = response.body()?.user
                    }
                    _userLiveData.value = null
                }

                override fun onFailure(call: Call<UserInfoResponse>, t: Throwable) {
                    _userLiveData.value = null
                }

            })
    }

    fun setProfileImage(file: File) {
        val multipartBody = MultipartBody.Part.createFormData("image", "avatar.jpg",
            file.asRequestBody("multipart/form-data".toMediaType()))

        ApiModule.retrofit.storeUserImage(multipartBody)
            .enqueue(object: Callback<StoreImageResponse> {
                override fun onResponse(call: Call<StoreImageResponse>, response: Response<StoreImageResponse>) {
                    if (response.isSuccessful) {
                        Log.i("PUT successful?","image stored successfully")
                    } else {
                        Log.i("PUT successful?","nope")
                    }
                }

                override fun onFailure(call: Call<StoreImageResponse>, t: Throwable) {
                    Log.i("PUT successful?","nope")
                }

            })
    }
}