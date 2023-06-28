package com.accubits.reltime.activity.v2.wallet.move

import android.animation.TimeInterpolator
import android.os.Bundle
import android.view.View
import android.view.animation.PathInterpolator
import androidx.appcompat.app.AppCompatActivity
import com.accubits.reltime.R
import com.accubits.reltime.databinding.ActivityWalletWithdrawSuccessBinding
import com.accubits.reltime.helpers.Utils
import com.accubits.reltime.utils.Extensions.copyToClipBoard
import com.accubits.reltime.utils.Extensions.shareReceipt
import com.accubits.reltime.utils.Extensions.showToast
import com.accubits.reltime.utils.convertRTOtoEURO
import com.google.android.material.bottomsheet.BottomSheetBehavior


class MoveSuccessActivity : AppCompatActivity() {

    companion object {
        const val TRANSACTION_ID = "transaction_id"
        const val TRANSACTION_TO = "transaction_to"
        const val TRANSACTION_FROM = "transaction_from"
        const val TIMESTAMP = "timestamp"

        const val FROM_AMOUNT = "from_amount"
        const val FROM_COIN_CODE = "from_coin_code"
        const val TO_AMOUNT = "to_amount"
        const val TO_COIN_CODE = "to_coin_code"


    }

    private lateinit var binding: ActivityWalletWithdrawSuccessBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWalletWithdrawSuccessBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setup()
        setData()
        actions()
    }


    private fun setup() {

        val behavior: BottomSheetBehavior<*> =
            BottomSheetBehavior.from<View>(binding.bottomSheetTransactionDetails)
        behavior.apply {
            peekHeight = resources.getDimensionPixelSize(R.dimen.dp_80)
            this.state = BottomSheetBehavior.STATE_COLLAPSED
        }
        setUpBottomSheetBehaviour(behavior)
        binding.ivCopy.visibility=View.VISIBLE
    }

    private fun actions() {
        binding.ivClose.setOnClickListener {
            finish()
        }
        binding.clSharedetails.setOnClickListener {
            share()
        }

        binding.ivCopy.setOnClickListener {
            copyToClipBoard(resources.getString(R.string.transcation_id), binding.tvTxnHash.text.toString())
        }
    }

    private fun setUpBottomSheetBehaviour(behavior: BottomSheetBehavior<*>){
        behavior.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {

            override fun onStateChanged(bottomSheet: View, newState: Int) {


            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                val lInter1: TimeInterpolator = PathInterpolator(.8f,-0.01f,.26f,.98f)
                val lInter: TimeInterpolator = PathInterpolator(.45f,1.11f,.49f,.96f)

                binding.viewTopArrow.alpha=1-lInter.getInterpolation(slideOffset)
                binding.tvTransactionDetails.alpha=1-lInter.getInterpolation(slideOffset)

                binding.viewHeaderBack.alpha=slideOffset
                binding.viewDash.alpha=lInter1.getInterpolation(slideOffset)
                binding.tvHead.alpha=lInter1.getInterpolation(slideOffset)
            }
        })
    }

    private fun setData() {

        intent.getStringExtra(TRANSACTION_TO)?.let {
            binding.tvTo.text = it.convertRTOtoEURO()
            binding.tvToAccountType.text=it.convertRTOtoEURO()
        }
        intent.getStringExtra(TRANSACTION_FROM)?.let {
            binding.tvFrom.text = it.convertRTOtoEURO()
            binding.tvFromAccountType.text=it.convertRTOtoEURO()
        }

        intent.getStringExtra(TRANSACTION_ID)?.let {
            binding.tvTxnHash.text = it
        }

        intent.getStringExtra(TIMESTAMP)?.let {
            binding.tvDate.text = Utils.getDateCurrentTimeZone1(it.toDouble(),Utils.DATE_FORMAT_DEFAULT)
            binding.tvDateTime.text =  Utils.getDateCurrentTimeZone1(it.toDouble())
            binding.tvTime.text = Utils.getDateCurrentTimeZone1(it.toDouble(),Utils.TIME_FORMAT_DEFAULT)
        }

        if (intent.getStringExtra(FROM_AMOUNT)!=null&&intent.getStringExtra(FROM_COIN_CODE)!=null)
        {
            binding.tvFromAmount.text = Utils.setAmountWithCoin(intent.getStringExtra(FROM_COIN_CODE), intent.getStringExtra(
                FROM_AMOUNT
            )?:"",sizeArray = arrayOf(0.5f, 1f, 0.5f))
            binding.tvMovedAmount.text = resources.getString(R.string.n_n,intent.getStringExtra(
                FROM_COIN_CODE
            ), intent.getStringExtra(FROM_AMOUNT))
        }
        if (intent.getStringExtra(TO_AMOUNT)!=null&&intent.getStringExtra(TO_COIN_CODE)!=null)
            binding.tvToAmount.text = Utils.setAmountWithCoin(intent.getStringExtra(TO_COIN_CODE),
                intent.getStringExtra(TO_AMOUNT)?:"", sizeArray = arrayOf(0.5f,1f,0.5f))

    }

    private fun share() {
        shareReceipt(binding.clDetails,resources.getString(R.string.move_successful))
    }
}