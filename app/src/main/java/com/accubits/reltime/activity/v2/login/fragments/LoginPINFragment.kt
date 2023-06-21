package com.accubits.reltime.activity.v2.login.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.accubits.reltime.activity.biometricLogin.LoginPINCreationActivity
import com.accubits.reltime.databinding.FragmentLoginPINBinding
import com.accubits.reltime.utils.Extensions.markRequiredInRed

class LoginPINFragment : Fragment() {
    private var _binding: FragmentLoginPINBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginPINBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.tvLoginPIN.markRequiredInRed()
        binding.tvForgotLoginPIN.setOnClickListener {
            startActivity(Intent(requireContext(), LoginPINCreationActivity::class.java))
        }
    }

    public fun getValue() = _binding?.pinView?.value.toString()

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}