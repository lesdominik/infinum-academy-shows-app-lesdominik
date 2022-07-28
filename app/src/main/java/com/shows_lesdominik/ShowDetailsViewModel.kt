package com.shows_lesdominik

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import model.Review

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
        val splittedEmail = email.split("@")
        _usernameLiveData.value = splittedEmail[0]
    }

//    fun setShowDetails(name: String, imageResourceId: Int, description: String) {
//        _showDetailsLiveData.value = Show(name, name, imageResourceId, description)
//    }
}