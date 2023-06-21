package com.accubits.reltime.activity.v2.welcomeScreen.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.accubits.reltime.R
import com.accubits.reltime.databinding.FragmentScreenFourBinding
import com.accubits.reltime.databinding.FragmentScreenThreeBinding
import com.bumptech.glide.Glide


class WelcomeScreenFour : Fragment() {

    lateinit var binding : FragmentScreenFourBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentScreenFourBinding.inflate(inflater, container, false)
        binding.txtLabel.visibility = View.GONE
        Glide.with(this)
            .asGif()
            .load(R.drawable.welcome_screen_four)
            .into(binding.gif)
        return binding.root
    }
}