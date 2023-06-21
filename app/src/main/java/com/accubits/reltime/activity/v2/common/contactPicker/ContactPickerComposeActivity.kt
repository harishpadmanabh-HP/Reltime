package com.accubits.reltime.activity.v2.common.contactPicker

import android.Manifest
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.loader.app.LoaderManager
import androidx.loader.content.CursorLoader
import androidx.loader.content.Loader
import coil.compose.rememberAsyncImagePainter
import com.accubits.reltime.R
import com.accubits.reltime.activity.v2.common.accountpicker.components.*
import com.accubits.reltime.activity.v2.common.contactPicker.viewmodel.ContactFetchViewModel
import com.accubits.reltime.activity.v2.ui.theme.*
import com.accubits.reltime.api.ApiCallStatus
import com.accubits.reltime.database.Contact
import com.accubits.reltime.helpers.PreferenceManager
import com.accubits.reltime.utils.Extensions.isNetworkAvailable
import com.accubits.reltime.utils.Extensions.openSystemPermissionSettings
import com.accubits.reltime.utils.Extensions.showToast
import com.accubits.reltime.views.privacy.PrivacyActivity
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import javax.inject.Inject


@AndroidEntryPoint
class ContactPickerComposeActivity : ComponentActivity(), LoaderManager.LoaderCallbacks<Cursor> {
    companion object {
        private const val CONTACT_LOADER = 1
        const val EXTRA_SELECTED_CONTACT = "selected_contact"
        const val EXTRA_SHOW_ALL_CONTACTS = "show_all_contacts"
    }

    @Inject
    lateinit var preferenceManager: PreferenceManager

    private val showAllContacts: Boolean by lazy {
        intent.getBooleanExtra(
            EXTRA_SHOW_ALL_CONTACTS,
            false
        )
    }
    private val viewModel by viewModels<ContactFetchViewModel>()

    @ExperimentalPermissionsApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ReltimeTheme {
                AppScaffoldV2(R.string.contact_list, backIcon = R.drawable.ic_close, content =  {
                    RequestContactPermission(onPermissionGrand = {
                        loadContact()
                        Content(
                            viewModel = viewModel,
                            selectedContact = intent.getParcelableExtra(EXTRA_SELECTED_CONTACT),
                            showAllContacts = showAllContacts,
                            onConfirmClick = { selectedContact ->
                                setOnNextButtonClick(selectedContact)
                            }, onInviteClick = {
                                onInviteClick(it)
                            }
                        )
                    }, onPolicyClick = {
                        val intent = Intent(this, PrivacyActivity::class.java)
                        intent.putExtra(PrivacyActivity.URL, PrivacyActivity.PRIVACY_POLICY)
                        startActivity(intent)
                    })
                }, {
                    onBackPressed()
                })
            }
        }
    }

    private fun setOnNextButtonClick(selectedContact: Contact) {
        val returnIntent = Intent()
        returnIntent.putExtra(
            EXTRA_SELECTED_CONTACT,
            selectedContact
        )
        setResult(RESULT_OK, returnIntent)
        finish()
    }

    private fun onInviteClick(contact: Contact){
        try {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("smsto:${contact.contactNumber}")
                putExtra(
                    "sms_body",
                    resources.getString(
                        R.string.share_message,
                        preferenceManager.getName(),
                        preferenceManager.getReferalCodeFromLogin()
                    )
                )
            }
            startActivity(intent)
        } catch (activity: Exception) {

        }
    }

    private fun loadContact() {
        LoaderManager.getInstance(this@ContactPickerComposeActivity).initLoader(
            CONTACT_LOADER,
            null, this@ContactPickerComposeActivity
        )
    }

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor> {
        val uri: Uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI
        return CursorLoader(
            this,
            uri,
            null,
            null,
            null,
            ContactsContract.Contacts.DISPLAY_NAME_PRIMARY
        )
    }

    override fun onLoadFinished(loader: Loader<Cursor>, data: Cursor?) {
        if (isNetworkAvailable()) {
            viewModel.processContactList(data)
        } else showToast(getString(R.string.activity_network_not_available))
        LoaderManager.getInstance(this).destroyLoader(CONTACT_LOADER)
    }

    override fun onLoaderReset(loader: Loader<Cursor>) {

    }
}

