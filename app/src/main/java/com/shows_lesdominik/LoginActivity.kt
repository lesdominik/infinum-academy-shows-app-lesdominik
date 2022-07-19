package com.shows_lesdominik

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import androidx.core.view.isEmpty
import androidx.core.widget.doAfterTextChanged
import androidx.core.widget.doOnTextChanged
import com.shows_lesdominik.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)

        setContentView(binding.root)

        binding.loginButton.setOnClickListener {
            val intent = Intent(this, ShowsActivity::class.java)
            intent.putExtra("USERNAME",
                binding.emailEdiText.text?.substring(0, binding.emailEdiText.text!!.indexOf("@"))
            )
            startActivity(intent)
        }


        var emailCorrect = false
        var passwordCorrect = false

        binding.emailEdiText.doAfterTextChanged {

            when {
                it.toString().isEmpty() -> {
                    binding.emailTextField.error = null
                    emailCorrect = false
                }
                Patterns.EMAIL_ADDRESS.matcher(it.toString()).matches() -> {
                    binding.emailTextField.error = null
                    emailCorrect = true
                }
                else -> {
                    binding.emailTextField.error = "Invalid email address"
                    emailCorrect = false
                }
            }
            binding.loginButton.isEnabled = emailCorrect && passwordCorrect
        }

        binding.passwordEditText.doAfterTextChanged {

            when {
                it.toString().isEmpty() -> {
                    binding.passwordTextField.error = null
                    passwordCorrect = false
                }
                it.toString().length < 6 -> {
                    binding.passwordTextField.error = "Minimum password length is 6 characters"
                    passwordCorrect = false
                }
                else -> {
                    binding.passwordTextField.error = null
                    passwordCorrect = true
                }
            }
            binding.loginButton.isEnabled = emailCorrect && passwordCorrect
        }


    }
}