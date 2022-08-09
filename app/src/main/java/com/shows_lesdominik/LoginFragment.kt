package com.shows_lesdominik

import android.os.Bundle
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.navigation.fragment.findNavController
import com.shows_lesdominik.databinding.FragmentLoginBinding

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() =_binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initListeners()
    }

    private fun initListeners() {

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


        binding.loginButton.setOnClickListener {
            val splitUsername = binding.emailEdiText.text.toString().split("@")
            val username = splitUsername[0]
            val directions = LoginFragmentDirections.toShowsFragment(username)
            findNavController().navigate(directions)
        }

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}