@ExperimentalPermissionsApi
@Composable
private fun RequestContactPermission(
    onPermissionGrand: @Composable () -> Unit = {},
    onPolicyClick: () -> Unit
) {

    val cameraPermissionState = rememberPermissionState(
        Manifest.permission.READ_CONTACTS
    )
    when (cameraPermissionState.status) {
        // If the camera permission is granted, then show screen with the feature enabled
        PermissionStatus.Granted -> {
            onPermissionGrand.invoke()
        }
        is PermissionStatus.Denied -> {
            val context = LocalContext.current
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Text(//modifier = Modifier.padding(top = 36.dp),
                    text = stringResource(id = R.string.contact_permission_access),
                    fontSize = 22.sp,
                    style = MaterialTheme.typography.h6,
                    fontWeight = FontWeight.SemiBold
                )

                Text(
                    modifier = Modifier.padding(top = 24.dp),
                    text = stringResource(id = R.string.contact_permission_desc),//textToShow,
                    style = MaterialTheme.typography.body1,
                    textAlign = TextAlign.Center
                )
                Text(
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .clickable {
                            onPolicyClick()
                        },
                    text =
                    buildAnnotatedString {
                        append(stringResource(id = R.string.for_more_please_check_our))
                        withStyle(style = SpanStyle(color = AppBlue)) {
                            append(stringResource(id = R.string.privacy_policy_))
                        }
                    },
                    //  stringResource(id = R.string.please_check_our_privacy_policy),
                    style = MaterialTheme.typography.body1,
                    textAlign = TextAlign.Center
                )
                Button(
                    onClick = {
                        if (cameraPermissionState.status.shouldShowRationale)
                            cameraPermissionState.launchPermissionRequest()
                        else context.openSystemPermissionSettings()
                    },
                    Modifier
                        .padding(top = 60.dp, bottom = 150.dp),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = AppBlue,
                        contentColor = White
                    )
                ) {
                    Text(
                        modifier = Modifier.padding(start = 12.dp, end = 12.dp),
                        text = if (cameraPermissionState.status.shouldShowRationale)
                            stringResource(id = R.string.request_permission)
                        else stringResource(id = R.string.go_to_settings),
                        style = MaterialTheme.typography.body1,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun Content(
    viewModel: ContactFetchViewModel,
    selectedContact: Contact?,
    onConfirmClick: (Contact) -> Unit, showAllContacts: Boolean,onInviteClick: (Contact) -> Unit
) {
    val contacts = viewModel.getContactsFromDB(!showAllContacts).collectAsState(initial = arrayListOf()).value
    val response = viewModel.contactSyncResponseFlow.collectAsState().value
    if (contacts.isNotEmpty()) {
        ContactsContainer(
            contacts = contacts,
            selectedContact = selectedContact,
            onConfirmClick = onConfirmClick, onInviteClick =onInviteClick
        )
    } else {
        if (response.status == ApiCallStatus.LOADING) {
            Loader()
        } else if (response.status == ApiCallStatus.ERROR) {
            ErrorView(response.errorMessage)
        } else if (response.status == ApiCallStatus.SUCCESS) {
            if (response.data?.success == true || response.data?.status == 200) {
                if (response.data.result.isEmpty())
                    EmptyView(R.string.no_contacts_found)
                else Loader()
            } else {
                ErrorView(response.data?.message)
            }
        }
    }
}


@Composable
fun ContactsContainer(
    contacts: List<Contact>, selectedContact: Contact?, onConfirmClick: (Contact) -> Unit,onInviteClick: (Contact) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 12.dp)
    ) {

        val searchState = remember { mutableStateOf(TextFieldValue("")) }
        SearchView(searchState, R.string.search_for_contacts)
        Text(
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 8.dp),
            text = stringResource(id = R.string.my_contacts).uppercase(),
            style = MaterialTheme.typography.body2, color = White60
        )

        val selectedState = remember { mutableStateOf(selectedContact) }
        Box(modifier = Modifier.fillMaxSize()) {
            LazyColumn {
                val searchedText = searchState.value.text
                val filteredCountries = if (searchedText.isEmpty()) {
                    contacts
                } else {
                    val resultList = ArrayList<Contact>()
                    for (contact in contacts) {
                        if (contact.contactName.lowercase(Locale.getDefault())
                                .contains(searchedText.lowercase(Locale.getDefault())) ||
                            contact.contactNumber.lowercase(Locale.getDefault())
                                .contains(searchedText.lowercase(Locale.getDefault()))
                        ) {
                            resultList.add(contact)
                        }
                    }
                    resultList
                }
                filteredCountries.forEachIndexed { index, contact ->
                    item {
                        ContactItem(
                            contact,
                            !(index != 0 && filteredCountries[index - 1].contactName[0].uppercase() == contact.contactName[0].uppercase()),
                            selectedState, onInviteClick = onInviteClick
                        )
                    }
                }
                item {
                    Spacer(Modifier.height(70.dp))
                }
            }
            if (selectedState.value != null)
                AppGradientButton(text = stringResource(id = R.string.next)) {
                    selectedState.value?.let {
                        onConfirmClick(it)
                    }
                }
        }

    }
}


@Composable
fun ContactItem(
    contact: Contact,
    isFirstAlphabetItem: Boolean,
    selectedContactState: MutableState<Contact?>,onInviteClick: (Contact) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                if(contact.exist)
                selectedContactState.value = contact
            }
            .padding(start = 16.dp, end = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            modifier = Modifier
                .wrapContentSize()
                .alpha(if (isFirstAlphabetItem) 1f else 0f),
            text = contact.contactName[0].uppercase(),
            style = MaterialTheme.typography.subtitle2, color = AppLightBlue,
            textAlign = TextAlign.Center,
        )

        Column(
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(start = 14.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .background(color = AppBlue, shape = CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        modifier = Modifier
                            .wrapContentSize(),
                        text = contact.contactName[0].uppercase(),
                        style = MaterialTheme.typography.body1, color = White
                    )
                    if (contact.contactImageUri != null)
                        Image(
                            painter = rememberAsyncImagePainter(contact.contactImageUri),
                            contentDescription = "avatar",
                            contentScale = ContentScale.Crop,            // crop the image if it's not a square
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape)                       // clip to the circle shape
                        )

                }

                Text(
                    modifier = Modifier
                        .padding(
                            start = 16.dp,
                            end = 16.dp,
                            top = 8.dp,
                            bottom = 8.dp
                        )
                        .weight(1f),
                    text = contact.contactName,
                    style = MaterialTheme.typography.body1,
                    maxLines = 1, overflow = TextOverflow.Ellipsis
                )
                if(!contact.exist)
                    Text(modifier = Modifier.padding(start = 12.dp, end = 12.dp).clickable { onInviteClick(contact) },text = stringResource(id = R.string.invite).uppercase(), style =MaterialTheme.typography.body1, color = AppLightBlue )
                selectedContactState.value?.let {
                    if (it.contactNumber == contact.contactNumber)
                        Icon(
                            painter = painterResource(R.drawable.ic_checked_sort),
                            contentDescription = "",
                            tint = White,
                            modifier = Modifier
                                .size(20.dp)
                        )
                }
            }
            Divider(
                color = White20,
                thickness = 0.3.dp,
                modifier = Modifier
            )
        }

    }

}
