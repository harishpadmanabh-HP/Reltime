package com.accubits.reltime.activity.jointAccount

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.accubits.reltime.R
import com.accubits.reltime.activity.contacts.model.ContactData
import com.accubits.reltime.activity.jointAccount.adapter.ClickListener
import com.accubits.reltime.activity.jointAccount.adapter.JointAccountSelectedContactAdapter
import com.accubits.reltime.activity.jointAccount.adapter.PermissionListAdapter
import com.accubits.reltime.activity.jointAccount.adapter.ViewJointAccountUserListAdapter
import com.accubits.reltime.activity.jointAccount.model.DeleteUserRequestModel
import com.accubits.reltime.activity.jointAccount.model.EditJointAccountRequestModel
import com.accubits.reltime.activity.jointAccount.model.PermissionRequestModel
import com.accubits.reltime.activity.jointAccount.model.singleJointAccountModel.Member
import com.accubits.reltime.activity.jointAccount.model.singleJointAccountModel.Result
import com.accubits.reltime.activity.jointAccount.viewmodel.EditJointAccountViewModel
import com.accubits.reltime.activity.jointAccount.viewmodel.ViewJointAccountViewModel
import com.accubits.reltime.activity.myAccounts.model.JointAccount
import com.accubits.reltime.api.ApiCallStatus
import com.accubits.reltime.databinding.ActivityEditJointBinding
import com.accubits.reltime.models.PermissionList
import com.accubits.reltime.utils.Extensions.isNetworkAvailable
import com.accubits.reltime.utils.Extensions.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.io.Serializable

