package com.accubits.reltime.activity.settings.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.accubits.reltime.R
import com.accubits.reltime.activity.settings.model.FAQListItem
import com.accubits.reltime.databinding.FaqListviewBinding
import com.accubits.reltime.models.ResultFaqSuccessModel
import java.util.*

class FAQListAdapter() : BaseAdapter() {
    private var faqList: List<ResultFaqSuccessModel>? = null
    private lateinit var touchList: ArrayList<Boolean>
    fun setItems(faqList: List<ResultFaqSuccessModel>) {
        this.faqList = faqList
        touchList = ArrayList(Collections.nCopies(faqList.size, false))
    }

    override fun getCount(): Int {
        return faqList!!.size
    }

    override fun getItem(position: Int): Any {
        return faqList?.get(position) as FAQListItem
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val binder = FaqListviewBinding.inflate(LayoutInflater.from(parent?.context))
        onClick(binder, position)
        setContentVisibilty(binder, position)
        binder.tvHeader.text = faqList?.get(position)?.question
        binder.tvContent.text = faqList?.get(position)?.answer
        return binder.root
    }

    fun onClick(binder: FaqListviewBinding, position: Int) {
        binder.ivArrow.setOnClickListener {
            setContentVisibilty(binder, position, true)
        }
    }

    fun setContentVisibilty(
        binder: FaqListviewBinding,
        position: Int,
        viewOnTouch: Boolean = false
    ) {
        val visible = touchList[position]
        val element: Boolean
        if (visible) {
            binder.ivArrow.setImageResource(R.drawable.ic_up)
            binder.tvContent.visibility = View.VISIBLE
            element = false
        } else {
            binder.ivArrow.setImageResource(R.drawable.ic_down)
            binder.tvContent.visibility = View.GONE
            element = true
        }
        if (viewOnTouch) {
            touchList.removeAt(position)
            touchList.add(position, element)
            notifyDataSetChanged()
        }
    }

}
