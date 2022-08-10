package com.shows_lesdominik

import android.app.Application

class ShowsApplication : Application() {

    val database by lazy {
        ShowsDatabase.getDatabase(this)
    }
}