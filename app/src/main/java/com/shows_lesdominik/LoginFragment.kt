package com.shows_lesdominik

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.edit
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.shows_lesdominik.databinding.FragmentLoginBinding

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() =_binding!!

    private val viewModel by viewModels<LoginViewModel>()

    private lateinit var sharedPreferences: SharedPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences = requireContext().getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getUserEmail(sharedPreferences)
        viewModel.userEmail.observe(viewLifecycleOwner) { userEmail ->
            val directions = LoginFragmentDirections.toShowsFragment(userEmail)
            findNavController().navigate(directions)
        }

        initListeners()
    }

    private fun initListeners() {

        var emailCorrect = false
        var passwordCorrect = false

        binding.emailEdiText.doAfterTextChanged { email ->

            when {
                email.toString().isEmpty() -> {
                    binding.emailTextField.error = null
                    emailCorrect = false
                }
                Patterns.EMAIL_ADDRESS.matcher(email.toString()).matches() -> {
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


        binding.passwordEditText.doAfterTextChanged { password ->

            when {
                password.toString().isEmpty() -> {
                    binding.passwordTextField.error = null
                    passwordCorrect = false
                }
                password.toString().length < 6 -> {
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


        binding.rememberMeCheckBox.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setRememberMeChecked(sharedPreferences, isChecked)
        }


        binding.loginButton.setOnClickListener {
            val userEmail = binding.emailEdiText.text.toString()
            viewModel.storeUserEmail(sharedPreferences, userEmail)

            val directions = LoginFragmentDirections.toShowsFragment(userEmail)
            findNavController().navigate(directions)
        }

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}