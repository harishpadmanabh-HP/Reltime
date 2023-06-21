package com.accubits.reltime.views.lend

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.accubits.reltime.R
import com.accubits.reltime.databinding.ActivityLendSuccessBinding
import com.accubits.reltime.helpers.Utils
import com.accubits.reltime.utils.Extensions.getBitmapFromView


class LendSuccessActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLendSuccessBinding
    var shareData = ""

    companion object {
        const val TITLE = "title"

        const val TRANSACTION_ID = "transaction_id"
        const val LEND_TO = "lend_to"
        const val MONTHLY_PAY = "monthly_pay"
        const val AMOUNT = "amount"
        const val INTEREST = "interest"
        const val INSTALLMENT = "installment"
        const val COLLATERAL = "collateral"
        const val DATE = "date"
        const val IS_COME_DIRECT_LANDING = "isComeFromDirectLanding"
        //  const val TIME = "time"

        const val SHARE_OPTION = "share_option"

        const val DONE_BUTTON_LABEL = "done_label"

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLendSuccessBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setData()
        binding.btGotoWallet.setOnClickListener {
            /*  val intent = Intent(this, ListedTokenActivity::class.java)
              startActivity(intent)*/
            finish()
        }
        binding.llShare.setOnClickListener {
            // shareTextViaApps(shareData)
            share()
        }

        if (intent.hasExtra(IS_COME_DIRECT_LANDING)) {
            binding.btGotoWallet.setText(R.string.go_to_my_lending)
        }
        intent.getStringExtra(DONE_BUTTON_LABEL)?.let {
            binding.btGotoWallet.text = it
        }
    }

    private fun setData() {
        intent.getStringExtra(TITLE)?.let {
            binding.textView66.text = it
        }

        intent.getStringExtra(TRANSACTION_ID)?.let {
            setDataRow(binding.tvListing, binding.tvId, "#$it")
        } ?: setDataRow(binding.tvListing, binding.tvId, null)

        setDataRow(
            binding.tvLoanAmountTitle,
            binding.tvLoanAmount,
            intent.getStringExtra(AMOUNT)
        )

        intent.getStringExtra(MONTHLY_PAY)?.let {
            setDataRow(binding.tvMonthlyPaymentTitle, binding.tvMonthlyPayment, it)
        } ?: setDataRow(binding.tvMonthlyPaymentTitle, binding.tvMonthlyPayment, null)

        intent.getStringExtra(LEND_TO)?.let {
            setDataRow(binding.tvLendToTitle, binding.tvLendTo, it)
        } ?: setDataRow(binding.tvLendToTitle, binding.tvLendTo, null)

        intent.getStringExtra(INTEREST)?.let {
            setDataRow(binding.tvInterestRateTitle, binding.tvInterestRate, "$it%")
        } ?: setDataRow(binding.tvInterestRateTitle, binding.tvInterestRate, null)

        intent.getStringExtra(INSTALLMENT)?.let {
            setDataRow(
                binding.tvLoanTermTitle,
                binding.tvLoanTerm,
                resources.getString(R.string.n_months, it)
            )
        } ?: setDataRow(binding.tvLoanTermTitle, binding.tvLoanTerm, null)

        val collateral =
            Utils.getCollateralValue(this, intent.getStringExtra(COLLATERAL).toString(), null, null)

        setDataRow(binding.tvCollateralTitle, binding.tvCollateral, collateral)

        intent.getStringExtra(DATE)?.let {
            setDataRow(binding.tvDateTitle, binding.tvDate, it)
        } ?: setDataRow(binding.tvDateTitle, binding.tvDate, null)

        binding.llShare.visibility =
            if (intent.getBooleanExtra(SHARE_OPTION, false)) View.VISIBLE else View.GONE


        buildText(binding.tvId, R.string.loan_id)
        buildText(binding.tvLoanAmount, R.string.loan_amount)
        buildText(binding.tvMonthlyPayment, R.string.monthly_installment)
        buildText(binding.tvLendTo, R.string.lent_to)
        buildText(binding.tvInterestRate, R.string.interest__rate)
        buildText(binding.tvPayBack, R.string.payback_period_)
        buildText(binding.tvLoanTerm, R.string.loan_term_)
        buildText(binding.tvCollateral, R.string.collateral)
        buildText(binding.tvDate, R.string.date)
    }

    private fun setDataRow(tvTitle: TextView, tvDesc: TextView, value: String?) {
        if (value == null) {
            tvTitle.visibility = View.GONE
            tvDesc.visibility = View.GONE
        } else {
            tvTitle.visibility = View.VISIBLE
            tvDesc.visibility = View.VISIBLE
            tvDesc.text = value
        }
    }


    private fun buildText(tv: TextView, id: Int) {
        if (tv.text.toString().isNotEmpty()) {
            shareData += resources.getString(
                R.string.n_n_n,
                resources.getString(id),
                tv.text.toString()
            )
        }
    }

    private fun share() {
        getBitmapFromView(binding.constraintLayout20) {
            val imageFile = Utils.storeFile(this, it)
            val intent = Intent()
            intent.action = Intent.ACTION_SEND

            val uri = imageFile?.let { it1 ->
                FileProvider.getUriForFile(
                    applicationContext, "$packageName.fileprovider",
                    it1
                )
            }
            intent.putExtra(Intent.EXTRA_STREAM, uri)
            intent.putExtra(Intent.EXTRA_TEXT, binding.textView66.text.toString())
            intent.type = "image/*"
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            startActivity(intent)

        }
    }

}