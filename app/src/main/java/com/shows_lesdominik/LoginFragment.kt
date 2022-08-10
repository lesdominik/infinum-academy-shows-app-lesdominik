package com.shows_lesdominik

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.BounceInterpolator
import android.view.animation.OvershootInterpolator
import androidx.core.content.edit
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.shows_lesdominik.databinding.FragmentLoginBinding

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() =_binding!!

    private val args by navArgs<LoginFragmentArgs>()
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

        viewModel.animateOnlyAfterSplash()
        viewModel.alreadyAnimatedLiveData.observe(viewLifecycleOwner) { alreadyAnimated ->
            if (!alreadyAnimated) {
                animateLogoIcon()
                animateLogoText()
            }
        }

        if (args.afterRegistration) {
            binding.title.text = getString(R.string.registration_successful)
            binding.registerTextButton.isVisible = false
        }

        viewModel.loginResultLiveData.observe(viewLifecycleOwner) { loginSuccessful ->
            afterLoginValidation(loginSuccessful)
            binding.loginGroup.isVisible = true
            binding.loadingLogin.isVisible = false
        }

        initListeners()
        initLoginButton()
    }

    private fun animateLogoIcon() = with(binding.logoImage) {
        translationY = -400f
        animate()
            .translationY(0f)
            .setDuration(1500)
            .setInterpolator(BounceInterpolator())
            .start()
    }

    private fun animateLogoText() = with(binding.logoText) {
        scaleX = 0f
        scaleY = 0f
        animate()
            .setStartDelay(1500)
            .scaleX(1f)
            .scaleY(1f)
            .setDuration(1500)
            .setInterpolator(OvershootInterpolator())
            .start()
    }

    private fun afterLoginValidation(loginSuccessful: Boolean) = with(binding) {
        if (loginSuccessful) {
            val userEmail = emailEdiText.text.toString()
            val directions = LoginFragmentDirections.toShowsFragment(userEmail)
            findNavController().navigate(directions)
        } else {
            loginButton.isEnabled = false
            emailEdiText.setText("")
            passwordEditText.setText("")
            message.text = getString(R.string.login_failed)
            message.setTextColor(Color.RED)
        }
    }

    private fun initLoginButton() = with(binding) {
        loginButton.setOnClickListener {
            binding.loginGroup.isVisible = false
            binding.loadingLogin.isVisible = true
            viewModel.onLoginButtonClicked(
                sharedPreferences,
                email = emailEdiText.text.toString(),
                password = passwordEditText.text.toString(),
                context = requireContext()
            )
        }
    }

    private fun initListeners() {
        binding.emailEdiText.doAfterTextChanged { email ->
            viewModel.emailValidation(email.toString())
            viewModel.isLoginButtonEnabledLiveData.observe(viewLifecycleOwner) { isEnabled ->
                binding.loginButton.isEnabled = isEnabled
            }
        }


        binding.passwordEditText.doAfterTextChanged { password ->
            viewModel.passwordValidation(password.toString())
            viewModel.isLoginButtonEnabledLiveData.observe(viewLifecycleOwner) { isEnabled ->
                binding.loginButton.isEnabled = isEnabled
            }
        }


        binding.rememberMeCheckBox.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setRememberMeChecked(sharedPreferences, isChecked)
        }


        binding.registerTextButton.setOnClickListener {
            findNavController().navigate(R.id.toRegisterFragment)
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}