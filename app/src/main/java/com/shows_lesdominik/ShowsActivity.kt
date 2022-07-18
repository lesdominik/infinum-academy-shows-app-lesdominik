package com.shows_lesdominik

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.shows_lesdominik.databinding.ActivityShowsBinding

class ShowsActivity : AppCompatActivity() {

    lateinit var binding: ActivityShowsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityShowsBinding.inflate(layoutInflater)

        setContentView(binding.root)
    }
}