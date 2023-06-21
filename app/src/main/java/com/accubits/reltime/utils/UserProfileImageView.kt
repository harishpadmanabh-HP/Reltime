package com.accubits.reltime.utils

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.accubits.reltime.R
import com.accubits.reltime.databinding.ItemUserprofileBinding
import com.bumptech.glide.Glide


class UserProfileImageView(context: Context, attrs: AttributeSet) :
    ConstraintLayout(context, attrs) {

    private var binding =
        ItemUserprofileBinding.inflate(LayoutInflater.from(context), this, true)


    fun setUserProfileImage(imageUrl: String?) {
        binding.ivProfileImage.visibility = View.VISIBLE
        binding.tvFirstLetter.visibility = View.GONE

        Glide.with(this)
            .asGif()
            .load(imageUrl)
            .into(binding.ivProfileImage)

    }

    fun setFirstLetter(userName: String) {
        binding.ivProfileImage.visibility = View.GONE
        binding.tvFirstLetter.visibility = View.VISIBLE
        var firstAlphabet = userName[0]?.uppercaseChar()
        binding.tvFirstLetter.text = firstAlphabet.toString()

    }
}