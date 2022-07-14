package com.shows_lesdominik

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.shows_lesdominik.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)

        setContentView(binding.root)

        binding.loginButton.setOnClickListener {
            val intent = Intent(this, WelcomeActivity::class.java)
            startActivity(intent)
        }

//        val emailPattern = Regex(""".+""")
//
//        if (binding.passwordTextField.editText?.text.toString().length >= 6) {
//            binding.loginButton.setBackgroundColor(Color.WHITE)
//            binding.loginButton.setTextColor(Color.BLACK)
//            binding.loginButton.isEnabled = true
//        }
    }
}