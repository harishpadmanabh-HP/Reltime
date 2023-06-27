package com.accubits.reltime.activity.v2.wallet.transactiondetail

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.accubits.reltime.R
import com.accubits.reltime.databinding.ActivityTransactionDetailsV2Binding
import com.accubits.reltime.helpers.Utils
import com.accubits.reltime.models.TransactionItem
import com.accubits.reltime.utils.Extensions.copyToClipBoard
import com.accubits.reltime.utils.Extensions.shareReceipt
import com.accubits.reltime.utils.convertRTOtoEURO
import com.accubits.reltime.utils.convertReltimeToNagra
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TransactionDetailV2Activity : AppCompatActivity() {

    private lateinit var binding: ActivityTransactionDetailsV2Binding
    private val item: TransactionItem? by lazy {
        intent.getParcelableExtra("ITEM")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTransactionDetailsV2Binding.inflate(layoutInflater)
        setContentView(binding.root)
        setData()
        setListener()
    }

    private fun setData() {
        binding.layoutTitle.tvTitle.text = getString(R.string.transaction_details)
        binding.layoutTitle.imgShare.visibility = View.GONE

        item?.let { transactionItem ->
            transactionData(it = transactionItem)
        }
    }

    private fun transactionData(it: TransactionItem) {
        binding.tvTransactionId.text = it.txnId
        binding.tvTo.text = it.receiver?.name?.convertRTOtoEURO()?.convertReltimeToNagra()
        binding.tvFrom.text = it.sender?.name?.convertRTOtoEURO()?.convertReltimeToNagra()

        binding.tvDate.text =
            Utils.getDateCurrentTimeZone1(it.timestamp.toDouble(), Utils.DATE_FORMAT_DEFAULT)
        binding.tvTime.text =
            Utils.getDateCurrentTimeZone1(it.timestamp.toDouble(), Utils.TIME_FORMAT_DEFAULT)

        binding.tvAmount.text = if (it.type == "Received")
            it.receiver?.let { receiver ->
                resources.getString(
                    R.string.n_n, Utils.coinCodeWithSymbol(
                        this,
                        receiver.coinCode, receiver.symbol
                    ), receiver.amount
                )
            }
        else
            it.sender?.let { receiver ->
                getString(
                    R.string.n_n, Utils.coinCodeWithSymbol(
                        this,
                        receiver.coinCode, receiver.symbol
                    ), receiver.amount
                )
            }
    }

    private fun setListener() {
        binding.layoutTitle.ivBack.setOnClickListener {
            onBackPressed()
        }
        binding.layoutShare.setOnClickListener {
            share()
        }
        binding.ivCopy.setOnClickListener {
            copyToClipBoard(
                resources.getString(R.string.transcation_id),
                binding.tvTransactionId.text.toString()
            )
        }
    }

    private fun share() {
        item?.let {
            shareReceipt(
                binding.layoutDetails, resources.getString(when (it.type) {
                    "Moved" -> R.string.move_successful
                    "Swap" -> R.string.swap_successful
                    "Bridging" -> R.string.bridge_successful
                    else -> R.string.transaction_successful
                })
            )
        }
    }
}
