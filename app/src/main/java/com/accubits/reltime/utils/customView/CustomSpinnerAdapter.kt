package com.accubits.reltime.utils.customView

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.annotation.NonNull

class CustomSpinnerAdapter(
    context: Context,
    private val textViewResourceId: Int,
    objects: ArrayList<String>
) :
    ArrayAdapter<String>(context, textViewResourceId, objects) {
    override fun getDropDownView(
        position: Int,
        convertView: View?,
        @NonNull parent: ViewGroup
    ): View {
        val textView: TextView = if (convertView == null) {
            LayoutInflater.from(context)
                .inflate(textViewResourceId, parent, false) as TextView
        } else {
            convertView as TextView
        }
        textView.text = getItem(position)
        if (position == 0) {
            val layoutParams = textView.layoutParams
            layoutParams.height = 1
            textView.layoutParams = layoutParams
        } else {
            val layoutParams = textView.layoutParams
            layoutParams.height = ITEM_HEIGHT
            textView.layoutParams = layoutParams
        }
        return textView
    }

    companion object {
        private const val ITEM_HEIGHT = ViewGroup.LayoutParams.WRAP_CONTENT
    }
}