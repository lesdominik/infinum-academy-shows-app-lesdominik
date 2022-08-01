package com.shows_lesdominik

import android.content.Context
import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        ShowEntity::class,
        ReviewEntity::class
    ],
    version = 1
)
abstract class ShowsDatabase : RoomDatabase() {

    companion object {

        @Volatile
        private var INSTANCE: ShowsDatabase? = null

        fun getDatabase(context: Context): ShowsDatabase? {
            return INSTANCE ?: synchronized(this) {
                //TODO: instantiate database
                null
            }
        }
    }

    abstract fun showDao(): ShowDao
}