package com.shows_lesdominik

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ReviewCreateResponse(
    @SerialName("review") val review: Review
)
