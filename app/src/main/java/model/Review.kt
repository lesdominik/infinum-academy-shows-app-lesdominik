package model

import androidx.annotation.DrawableRes

data class Review(
    val user: String,
    @DrawableRes val profileImageResourceId: Int,
    val rating: Int,
    val comment: String?
)
