package com.accubits.reltime.views.reset

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.accubits.reltime.R
import com.accubits.reltime.activity.v2.welcomeScreen.WelcomeScreen
import com.accubits.reltime.databinding.ActivityResetPasswordBinding
import com.accubits.reltime.helpers.PreferenceManager
import com.accubits.reltime.helpers.Utils
import com.accubits.reltime.models.NewMpinRequestModel
import com.accubits.reltime.models.NewPasswordRequestModel
import com.accubits.reltime.utils.Utils.isValidPasswordFormat
import com.accubits.reltime.views.forgot.ForgotPasswordActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ResetPasswordActivity : AppCompatActivity() {
    private var email: String? = null
    private var showPassword = false
    private lateinit var activityResetPasswordBinding: ActivityResetPasswordBinding
    private val resetViewModel by viewModels<ResetPasswordViewModel>()

    @Inject
    lateinit var preferenceManager: PreferenceManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityResetPasswordBinding = ActivityResetPasswordBinding.inflate(layoutInflater)
        setContentView(activityResetPasswordBinding.root)
        email = intent.getStringExtra("emailorphone")
        activityResetPasswordBinding.btReset.setOnClickListener {
            doChangePassword()
        }
        observeLivedata()

        activityResetPasswordBinding.etNewPass.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (activityResetPasswordBinding.tvNewPassError.visibility == View.VISIBLE) {
                    activityResetPasswordBinding.cardView3.setCardBackgroundColor(getColor(R.color.card_color))
                    activityResetPasswordBinding.tvNewPassError.visibility = View.GONE
                    activityResetPasswordBinding.cardView3.strokeWidth = 1
                    activityResetPasswordBinding.cardView3.strokeColor = getColor(R.color.darkblue)
                }
            }

            override fun afterTextChanged(p0: Editable?) {
                if (p0.toString().isEmpty()) {

                }
            }
        })

        activityResetPasswordBinding.etConfirmPass.addTextChangedListener(object :
            TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (activityResetPasswordBinding.tvConfirmPass.visibility == View.VISIBLE) {
                    activityResetPasswordBinding.cardView4.setCardBackgroundColor(getColor(R.color.card_color))
                    activityResetPasswordBinding.tvConfirmPass.visibility = View.GONE
                    activityResetPasswordBinding.cardView4.strokeWidth = 1
                    activityResetPasswordBinding.cardView4.strokeColor = getColor(R.color.darkblue)
                }
            }

            override fun afterTextChanged(p0: Editable?) {
                if (p0.toString().isEmpty()) {

                }
            }
        })

        activityResetPasswordBinding.etNewPass.onFocusChangeListener =
            View.OnFocusChangeListener { _, _ ->
                activityResetPasswordBinding.cardView3.setCardBackgroundColor(getColor(R.color.card_color))
                activityResetPasswordBinding.tvNewPassError?.visibility = View.GONE
                activityResetPasswordBinding.cardView3.strokeWidth = 1
                activityResetPasswordBinding.cardView3.strokeColor = getColor(R.color.darkblue)
                activityResetPasswordBinding.cardView4.strokeWidth = 0

            }
        activityResetPasswordBinding.etConfirmPass.onFocusChangeListener =
            View.OnFocusChangeListener { _, _ ->
                activityResetPasswordBinding.cardView4.setCardBackgroundColor(getColor(R.color.card_color))
                activityResetPasswordBinding.tvConfirmPass.visibility = View.GONE
                activityResetPasswordBinding.cardView4.strokeWidth = 1
                activityResetPasswordBinding.cardView4.strokeColor = getColor(R.color.darkblue)
                activityResetPasswordBinding.cardView3.strokeWidth = 0

            }
        activityResetPasswordBinding.ivEye.setOnClickListener {
            if (!showPassword) {
                showPassword = true
                activityResetPasswordBinding.etNewPass.transformationMethod =
                    HideReturnsTransformationMethod.getInstance()
                activityResetPasswordBinding.ivEye.setImageDrawable(getDrawable(R.drawable.ic_eye))
            } else {
                activityResetPasswordBinding.etNewPass.transformationMethod =
                    PasswordTransformationMethod.getInstance()
                showPassword = false
                activityResetPasswordBinding.ivEye.setImageDrawable(getDrawable(R.drawable.password_toggle_eye_close))
            }
        }
        activityResetPasswordBinding.ivConfirmPassword.setOnClickListener {
            if (activityResetPasswordBinding.etConfirmPass.transformationMethod != HideReturnsTransformationMethod.getInstance()) {
                activityResetPasswordBinding.etConfirmPass.transformationMethod =
                    HideReturnsTransformationMethod.getInstance()
                activityResetPasswordBinding.ivConfirmPassword.setImageDrawable(getDrawable(R.drawable.ic_eye))
            } else {
                activityResetPasswordBinding.etConfirmPass.transformationMethod =
                    PasswordTransformationMethod.getInstance()
                activityResetPasswordBinding.ivConfirmPassword.setImageDrawable(getDrawable(R.drawable.password_toggle_eye_close))
            }
        }
        activityResetPasswordBinding.btCancel.setOnClickListener {
            val intent = Intent(this, WelcomeScreen::class.java)
            startActivity(intent)
            finishAffinity()
        }
    }

    private fun observeLivedata() {
        resetViewModel.commonModel.observe(this) { model ->
            activityResetPasswordBinding.progressBar.visibility = View.GONE
            if (!model.error) {
                if (model.success) {
                    val intent = Intent(this, ResetSuccessActivity::class.java)
                    intent.putExtra(ForgotPasswordActivity.EXTRA_IS_FORGOT_PASSWORD,true)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, model.message, Toast.LENGTH_LONG).show()

                }
            } else {
                Toast.makeText(this, getString(R.string.some_thing_went_wrong), Toast.LENGTH_LONG)
                    .show()
            }
        }
    }

    private fun doChangePassword() {
        if (Utils.isNetworkAvailable(this)!!) {
            if (activityResetPasswordBinding.etNewPass.text.toString() != "" && activityResetPasswordBinding.etNewPass.text.toString() != "") {
                if (activityResetPasswordBinding.etNewPass.text.toString()
                        .isValidPasswordFormat()
                ) {
                    activityResetPasswordBinding.tvNewPassError.visibility = View.GONE
                    activityResetPasswordBinding.tvConfirmPass.visibility = View.GONE
                    if (activityResetPasswordBinding.etNewPass.text.toString() == activityResetPasswordBinding.etConfirmPass.text.toString()) {
                        activityResetPasswordBinding.progressBar.visibility = View.VISIBLE
                        resetViewModel.dosetNewPassword(
                            NewPasswordRequestModel(
                                intent.getStringExtra("emailorphone").toString().trim(),
                                activityResetPasswordBinding.etNewPass.text.toString(),
                                activityResetPasswordBinding.etConfirmPass.text.toString()
                            )
                        )
                    } else {
                        activityResetPasswordBinding.cardView4.strokeWidth = 1
                        activityResetPasswordBinding.cardView4.strokeColor =
                            getColor(R.color.dark_red)
                        activityResetPasswordBinding.cardView4.setCardBackgroundColor(getColor(R.color.error_red60))
                        activityResetPasswordBinding.tvConfirmPass.visibility = View.VISIBLE
                        activityResetPasswordBinding.tvConfirmPass.text =
                            getString(R.string.new_pass_and_confirm_pass_match)
                    }
                } else {
                    activityResetPasswordBinding.cardView3.strokeWidth = 1
                    activityResetPasswordBinding.cardView3.strokeColor =
                        getColor(R.color.dark_red)
                    activityResetPasswordBinding.cardView3.setCardBackgroundColor(getColor(R.color.error_red60))
                    activityResetPasswordBinding.tvNewPassError.visibility = View.VISIBLE
                    activityResetPasswordBinding.tvNewPassError.text =
                        "Your new password should have minimum eight characters, one uppercase letter, one lowercase letter, one number and one special character"
                }
            } else {
                if (activityResetPasswordBinding.etNewPass.text.toString() == "") {
                    activityResetPasswordBinding.cardView3.strokeWidth = 1
                    activityResetPasswordBinding.cardView3.strokeColor = getColor(R.color.dark_red)
                    activityResetPasswordBinding.cardView3.setCardBackgroundColor(getColor(R.color.error_red60))
                    activityResetPasswordBinding.tvNewPassError.visibility = View.VISIBLE
                    activityResetPasswordBinding.tvNewPassError.text =
                        "Please enter new password"
                }
                if (activityResetPasswordBinding.etConfirmPass.text.toString() == "") {
                    activityResetPasswordBinding.cardView4.strokeWidth = 1
                    activityResetPasswordBinding.cardView4.strokeColor = getColor(R.color.dark_red)
                    activityResetPasswordBinding.cardView4.setCardBackgroundColor(getColor(R.color.error_red60))
                    activityResetPasswordBinding.tvConfirmPass.visibility = View.VISIBLE
                    activityResetPasswordBinding.tvConfirmPass.text =
                        "Please enter confirm password"
                }
            }
        } else {
            Toast.makeText(this, getString(R.string.please_check_net), Toast.LENGTH_LONG).show()
        }

    }

}