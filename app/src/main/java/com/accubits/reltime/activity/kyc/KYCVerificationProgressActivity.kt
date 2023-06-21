package com.accubits.reltime.activity.kyc

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.accubits.reltime.databinding.ActivityKycverificationProgressBinding

class KYCVerificationProgressActivity : AppCompatActivity() {
    lateinit var binder: ActivityKycverificationProgressBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binder =
            ActivityKycverificationProgressBinding.inflate(layoutInflater)
        setContentView(binder.root)
        binder.btFinish.setOnClickListener {
            finish()
        }
    }
}