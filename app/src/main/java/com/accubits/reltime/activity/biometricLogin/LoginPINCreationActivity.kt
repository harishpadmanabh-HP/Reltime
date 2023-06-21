package com.accubits.reltime.activity.biometricLogin

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.accubits.reltime.R
import com.accubits.reltime.activity.biometricLogin.viewmodel.LoginPINCreateViewModel
import com.accubits.reltime.api.ApiCallStatus
import com.accubits.reltime.databinding.ActivityLoginPincreationBinding
import com.accubits.reltime.helpers.PreferenceManager
import com.accubits.reltime.helpers.Utils
import com.accubits.reltime.utils.Extensions.hideSoftKeyboard
import com.accubits.reltime.utils.Extensions.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class LoginPINCreationActivity : AppCompatActivity() {
    private lateinit var binder: ActivityLoginPincreationBinding
    private val viewModel by viewModels<LoginPINCreateViewModel>()

    @Inject
    lateinit var preferenceManager: PreferenceManager

    companion object {
        const val IS_CREATE_PIN = "create_pin"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binder = ActivityLoginPincreationBinding.inflate(layoutInflater)
        setContentView(binder.root)

        init()
        setListeners()
        collectData()
    }

    private fun init() {
        if (!intent.getBooleanExtra(IS_CREATE_PIN, true)) {
            binder.textView70.text = resources.getString(R.string.reset_reltime_pin)
            binder.tvSent.text = resources.getString(R.string.reset_pin_desc)
        }
    }

    private fun setListeners() {
        binder.btnCreate.setOnClickListener {
            validate()
        }
        binder.ivBack.setOnClickListener {
            onBackPressed()
        }

    }

    private fun collectData() {
        lifecycleScope.launch {
            viewModel.loginResponseFlow.collectLatest { response ->
                when (response.status) {
                    ApiCallStatus.LOADING -> {
                        binder.progressBar.visibility = View.VISIBLE
                        binder.btnCreate.visibility = View.INVISIBLE
                    }
                    ApiCallStatus.SUCCESS -> {
                        binder.progressBar.visibility = View.GONE
                        binder.btnCreate.visibility = View.VISIBLE
                        val responseOk = response.data?.status == 200 && response.data.success
                        if (responseOk && response.data != null) {
                            val isNewPin = intent.getBooleanExtra(IS_CREATE_PIN, true)
                            if (isNewPin) {
                                preferenceManager.setLoginPINSetStatus(true)
                                preferenceManager.setLoginPINEnabled(true)
                                showToast(response.data.message)
                                val intents = Intent()
                                setResult(RESULT_OK, intents)
                            } else {
                                showToast(resources.getString(R.string.login_pin_update_message))
                            }
                            finish()
                        } else {
                            response.data?.let { showToast(it.message) }
                        }
                    }
                    ApiCallStatus.ERROR -> {
                        response.errorMessage?.let { showToast(it) }
                        binder.progressBar.visibility = View.GONE
                        binder.btnCreate.visibility = View.VISIBLE
                    }
                    else -> {}
                }
            }
        }
    }

    private fun validate() {
        if (binder.pinView.value.isEmpty() || binder.pinView.value.length < 4)
            showToast(resources.getString(R.string.please_enter_login_pin))
        else if (binder.pinViewConfirm.value.isEmpty() || binder.pinViewConfirm.value.length < 4)
            showToast(resources.getString(R.string.please_confirm_login_pin))
        else if (binder.pinView.value != binder.pinViewConfirm.value)
            showToast(resources.getString(R.string.login_pin_confirm_pin_should_same))
        else callAPI()
    }

    private fun callAPI() {
        if (Utils.isNetworkAvailable(this) == true) {
            hideSoftKeyboard()
            viewModel.createLoginPIN(binder.pinView.value)
        } else {
            Toast.makeText(this, getString(R.string.please_check_net), Toast.LENGTH_LONG).show()
        }
    }

}