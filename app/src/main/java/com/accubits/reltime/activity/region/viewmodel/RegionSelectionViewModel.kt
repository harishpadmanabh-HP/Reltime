package com.accubits.reltime.activity.region.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.accubits.reltime.activity.region.model.Country
import com.accubits.reltime.constants.ReltimeConstants.countries
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.launch
import java.io.*


class RegionSelectionViewModel constructor(
) : ViewModel() {
    var countryList: MutableLiveData<Boolean> = MutableLiveData()

    fun extractCountryFromJson(inputStream: InputStream) {

        viewModelScope.launch {
            val writer: Writer = StringWriter()
            val buffer = CharArray(1024)
            try {
                val reader: Reader = BufferedReader(InputStreamReader(inputStream, "UTF-8"))
                var n: Int
                while (reader.read(buffer).also { n = it } != -1) {
                    writer.write(buffer, 0, n)
                }
            } finally {
                inputStream.close()
            }
            val jsonString: String = writer.toString()
            val gson = Gson()
            val itemType = object : TypeToken<List<Country>>() {}.type
            countries = gson.fromJson<List<Country>>(jsonString, itemType) as ArrayList<Country>?
            countryList.value = true
        }
    }
}