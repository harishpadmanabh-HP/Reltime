package com.accubits.reltime.activity.v2.wallet.swap

import android.animation.TimeInterpolator
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.RelativeSizeSpan
import android.view.View
import android.view.animation.PathInterpolator
import androidx.appcompat.app.AppCompatActivity
import com.accubits.reltime.R
import com.accubits.reltime.databinding.ActivitySwapSuccessBinding
import com.accubits.reltime.helpers.Utils
import com.accubits.reltime.utils.Extensions.copyToClipBoard
import com.accubits.reltime.utils.Extensions.shareReceipt
import com.accubits.reltime.utils.convertRTOtoEURO
import com.accubits.reltime.utils.convertReltimeToNagra
import com.google.android.material.bottomsheet.BottomSheetBehavior


class SwapSuccessActivity : AppCompatActivity() {

    companion object {
        const val UI_TYPE = "ui_type"

        const val TRANSACTION_ID = "transaction_id"
        const val TRANSACTION_TO = "transaction_to"
        const val TRANSACTION_FROM = "transaction_from"
        const val TIMESTAMP = "timestamp"

        const val FROM_AMOUNT = "from_amount"
        const val FROM_COIN_CODE = "from_coin_code"
        const val TO_AMOUNT = "to_amount"
        const val TO_COIN_CODE = "to_coin_code"

        const val TRANSACTION_TYPE = "transaction_type"

    }

    private val uiType: String? by lazy { intent.getStringExtra(UI_TYPE) }

    private lateinit var binding: ActivitySwapSuccessBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySwapSuccessBinding.inflate(layoutInflater)
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
        binding.ivCopy.visibility = View.VISIBLE
    }

    private fun actions() {
        binding.clSharedetails.setOnClickListener {
            share()
        }
        binding.ivClose.setOnClickListener {
            finish()
        }
        binding.ivCopy.setOnClickListener {
            copyToClipBoard(
                resources.getString(R.string.transcation_id),
                binding.tvTxnHash.text.toString()
            )
        }
    }

    private fun setUpBottomSheetBehaviour(behavior: BottomSheetBehavior<*>) {
        behavior.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {

            override fun onStateChanged(bottomSheet: View, newState: Int) {


            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                val lInter1: TimeInterpolator = PathInterpolator(.8f, -0.01f, .26f, .98f)
                val lInter: TimeInterpolator = PathInterpolator(.45f, 1.11f, .49f, .96f)

                binding.viewTopArrow.alpha = 1 - lInter.getInterpolation(slideOffset)
                binding.tvTransactionDetails.alpha = 1 - lInter.getInterpolation(slideOffset)

                binding.viewHeaderBack.alpha = slideOffset
                binding.viewDash.alpha = lInter1.getInterpolation(slideOffset)
                binding.tvHead.alpha = lInter1.getInterpolation(slideOffset)
            }
        })
    }

    private fun setData() {
        if (uiType == SwapActivity.PAGE_ID) {
            binding.tvTitle.text = resources.getString(R.string.swap_success)
            if (intent.getStringExtra(FROM_AMOUNT) != null && intent.getStringExtra(FROM_COIN_CODE) != null)
                binding.tvFromAmount.text = Utils.setAmountWithCoin(
                    intent.getStringExtra(FROM_COIN_CODE), intent.getStringExtra(
                        FROM_AMOUNT
                    ) ?: "", sizeArray = arrayOf(0.5f, 1f, 0.5f)
                )
            if (intent.getStringExtra(TO_AMOUNT) != null && intent.getStringExtra(TO_COIN_CODE) != null)
                binding.tvToAmount.text = Utils.setAmountWithCoin(
                    intent.getStringExtra(TO_COIN_CODE),
                    intent.getStringExtra(TO_AMOUNT) ?: "", sizeArray = arrayOf(0.5f, 1f, 0.5f)
                )
            intent.getStringExtra(TRANSACTION_TO)?.let {
                binding.tvTo.text = it.convertRTOtoEURO().convertReltimeToNagra()
            }
            intent.getStringExtra(TRANSACTION_FROM)?.let {
                binding.tvFrom.text = it.convertRTOtoEURO().convertReltimeToNagra()
            }
        } else {
            binding.tvTitle.text = resources.getString(R.string.bridge_success)
            if (intent.getStringExtra(TRANSACTION_FROM) != null && intent.getStringExtra(
                    FROM_COIN_CODE
                ) != null
            ) {
                val from = resources.getString(
                    R.string.n_n,
                    intent.getStringExtra(FROM_COIN_CODE),
                    intent.getStringExtra(TRANSACTION_FROM)
                ).convertRTOtoEURO().convertReltimeToNagra()
                binding.tvFromAmount.text = setLabelSize(from)
                binding.tvFrom.text = from
            }
            if (intent.getStringExtra(TRANSACTION_TO) != null && intent.getStringExtra(TO_COIN_CODE) != null) {
                val to = resources.getString(
                    R.string.n_n,
                    intent.getStringExtra(TO_COIN_CODE),
                    intent.getStringExtra(TRANSACTION_TO)
                ).convertRTOtoEURO().convertReltimeToNagra()
                binding.tvToAmount.text = setLabelSize(to)
                binding.tvTo.text = to
            }
            if (intent.getStringExtra(TO_AMOUNT) != null && intent.getStringExtra(TO_COIN_CODE) != null) {
                binding.tvBridgeAmount.text = Utils.setAmountWithCoin(
                    intent.getStringExtra(TO_COIN_CODE),
                    intent.getStringExtra(TO_AMOUNT) ?: "", sizeArray = arrayOf(0.8f, 1f, 0.8f)
                )
                binding.tvAmountTitle.visibility = View.VISIBLE
                binding.tvBridgeAmount.visibility = View.VISIBLE
            }
        }

        intent.getStringExtra(TRANSACTION_ID)?.let {
            binding.tvTxnHash.text = it
        }

        intent.getStringExtra(TIMESTAMP)?.let {
            binding.tvDate.text =
                Utils.getDateCurrentTimeZone1(it.toDouble(), Utils.DATE_FORMAT_DEFAULT)
            binding.tvDateTime.text = Utils.getDateCurrentTimeZone1(it.toDouble())
            binding.tvTime.text =
                Utils.getDateCurrentTimeZone1(it.toDouble(), Utils.TIME_FORMAT_DEFAULT)

        }
    }

    private fun setLabelSize(value: String): SpannableStringBuilder {
        val coin = SpannableString(value)
        coin.setSpan(
            RelativeSizeSpan(0.8f),
            0,
            coin.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        val cointOut = SpannableStringBuilder()
        cointOut.append(coin)
        return cointOut
    }

    private fun share() {
        shareReceipt(
            binding.clReceipt, resources.getString(
                if (intent.getStringExtra(TRANSACTION_TYPE) == "Swap") R.string.swap_successful
                else R.string.bridge_successful
            )
        )
    }
}