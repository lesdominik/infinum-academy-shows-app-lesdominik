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

        viewModel.registrationResultLiveData.observe(viewLifecycleOwner) { registrationSuccessful ->
            afterRegistrationValidation(registrationSuccessful)
        }

        initListeners()
        initRegisterButton()
    }

    private fun afterRegistrationValidation(registrationSuccessful: Boolean) = with(binding) {
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
        binding.emailEdiText.doAfterTextChanged { email ->
            viewModel.emailValidation(email.toString())

            viewModel.emailErrorLiveData.observe(viewLifecycleOwner) { errorInt ->
                if (errorInt == null) {
                    binding.emailTextField.error = null
                } else {
                    binding.emailTextField.error = getString(errorInt)
                }
            }

            viewModel.isRegisterButtonEnabledLiveData.observe(viewLifecycleOwner) { isEnabled ->
                binding.registerButton.isEnabled = isEnabled
            }
        }


        binding.passwordEditText.doAfterTextChanged { password ->
            viewModel.passwordValidation(password.toString())

            viewModel.passwordErrorLiveData.observe(viewLifecycleOwner) { errorInt ->
                if (errorInt == null) {
                    binding.passwordTextField.error = null
                } else {
                    binding.passwordTextField.error = getString(errorInt)
                }
            }

            viewModel.isRegisterButtonEnabledLiveData.observe(viewLifecycleOwner) { isEnabled ->
                binding.registerButton.isEnabled = isEnabled
            }
        }


        binding.repeatPasswordEditText.doAfterTextChanged { repeatPassword ->
            viewModel.repeatPasswordValidation(repeatPassword.toString())

            viewModel.repeatPasswordErrorLiveData.observe(viewLifecycleOwner) { errorInt ->
                if (errorInt == null) {
                    binding.repeatPasswordTextField.error = null
                } else {
                    binding.repeatPasswordTextField.error = getString(errorInt)
                }
            }

            viewModel.isRegisterButtonEnabledLiveData.observe(viewLifecycleOwner) { isEnabled ->
                binding.registerButton.isEnabled = isEnabled
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}