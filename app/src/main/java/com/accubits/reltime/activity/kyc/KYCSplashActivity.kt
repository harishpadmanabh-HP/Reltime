package com.accubits.reltime.activity.kyc

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.accubits.reltime.databinding.ActivityKycsplashBinding

class KYCSplashActivity : AppCompatActivity() {
    lateinit var binder: ActivityKycsplashBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binder = ActivityKycsplashBinding.inflate(layoutInflater)
        setContentView(binder.root)
    }
}