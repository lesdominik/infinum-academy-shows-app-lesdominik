package com.shows_lesdominik

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.shows_lesdominik.databinding.ActivityShowDetailsBinding
import com.shows_lesdominik.databinding.ActivityShowsBinding

class ShowDetailsActivity : AppCompatActivity() {

    lateinit var binding: ActivityShowDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityShowDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
    }
}