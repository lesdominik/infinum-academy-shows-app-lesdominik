package com.shows_lesdominik

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.shows_lesdominik.databinding.ActivityWelcomeBinding

class WelcomeActivity : AppCompatActivity() {

    lateinit var binding: ActivityWelcomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityWelcomeBinding.inflate(layoutInflater)

        setContentView(binding.root)

        val splitEmail = intent.extras?.getString("EMAIL")?.split("@")
        binding.welcomeText.text = "Welcome, " + (splitEmail?.get(0) ?: "")
    }
}