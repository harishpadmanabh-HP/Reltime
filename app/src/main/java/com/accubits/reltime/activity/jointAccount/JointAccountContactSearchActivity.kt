package com.accubits.reltime.activity.jointAccount

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.loader.app.LoaderManager
import androidx.loader.content.CursorLoader
import androidx.loader.content.Loader
import androidx.recyclerview.widget.LinearLayoutManager
import com.accubits.reltime.R
import com.accubits.reltime.activity.contacts.ContactItemListener
import com.accubits.reltime.activity.contacts.model.ContactData
import com.accubits.reltime.activity.contacts.viewmodel.ContactsViewModel
import com.accubits.reltime.activity.jointAccount.adapter.JointAccountContactSearchAdapter
import com.accubits.reltime.activity.jointAccount.model.singleJointAccountModel.Member
import com.accubits.reltime.constants.ReltimeConstants
import com.accubits.reltime.databinding.ActivityJointAccountContactSearchBinding
import com.accubits.reltime.helpers.PreferenceManager
import com.accubits.reltime.helpers.Utils
import com.accubits.reltime.utils.Extensions.createCharacterListMap
import com.accubits.reltime.utils.Extensions.isNetworkAvailable
import com.accubits.reltime.utils.Extensions.makeVisibility
import com.accubits.reltime.utils.Extensions.showAlertDialog
import com.accubits.reltime.utils.Extensions.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class JointAccountContactSearchActivity : AppCompatActivity(),
    LoaderManager.LoaderCallbacks<Cursor>,
    View.OnClickListener, ContactItemListener {

    private val viewModel by viewModels<ContactsViewModel>()
    private lateinit var binder: ActivityJointAccountContactSearchBinding
    private var contactsAdapter: JointAccountContactSearchAdapter? = null
    private val permissionRequest = Manifest.permission.READ_CONTACTS
    private val PERMISSION_REQUEST_CODE = 1001
    private val CONTACT_LOADER = 1
    private var isLoading: Boolean = true
    private var isInitialLoad = true

    @Inject
    lateinit var preferenceManager: PreferenceManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binder = ActivityJointAccountContactSearchBinding.inflate(layoutInflater)
        setContentView(binder.root)
        checkPermissions()
        setupSearchView()
        binder.tvSync.setOnClickListener(this)
        binder.ivClearText.setOnClickListener(this)
        binder.btDone.setOnClickListener(this)
        binder.ivBack.setOnClickListener(this)
    }

    private fun checkPermissions() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                permissionRequest
            ) == PackageManager.PERMISSION_GRANTED -> {
                // You can use the API that requires the permission.
                readContacts()
            }
            else -> {
                // You can directly ask for the permission.
                Utils.createContactDisclosureAlert(this) {
                    if (it == ReltimeConstants.DIALOG_POSITIVE) {
                        requestPermissions(
                            arrayOf(permissionRequest),
                            PERMISSION_REQUEST_CODE
                        )
                    } else finish()
                }
            }
        }
    }

    private fun setupSearchView() {
        binder.edSearchView.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val text = s.toString()
                if (text.isEmpty()) {
                    binder.ivClearText.visibility = View.INVISIBLE
                } else {
                    binder.ivClearText.visibility = View.VISIBLE
                }
                contactsAdapter?.filter?.filter(text)
            }

            override fun afterTextChanged(s: Editable?) {

            }

        })
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSION_REQUEST_CODE -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() &&
                            grantResults[0] == PackageManager.PERMISSION_GRANTED)
                ) {
                    // Permission is granted. Continue the action or workflow
                    // in your app.
                    readContacts()
                } else {
                    // Explain to the user that the feature is unavailable because
                    // the features requires a permission that the user has denied.
                    // At the same time, respect the user's decision. Don't link to
                    // system settings in an effort to convince the user to change
                    // their decision.
                    showToast(getString(R.string.contact_activity_grant_permission_read_contact))
                }
                return
            }

            // Add other 'when' lines to check for other
            // permissions this app might request.
            else -> {
                // Ignore all other requests.
            }
        }
    }

    private fun syncContacts() {
        contactsAdapter?.clearList()
        contactsAdapter?.notifyDataSetChanged()
        binder.tvStatusText.text = getString(R.string.contact_activity_loading_contacts)
        binder.progressBar.makeVisibility(true)
        binder.llProgressContainer.makeVisibility(true)
        binder.llContactsNotAvailable.makeVisibility(false)
        binder.tvSearchResults.makeVisibility(false)
        viewModel.syncContacts()
    }

    private fun readContacts() {
        lifecycleScope.launch {
            binder.llProgressContainer.makeVisibility(true)
            binder.progressBar.makeVisibility(true)
            viewModel.fetchDataFromDB()
            viewModel.rowCountFlow?.collectLatest {
                if (it == 0) { // When row count is zero, we need to load the contact from phone storage
                    isLoading = true
                    binder.tvStatusText.text =
                        getString(R.string.contact_activity_loading_contacts)
                    LoaderManager.getInstance(this@JointAccountContactSearchActivity).initLoader(
                        CONTACT_LOADER,
                        null, this@JointAccountContactSearchActivity
                    )
                }
            }
        }
        setAdapter()
    }

    private fun setAdapter() {
        contactsAdapter = JointAccountContactSearchAdapter(this)
        binder.rvContactRecycler.apply {
            layoutManager = LinearLayoutManager(this@JointAccountContactSearchActivity)
            setHasFixedSize(true)
           adapter = contactsAdapter
        }
        lifecycleScope.launch {
            viewModel.dataPairFlow?.collectLatest {
                isLoading = false
                if (it.first) {
                    binder.llProgressContainer.makeVisibility(false)
                    val existList = it.second?.second?.filter { it.exist }

                    if (existList.isNullOrEmpty()) {
                        onListEmpty(onSearch = false, isFiltering = true)
                    } else {
                        val selectedUserMap =
                            intent.getSerializableExtra("chosenContactMap") as HashMap<String, ContactData>?
                        var invitedContactMap: List<Member> = ArrayList()
                        if(intent.getSerializableExtra("invitedContactMap")!=null)
                         invitedContactMap =
                            intent.getSerializableExtra("invitedContactMap") as List<Member>
                        val characterMap =
                            createCharacterListMap(existList as ArrayList<ContactData>?)
                        contactsAdapter?.onDataChange(
                            characterMap!!,
                            existList,
                            selectedUserMap = selectedUserMap,
                            invitedContactMap= invitedContactMap
                        )
                        contactsAdapter?.notifyDataSetChanged()
                    }
                    onClick(binder.ivClearText) // to clear values in search box if not empty
                } else {
                    if (!isInitialLoad) {
                        binder.progressBar.makeVisibility(false)
                        binder.tvStatusText.text =
                            getString(R.string.contact_activity_no_contact_found)
                    }
                    isInitialLoad = false
                }
            }
        }
    }

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor> {
//        val uri: Uri = ContactsContract.Contacts.CONTENT_URI
        val uri: Uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI
        return CursorLoader(
            applicationContext,
            uri,
            null,
            null,
            null,
            ContactsContract.Contacts.DISPLAY_NAME_PRIMARY
        )
    }

    override fun onLoadFinished(loader: Loader<Cursor>, fetchedData: Cursor?) {
        if (isNetworkAvailable()) {
            viewModel.processContactList(fetchedData)
        } else {
            showToast(getString(R.string.activity_network_not_available))
            binder.tvStatusText.text = getString(R.string.activity_network_not_available)
            isLoading = false
        }
        LoaderManager.getInstance(this).destroyLoader(CONTACT_LOADER)
    }

    override fun onLoaderReset(loader: Loader<Cursor>) {

    }

    override fun onClick(v: View?) {
        binder.apply {
            when (v) {
                tvSync -> {
                    if (!isLoading) {
                        syncContacts()
                    } else {
                        showToast(getString(R.string.contact_activity_contact_sync_inprogress))
                    }
                }

                ivClearText -> {
                    binder.edSearchView.setText("")
                }

                btDone -> {
                    returnResultToActivity()
                }
                ivBack->{
                    onBackPressed()
                }
            }
        }

    }

    override fun onInviteClicked(contactData: ContactData) {

    }

    override fun onContactNameClicked(contactData: ContactData) {

    }

    override fun onContactIconClicked(contactData: ContactData) {

    }

    override fun onListEmpty(onSearch: Boolean, isFiltering: Boolean) {
        binder.llContactsNotAvailable.apply {
            visibility = if (onSearch) {
                View.VISIBLE
            } else {
                View.GONE
            }
        }
        binder.tvSearchResults.apply {
            visibility = if (isFiltering) {
                View.VISIBLE
            } else {
                View.GONE
            }
        }

    }

    private fun returnResultToActivity() {
        val resultIntent = Intent()
        resultIntent.putExtra("chosenUserFromList", contactsAdapter?.getSelectedContacts())
        setResult(RESULT_OK, resultIntent)
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.nullify()
        contactsAdapter?.clearList()
        binder.rvContactRecycler.adapter = null
        contactsAdapter = null
    }
}