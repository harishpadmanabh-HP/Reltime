package com.accubits.reltime.activity.contacts.viewmodel

import android.annotation.SuppressLint
import android.database.Cursor
import android.provider.ContactsContract
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.accubits.reltime.activity.contacts.model.ContactData
import com.accubits.reltime.activity.contacts.model.ContactRequestModel
import com.accubits.reltime.activity.contacts.model.ContactResponseModel
import com.accubits.reltime.api.ApiCallStatus
import com.accubits.reltime.database.Contact
import com.accubits.reltime.database.ContactRepository
import com.accubits.reltime.helpers.PreferenceManager
import com.accubits.reltime.repository.ReltimeRepository
import com.accubits.reltime.utils.Extensions.createCharacterListMap
import com.accubits.reltime.utils.Extensions.removeAllSpaces
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

private const val TAG = "ContactsViewModel"

@HiltViewModel
class ContactsViewModel @Inject constructor(
    private val reltimeRepository: ReltimeRepository,
    private val preferenceManager: PreferenceManager,
    private val contactRepository: ContactRepository
) : ViewModel() {
    private var contactList: ArrayList<ContactData>? = null
    private var characterMap: HashMap<Int, Char>? = null


    /*val contactList: ArrayList<ContactData>? get() = _contactList
    val characterMap: HashMap<Int, Char>? get() = _characterMap*/

    var dataPairFlow: MutableStateFlow<Pair<Boolean, Pair<HashMap<Int, Char>, ArrayList<ContactData>>?>>? =
        MutableStateFlow(Pair(false, Pair(HashMap(), ArrayList())))
    var rowCountFlow: MutableStateFlow<Int>? = MutableStateFlow(-1)

    fun nullify() {
        contactList?.clear(); characterMap?.clear();
        contactList = null; characterMap = null;
        dataPairFlow = null
        rowCountFlow = null
    }

    fun syncContacts() {
        viewModelScope.launch(Dispatchers.IO) {
            val result = contactRepository.deleteAllContacts()
            withContext(Dispatchers.IO) {
                characterMap?.clear()
                contactList?.clear()
                rowCountFlow?.emit(0)
            }
        }
    }

    fun fetchDataFromDB() {
        viewModelScope.launch(Dispatchers.IO) {
            val rowList = contactRepository.contacts
            rowList.collect { list ->
                if (list.isEmpty()) {
                    rowCountFlow?.emit(0)
                } else {
                    contactList = ArrayList()
                    list.forEach {
                        contactList?.add(
                            ContactData(
                                contactName = it.contactName,
                                contactNumber = it.contactNumber.removeAllSpaces(),
                                contactType = it.contactType,
                                contactImageUri = it.contactImageUri,
                                exist = it.exist,
                                userId = it.userId
                            )
                        )
                    }
                    characterMap = createCharacterListMap(contactList)
                    dataPairFlow?.value = Pair(true, Pair(characterMap!!, contactList!!))
                    rowCountFlow?.emit(list.size)
                }
            }
        }
    }

    private fun updateContactListToRoomDB() {
        viewModelScope.launch(Dispatchers.IO) {
            val list = ArrayList<Contact>()
            contactList?.forEach {
                val contact = Contact(
                    id = 0,
                    contactName = it.contactName,
                    contactNumber = it.contactNumber,
                    contactImageUri = it.contactImageUri,
                    contactType = it.contactType,
                    exist = it.exist,
                    userId = it.userId
                )
                list.add(contact)
            }
            contactRepository.insertContacts(list)
            withContext(Dispatchers.Main) {
                //handle UI here
                if (characterMap != null && contactList != null) {
                    dataPairFlow?.value = Pair(true, Pair(characterMap!!, contactList!!))
                } else {
                    dataPairFlow?.value = Pair(false, null)
                }
            }
        }
    }

    @SuppressLint("Range")
    fun processContactList(
        cursor: Cursor?,
    ) {
        // work as background thread
        viewModelScope.launch() {
            contactList = ArrayList()
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
                    val has_number =
                        cursor.getString(
                            cursor.getColumnIndex(
                                ContactsContract.CommonDataKinds.Phone.HAS_PHONE_NUMBER
                            )
                        )
                    var photo_uri =
                        cursor.getString(
                            cursor.getColumnIndex(
                                ContactsContract.CommonDataKinds.Phone.PHOTO_URI
                            )
                        )
                    var type_of_contact =
                        cursor.getString(
                            cursor.getColumnIndex(
                                ContactsContract.CommonDataKinds.Phone.TYPE
                            )
                        )
                    //photo_uri = if (photo_uri == null) "null" else photo_uri
                    // Only contacts with numbers are saved.
                    if (has_number.toInt() == 1) {
                        val isAlreadyAvailble: ContactData? = contactList?.find {
                            it.contactNumber.removeAllSpaces() == number.removeAllSpaces() ||
                                    it.contactNumber.contains(number) || number.contains(it.contactNumber)
                        }
                        if (isAlreadyAvailble == null) {
                            Log.d("Contact", number.removeAllSpaces())
                            contactList?.add(
                                ContactData(
                                    contactName = name,
                                    contactNumber = number.removeAllSpaces(),
                                    contactImageUri = photo_uri,
                                    contactType = when (type_of_contact) {
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
                                    //getUserProfileLevel((0 until 4).random())
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
                if (localListForRequest.isEmpty()) {
                    dataPairFlow?.value = Pair(false, null)
                } else {
                    val contactRequestModel = ContactRequestModel()
                    contactRequestModel.contacts = localListForRequest
                    getUpdatedContactListFromServer(contactRequestModel)
                }
            }
        }
    }

    private fun getUpdatedContactListFromServer(
        contactRequestModel: ContactRequestModel,
    ) {
        viewModelScope.launch {
            Log.d("payload", "KEY = "+preferenceManager.getApiKey()+" contactdetail = "+contactRequestModel)
            val response = reltimeRepository.getRegisteredContactList(
                preferenceManager.getApiKey(),
                contactRequestModel
            )
            when (response.status) {
                ApiCallStatus.SUCCESS -> {
                    Log.d("payload", "response = "+response.data)
                    processContactDataFromServer(response.data)
                }
                ApiCallStatus.ERROR -> {
                    dataPairFlow?.value = Pair(false, null)
                }
                else -> {}
            }
        }
    }

    private fun processContactDataFromServer(
        data: ContactResponseModel?,
    ) {
        viewModelScope.launch() {
            if (data != null) {
                if (data.success && data.status == 200) {
                    val finalResult = data.result
                    finalResult.forEach { contact ->
                        contactList?.find { contact.contact_number == it.contactNumber }.let {
                            it?.exist = contact.is_exist
                            it?.userId = contact.user_id
                        }
                    }
                    contactList?.sortBy { it.contactName }
                    characterMap = createCharacterListMap(contactList)
                    if (characterMap != null) {
                        updateContactListToRoomDB()
                    } else {
                        dataPairFlow?.value = Pair(false, null)
                    }

                }
            }
        }
    }


    /*private fun createCharacterListMap(): HashMap<Int, Char>? {
        contactList?.apply {
            return if (isNotEmpty()) {
                val characterMap = HashMap<Int, Char>()
                var currentChar = ""
                forEachIndexed { index, contactData ->
                    val firstChar = contactData.contactName[0].toString()
                    if (firstChar != currentChar) {
                        currentChar = firstChar
                        characterMap[index] = contactData.contactName[0]
                    }
                }
                characterMap
            } else {
                null
            }
        }
        return null
    }*/


}