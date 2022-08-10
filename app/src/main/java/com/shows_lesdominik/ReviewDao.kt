package com.shows_lesdominik

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ReviewDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun createReviews(reviews: List<ReviewEntity>)

    @Query("SELECT * FROM reviews WHERE show_id IS :showId")
    fun getAllReviews(showId: String): LiveData<List<ReviewEntity>>

    @Query("SELECT * FROM reviews WHERE id is :reviewId")
    fun getReview(reviewId: String): LiveData<ReviewEntity>
}