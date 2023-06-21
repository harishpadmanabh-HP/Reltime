package com.accubits.reltime.activity.v2.wallet.qrCode

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.accubits.reltime.R
import com.accubits.reltime.databinding.ActivityQrCodeV2Binding
import dagger.hilt.android.AndroidEntryPoint
import io.github.vejei.viewpagerindicator.indicator.CircleIndicator

@AndroidEntryPoint
class QrCodeV2Activity : AppCompatActivity() {

    companion object {

        const val QR_SCAN = "scan_qr"
        const val WALLET_DETAIL= "wallet_details"

    }

    private lateinit var binding: ActivityQrCodeV2Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQrCodeV2Binding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
    }

    private fun init() {
        binding.layoutTitle.imgShare.visibility = View.GONE
        binding.layoutTitle.tvTitle.text = getString(R.string.reltime_quick_pay)
        binding.layoutTitle.ivBack.setOnClickListener {
            onBackPressed()
        }
        val list = ArrayList<Fragment>()
        if(intent.getBooleanExtra(QR_SCAN,true))
            list.add(ScanQrCodeFragment())
        if(intent.getBooleanExtra(WALLET_DETAIL,true))
            list.add(QRCodeFragmentV2())
        val pagerAdapter = QrCodeAdapterV2(this, list)

        binding.viewPager.adapter = pagerAdapter
        if (list.size <= 1)
            binding.circleIndicator.visibility = View.GONE
        else {
            binding.circleIndicator.setWithViewPager2(binding.viewPager, false)
            binding.circleIndicator.itemCount = 2
            binding.circleIndicator.setAnimationMode(CircleIndicator.AnimationMode.SLIDE)
        }
    }
}