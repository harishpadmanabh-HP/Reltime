package com.accubits.reltime.activity.settings

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.accubits.reltime.R
import com.accubits.reltime.activity.addBankAccount.AddBankAccountActivity
import com.accubits.reltime.activity.jointAccount.CreateJointAccountActivity
import com.accubits.reltime.activity.myAccounts.MyAccountsActivity
import com.accubits.reltime.databinding.ActivityMyAccountStartBinding
import com.accubits.reltime.helpers.PreferenceManager
import com.accubits.reltime.utils.Extensions.openActivity
import com.accubits.reltime.utils.Extensions.showToast
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MyAccountStartActivity : AppCompatActivity() {
    @Inject
    lateinit var preferenceManager: PreferenceManager
    private lateinit var startBinding: ActivityMyAccountStartBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startBinding = ActivityMyAccountStartBinding.inflate(layoutInflater)
        setContentView(startBinding.root)
        startBinding.rootReltimeAccount.setOnClickListener {
            val intent = Intent(this, MyAccountsActivity::class.java)
            startActivity(intent)
            finish()
        }
        startBinding.addJointAccount.setOnClickListener {
            if (!preferenceManager.getMomic())
                showToast(resources.getString(R.string.wallet_not_available_error))
            else {
                val intent = Intent(this, CreateJointAccountActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
        startBinding.clAddBankAccount.setOnClickListener {
            openActivity(AddBankAccountActivity::class.java, shouldFinish = true)
        }
        startBinding.ivBack.setOnClickListener {
            finish()
        }
    }

}