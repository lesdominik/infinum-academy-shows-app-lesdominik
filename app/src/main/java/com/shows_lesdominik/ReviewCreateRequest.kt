package com.shows_lesdominik

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ReviewCreateRequest(
    @SerialName("rating") val rating: Int,
    @SerialName("comment") val comment: String?,
    @SerialName("show_id") val showId: Int
)
