package com.accubits.reltime.views.mpin

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.accubits.reltime.R
import com.accubits.reltime.constants.ReltimeConstants
import com.accubits.reltime.databinding.ActivityMpincreateBinding
import com.accubits.reltime.helpers.PreferenceManager
import com.accubits.reltime.utils.Extensions.showToast
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MPINCreateActivity : AppCompatActivity() {
    private lateinit var activityMpinConfirmBinding: ActivityMpincreateBinding

    @Inject
    lateinit var preferenceManager: PreferenceManager
    private val isCreatePin by lazy {
        intent.getBooleanExtra(
            EXTRA_IS_CREATE_PIN,
            false
        )
    }

    companion object {
        const val EXTRA_IS_CREATE_PIN = "extra_is_create_pin"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityMpinConfirmBinding = ActivityMpincreateBinding.inflate(layoutInflater)
        setContentView(activityMpinConfirmBinding.root)
        if (!isCreatePin) {
            activityMpinConfirmBinding.textView70.text = resources.getText(R.string.reset_mpin)
            activityMpinConfirmBinding.btVerify.text = resources.getText(R.string.reset_mpin)
        }
        activityMpinConfirmBinding.btVerify.setOnClickListener {
            if (activityMpinConfirmBinding.pinView.value
                    .trim() != ""
            ) {
                val mpin =
                    activityMpinConfirmBinding.pinView.value
                if (!mpin.contains(".") && !mpin.contains(",") && (mpin.length == 4)) {
                    if (!isCreatePin) {
                        val intents = Intent(this, MpinConfirmActivity::class.java)
                        intents.putExtra(EXTRA_IS_CREATE_PIN, isCreatePin)
                        intents.putExtra(ReltimeConstants.MPIN, mpin)
                        intents.putExtra(
                            "emailorphone",
                            intent.getStringExtra("emailorphone").toString()
                        )
                        startActivity(intents)
                    } else {
                        val intents = Intent(this, MpinConfirmActivity::class.java)
                        intents.putExtra(EXTRA_IS_CREATE_PIN, isCreatePin)
                        intents.putExtra(ReltimeConstants.MPIN, mpin)
                        startActivity(intents)
                    }
                    finish()
                } else showToast(resources.getString(R.string.please_enter_a_valid_pin))
            } else
                showToast(resources.getString(R.string.please_enter_pin))
        }
        activityMpinConfirmBinding.ivBack.setOnClickListener {
            onBackPressed()
        }
    }


}