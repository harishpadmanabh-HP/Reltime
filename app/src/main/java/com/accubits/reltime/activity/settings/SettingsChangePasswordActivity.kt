package com.accubits.reltime.activity.settings

import android.os.Bundle
import android.text.InputType
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import com.accubits.reltime.R
import com.accubits.reltime.databinding.ActivitySettingsChangePasswordBinding

class SettingsChangePasswordActivity : AppCompatActivity(), View.OnClickListener {
    lateinit var binder: ActivitySettingsChangePasswordBinding
    private val toggleEye = MutableLiveData<Boolean>(false)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binder = ActivitySettingsChangePasswordBinding.inflate(layoutInflater)
        setContentView(binder.root)
        setListeners()
        setLiveDataObservers()
    }

    fun setListeners() {
        binder.ivEyeToggle.setOnClickListener(this)
        binder.btChangePassword.setOnClickListener(this)
        binder.btCancel.setOnClickListener(this)
    }

    fun setLiveDataObservers() {
        toggleEye.observe(this, { show ->
            val resourceId: Int
            val input_type: Int
            if (show) {
                resourceId = R.drawable.ic_eye
                input_type = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            } else {
                resourceId = R.drawable.password_toggle_eye_close
                input_type = InputType.TYPE_TEXT_VARIATION_PASSWORD
            }
            binder.edNewPassword.apply {
                inputType = input_type
            }
            binder.edNewPassword.setSelection(binder.edNewPassword.getText().toString().length);
            binder.ivEyeToggle.setImageResource(resourceId)
        })
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.iv_eye_toggle -> {
                toggleEye.value = !toggleEye.value!!
            }
        }
    }
}