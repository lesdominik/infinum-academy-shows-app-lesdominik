package com.shows_lesdominik

import androidx.annotation.DrawableRes

data class Review(
    val user: String,
    val userImageUriString: String,
    val rating: Int,
    val comment: String?
)
