package com.accubits.reltime.activity.settings.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.accubits.reltime.R
import com.accubits.reltime.models.ResultTag

class FaqTagAdapter() :
    RecyclerView.Adapter<FaqTagAdapter.ImageViewHolder>() {

    private lateinit var resultTagListData: List<ResultTag>
    private var positionTag: Int = 0
    lateinit var context: Context
    private var onItemClickListener: ((ResultTag) -> Unit)? = null

    override fun getItemCount() = resultTagListData.size

    class ImageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvTagName: TextView = view.findViewById(R.id.tv_tage_name)
    }

    fun setItems(faqList: List<ResultTag>) {
        this.resultTagListData = faqList
    }

    override fun onBindViewHolder(
        holder: ImageViewHolder,
        @SuppressLint("RecyclerView") position: Int
    ) {
        if (positionTag == position) {
            holder.tvTagName.setTextColor(context.getColor(R.color.white))
        } else {
            holder.tvTagName.setTextColor(context.getColor(R.color.grey51))
        }
        holder.tvTagName.text = resultTagListData[position].key
        holder.tvTagName.setOnClickListener {
            positionTag = position
            notifyDataSetChanged()
            onItemClickListener?.let { it1 -> it1(resultTagListData[position]) }
        }
    }

    fun setOnItemClickListener(listener: (ResultTag) -> Unit) {
        onItemClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        context = parent.context
        return ImageViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.faq_tag, parent, false)
        )
    }
}