package com.shows_lesdominik

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LoginAndRegisterResponse(
    @SerialName("user") val user: User
)