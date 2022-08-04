package com.shows_lesdominik

import android.content.Context
import android.net.Uri
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.appcompat.widget.Toolbar
import com.bumptech.glide.Glide
import com.shows_lesdominik.databinding.CustomToolbarBinding

class CustomToolbar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : Toolbar(context, attrs, defStyleAttr) {

    lateinit var binding: CustomToolbarBinding

    init {
        binding = CustomToolbarBinding.inflate(LayoutInflater.from(context), this)

    }

    fun setUserIconFromUri(uri: Uri) {
        binding.userIcon.setImageURI(uri)
    }

    fun setDefaultUserIcon() {
        binding.userIcon.setImageResource(R.drawable.default_user)
    }

    fun setUserIconFromUrl(url: String?) {
        Glide.with(binding.root).load(url).into(binding.userIcon)
    }

    fun onUserIconClick(onUserImageClickCallback: () -> Unit) {
        binding.userIcon.setOnClickListener {
            onUserImageClickCallback()
        }
    }
}