@AndroidEntryPoint
class EditJointActivity : AppCompatActivity(), View.OnClickListener,
    ViewJointAccountUserListAdapter.Listener, UserListListener {

    lateinit var binder: ActivityEditJointBinding
    private val viewModel by viewModels<ViewJointAccountViewModel>()
    private val editViewModel by viewModels<EditJointAccountViewModel>()
    private var jointAccountId: Int?=null
    private lateinit var userListAdapter: ViewJointAccountUserListAdapter
    private lateinit var newUserListAdapter: JointAccountSelectedContactAdapter
    private var chosenContacts: HashMap<String, ContactData>? = null
    private var activityResultLauncher: ActivityResultLauncher<Intent>? = null
    private var permissionList: ArrayList<PermissionList>? = null
    private var usersList: List<Member>? = null
    var currentMemberId = 0
    var isAdmin = false
    private lateinit var selectedMember: Member
    var deleteAccount = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binder = ActivityEditJointBinding.inflate(layoutInflater)
        setContentView(binder.root)
        setListeners()
        jointAccountId = intent.getIntExtra("jointAccount",0)
        setupAdapters()
        collectDetailsFlow()
        collectPermissions()
        collectResponse()
        collectTxnBuild()
        collectPermissionsList()
        collectRemoveUser()
        prepareRegisters()
        fetchDetailsOfAccount()
    }

    private fun setListeners() {
        binder.apply {
            ivBack.setOnClickListener(this@EditJointActivity)
            btSave.setOnClickListener(this@EditJointActivity)
            btAddNewUser.setOnClickListener(this@EditJointActivity)
            ivDelete.setOnClickListener(this@EditJointActivity)
        }
    }

    private fun collectDetailsFlow() {
        lifecycleScope.launch {
            viewModel.jointAccountDetailsFlow.collectLatest { responseModel ->
                when (responseModel.status) {
                    ApiCallStatus.LOADING -> {

                    }
                    ApiCallStatus.SUCCESS -> {
                        val responseOk =
                            responseModel.data?.status == 200 && responseModel.data.success
                        if (responseOk) {
                            val jointAccountDetails = responseModel.data!!.result
                            jointAccountDetails?.apply {
                                usersList = members
                                val memberList: MutableList<Member> = ArrayList()
                                for (item in members) {
                                    if (item.isAccepted)
                                        if(item.isAdmin)
                                            memberList.add(0,item)
                                        else
                                            memberList.add(item)
                                }
                                if (memberList.isNotEmpty()) {
                                    userListAdapter.updateList(memberList)
                                    binder.rvUsersList.visibility = View.VISIBLE
                                    binder.emptyList.visibility = View.GONE
                                } else {
                                    binder.rvUsersList.visibility = View.GONE
                                    binder.emptyList.visibility = View.VISIBLE
                                }
                                updateUi(this)
                            }
                        } else {
                            showToast(
                                responseModel.data?.message
                                    ?: "Some error occurred while fetching joint account details"
                            )
                        }
                    }
                    ApiCallStatus.ERROR -> {
                        showToast(
                            responseModel.errorMessage
                                ?: "Some error occurred while fetching joint account details"
                        )
                    }
                    else -> {}
                }
            }
        }
    }

    private fun updateUi(result: Result) {
        binder.apply {
            edAccountName.setText(result.name)
            isAdmin = result.isAdmin
            userListAdapter.setAdmin(isAdmin)
            edAccountName.isFocusable = isAdmin
            edAccountName.isClickable = isAdmin
            btAddNewUser.visibility = if (!isAdmin) View.GONE else View.VISIBLE
            btSave.visibility = if (!isAdmin) View.GONE else View.VISIBLE
            //   ivDelete.visibility=if (!isAdmin)View.GONE else View.VISIBLE
        }
    }

    private fun setupAdapters() {
        userListAdapter = ViewJointAccountUserListAdapter(this)
        binder.rvUsersList.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(this@EditJointActivity)
            adapter = userListAdapter
        }

        newUserListAdapter = JointAccountSelectedContactAdapter(this)
        binder.rvNewUsersList.apply {
            layoutManager =
                LinearLayoutManager(this@EditJointActivity)
            setHasFixedSize(true)
            adapter = newUserListAdapter
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
            binder.rvNewUsersList.visibility = View.GONE
        } else {
            binder.rvNewUsersList.visibility = View.VISIBLE
        }
        newUserListAdapter?.updateList(chosenContacts!!)
        newUserListAdapter?.notifyDataSetChanged()
    }

    private fun validateData() {
        val accountName = binder.edAccountName.text.toString().trim()
        if (accountName.isNullOrEmpty()) {
            showToast("Enter a valid account name")
        } /*else if (((chosenContacts?.size?:0)+(userListAdapter.itemCount)) < 2) {
            showToast("Add at least two contacts")
        } */ else {
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
                editViewModel.editJointAccount(
                    EditJointAccountRequestModel(
                        accountName = accountName,
                        jointAccount = jointAccountId,
                        user = userList
                    )
                )
            } else {
                showToast(getString(R.string.activity_network_not_available))
            }
        }

    }

    private fun collectTxnBuild() {
        lifecycleScope.launch {
            editViewModel.addUserResponseFlow.collectLatest { response ->
                when (response.status) {
                    ApiCallStatus.LOADING -> {
                    }
                    ApiCallStatus.SUCCESS -> {
                        val data = response.data
                        if (data != null && data.success && data.status == 200) {
                            if (data.result != null) {
                                showProgress(true)
                                editViewModel.signJoinAccountHash(data, "inviteUser")
                            } else {
                                showProgress(false)
                                showToast(data.message)
                                finish()
                            }
                        } else {
                            showProgress(false)
                            showToast(data?.message!!)
                        }
                        //showToast(data?.message?:"Error server response")
                    }
                    ApiCallStatus.ERROR -> {
                        showToast(
                            response.errorMessage ?: "Some error occured while updating account"
                        )
                        showProgress(false)
                    }
                    else -> {}
                }
            }
        }

    }

    private fun collectRemoveUser() {
        lifecycleScope.launch {
            editViewModel.removeUserResponseFlow.collectLatest { response ->
                when (response.status) {
                    ApiCallStatus.LOADING -> {
                    }
                    ApiCallStatus.SUCCESS -> {
                        val data = response.data
                        if (data != null && data.success && data.status == 200) {
                            if (data.result != null) {
                                showProgress(true)
                                if(deleteAccount)
                                    editViewModel.signJoinAccountHash(data, "removeJointAccount")
                                else
                                    editViewModel.signJoinAccountHash(data, "removeUser")
                            } else {
                                showProgress(false)
                                showToast(data.message)
                            }
                        } else {
                            showProgress(false)
                            showToast(data?.message!!)
                        }
                        //showToast(data?.message?:"Error server response")
                    }
                    ApiCallStatus.ERROR -> {
                        showToast(
                            response.errorMessage
                                ?: "Some error occurred while removing user account"
                        )
                        showProgress(false)
                    }
                    else -> {}
                }
            }
        }

    }

    private fun collectResponse() {
        lifecycleScope.launch {
            editViewModel.confirmationResponseFlow.collectLatest { response ->
                when (response.status) {
                    ApiCallStatus.LOADING -> {
                    }
                    ApiCallStatus.SUCCESS -> {
                        val data = response.data
                        if (data != null && data.success && data.status == 200) {
                            showProgress(false)
                            finish()
                            /*if(deleteAccount)
                                finish()
                            else
                                fetchDetailsOfAccount()*/
                        } else {
                            showProgress(false)
                        }
                        showToast(data?.message ?: "Error server response")
                    }
                    ApiCallStatus.ERROR -> {
                        showToast(response.errorMessage ?: "Some error occurred while confirming")
                        showProgress(false)
                    }
                    else -> {}
                }
            }
        }
    }

    private fun collectPermissionsList() {
        lifecycleScope.launch {
            editViewModel.getPermissionListResponseFlow.collectLatest { response ->
                when (response.status) {
                    ApiCallStatus.LOADING -> {
                    }
                    ApiCallStatus.SUCCESS -> {
                        val data = response.data
                        if (data != null && data.success && data.status == 200) {
                            showProgress(false)
                            permissionList = response.data.result
                            permissionAlert()
                        } else {
                            showProgress(false)
                            showToast(data?.message ?: "Error server response")
                        }
                    }
                    ApiCallStatus.ERROR -> {
                        showToast(response.errorMessage ?: "Some error occurred while confirming")
                        showProgress(false)
                    }
                    else -> {}
                }
            }
        }
    }

    private fun collectPermissions() {
        lifecycleScope.launch {
            editViewModel.getPermissionResponseFlow.collectLatest { response ->
                when (response.status) {
                    ApiCallStatus.LOADING -> {
                    }
                    ApiCallStatus.SUCCESS -> {
                        val data = response.data
                        if (data != null && data.success && data.status == 200) {
                            if (data.result != null) {
                                showProgress(true)
                                editViewModel.signJoinAccountHash(response.data, "setPermission")
                            } else {
                                showProgress(false)
                                showToast(data.message)
                            }
                        } else {
                            showProgress(false)
                            showToast(data?.message ?: "Error server response")
                        }
                    }
                    ApiCallStatus.ERROR -> {
                        showToast(
                            response.errorMessage ?: "Some error occurred while setting permission"
                        )
                        showProgress(false)
                    }
                    else -> {}
                }
            }
        }
    }

    private fun showProgress(show: Boolean) {
        if (show) {
            binder.btSave.visibility = View.INVISIBLE
            binder.progressBar.visibility = View.VISIBLE
            //binder.rvUsersList.visibility = View.INVISIBLE
        } else {
            binder.btSave.visibility = View.VISIBLE
            //binder.rvUsersList.visibility = View.VISIBLE
            binder.progressBar.visibility = View.INVISIBLE
        }
    }

    private fun fetchDetailsOfAccount() {
        viewModel.fetchJointAccounts(jointAccountId!!)
    }

    override fun onSetPermission(member: Member) {
        selectedMember = member
        currentMemberId = member.user
        if (permissionList == null) {
            showProgress(true)
            editViewModel.getAccountPermission()
        } else {
            permissionAlert()
        }
    }

    override fun onRemove(member: Member) {
        removeUserAlert(member)
    }

    override fun onClick(v: View?) {
        binder.apply {
            when (v?.id) {
                ivBack.id -> {
                    finish()
                }
                btSave.id -> {
                    validateData()
                }
                btAddNewUser.id -> {
                    if (chosenContacts == null) {
                        chosenContacts = HashMap()
                    }
                    val resultIntent = Intent(this@EditJointActivity, JointAccountContactSearchActivity::class.java)
                    resultIntent.putExtra("chosenContactMap", chosenContacts)
                    resultIntent.putExtra("invitedContactMap", usersList as ArrayList)
                    activityResultLauncher?.launch(resultIntent)
                }
                ivDelete.id->{
                    createAlert()
                }
            }
        }

    }

    private fun createAlert() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.alert_dialog)
        val btnCancel: Button = dialog.findViewById(R.id.button) as Button
        val tvHeading: TextView = dialog.findViewById(R.id.tv_heading) as TextView
        val tvDes: TextView = dialog.findViewById(R.id.tv_des) as TextView
        val btnProcced: Button = dialog.findViewById(R.id.button2) as Button
        // if button is clicked, close the custom dialog
        // if button is clicked, close the custom dialog
        //tvHeading.text = "Create New"
        tvDes.text =resources.getString(R.string.edit_joint_account_delete_account_message)
        btnProcced.setOnClickListener(View.OnClickListener {
            dialog.dismiss()
//            collectRemoveUser()
//            collectResponse()
            deleteAccount =  true
            editViewModel.removeJointAccount(
                DeleteUserRequestModel(
                    jointAccount = jointAccountId
                )
            )
        })
        btnCancel.setOnClickListener(View.OnClickListener {
            dialog.dismiss()

        })
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.show()
    }
    private fun permissionAlert() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.popup_permission)
        var selectedId = selectedMember.permissions
        val btApply: Button = dialog.findViewById(R.id.btApply) as Button
        val permissionListView: ListView = dialog.findViewById(R.id.permissionList) as ListView
        // if button is clicked, close the custom dialog
        // if button is clicked, close the custom dialog
        val adapter = PermissionListAdapter( selectedId, listener = object : ClickListener {
            override fun onClick(permission: PermissionList) {
                selectedId = permission.permissionId
            }
        })
        if (!isAdmin) {
            permissionList?.removeAll { it.label.contains("Withdraw") }
        }
        permissionList?.let { adapter.setItems(it) }
        permissionListView.adapter = adapter
        btApply.setOnClickListener(View.OnClickListener {
            if (selectedId == 0) {
                Toast.makeText(applicationContext, "Select permission", Toast.LENGTH_LONG).show()
            } else {
                showProgress(true)
                editViewModel.setAccountPermission(
                    PermissionRequestModel(
                        jointAccount = jointAccountId,
                        user = currentMemberId,
                        permission = selectedId
                    )
                )
                dialog.dismiss()
            }
        })
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.show()
    }

    private fun removeUserAlert(member: Member) {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.alert_dialog)
        val btnCancel: Button = dialog.findViewById(R.id.button) as Button
        val tvHeading: TextView = dialog.findViewById(R.id.tv_heading) as TextView
        val tvDes: TextView = dialog.findViewById(R.id.tv_des) as TextView
        val btnProcced: Button = dialog.findViewById(R.id.button2) as Button
        // if button is clicked, close the custom dialog
        // if button is clicked, close the custom dialog
        //tvHeading.text = "Create New"
        tvDes.text =
            "Are you sure you want to remove ${member.name}?"
        btnProcced.setOnClickListener(View.OnClickListener {
            dialog.dismiss()

            showProgress(true)
            editViewModel.removeUserFromJointAccount(
                DeleteUserRequestModel(
                    jointAccount = jointAccountId,
                    user = member.user
                )
            )
        })
        btnCancel.setOnClickListener(View.OnClickListener {
            dialog.dismiss()

        })
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.show()
    }
}