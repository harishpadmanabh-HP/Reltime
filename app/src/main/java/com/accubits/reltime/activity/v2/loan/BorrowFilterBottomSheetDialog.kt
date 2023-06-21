package com.accubits.reltime.activity.v2.loan

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.Window
import com.accubits.reltime.R
import com.accubits.reltime.databinding.DialogFilterBinding
import com.accubits.reltime.helpers.Utils
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.slider.RangeSlider

class BorrowFilterBottomSheetDialog(
    context: Context,
    private val onFilter: (BottomSheetDialog) -> Unit,
    private val onClear: (BottomSheetDialog) -> Unit
) : BottomSheetDialog(context, R.style.TransparentBottomSheetDialogTheme) {
    var binding: DialogFilterBinding =
        DialogFilterBinding.inflate(layoutInflater)

    private var amountFrom = Utils.amount_from
    private var amountTo = Utils.amount_to
    private var installmentsFrom = Utils.installments_from
    private var installmentsTo = Utils.installments_to
    private var interestRate = Utils.interest_rate
    private var noCollateral = Utils.no_collateral
    private var collateralFrom = Utils.collateral_from
    private var collateralTo = Utils.collateral_to

    init {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(binding.root)
        setCanceledOnTouchOutside(true)

        init()
    }

    private fun init() {
        val sbLoanRange = binding.sbLoanRange
        val sbLoanTermRange = binding.sbLoanTermRange
        val sbCollateralRange = binding.sbCollateralRange
        val btnApplyFilter = binding.btnApplyFilter
        val btnClear = binding.btnClear
        val rbCollateral = binding.rbCollateral
        val rbInterestRate = binding.rbIntrestRate
        val tvNoCollateral = binding.tvNoCollateral
        val tvZero = binding.tvZero
        val tvLessFive = binding.tvLessFive
        val tvFiveToTen = binding.tvFiveToTen
        val tvGraterThenTen = binding.tvGraterThenTen
        val gCollateralRange = binding.gCollateralRange
        val tvLowRange = binding.tvLowLoanRange
        val tvHighLoanRange = binding.tvHighLoanRange
        val tvLowLoanTermRange = binding.tvLowLoanTermRange
        val tvHighLoanTermRange = binding.tvHighLoanTermRange
        val tvLowCollateralRange = binding.tvLowCollateralRange
        val tvHighCollateralRange = binding.tvHighCollateralRange
        val ivClose = binding.ivClose


        if (Utils.amount_from != "" && Utils.amount_to != "") {
            Utils.amount_to.let { it ->
                tvLowRange.text =
                    Utils.getRTOAmount(Utils.getFormattedAmount(it.toInt().toString()))
                Utils.amount_from.let {
                    amountFrom = Utils.amount_from
                    amountTo = Utils.amount_to

                    sbLoanRange.setValues(
                        Utils.amount_to.toFloat(),
                        Utils.amount_from.toFloat()
                    )
                    tvLowRange.text =
                        Utils.getRTOAmount(Utils.getFormattedAmount(amountFrom.toInt().toString()))
                    tvHighLoanRange.text =
                        Utils.getRTOAmount(Utils.getFormattedAmount(amountTo.toInt().toString()))
                }
            }
        } else {
            sbLoanRange.setValues(1F, 100000F)
            tvHighLoanRange.text = Utils.getRTOAmount(Utils.getFormattedAmount("100000"))
        }

        if (Utils.installments_from != "" && Utils.installments_to != "") {
            Utils.installments_to.let { it ->
                if (it == "1")
                    tvLowLoanTermRange.text = context.resources.getString(
                        R.string.n_month,
                        Utils.getFormattedAmount(it.toInt().toString())
                    )
                else
                    tvLowLoanTermRange.text = context.resources.getString(
                        R.string.n_months,
                        Utils.getFormattedAmount(installmentsFrom.toInt().toString())
                    )

                Utils.installments_from.let {
                    installmentsFrom = Utils.installments_from
                    installmentsTo = Utils.installments_to

                    sbLoanTermRange.setValues(
                        Utils.installments_to.toFloat(),
                        Utils.installments_from.toFloat()
                    )

                    tvHighLoanTermRange.text = context.resources.getString(
                        R.string.n_months,
                        Utils.getFormattedAmount(installmentsTo.toInt().toString())
                    )
                }
            }
        } else {
            sbLoanTermRange.setValues(1F, 36F)
            tvHighLoanTermRange.text =
                context.resources.getString(R.string.n_months, Utils.getFormattedAmount("36"))
        }


        if (Utils.interest_rate.isNotEmpty()) {
            when (Utils.interest_rate) {
                "1" -> {
                    tvZero.isChecked = true
                }
                "2" -> {
                    tvLessFive.isChecked = true
                }
                "3" -> {
                    tvFiveToTen.isChecked = true
                }
                "4" -> {
                    tvGraterThenTen.isChecked = true
                }
            }
        }

        if (noCollateral.isNotEmpty()) {
            tvNoCollateral.isChecked = true
            gCollateralRange.visibility = View.GONE
        } else if (collateralFrom.isNotEmpty() || collateralTo.isNotEmpty()) {
            if (Utils.collateral_from != "" && Utils.collateral_to != "") {
                Utils.collateral_to.let { it ->
                    tvLowCollateralRange.text =
                        Utils.getRTOAmount(Utils.getFormattedAmount(it.toInt().toString()))
                    Utils.collateral_from.let {
                        collateralFrom = Utils.collateral_from
                        collateralTo = Utils.collateral_to

                        sbCollateralRange.setValues(
                            Utils.collateral_to.toFloat(),
                            Utils.collateral_from.toFloat()
                        )
                        tvHighCollateralRange.text =
                            Utils.getRTOAmount(
                                Utils.getFormattedAmount(
                                    Utils.collateral_to.toInt().toString()
                                )
                            )
                        tvLowCollateralRange.text =
                            Utils.getRTOAmount(Utils.getFormattedAmount(it.toInt().toString()))
                    }
                }
            } else {
                sbCollateralRange.setValues(1F, 100000F)
                tvHighLoanTermRange.text =
                    Utils.getRTOAmount(Utils.getFormattedAmount("100000"))
            }
        }

        rbCollateral.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.tvNoCollateral -> {
                    gCollateralRange.visibility = View.GONE
                    collateralFrom = ""
                    collateralTo = ""
                    noCollateral = "1"

                }
                R.id.tvCollateralRange -> {
                    gCollateralRange.visibility = View.GONE
                    noCollateral = "0"
                    collateralFrom = "1"
                    collateralTo = "100000"

                }
                else -> {
                }
            }
        }
        rbInterestRate.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.tvZero -> {
                    interestRate = "1"
                }
                R.id.tvLessFive -> {
                    interestRate = "2"
                }
                R.id.tvFiveToTen -> {
                    interestRate = "3"
                }
                R.id.tvGraterThenTen -> {
                    interestRate = "4"
                }
                else -> {
                }
            }
        }

        btnApplyFilter.setOnClickListener {
            Utils.amount_from = amountFrom
            Utils.amount_to = amountTo
            Utils.installments_from = installmentsFrom
            Utils.installments_to = installmentsTo
            Utils.interest_rate = interestRate
            Utils.no_collateral = noCollateral
            Utils.collateral_from = collateralFrom
            Utils.collateral_to = collateralTo
            dismiss()
            onFilter.invoke(this)
        }
        btnClear.setOnClickListener {
            sbCollateralRange.setValues(
                1.toFloat(),
                100000.toFloat()
            )
            sbLoanRange.setValues(
                1.toFloat(),
                100000.toFloat()
            )
            sbLoanTermRange.setValues(
                1.toFloat(),
                36.toFloat()
            )

            rbInterestRate.clearCheck()
            rbCollateral.clearCheck()
            gCollateralRange.visibility = View.GONE
            Utils.amount_from = ""
            Utils.amount_to = ""
            Utils.installments_from = ""
            Utils.installments_to = ""
            Utils.interest_rate = ""
            Utils.no_collateral = ""
            Utils.collateral_from = ""
            Utils.collateral_to = ""

            dismiss()
            onClear.invoke(this)
        }

        ivClose.setOnClickListener {
            dismiss()
        }

        sbLoanRange.addOnChangeListener { rangeSlider: RangeSlider, _: Float, _: Boolean ->
            amountTo = rangeSlider.values[1].toInt().toString()
            amountFrom = rangeSlider.values[0].toInt().toString()
            tvLowRange.text = Utils.getRTOAmount(
                Utils.getFormattedAmount(
                    rangeSlider.values[0].toInt().toString()
                )
            )
            tvHighLoanRange.text = Utils.getRTOAmount(
                Utils.getFormattedAmount(
                    rangeSlider.values[1].toInt().toString()
                )
            )
        }

        sbLoanTermRange.addOnChangeListener { rangeSlider: RangeSlider, _: Float, _: Boolean ->
            installmentsTo = rangeSlider.values[1].toInt().toString()
            installmentsFrom = rangeSlider.values[0].toInt().toString()
            if (rangeSlider.values[0] == 1f)
                tvLowLoanTermRange.text =
                    context.resources.getString(
                        R.string.n_month,
                        Utils.getFormattedAmount(rangeSlider.values[0].toInt().toString())
                    )
            else
                tvLowLoanTermRange.text =
                    context.resources.getString(
                        R.string.n_months,
                        Utils.getFormattedAmount(rangeSlider.values[0].toInt().toString())
                    )
            tvHighLoanTermRange.text =
                context.resources.getString(
                    R.string.n_months,
                    Utils.getFormattedAmount(rangeSlider.values[1].toInt().toString())
                )
        }

        sbCollateralRange.addOnChangeListener { rangeSlider: RangeSlider, _: Float, _: Boolean ->
            collateralTo = rangeSlider.values[1].toInt().toString()
            collateralFrom = rangeSlider.values[0].toInt().toString()
            tvLowCollateralRange.text =
                Utils.getRTOAmount(
                    Utils.getFormattedAmount(
                        rangeSlider.values[0].toInt().toString()
                    )
                )
            tvHighCollateralRange.text =
                Utils.getRTOAmount(
                    Utils.getFormattedAmount(
                        rangeSlider.values[1].toInt().toString()
                    )
                )
        }



        apply {
            window?.apply {
                setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                attributes.windowAnimations = R.style.BottomDialogAnimation
                setGravity(Gravity.BOTTOM)
            }
        }
    }


}

