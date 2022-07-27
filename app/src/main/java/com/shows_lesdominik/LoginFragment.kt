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
import androidx.navigation.fragment.findNavController
import com.shows_lesdominik.databinding.FragmentLoginBinding

private const val REMEMBER_ME_CHECKED = "REMEMBER_ME_CHECKED"
private const val USER_EMAIL = "USER_EMAIL"

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() =_binding!!

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

        val rememberMeChecked = sharedPreferences.getBoolean(REMEMBER_ME_CHECKED, false)
        if (rememberMeChecked) {
            val userEmail = sharedPreferences.getString(USER_EMAIL, "")
            val directions = LoginFragmentDirections.toShowsFragment(userEmail.toString())
            findNavController().navigate(directions)
        }

        initListeners()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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


        binding.loginButton.setOnClickListener {
            val userEmail = binding.emailEdiText.text.toString()
            sharedPreferences.edit {
                putString(USER_EMAIL, userEmail)
            }

            val directions = LoginFragmentDirections.toShowsFragment(userEmail)
            findNavController().navigate(directions)
        }


        binding.registerTextButton.setOnClickListener {
            findNavController().navigate(R.id.toRegisterFragment)
        }

    }
}