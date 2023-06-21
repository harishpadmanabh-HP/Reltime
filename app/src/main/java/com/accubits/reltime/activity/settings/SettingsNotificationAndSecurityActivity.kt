package com.accubits.reltime.activity.settings

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.view.Window
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.accubits.reltime.R
import com.accubits.reltime.activity.biometricLogin.viewmodel.LoginPINCreateViewModel
import com.accubits.reltime.api.ApiCallStatus
import com.accubits.reltime.databinding.ActivitySettingsNotificationAndSecurityBinding
import com.accubits.reltime.helpers.PreferenceManager
import com.accubits.reltime.helpers.Utils
import com.accubits.reltime.models.BioMetricRequest
import com.accubits.reltime.utils.Extensions.getCoinCode
import com.accubits.reltime.utils.Extensions.hideSoftKeyboard
import com.accubits.reltime.utils.Extensions.showToast
import com.accubits.reltime.utils.biometricUtils.BiometricUtilities
import com.accubits.reltime.views.biometric.BiometricViewModel
import com.accubits.reltime.views.mpin.MPINCreateActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class SettingsNotificationAndSecurityActivity : AppCompatActivity() {
    lateinit var binder: ActivitySettingsNotificationAndSecurityBinding
    private var isNewPin = true
    private lateinit var dialog: Dialog

    @Inject
    lateinit var preferenceManager: PreferenceManager
    private val viewModel by viewModels<BiometricViewModel>()
    private val loginPinViewModel by viewModels<LoginPINCreateViewModel>()
    val swBiometricLoginStatusListener= CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
        callBiometricAPI(isChecked)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binder = ActivitySettingsNotificationAndSecurityBinding.inflate(layoutInflater)
        setContentView(binder.root)
        init()
        setListeners()
        collectData()
        collectLoginBiometricData()
    }

    private fun init() {
        binder.iSecurityLayout.swMarketVariation.isChecked = preferenceManager.getBiometric()
        setSWValues()
    }

    private fun setListeners() {
        binder.ivBack.setOnClickListener {
            finish()
        }
        binder.iSecurityLayout.swMarketVariation.setOnCheckedChangeListener { buttonView, isChecked ->
            setDataToServer(isChecked)
        }
        binder.iLoginPinLayout.swBiometricLogin.setOnCheckedChangeListener(swBiometricLoginStatusListener)

        binder.iLoginPinLayout.swLoginPin.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked && !preferenceManager.getLoginPINSetStatus()) {
                showLoginPINDialog(true)
            } else {
                preferenceManager.setLoginPINEnabled(isChecked)
                if (isChecked)
                    showToast(resources.getString(R.string.login_pin_enabled))
                else showToast(resources.getString(R.string.login_pin_disabled))
            }
        }

        if (BiometricUtilities.isBiometricHardWareAvailable(this) || BiometricUtilities.deviceHasPasswordPinLock(
                this
            )
        ) {
            binder.iLoginPinLayout.tvBiometricTitle.visibility = View.VISIBLE
            binder.iLoginPinLayout.tvBiometricsDesc.visibility = View.VISIBLE
            binder.iLoginPinLayout.swBiometricLogin.visibility = View.VISIBLE
            binder.iLoginPinLayout.vDv1.visibility = View.VISIBLE
        } else {
            binder.iLoginPinLayout.tvBiometricTitle.visibility = View.GONE
            binder.iLoginPinLayout.tvBiometricsDesc.visibility = View.GONE
            binder.iLoginPinLayout.swBiometricLogin.visibility = View.GONE
            binder.iLoginPinLayout.vDv1.visibility = View.GONE
        }

        binder.iLoginPinLayout.tvResetPin.setOnClickListener {
            showLoginPINDialog(false)
        }
        binder.iSecurityLayout.tvTransactionPin.setOnClickListener {
            if (!preferenceManager.getMpin()) {
                val intent = Intent(this, MPINCreateActivity::class.java)
                intent.putExtra(MPINCreateActivity.EXTRA_IS_CREATE_PIN,true)
                startActivity(intent)
            } else showToast(resources.getString(R.string.transaction_pin_already_created))
        }
    }

    private fun collectData() {
        lifecycleScope.launch {
            loginPinViewModel.loginResponseFlow.collectLatest { response ->
                when (response.status) {
                    ApiCallStatus.LOADING -> {
                        binder.progressBar.visibility = View.VISIBLE
                    }
                    ApiCallStatus.SUCCESS -> {
                        binder.progressBar.visibility = View.GONE
                        val responseOk = response.data?.status == 200 && response.data.success
                        if (responseOk && response.data != null) {
                            if (isNewPin) {
                                preferenceManager.setLoginPINSetStatus(true)
                                preferenceManager.setLoginPINEnabled(true)
                                showToast(response.data.message)
                                setSWValues()
                            } else {
                                showToast(resources.getString(R.string.login_pin_update_message))
                            }
                        } else {
                            response.data?.let { showToast(it.message) }
                            if (isNewPin)
                                setSWValues()
                        }
                    }
                    ApiCallStatus.ERROR -> {
                        response.errorMessage?.let { showToast(it) }
                        binder.progressBar.visibility = View.GONE
                        if (isNewPin)
                            setSWValues()
                    }
                    else -> {}
                }
            }
        }
    }
    private fun collectLoginBiometricData() {
        lifecycleScope.launch {
            viewModel.loginBiometricStatusFlow.collectLatest { response ->
                when (response.status) {
                    ApiCallStatus.LOADING -> {
                        binder.iLoginPinLayout.pbLoginStatus.visibility = View.VISIBLE
                        binder.iLoginPinLayout.swBiometricLogin.visibility = View.INVISIBLE
                    }
                    ApiCallStatus.SUCCESS -> {
                        binder.iLoginPinLayout.pbLoginStatus.visibility = View.GONE
                        binder.iLoginPinLayout.swBiometricLogin.visibility = View.VISIBLE
                        response.data?.message?.let { showToast(it) }

                        val responseOk = response.data?.status == 200 && response.data.success
                        if (responseOk && response.data != null) {
                            preferenceManager.setLoginBiometricEnabled(binder.iLoginPinLayout.swBiometricLogin.isChecked)
                        } else redoBiometricSwStatus()
                    }
                    ApiCallStatus.ERROR -> {
                        response.errorMessage?.let { showToast(it) }
                        binder.iLoginPinLayout.pbLoginStatus.visibility = View.GONE
                        binder.iLoginPinLayout.swBiometricLogin.visibility = View.VISIBLE
                        redoBiometricSwStatus()
                    }
                }
            }
        }
    }

    private fun redoBiometricSwStatus(){
        binder.iLoginPinLayout.swBiometricLogin.setOnCheckedChangeListener(null)
        binder.iLoginPinLayout.swBiometricLogin.isChecked= !binder.iLoginPinLayout.swBiometricLogin.isChecked
        binder.iLoginPinLayout.swBiometricLogin.setOnCheckedChangeListener(swBiometricLoginStatusListener)
    }


    private fun setDataToServer(isChecked: Boolean) {
        if (Utils.isNetworkAvailable(this)!!) {
            viewModel.doEnableOrDisableBiometric(
                preferenceManager.getApiKey(),
                BioMetricRequest(isChecked)
            )
            updateUi()
        } else {
            Toast.makeText(this, getString(R.string.please_check_net), Toast.LENGTH_LONG).show()
        }
    }

    private fun updateUi() {
        lifecycleScope.launch {
            viewModel.createMpinSuccessModel.collect { response ->
                when (response.status) {
                    ApiCallStatus.LOADING -> {
                        binder.progressBar.visibility = View.VISIBLE
                    }
                    ApiCallStatus.SUCCESS -> {
                        binder.progressBar.visibility = View.GONE
                        if (response.data!!.status == 200) {
                            if (preferenceManager.getBiometric()) {
                                preferenceManager.setBiometric(false)
                                Toast.makeText(
                                    this@SettingsNotificationAndSecurityActivity,
                                    "Biometric disabled",
                                    Toast.LENGTH_LONG
                                )
                                    .show()
                            } else {
                                Toast.makeText(
                                    this@SettingsNotificationAndSecurityActivity,
                                    "Biometric enabled",
                                    Toast.LENGTH_LONG
                                )
                                    .show()
                                preferenceManager.setBiometric(true)
                            }


                        } else {

                            Toast.makeText(
                                this@SettingsNotificationAndSecurityActivity,
                                response.data.message,
                                Toast.LENGTH_LONG
                            )
                                .show()
                        }
                    }
                    ApiCallStatus.ERROR -> {
                        binder.progressBar.visibility = View.GONE
                        /*Toast.makeText(
                            this@SettingsNotificationAndSecurityActivity,
                            getString(R.string.some_thing_went_wrong),
                            Toast.LENGTH_LONG
                        )
                            .show()*/
                    }
                    else -> {}
                }
            }
        }
    }

    private fun setSWValues() {
        binder.iLoginPinLayout.swBiometricLogin.isChecked =
            preferenceManager.getLoginBiometricEnabled()
        binder.iLoginPinLayout.swLoginPin.isChecked =
            preferenceManager.getLoginPINEnabled()

        if (preferenceManager.getLoginPINSetStatus())
            binder.iLoginPinLayout.tvResetPin.visibility = View.VISIBLE
        else
            binder.iLoginPinLayout.tvResetPin.visibility = View.GONE
    }

    private var loginPINCreateLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            showToast(resources.getString(R.string.login_pin_enabled))
        }
        setSWValues()
    }

    private fun showLoginPINDialog(isCreate: Boolean) {
        isNewPin = isCreate
        show(isCreate, {
            setSWValues()
        }, { pin, pinConfirm ->
            if (Utils.isNetworkAvailable(this) == true) {
                hideSoftKeyboard()
                loginPinViewModel.createLoginPIN(pin)
            } else {
                Toast.makeText(this, getString(R.string.please_check_net), Toast.LENGTH_LONG).show()
            }
        })

    }


    fun show(
        isCreate: Boolean,
        onCancelListener: (Boolean) -> Unit,
        callApi: (String, String) -> Unit
    ) {
        dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_login_pin)

        val btnCancel = dialog.findViewById<TextView>(R.id.btnCancel)
        val btnYes = dialog.findViewById<TextView>(R.id.btnYes)
        val tvHead = dialog.findViewById<TextView>(R.id.tvHead)
        val tvPin = dialog.findViewById<EditText>(R.id.tvPin)
        val tvConfirmPin = dialog.findViewById<EditText>(R.id.tvConfirmPin)

        val ivPin = dialog.findViewById<ImageView>(R.id.ivPin)
        val ivConfirmPin = dialog.findViewById<ImageView>(R.id.ivConfirmPin)

        Utils.setPasswordToggle(ivPin,tvPin)
        Utils.setPasswordToggle(ivConfirmPin,tvConfirmPin)

        dialog.setCancelable(false)
        btnCancel.setOnClickListener {
            onCancelListener.invoke(isCreate)
            dialog.dismiss()
        }

        btnYes.setOnClickListener {
            if (tvPin.text.toString().isEmpty())
                showToast(resources.getString(R.string.please_enter_login_pin))
            else if (tvPin.text.toString().length < 4)
                showToast(resources.getString(R.string.login_pin_should_be_4_digit))
            else if (tvConfirmPin.text.toString()
                    .isEmpty() //|| tvConfirmPin.text.toString().length < 4
            )
                showToast(resources.getString(R.string.please_confirm_login_pin))
            else if (tvPin.text.toString() != tvConfirmPin.text.toString())
                showToast(resources.getString(R.string.login_pin_confirm_pin_should_same))
            else {
                callApi.invoke(tvPin.text.toString(), tvConfirmPin.text.toString())
                dialog.dismiss()
            }
        }

        if (!isCreate) {
            tvHead.text = resources.getString(R.string.reset_reltime_pin)
        }

        dialog.apply {
            show()
            window?.apply {
                setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::dialog.isInitialized && dialog.isShowing) {
            dialog.cancel()
        }
    }

    private fun callBiometricAPI(isChecked :Boolean){
        if (Utils.isNetworkAvailable(this)!!)
            viewModel.changeLoginBiometricStatus(preferenceManager.getApiKey(),isChecked)
        else showToast( getString(R.string.please_check_net))
    }
}