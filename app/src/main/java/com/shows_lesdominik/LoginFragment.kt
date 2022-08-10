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
import androidx.core.content.edit
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.shows_lesdominik.databinding.FragmentLoginBinding

private const val REMEMBER_ME_CHECKED = "REMEMBER_ME_CHECKED"
private const val USER_EMAIL = "USER_EMAIL"
private const val ACCESS_TOKEN = "ACCESS_TOKEN"

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() =_binding!!

    private lateinit var sharedPreferences: SharedPreferences

    private val args by navArgs<LoginFragmentArgs>()
    private val viewModel by viewModels<LoginViewModel>()

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

        if (args.afterRegistration) {
            binding.title.text = getString(R.string.registration_successful)
            binding.registerTextButton.isVisible = false
        }

        if (!sharedPreferences.getBoolean(REMEMBER_ME_CHECKED, false)) {
            sharedPreferences.edit {
                remove(ACCESS_TOKEN)
            }
        }

        val rememberMeChecked = sharedPreferences.getBoolean(REMEMBER_ME_CHECKED, false)
        if (rememberMeChecked) {
            val userEmail = sharedPreferences.getString(USER_EMAIL, "")
            val directions = LoginFragmentDirections.toShowsFragment(userEmail.toString())
            findNavController().navigate(directions)
        }

        ApiModule.initRetrofit(requireContext())

        viewModel.loginResultLiveData.observe(viewLifecycleOwner) { loginSuccessful ->
            loginOutcome(loginSuccessful)
        }

        initListeners()
        initLoginButton()
    }

    private fun loginOutcome(loginSuccessful: Boolean) = with(binding) {
        if (loginSuccessful) {
            val userEmail = emailEdiText.text.toString()
            sharedPreferences.edit {
                putString(USER_EMAIL, userEmail)
            }

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
            viewModel.onLoginButtonClicked(
                email = emailEdiText.text.toString(),
                password = passwordEditText.text.toString(),
                context = requireContext()
            )
        }
    }

    private fun initListeners() {

        var emailNotEmpty = false
        var passwordNotEmpty = false

        binding.emailEdiText.doAfterTextChanged {
            emailNotEmpty = Patterns.EMAIL_ADDRESS.matcher(it.toString()).matches()
            binding.loginButton.isEnabled = emailNotEmpty && passwordNotEmpty
        }


        binding.passwordEditText.doAfterTextChanged {
            passwordNotEmpty = it.toString().isNotEmpty()
            binding.loginButton.isEnabled = emailNotEmpty && passwordNotEmpty
        }


        binding.rememberMeCheckBox.setOnCheckedChangeListener { _, isChecked ->
            sharedPreferences.edit {
                putBoolean(REMEMBER_ME_CHECKED, isChecked)
            }
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