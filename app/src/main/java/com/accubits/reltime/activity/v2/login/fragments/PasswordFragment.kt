package com.accubits.reltime.activity.v2.login.fragments

import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.accubits.reltime.R
import com.accubits.reltime.databinding.FragmentPasswordBinding
import com.accubits.reltime.utils.Extensions.markRequiredInRed
import com.accubits.reltime.utils.Extensions.openActivity
import com.accubits.reltime.views.forgot.ForgotPasswordActivity

class PasswordFragment : Fragment() {
    private var _binding: FragmentPasswordBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPasswordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.tvPassword.markRequiredInRed()
        binding.tvForgotPassword.setOnClickListener {
            activity?.openActivity(ForgotPasswordActivity::class.java){
                this.putBoolean(ForgotPasswordActivity.EXTRA_IS_FORGOT_PASSWORD,true)
            }
        }
        binding.ivPassword.setOnClickListener {
            if (binding.etPassword.transformationMethod != HideReturnsTransformationMethod.getInstance()) {
                binding.etPassword.transformationMethod =
                    HideReturnsTransformationMethod.getInstance()
                binding.ivPassword.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.ic_eye
                    )
                )
            } else {
                binding.etPassword.transformationMethod =
                    PasswordTransformationMethod.getInstance()
                binding.ivPassword.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.password_toggle_eye_close
                    )
                )
            }
        }
    }

    public fun getValue() = _binding?.etPassword?.text.toString()

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}