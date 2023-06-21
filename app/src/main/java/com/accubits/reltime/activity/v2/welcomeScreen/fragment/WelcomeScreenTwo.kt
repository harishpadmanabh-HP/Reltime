package com.accubits.reltime.activity.v2.welcomeScreen.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.accubits.reltime.R
import com.accubits.reltime.databinding.FragmentScreenOneBinding
import com.accubits.reltime.databinding.FragmentScreenTwoBinding
import com.bumptech.glide.Glide


class WelcomeScreenTwo : Fragment() {

    lateinit var binding : FragmentScreenTwoBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentScreenTwoBinding.inflate(inflater, container, false)
        binding.txtLabel.visibility = View.GONE
        Glide.with(this)
            .asGif()
            .load(R.drawable.welcome_screen_two)
            .into(binding.gif)
        return binding.root
    }
}