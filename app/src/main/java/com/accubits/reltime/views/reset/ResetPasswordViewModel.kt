package com.accubits.reltime.views.reset

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.accubits.reltime.models.CommonModel
import com.accubits.reltime.models.NewMpinRequestModel
import com.accubits.reltime.models.NewPasswordRequestModel
import com.accubits.reltime.repository.ReltimeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ResetPasswordViewModel @Inject constructor(
    private val reltimeRepoFactory: ReltimeRepository,
) : ViewModel() {
    var commonModel: MutableLiveData<CommonModel> = MutableLiveData()

    fun dosetNewPassword(newPasswordRequestModel: NewPasswordRequestModel) {
        viewModelScope.launch {
            var response =
                reltimeRepoFactory.doSetNewPassword(newPasswordRequestModel)
            if (response.error) {
                commonModel.value = response
            } else {
                commonModel.value = response
            }
        }
    }
    fun dosetNewMpin(token: String, newMpinRequestModel: NewMpinRequestModel) {
        viewModelScope.launch {
            var response =
                reltimeRepoFactory.doSetNewMpin(token,newMpinRequestModel)
            if (response.error) {
                commonModel.value = response
            } else {
                commonModel.value = response
            }
        }
    }

}