package com.accubits.reltime.mnemonic.adapter

import android.graphics.BlurMaskFilter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.accubits.reltime.R
import com.accubits.reltime.databinding.RvItemMemonicBinding

class MnemonicAdapter :
    RecyclerView.Adapter<MnemonicAdapter.ViewHolder>() {
    companion object{
        const val SPAN_COUNT=2
    }
    private val dataSet = ArrayList<String>()
    private var enableBlur: Boolean = false

    inner class ViewHolder(private val binder: RvItemMemonicBinding) :
        RecyclerView.ViewHolder(binder.root) {

        fun setData(position: Int) {
            val mid = dataSet.size / 2
            val newPosition=position.plus(1)
            val number =
                if (newPosition % 2 == 0) (newPosition / 2).plus(mid) else (newPosition / 2).plus(1)
            binder.tvNumber.text =number.toString().plus(".")
            binder.tvMnemonic.text =dataSet[number-1]

            if (enableBlur) {
                binder.tvNumber.setLayerType(View.LAYER_TYPE_SOFTWARE, null)
                binder.tvNumber.paint.maskFilter = null
                binder.tvMnemonic.setLayerType(View.LAYER_TYPE_SOFTWARE, null)
                binder.tvMnemonic.paint.maskFilter = null
            } else {
                binder.tvNumber.setLayerType(View.LAYER_TYPE_SOFTWARE, null)
                binder.tvMnemonic.setLayerType(View.LAYER_TYPE_SOFTWARE, null)
                binder.tvNumber.paint.maskFilter =
                    BlurMaskFilter(binder.tvNumber.textSize / 3, BlurMaskFilter.Blur.NORMAL)
                binder.tvMnemonic.paint.maskFilter =
                    BlurMaskFilter(binder.tvMnemonic.textSize / 3, BlurMaskFilter.Blur.NORMAL)
            }
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            RvItemMemonicBinding.inflate(
                LayoutInflater.from(viewGroup.context),
                viewGroup,
                false
            )
        )
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.setData(position)
    }

    override fun getItemCount() = dataSet.size

    fun setData(list: MutableList<String>) {
        dataSet.clear()
        dataSet.addAll(list)
        notifyItemRangeChanged(0, dataSet.size)
    }

    fun enableBlurView(enable: Boolean) {
        enableBlur = enable
        notifyItemRangeChanged(0, dataSet.size)
    }
}
