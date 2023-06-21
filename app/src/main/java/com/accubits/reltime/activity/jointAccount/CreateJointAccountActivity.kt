package com.accubits.reltime.activity.jointAccount

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.accubits.reltime.R
import com.accubits.reltime.activity.contacts.model.ContactData
import com.accubits.reltime.activity.jointAccount.adapter.JointAccountSelectedContactAdapter
import com.accubits.reltime.activity.jointAccount.model.CreateJointAccountRequestModel
import com.accubits.reltime.activity.jointAccount.viewmodel.CreateJointAccountViewModel
import com.accubits.reltime.api.ApiCallStatus
import com.accubits.reltime.databinding.ActivityAddJointBinding
import com.accubits.reltime.utils.Extensions.isNetworkAvailable
import com.accubits.reltime.utils.Extensions.markRequiredInRed
import com.accubits.reltime.utils.Extensions.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CreateJointAccountActivity : AppCompatActivity(), View.OnClickListener, UserListListener {
    private lateinit var binder: ActivityAddJointBinding
    private var chosenContacts: HashMap<String, ContactData>? = null
    private var activityResultLauncher: ActivityResultLauncher<Intent>? = null
    private var userListAdapter: JointAccountSelectedContactAdapter? = null
    private val viewModel by viewModels<CreateJointAccountViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binder = ActivityAddJointBinding.inflate(layoutInflater)
        setContentView(binder.root)
        prepareRegisters()
        setupAdapters()
        setListeners()
        collectTxnBuild()
        collectResponse()
    }

    private fun setListeners() {
        binder.clSelectPhoneNumber.setOnClickListener(this)
        binder.btInvite.setOnClickListener(this)
        binder.ivBack.setOnClickListener(this)
        binder.edSearchNumber.setOnClickListener(this)
        binder.ivSearchContact.setOnClickListener(this)
    }

    private fun collectTxnBuild() {
        lifecycleScope.launch {
            viewModel.txnBuildResponseFlow.collectLatest { response ->
                when (response.status) {
                    ApiCallStatus.LOADING -> {
                    }
                    ApiCallStatus.SUCCESS -> {
                        val data = response.data
                        if (data != null && data.success && data.status == 200) {
                            if (data.result != null) {
                                showProgress(true)
                                viewModel.signJoinAccountHash(data)
                            }
                            else {
                                showProgress(false)
                                showToast(data.message)
                            }
                        } else {
                            showProgress(false)
                            showToast(data?.message?:"Error server response")
                        }
                        //showToast(data?.message?:"Error server response")
                    }
                    ApiCallStatus.ERROR -> {
                        showToast(response.errorMessage?:"Some error occured while creating account")
                        showProgress(false)
                    }
                    else -> {}
                }
            }
        }

    }

    private fun collectResponse() {
        lifecycleScope.launch {
            viewModel.confirmationResponseFlow.collectLatest { response ->
                when (response.status) {
                    ApiCallStatus.SUCCESS -> {
                        val data = response.data
                        if (data != null && data.success && data.status == 200) {
                            finish()
                        } else {
                            showProgress(false)
                        }
                        showToast(data?.message?:"Error server response")
                    }
                    ApiCallStatus.ERROR -> {
                        showToast(response.errorMessage?:"Some error occurred while creating account")
                        showProgress(false)
                    }
                    else -> {}
                }
            }
        }
    }

    private fun showProgress(show: Boolean) {
        if (show) {
            binder.btInvite.visibility = View.INVISIBLE
            binder.progressBar.visibility = View.VISIBLE
            binder.rvUsersList.visibility = View.INVISIBLE
        } else {
            binder.btInvite.visibility = View.VISIBLE
            binder.rvUsersList.visibility = View.VISIBLE
            binder.progressBar.visibility = View.INVISIBLE
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.clSelectPhoneNumber, R.id.edSearchNumber, R.id.ivSearchContact -> {
                if (chosenContacts == null) {
                    chosenContacts = HashMap()
                }
                val resultIntent = Intent(this, JointAccountContactSearchActivity::class.java)
                resultIntent.putExtra("chosenContactMap", chosenContacts)
                activityResultLauncher?.launch(resultIntent)
            }
            R.id.btInvite -> {
                validateData()
            }
            R.id.iv_back -> {
                finish()
            }
        }
    }

    private fun validateData() {
        val accountName = binder.edAccountName.text.toString().trim()
        if (accountName.isNullOrEmpty()) {
            showToast("Enter a valid account name")
        }/* else if (chosenContacts == null || chosenContacts.isNullOrEmpty() || chosenContacts?.size?:0 < 2) {
            showToast("Add at least two contacts")
        } */else {
            val userList: ArrayList<Int> = ArrayList()
            val invalidUser = 0
            chosenContacts?.forEach {
                userList.add(it.value.userId?.toInt() ?: invalidUser)
            }
//            userList.clear()
//            userList.add(157)
//            userList.add(156)
            /*userList.apply {
                add(156); add(157)
            }*/
            if (isNetworkAvailable()) {
                showProgress(true)
                viewModel.createJointAccount(
                    CreateJointAccountRequestModel(
                        accountName = accountName,
                        user = userList
                    )
                )
            } else {
                showToast(getString(R.string.activity_network_not_available))
            }
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        chosenContacts = null; binder.rvUsersList.adapter = null
        userListAdapter = null; activityResultLauncher = null
    }

    private fun setupAdapters() {
        binder.tvAccountNameLabel.markRequiredInRed()
        binder.tvCreatedDateLabel.markRequiredInRed()
        userListAdapter = JointAccountSelectedContactAdapter(this)
        binder.rvUsersList.apply {
            layoutManager =
                LinearLayoutManager(this@CreateJointAccountActivity)
            setHasFixedSize(true)
            adapter = userListAdapter
        }
    }

    private fun prepareRegisters() {
        if (activityResultLauncher == null) {
            activityResultLauncher =
                registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { activityResult ->
                    if (activityResult.resultCode == Activity.RESULT_OK) {
                        chosenContacts =
                            activityResult.data?.getSerializableExtra("chosenUserFromList") as HashMap<String, ContactData>?
                        if (chosenContacts != null) {
                            updateList()
                        }
                    }
                }
        }
    }

    override fun onRemove(contactData: ContactData) {
        chosenContacts?.remove(contactData.userId!!)
        updateList()
    }

    private fun updateList() {
        if (chosenContacts.isNullOrEmpty()) {
            binder.rvUsersList.visibility = View.GONE
        } else {
            binder.rvUsersList.visibility = View.VISIBLE
        }
        userListAdapter?.updateList(chosenContacts!!)
        userListAdapter?.notifyDataSetChanged()
    }
}
