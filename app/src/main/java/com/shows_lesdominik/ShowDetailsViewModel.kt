package com.shows_lesdominik

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ShowDetailsViewModel : ViewModel() {

    private val reviews = emptyList<Review>()

    private val _reviewsLiveData = MutableLiveData<List<Review>>()
    val reviewsLiveData: LiveData<List<Review>> = _reviewsLiveData

    private val _usernameLiveData = MutableLiveData<String>()
    val usernameLiveData: LiveData<String> = _usernameLiveData

    private val _showDetailsLiveData = MutableLiveData<Show>()
    val showDetailsLiveData: LiveData<Show> = _showDetailsLiveData

    init {
        _reviewsLiveData.value = reviews
    }

    fun getUsername(email: String) {
        val splitEmail = email.split("@")
        _usernameLiveData.value = splitEmail[0]
    }

    fun getShowDetails(showId: String) {
        ApiModule.retrofit.getShowDetails(showId)
            .enqueue(object: Callback<ShowDetailsResponse> {
                override fun onResponse(call: Call<ShowDetailsResponse>, response: Response<ShowDetailsResponse>) {
                    _showDetailsLiveData.value = response.body()?.show
                }

                override fun onFailure(call: Call<ShowDetailsResponse>, t: Throwable) {
                    TODO("Not yet implemented")
                }

            })
    }
}