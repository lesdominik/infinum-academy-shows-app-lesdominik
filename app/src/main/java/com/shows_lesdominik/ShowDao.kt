package com.shows_lesdominik

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ShowDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertShows(shows: List<ShowEntity>)

    @Query("SELECT * FROM shows")
    fun getAllShows() : LiveData<List<ShowEntity>>

    @Query("SELECT * FROM shows WHERE id IS :showId")
    fun getShow(showId: String) : LiveData<ShowEntity>
}