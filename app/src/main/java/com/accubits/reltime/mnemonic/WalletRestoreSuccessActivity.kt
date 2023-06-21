package com.accubits.reltime.mnemonic

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.accubits.reltime.databinding.ActivityWalletRestoreSuccessBinding
import com.accubits.reltime.helpers.PreferenceManager
import com.accubits.reltime.views.home.ContainerActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class WalletRestoreSuccessActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWalletRestoreSuccessBinding

    @Inject
    lateinit var preferenceManager: PreferenceManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityWalletRestoreSuccessBinding.inflate(layoutInflater)
        setContentView(binding.root)


        if (intent.hasExtra("walletCreated")) {
            binding.apply {
                textView28.text = "Reltime wallet created"
                textView29.text =
                    "Your wallet was successfully created. All the services in Reltime has been unlocked"
                tvWalletAddress.text = preferenceManager.getPublicKeyFromLogin()
                cWalletDetails.visibility = View.VISIBLE

            }
        }

        lifecycleScope.launch {
            delay(3000)
            backToHome()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        backToHome()
    }

    private fun backToHome(){
        val intent = Intent(this, ContainerActivity::class.java)
        startActivity(intent)
        finishAffinity()
    }
}