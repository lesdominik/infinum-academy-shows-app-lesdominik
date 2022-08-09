package com.shows_lesdominik

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import model.Review
import model.Show

private const val URI = "URI"

class ShowDetailsViewModel : ViewModel() {

    private val reviews = emptyList<Review>()

    private val _reviewsLiveData = MutableLiveData<List<Review>>()
    val reviewsLiveData: LiveData<List<Review>> = _reviewsLiveData

    private val _createdReviewLiveData = MutableLiveData<Review>()
    val createdReviewLiveData: LiveData<Review> = _createdReviewLiveData

    private val _showDetailsLiveData = MutableLiveData<Show>()
    val showDetailsLiveData: LiveData<Show> = _showDetailsLiveData


    init {
        _reviewsLiveData.value = reviews
    }

    fun setShowDetails(name: String, imageResourceId: Int, description: String) {
        _showDetailsLiveData.value = Show(name, name, imageResourceId, description)
    }

    fun createReview(sharedPreferences: SharedPreferences, userEmail: String, rating: Int, comment: String?){
        var userImageUri = sharedPreferences.getString(URI, null)
        if (userImageUri.isNullOrEmpty()) {
            userImageUri = "android.resource://com.shows_lesdominik/" + R.drawable.default_user
        }

        val splitEmail = userEmail.split("@")
        val username = splitEmail[0]

        _createdReviewLiveData.value = Review(username, userImageUri, rating, comment)
    }
}