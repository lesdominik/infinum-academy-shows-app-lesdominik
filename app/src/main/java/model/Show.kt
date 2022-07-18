package model

import androidx.annotation.DrawableRes

data class Show(
    val id: String,
    val name: String,
    @DrawableRes val imageResourceId: Int,
    val description: String = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor"
)