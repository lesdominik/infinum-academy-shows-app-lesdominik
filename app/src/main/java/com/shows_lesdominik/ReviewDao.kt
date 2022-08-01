package com.shows_lesdominik

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ReviewDao {

    @Insert
    fun createReview(review: ReviewEntity)

    @Query("SELECT * FROM reviews")
    fun getAllReviews(): LiveData<List<ReviewEntity>>

    @Query("SELECT * FROM reviews WHERE id is :reviewId")
    fun getReview(reviewId: String)
}