package com.accubits.reltime.activity.v2.support

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.accubits.reltime.R
import com.accubits.reltime.databinding.ActivityAboutBinding
import com.accubits.reltime.databinding.ActivitySupportBinding

class SupportActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySupportBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySupportBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.ivBack.setOnClickListener {
            finish()
        }
    }
}