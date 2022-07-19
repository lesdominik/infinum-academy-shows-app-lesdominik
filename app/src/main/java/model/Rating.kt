package model

import androidx.annotation.DrawableRes

data class Rating(
    val user: String,
    @DrawableRes val profileImageResourceId: Int,
    val comment: String
)
