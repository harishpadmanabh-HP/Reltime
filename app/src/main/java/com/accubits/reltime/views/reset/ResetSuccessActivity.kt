package com.accubits.reltime.views.reset

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.accubits.reltime.R
import com.accubits.reltime.activity.v2.welcomeScreen.WelcomeScreen
import com.accubits.reltime.constants.ReltimeConstants
import com.accubits.reltime.databinding.ActivityResetSuccessBinding
import com.accubits.reltime.helpers.PreferenceManager
import com.accubits.reltime.views.forgot.ForgotPasswordActivity
import com.accubits.reltime.views.home.ContainerActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class ResetSuccessActivity : AppCompatActivity() {
    private lateinit var activityResetSuccessBinding: ActivityResetSuccessBinding
    @Inject
    lateinit var preferenceManager: PreferenceManager
    private val isForgotPassword by lazy {
        intent.getBooleanExtra(
            ForgotPasswordActivity.EXTRA_IS_FORGOT_PASSWORD,
            false
        )
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityResetSuccessBinding = ActivityResetSuccessBinding.inflate(layoutInflater)
        setContentView(activityResetSuccessBinding.root)
        if(!isForgotPassword) {
            activityResetSuccessBinding.btGoLogin.text =
                resources.getText(R.string.go_to_home)
            activityResetSuccessBinding.textView16.text =
                resources.getText(R.string.pin_n_successfully_reset)
            activityResetSuccessBinding.textView17.text =
                resources.getText(R.string.your_pin_has_been_successfully_reset_for_your_account)
        }
        activityResetSuccessBinding.btGoLogin.setOnClickListener {
            if(!isForgotPassword){
                val intent = Intent(this, ContainerActivity::class.java)
                startActivity(intent)
                finishAffinity()
            }else {
                val intent = Intent(this, WelcomeScreen::class.java)
                startActivity(intent)
                finishAffinity()
            }
        }
    }
}