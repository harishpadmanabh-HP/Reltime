package com.accubits.reltime.activity.settings.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.accubits.reltime.models.CommonModel
import com.accubits.reltime.models.UpdateAddressRequestModel
import com.accubits.reltime.repository.ReltimeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileEditViewModel @Inject constructor(
    private var reltimeRepository: ReltimeRepository
) : ViewModel() {
    var commonModel: MutableLiveData<CommonModel> = MutableLiveData()

    fun setAddress(token: String, updateAddressRequestModel: UpdateAddressRequestModel) {
        viewModelScope.launch {
            var response =
                reltimeRepository.dosetAddress(token, updateAddressRequestModel)
            commonModel.value = response
        }
    }
}