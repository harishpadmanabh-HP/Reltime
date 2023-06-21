package com.accubits.reltime.activity.settings.popup

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.accubits.reltime.R
import com.accubits.reltime.activity.region.model.Country
import com.accubits.reltime.activity.region.viewmodel.RegionSelectionViewModel
import com.accubits.reltime.activity.settings.adapter.DialogCountrySelectionAdapter
import com.accubits.reltime.constants.ReltimeConstants.countries
import com.accubits.reltime.databinding.PopupCountrySearchBinding


fun AppCompatActivity.CountryPopupDialog(onCountrySelected: (country: Country) -> Unit) {
    val view = PopupCountrySearchBinding.inflate(layoutInflater)
    val viewModel by viewModels<RegionSelectionViewModel>()
    val dialog = Dialog(this)
    val listListener = object : CountryListListener{
        override fun onItemClick(country: Country) {
            onCountrySelected(country)
            dialog.dismiss()
        }
        override fun onFilterList(isEmpty: Boolean) {
            if (isEmpty) {
                view.tvError.visibility = View.VISIBLE
            } else {
                view.tvError.visibility = View.GONE
            }
        }
    }
    val adapter : DialogCountrySelectionAdapter ?= DialogCountrySelectionAdapter(listListener)
    view.tvError.visibility = View.INVISIBLE
    viewModel.countryList.observe(this) { countriesFetched ->
        if (countriesFetched){
            countries?.let {
                adapter?.setItems(it)
                view.lvCountrylist.adapter = adapter
            }
        } else {
            view.tvError.visibility = View.VISIBLE
        }
    }
    view.edPopupSearchView.addTextChangedListener(object : TextWatcher{
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            val searchText = s.toString().trim()
            if (searchText.isNotEmpty()) {
                adapter?.filter?.filter(searchText)
            }
        }

        override fun afterTextChanged(s: Editable?) {

        }

    })
    viewModel.extractCountryFromJson(resources.openRawResource(R.raw.country_codes))

    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
    dialog.setContentView(view.root)
    dialog.apply {
        show()
        window?.apply {
            setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            attributes.windowAnimations = R.style.BottomDialogAnimation
//            setGravity(Gravity.BOTTOM)
        }
    }
}

interface CountryListListener {
    fun onItemClick(country: Country)
    fun onFilterList(isEmpty: Boolean)
}