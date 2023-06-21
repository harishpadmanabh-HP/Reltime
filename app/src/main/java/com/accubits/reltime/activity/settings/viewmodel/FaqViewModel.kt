package com.accubits.reltime.activity.settings.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.accubits.reltime.models.FaqSuccessModel
import com.accubits.reltime.models.TagSuccessModel
import com.accubits.reltime.repository.ReltimeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FaqViewModel @Inject constructor(
    private var reltimeRepository: ReltimeRepository
) : ViewModel() {
    var tagSuccessModel: MutableLiveData<TagSuccessModel> = MutableLiveData()
    var faqSuccessModel: MutableLiveData<FaqSuccessModel> = MutableLiveData()

    fun getAllTag() {
        viewModelScope.launch {
            var response =
                reltimeRepository.getAllTag()
            tagSuccessModel.value = response
        }
    }

    fun getAllFaq(tagId: String) {
        viewModelScope.launch {
            var response =
                reltimeRepository.getAllFaq(tagId)
            faqSuccessModel.value = response
        }
    }
}