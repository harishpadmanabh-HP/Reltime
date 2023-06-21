package com.accubits.reltime.views.mpin

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.accubits.reltime.R
import com.accubits.reltime.api.ApiCallStatus
import com.accubits.reltime.constants.ReltimeConstants
import com.accubits.reltime.databinding.ActivityMpinConfirmBinding
import com.accubits.reltime.helpers.PreferenceManager
import com.accubits.reltime.models.NewMpinRequestModel
import com.accubits.reltime.models.RequstModelForMpinCreation
import com.accubits.reltime.utils.Extensions.showToast
import com.accubits.reltime.views.forgot.ForgotPasswordActivity
import com.accubits.reltime.views.reset.ResetPasswordViewModel
import com.accubits.reltime.views.reset.ResetSuccessActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MpinConfirmActivity : AppCompatActivity() {
    private var mpin: String = ""
    private lateinit var activityMpinConfirmBinding: ActivityMpinConfirmBinding
    private val isCreatePin by lazy {
        intent.getBooleanExtra(
            MPINCreateActivity.EXTRA_IS_CREATE_PIN,
            false
        )
    }

    @Inject
    lateinit var preferenceManager: PreferenceManager
    private val viewModel by viewModels<MpinCreateViewModel>()
    private val viewModelRestMpin by viewModels<ResetPasswordViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityMpinConfirmBinding = ActivityMpinConfirmBinding.inflate(layoutInflater)
        setContentView(activityMpinConfirmBinding.root)
        observeLivedata()
        mpin = intent.getStringExtra(ReltimeConstants.MPIN).toString()
        activityMpinConfirmBinding.btVerify.setOnClickListener {
            if (activityMpinConfirmBinding.pinView.value
                    .trim() != ""
            ) {
                val mpinSecond =
                    activityMpinConfirmBinding.pinView.value
                if (!mpin.contains(".") && !mpin.contains(",") && (mpinSecond.length == 4)) {
                    if (mpin == mpinSecond) {
                        if (!isCreatePin) {
                            viewModelRestMpin.dosetNewMpin(
                                preferenceManager.getApiKey(),
                                NewMpinRequestModel(
                                    intent.getStringExtra("emailorphone").toString().trim(),
                                    mpin,
                                    mpinSecond
                                )
                            )
                        } else {
                            viewModel.performCreateMpin(
                                preferenceManager.getApiKey(),
                                RequstModelForMpinCreation(mpin, mpinSecond)
                            )
                            updateUi()
                        }
                    } else showToast(resources.getString(R.string.pin1_and_pin2_must_be_same))
                } else showToast(resources.getString(R.string.please_enter_a_valid_pin))
            } else showToast(resources.getString(R.string.please_enter_pin2))
        }
        activityMpinConfirmBinding.ivBack.setOnClickListener {
            finish()
        }

    }

    private fun updateUi() {
        lifecycleScope.launch {
            viewModel.createMpinSuccessModel.collect { response ->
                when (response.status) {
                    ApiCallStatus.LOADING -> {
                        activityMpinConfirmBinding.progressBar.visibility = View.VISIBLE
                    }
                    ApiCallStatus.SUCCESS -> {
                        activityMpinConfirmBinding.progressBar.visibility = View.GONE
                        if (response.data!!.status == 200) {
                            showToast(response.data.message)
                            preferenceManager.setMpin(true)
                            finish()
                        } else
                            showToast(response.data.message)
                    }
                    ApiCallStatus.ERROR -> {
                        activityMpinConfirmBinding.progressBar.visibility = View.GONE
                    }
                    else -> {}
                }
            }
        }
    }

    private fun observeLivedata() {
        viewModelRestMpin.commonModel.observe(this) { model ->
            activityMpinConfirmBinding.progressBar.visibility = View.GONE
            if (!model.error) {
                if (model.success) {
                    val intent = Intent(this, ResetSuccessActivity::class.java)
                    intent.putExtra(
                        ForgotPasswordActivity.EXTRA_IS_FORGOT_PASSWORD,
                        false
                    )
                    startActivity(intent)
                    finish()
                } else showToast(model.message)
            } else showToast(getString(R.string.some_thing_went_wrong))
        }
    }
}