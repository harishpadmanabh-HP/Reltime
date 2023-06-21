package com.accubits.reltime.activity.v2.common.contactPicker.viewmodel

import android.annotation.SuppressLint
import android.database.Cursor
import android.provider.ContactsContract
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.accubits.reltime.activity.contacts.model.ContactRequestModel
import com.accubits.reltime.activity.contacts.model.ContactResponseModel
import com.accubits.reltime.api.ApiCallStatus
import com.accubits.reltime.api.ApiMapper
import com.accubits.reltime.database.Contact
import com.accubits.reltime.database.ContactRepository
import com.accubits.reltime.helpers.PreferenceManager
import com.accubits.reltime.repository.ReltimeRepository
import com.accubits.reltime.utils.Extensions.removeAllSpaces
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ContactFetchViewModel @Inject constructor(
    private val reltimeRepository: ReltimeRepository,
    private val preferenceManager: PreferenceManager,
    private val contactRepository: ContactRepository,
) : ViewModel() {
    val contactSyncResponseFlow =
        MutableStateFlow(ApiMapper<ContactResponseModel>(ApiCallStatus.EMPTY, null, null))

    @SuppressLint("Range")
    fun processContactList(
        cursor: Cursor?,
    ) {
        // work as background thread
        viewModelScope.launch() {
            val contactList = ArrayList<Contact>()
            val localListForRequest = ArrayList<ContactRequestModel.Contact>()
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    val name =
                        cursor.getString(
                            cursor.getColumnIndex(
                                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_PRIMARY
                            )
                        ).trim()
                    val number =
                        cursor.getString(
                            cursor.getColumnIndex(
                                ContactsContract.CommonDataKinds.Phone.NUMBER
                            )
                        ).trim()
                    val hasNumber =
                        cursor.getString(
                            cursor.getColumnIndex(
                                ContactsContract.CommonDataKinds.Phone.HAS_PHONE_NUMBER
                            )
                        )
                    val photoUri =
                        cursor.getString(
                            cursor.getColumnIndex(
                                ContactsContract.CommonDataKinds.Phone.PHOTO_URI
                            )
                        )
                    val typeOfContact =
                        cursor.getString(
                            cursor.getColumnIndex(
                                ContactsContract.CommonDataKinds.Phone.TYPE
                            )
                        )
                    //photo_uri = if (photo_uri == null) "null" else photo_uri
                    // Only contacts with numbers are saved.
                    if (hasNumber.toInt() == 1) {
                        val isAlreadyAvailble: Contact? = contactList.find {
                            it.contactNumber.removeAllSpaces() == number.removeAllSpaces() ||
                                    it.contactNumber.contains(number) || number.contains(it.contactNumber)
                        }
                        if (isAlreadyAvailble == null) {
                            contactList.add(
                                Contact(
                                    contactName = name,
                                    contactNumber = number.removeAllSpaces(),
                                    contactImageUri = photoUri,
                                    contactType = when (typeOfContact) {
                                        "1" -> {
                                            "Home"
                                        }
                                        "2" -> {
                                            "Mobile"
                                        }
                                        "3" -> {
                                            "Work"
                                        }
                                        else -> {
                                            "Mobile"
                                        }
                                    }
                                )
                            )
                            localListForRequest.add(
                                ContactRequestModel.Contact(
                                    contact_number = number.removeAllSpaces(), name = name
                                )
                            )
                        }
                    }
                }
                syncContacts(ContactRequestModel(localListForRequest), contactList)
            }
        }
    }

    private fun syncContacts(
        contactRequestModel: ContactRequestModel, contactList: ArrayList<Contact>
    ) {
        viewModelScope.launch {
            try {
                contactSyncResponseFlow.value = ApiMapper(
                    ApiCallStatus.LOADING,
                    null,
                    null
                )
                val response = reltimeRepository.getRegisteredContactList(
                    preferenceManager.getApiKey(),
                    contactRequestModel
                )
                when (response.status) {
                    ApiCallStatus.SUCCESS -> {
                        processContactDataFromServer(response.data, contactList)
                        contactSyncResponseFlow.value =
                            ApiMapper(ApiCallStatus.SUCCESS, response.data, null)
                    }
                    ApiCallStatus.ERROR -> {
                        contactSyncResponseFlow.value =
                            ApiMapper(ApiCallStatus.ERROR, null, response.errorMessage)
                    }
                    else -> {}
                }
            } catch (e: Exception) {
                contactSyncResponseFlow.value =
                    ApiMapper(ApiCallStatus.ERROR, null, e.localizedMessage)
            }
        }
    }

    fun getContactsFromDB(showAllContacts: Boolean) =if (showAllContacts)
        contactRepository.getAllContacts(showAllContacts)
    else contactRepository.contacts


    private fun processContactDataFromServer(
        data: ContactResponseModel?, contactList: ArrayList<Contact>
    ) {
        viewModelScope.launch() {
            if (data != null) {
                if (data.success && data.status == 200) {
                    val finalResult = data.result
                    finalResult.forEach { contact ->
                        contactList.find { contact.contact_number == it.contactNumber }.let {
                            it?.exist = contact.is_exist
                            it?.userId = contact.user_id
                        }
                    }
                    contactList.sortBy { it.contactName }

                    updateContactsToRoomDB(contactList)
                }
            }
        }
    }

    private fun updateContactsToRoomDB(contactList: ArrayList<Contact>) {
        viewModelScope.launch(Dispatchers.IO) {
            contactRepository.deleteAllContacts()
            contactRepository.insertContacts(contactList)
        }
    }

}