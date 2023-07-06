package com.accubits.reltime.activity.v2.transfer

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.accubits.reltime.R
import com.accubits.reltime.activity.v2.transfer.viewmodel.TransferViewModel
import com.accubits.reltime.databinding.ActivityTransferSuccessBinding
import com.accubits.reltime.databinding.ActivityTransferSuccessBinding.inflate
import com.accubits.reltime.helpers.Utils
import com.accubits.reltime.utils.Extensions.copyToClipBoard
import com.accubits.reltime.utils.Extensions.shareReceipt
import com.accubits.reltime.utils.convertRTOtoEURO
import com.google.android.material.bottomsheet.BottomSheetBehavior
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TransferSuccessActivity : AppCompatActivity() {

    private val viewModel by viewModels<TransferViewModel>()
    private lateinit var binding: ActivityTransferSuccessBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = inflate(layoutInflater)
        setContentView(binding.root)

        setup()
        setData()
        actions()
    }

    private fun actions() {

        binding.ivCopy.setOnClickListener {
            copyToClipBoard(resources.getString(R.string.transcation_id), binding.tvTxnId.text.toString())
        }
        binding.ivClose.setOnClickListener {
            finish()
        }
        binding.clSharedetails.setOnClickListener {
            share()
        }
    }

    private fun setup() {

        val behavior: BottomSheetBehavior<*> =
            BottomSheetBehavior.from<View>(binding.bottomSheetTransactionDetails)
        behavior.apply {
            peekHeight = resources.getDimensionPixelSize(R.dimen.dp_80)
            this.state = BottomSheetBehavior.STATE_COLLAPSED
        }
        Utils.handleBottomSheet(behavior, binding.clHeader)

    }

    private fun setData() {

        viewModel.timeStamp = intent.getStringExtra(TransferObject.TIMESTAMP).toString()
        viewModel.coinCode = intent.getStringExtra(TransferObject.COIN_CODE).toString()
        viewModel.mode = intent.getStringExtra(TransferObject.MODE).toString()
        viewModel.reciver = intent.getStringExtra(TransferObject.RECEIVER_NAME).toString()
        viewModel.senderName = intent.getStringExtra(TransferObject.SENDER_NAME).toString()
        viewModel.senderNumber = intent.getStringExtra(TransferObject.SENDER_NUMBER).toString()
        viewModel.senderEmail = intent.getStringExtra(TransferObject.SENDER_EMAIL).toString()
        viewModel.senderAddress =
            intent.getStringExtra(TransferObject.SENDER_WALLET_ADDRESS).toString()
        viewModel.receiverEmail = intent.getStringExtra(TransferObject.RECEIVER_EMAIL).toString()
        viewModel.receiverNumber = intent.getStringExtra(TransferObject.RECEIVER_NUMBER).toString()
        viewModel.receiverAddress =
            intent.getStringExtra(TransferObject.RECEIVER_WALLET_ADDRESS).toString()
        viewModel.profileImage = intent.getStringExtra(TransferObject.PROFILE_IMAGE).toString()

        binding.ivUserprofileimage.setFirstLetter(viewModel.reciver)

        when (viewModel.mode) {
            TransferObject.PHONE -> {
                binding.tvTo.text = "${viewModel.receiverNumber} ${viewModel.reciver}"
                binding.tvFrom.text = "${viewModel.senderNumber}  ${viewModel.senderName}"
                binding.tvNumber.text = viewModel.receiverNumber

            }
            TransferObject.EMAIL -> {
                binding.tvTo.text = "${viewModel.receiverEmail} ${viewModel.reciver}"
                binding.tvFrom.text = "${viewModel.senderEmail} ${viewModel.senderName}"
                binding.tvNumber.text = viewModel.receiverEmail

            }
            TransferObject.ADDRESS -> {
                binding.tvTo.text = "${viewModel.receiverAddress} ${viewModel.reciver}"
                binding.tvFrom.text = "${viewModel.senderAddress} ${viewModel.senderName}"
                binding.tvNumber.text = viewModel.receiverAddress

            }
        }

        viewModel.timeStamp.let {
            binding.tvDate.text = viewModel.timeStamp.toDouble()
                .let { Utils.getDateCurrentTimeZone1(it, Utils.DATE_FORMAT_DEFAULT) }
            binding.tvTransactionTimeDate.text =  Utils.getDateCurrentTimeZone1(viewModel.timeStamp.toDouble())
            binding.tvTime.text =
                viewModel.timeStamp.toDouble()
                    .let { Utils.getDateCurrentTimeZone1(it.toDouble(), Utils.TIME_FORMAT_DEFAULT) }


        }


        intent.getStringExtra(TransferObject.Amount)?.let {
            binding.tvAmount.text = Utils.setAmountWithCoin(viewModel.coinCode, it)
            binding.tvWithdrewAmount.text =
                resources.getString(R.string.n_n, viewModel.coinCode, it)
        }


        intent.getStringExtra(TransferObject.NAME)?.let {
            binding.tvToName.text = it
        }


        intent.getStringExtra(TransferObject.TRANSACTION_TO)?.let {
            binding.tvTo.text = it.convertRTOtoEURO()
        }
        intent.getStringExtra(TransferObject.TRANSACTION_FROM)?.let {
            binding.tvFrom.text = it.convertRTOtoEURO()
        }

        intent.getStringExtra(TransferObject.TRANSACTION_ID)?.let {
            binding.tvTxnId.text = it.toString()
        }

        intent.getStringExtra(TransferObject.ACCOUNT)?.let {
            binding.tvAccount.text = it.convertRTOtoEURO()
        }
    }

    private fun share() {
        shareReceipt(binding.clReceipt, resources.getString(R.string.transaction_successful))
    }
}

