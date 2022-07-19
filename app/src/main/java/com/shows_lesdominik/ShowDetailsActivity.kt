package com.shows_lesdominik

import android.content.Intent
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

        binding.showTitle.text = intent.extras?.getString("NAME")
        intent.extras?.getInt("PICTURE")?.let { binding.fullImage.setImageResource(it) }

        binding.backArrow.setOnClickListener {
            val intent = Intent(this, ShowsActivity::class.java)
            startActivity(intent)
        }
    }
}