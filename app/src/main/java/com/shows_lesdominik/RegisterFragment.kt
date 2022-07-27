package com.shows_lesdominik

import android.os.Bundle
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.doOnDetach
import androidx.core.widget.doAfterTextChanged
import com.shows_lesdominik.databinding.FragmentLoginBinding
import com.shows_lesdominik.databinding.FragmentRegisterBinding

class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() =_binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initListeners()
    }

    private fun initListeners() {
        var emailCorrect = false
        var passwordCorrect = false
        var passwordRepeatCorrect = false

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
            binding.registerButton.isEnabled = emailCorrect && passwordCorrect && passwordRepeatCorrect
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
            binding.registerButton.isEnabled = emailCorrect && passwordCorrect && passwordRepeatCorrect
        }


        binding.repeatPasswordEditText.doOnDetach {

            when {
                it.toString().isEmpty() -> {
                    binding.repeatPasswordTextField.error = null
                    passwordRepeatCorrect = false
                }
                it.toString() != binding.passwordEditText.text.toString() -> {
                    binding.repeatPasswordTextField.error = "Passwords don't match"
                    passwordRepeatCorrect = false
                }
                else -> {
                    binding.repeatPasswordTextField.error = null
                    passwordRepeatCorrect = true
                }
            }
            binding.registerButton.isEnabled = emailCorrect && passwordCorrect && passwordRepeatCorrect

        }


        binding.registerButton.setOnClickListener {
            
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}