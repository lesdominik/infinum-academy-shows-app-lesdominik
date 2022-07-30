package com.shows_lesdominik

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ShowDetailsViewModel : ViewModel() {

    private val _reviewsLiveData = MutableLiveData<List<Review>>()
    val reviewsLiveData: LiveData<List<Review>> = _reviewsLiveData

    private val _newReviewLiveData = MutableLiveData<Review>()
    val newReviewLiveData: LiveData<Review> = _newReviewLiveData

    private val _showDetailsLiveData = MutableLiveData<Show>()
    val showDetailsLiveData: LiveData<Show> = _showDetailsLiveData


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

    fun getShowReviews(showId: String) {
        ApiModule.retrofit.getShowReviews(showId)
            .enqueue(object: Callback<ReviewListResponse> {
                override fun onResponse(call: Call<ReviewListResponse>, response: Response<ReviewListResponse>) {
                    if (response.isSuccessful) {
                        _reviewsLiveData.value = response.body()?.reviews
                    } else {
                        _reviewsLiveData.value = emptyList()
                    }
                }

                override fun onFailure(call: Call<ReviewListResponse>, t: Throwable) {
                    _reviewsLiveData.value = emptyList()
                }

            })
    }

    fun addReview(rating: Int, comment: String?, showId: String) {
        val reviewCreateRequest = ReviewCreateRequest(
            rating = rating,
            comment = comment,
            showId = showId.toInt()
        )

        ApiModule.retrofit.createShowReview(reviewCreateRequest)
            .enqueue(object: Callback<ReviewCreateResponse> {
                override fun onResponse(call: Call<ReviewCreateResponse>, response: Response<ReviewCreateResponse>) {
                    if (response.isSuccessful) {
                        _newReviewLiveData.value = response.body()?.review
                    }
                    _newReviewLiveData.value = null
                }

                override fun onFailure(call: Call<ReviewCreateResponse>, t: Throwable) {
                    _newReviewLiveData.value = null
                }

            })
    }
}