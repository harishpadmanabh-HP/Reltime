package com.accubits.reltime.activity.notification

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.accubits.reltime.databinding.ActivityWalletV2Binding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WalletV2Activity : AppCompatActivity() {

    private lateinit var binding: ActivityWalletV2Binding

    companion object {
        const val TITLE = "title"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWalletV2Binding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.layoutTitle.tvTitle.text = intent.getStringExtra(TITLE)
        binding.layoutTitle.imgShare.visibility = View.GONE
        binding.layoutTitle.ivBack.setOnClickListener {
            onBackPressed()
        }
    }
}