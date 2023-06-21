package com.accubits.reltime.activity.settings.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.accubits.reltime.models.UserSuccessModel
import com.accubits.reltime.repository.ReltimeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val repository: ReltimeRepository
) : ViewModel() {
    private val _userSuccessModel: MutableLiveData<UserSuccessModel> = MutableLiveData()
    val userSuccessModel: LiveData<UserSuccessModel> get() = _userSuccessModel

    fun getProfile(token: String) {
        viewModelScope.launch {
            try {
                val response =
                    repository.getProfile(token)
                _userSuccessModel.value = response
            } catch (e: Exception) {

            }
        }
    }
}