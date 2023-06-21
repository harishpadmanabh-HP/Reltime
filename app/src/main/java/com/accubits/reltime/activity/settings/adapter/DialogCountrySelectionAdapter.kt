package com.accubits.reltime.activity.settings.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Filter
import android.widget.Filterable
import androidx.emoji.text.EmojiCompat
import com.accubits.reltime.activity.region.model.Country
import com.accubits.reltime.activity.settings.popup.CountryListListener
import com.accubits.reltime.constants.ReltimeConstants
import com.accubits.reltime.databinding.LayoutCountryViewBinding
import com.accubits.reltime.helpers.Utils
import java.util.*

class DialogCountrySelectionAdapter(private val listListener: CountryListListener): BaseAdapter(), Filterable {

    private var countryList: ArrayList<Country> ?= ArrayList()
    private var filterList: ArrayList<Country> ?= ArrayList()

    fun setItems(itemList: ArrayList<Country>) {
       this.countryList = itemList
       this.filterList = itemList
    }

    override fun getCount(): Int = countryList!!.size //itemList.size

    override fun getItem(position: Int): Any = countryList!![position]

    override fun getItemId(position: Int): Long = 0

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val context = parent?.context
        val layoutCountryViewBinding =
            LayoutCountryViewBinding.inflate(LayoutInflater.from(context))
        setValues(layoutCountryViewBinding, position)
        return layoutCountryViewBinding.root
    }

    private fun setValues(layoutCountryViewBinding: LayoutCountryViewBinding, position: Int) {
        val country = getItem(position) as Country
        var selectedEmoji: String = ""
        try {
            selectedEmoji =
                EmojiCompat.get().process(Utils.convertToEmoji(country.countryISOCode)).toString()
        } catch (e: Exception) {

        }
        layoutCountryViewBinding.ejTv.text =
            selectedEmoji + ReltimeConstants.SPACE_MULTIPLIER + country.countryName
        layoutCountryViewBinding.llCountryParentView.setOnClickListener {
            country.emojiString =
                selectedEmoji + " " + country.dialCode
            listListener.onItemClick(country)
        }
    }

    override fun getFilter(): Filter {

        return object : Filter() {
            override fun performFiltering(p0: CharSequence?): FilterResults {
                val filteredList = filterList?.filter { it.countryName.lowercase(Locale.getDefault())
                    .contains(p0.toString().lowercase(Locale.getDefault()))||it.dialCode.lowercase(Locale.getDefault())
                    .contains(p0.toString().lowercase(Locale.getDefault()))
                }
                val filterResult = FilterResults()
                filterResult.values = filteredList
                filterResult.count = filteredList?.size?:0
                return filterResult;
            }

            override fun publishResults(p0: CharSequence?, filterResult: FilterResults?) {
                countryList = filterResult?.values as ArrayList<Country>?
                if (countryList?.isNotEmpty() ==true) {
                    listListener.onFilterList(isEmpty = false)
                } else {
                    listListener.onFilterList(isEmpty = true)
                }
                notifyDataSetChanged()
            }
        }

    }


}