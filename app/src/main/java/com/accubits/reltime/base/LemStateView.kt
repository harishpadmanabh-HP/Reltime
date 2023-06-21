package com.accubits.reltime.base

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import com.accubits.reltime.R
import com.accubits.reltime.databinding.LemStateViewBinding


class LEMStateView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private var binding: LemStateViewBinding? = null

    init {
        binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.lem_state_view, this, true)
    }

    fun setLEMStateViews(
        showEmptyView: Boolean = false, emptyMessage: String = "",
        showProgressView: Boolean = false, progressMessage: String = "",
        showErrorView: Boolean = false, errorMessage: String = "",
        errorBtnText: String = "", errorCallback: OnErrorBtnCallback? = null
    ) {

        binding?.showEmptyView = showEmptyView
        binding?.tvEmptyMessage?.text = emptyMessage

        binding?.showProgressView = showProgressView
        binding?.tvLoadingMessage?.text = progressMessage

        binding?.showErrorView = showErrorView
        binding?.tvErrorMessage?.text = errorMessage

        binding?.btnText?.visibility = if (errorBtnText.isEmpty()) View.GONE else View.VISIBLE
        binding?.btnText?.text = errorBtnText

        errorCallback?.let {
            binding?.btnText?.setOnClickListener { errorCallback.onErrorBtnClicked() }
        }
    }

}

interface OnErrorBtnCallback {
    fun onErrorBtnClicked()
}
