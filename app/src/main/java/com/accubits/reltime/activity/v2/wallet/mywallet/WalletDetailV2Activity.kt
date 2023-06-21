package com.accubits.reltime.activity.v2.wallet.mywallet

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.accubits.reltime.databinding.ActivityWalletDetailsV2Binding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WalletDetailV2Activity : AppCompatActivity() {

    private lateinit var binding: ActivityWalletDetailsV2Binding

    companion object {
        const val TITLE = "title"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWalletDetailsV2Binding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.layoutTitle.tvTitle.text = intent.getStringExtra(TITLE)
        binding.layoutTitle.imgShare.visibility=View.GONE
        binding.layoutTitle.ivBack.setOnClickListener {
            onBackPressed()
        }
    }
}
