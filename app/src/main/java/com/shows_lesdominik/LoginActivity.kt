package com.shows_lesdominik

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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
            val intent = Intent(this, WelcomeActivity::class.java)
            intent.putExtra("EMAIL", binding.emailTextField.editText?.text.toString())
            startActivity(intent)
        }

        var emailCorrect = false
        var passwordCorrect = false
        val emailPattern = Regex("""^.+@.+\..+$""")

        binding.emailTextField.editText?.doAfterTextChanged {
            if (it.toString().isEmpty()) {
                binding.emailTextField.error = null
                binding.loginButton.isEnabled = false
                emailCorrect = false
            } else if (emailPattern.matches(it.toString())) {
                binding.emailTextField.error = null
                emailCorrect = true
                if (passwordCorrect) binding.loginButton.isEnabled = true else binding.loginButton.isEnabled = false
            } else {
                binding.emailTextField.error = "Invalid email address"
                binding.loginButton.isEnabled = false
                emailCorrect = false
            }
        }

        binding.passwordTextField.editText?.doAfterTextChanged {
            if (it.toString().isEmpty()) {
                binding.passwordTextField.error = null
                binding.loginButton.isEnabled = false
                passwordCorrect = false
            } else if (it.toString().length < 6) {
                binding.passwordTextField.error = "Minimum password length is 6 characters"
                binding.loginButton.isEnabled = false
                passwordCorrect = false
            } else {
                binding.passwordTextField.error = null
                passwordCorrect = true
                if (emailCorrect) binding.loginButton.isEnabled = true else binding.loginButton.isEnabled = false
            }
        }


    }
}