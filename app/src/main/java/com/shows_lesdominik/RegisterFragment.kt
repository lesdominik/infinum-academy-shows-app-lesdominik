package com.shows_lesdominik

import android.os.Bundle
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.doOnDetach
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.shows_lesdominik.databinding.FragmentLoginBinding
import com.shows_lesdominik.databinding.FragmentRegisterBinding

class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() =_binding!!

    private val viewModel by viewModels<RegisterViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ApiModule.initRetrofit(requireContext())

        viewModel.registrationResultLiveData.observe(viewLifecycleOwner) { registrationSuccessful ->
            registrationOutcome(registrationSuccessful)
        }

        initListeners()
        initRegisterButton()
    }

    private fun registrationOutcome(registrationSuccessful: Boolean) = with(binding) {
        if (registrationSuccessful) {
            val destination = RegisterFragmentDirections.toLoginFragment(true)
            findNavController().navigate(destination)
        } else {
            registerButton.isEnabled = false
            emailEdiText.setText("")
            passwordEditText.setText("")
            repeatPasswordEditText.setText("")
            errorMessage.isVisible = true
        }
    }

    private fun initRegisterButton() = with(binding) {
        registerButton.setOnClickListener {
            viewModel.onRegisterButtonClicked(
                email = emailEdiText.text.toString(),
                password = passwordEditText.text.toString(),
                passwordConfirmation = repeatPasswordEditText.text.toString()
            )
        }
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
                    binding.emailTextField.error = getString(R.string.invalid_email_address)
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
                    binding.passwordTextField.error = getString(R.string.password_length_message)
                    passwordCorrect = false
                }
                else -> {
                    binding.passwordTextField.error = null
                    passwordCorrect = true
                }
            }
            binding.registerButton.isEnabled = emailCorrect && passwordCorrect && passwordRepeatCorrect
        }


        binding.repeatPasswordEditText.doAfterTextChanged {

            when {
                it.toString().isEmpty() -> {
                    binding.repeatPasswordTextField.error = null
                    passwordRepeatCorrect = false
                }
                it.toString() != binding.passwordEditText.text.toString() -> {
                    binding.repeatPasswordTextField.error = getString(R.string.passwords_not_matching)
                    passwordRepeatCorrect = false
                }
                else -> {
                    binding.repeatPasswordTextField.error = null
                    passwordRepeatCorrect = true
                }
            }
            binding.registerButton.isEnabled = emailCorrect && passwordCorrect && passwordRepeatCorrect

        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}