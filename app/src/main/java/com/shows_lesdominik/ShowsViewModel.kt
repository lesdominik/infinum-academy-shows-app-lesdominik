package com.shows_lesdominik

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ShowsViewModel : ViewModel() {

    private val _showsLiveData = MutableLiveData<List<Show>>()
    val showsLiveData: LiveData<List<Show>> = _showsLiveData

    fun getShows() {
        ApiModule.retrofit.getShows()
            .enqueue(object: Callback<ShowsResponse> {
                override fun onResponse(call: Call<ShowsResponse>, response: Response<ShowsResponse>) {
                    if (response.isSuccessful) {
                        _showsLiveData.value = response.body()?.shows
                    }
                }

                override fun onFailure(call: Call<ShowsResponse>, t: Throwable) {
                    _showsLiveData.value = emptyList()
                }

            })
    }
}