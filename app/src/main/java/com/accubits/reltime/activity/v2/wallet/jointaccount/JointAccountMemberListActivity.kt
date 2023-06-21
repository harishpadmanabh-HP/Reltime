package com.accubits.reltime.activity.v2.wallet.jointaccount

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.accubits.reltime.R
import com.accubits.reltime.activity.jointAccount.model.DeleteUserRequestModel
import com.accubits.reltime.activity.jointAccount.model.PermissionRequestModel
import com.accubits.reltime.activity.jointAccount.model.singleJointAccountModel.Member
import com.accubits.reltime.api.ApiCallStatus
import com.accubits.reltime.databinding.ActivityJointAccountMemberListBinding
import com.accubits.reltime.models.PermissionList
import com.accubits.reltime.utils.Extensions.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class JointAccountMemberListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityJointAccountMemberListBinding
    private val viewModel by viewModels<JointAccountMemberViewModel>()
    private var memberList: ArrayList<Member>? = ArrayList()
    private lateinit var userListAdapter: JointAccountMemberListAdapter
    private var name: String? = null
    private var jointAccountId: Int = 0
    private var permissionList: ArrayList<PermissionList>? = null

    private val memberOptionBottomSheet by lazy {
        MemberOptionBottomSheet(this, { member: Member, s: String, permissionId: Int? ->
            viewModel.setAccountPermission(
                PermissionRequestModel(
                    jointAccount = jointAccountId,
                    user = member.user,
                    permission = permissionId
                )
            )
            hideBottomSheet()
        }, {
            removeUserAlert(it)
            hideBottomSheet()
        })
    }

    private fun hideBottomSheet() {
        if (memberOptionBottomSheet.isShowing)
            memberOptionBottomSheet.dismiss()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityJointAccountMemberListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
        setListeners()
        observeData()
    }

    private fun init() {
        name = intent.getStringExtra("name")
        val memberListArray = intent.getParcelableArrayListExtra<Member>("memberList")
        jointAccountId = intent.getIntExtra("jointAccountId", 0)
        fetchDetailsOfAccount()

        if (memberListArray != null) {
            for (item in memberListArray) {
                if (item.isAccepted)
                    if(item.isAdmin)
                        memberList!!.add(0,item)
                    else
                        memberList!!.add(item)
            }
        }

//        viewModel.getAccountPermission()
        populateValue()
    }

    private fun populateValue() {
        binding.layoutTitle.tvTitle.text = "$name Members"
        binding.layoutTitle.imgShare.visibility = View.GONE
        binding.rvUsersList.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        userListAdapter = JointAccountMemberListAdapter {
            Log.e("position", it.toString())
            memberOptionBottomSheet.getData(memberList?.get(it))
            if (!memberOptionBottomSheet.isShowing)
                memberOptionBottomSheet.show()
        }
        binding.rvUsersList.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(this@JointAccountMemberListActivity)
            adapter = userListAdapter
        }

        memberList?.let { userListAdapter.updateList(it) }
    }

    private fun setListeners() {
        binding.layoutTitle.ivBack.setOnClickListener {
            onBackPressed()
        }

    }

    private fun removeUserAlert(member: Member) {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.alert_dialog)
        val btnCancel: Button = dialog.findViewById(R.id.button) as Button
        val tvHeading: TextView = dialog.findViewById(R.id.tv_heading) as TextView
        val tvDes: TextView = dialog.findViewById(R.id.tv_des) as TextView
        val btnProcced: Button = dialog.findViewById(R.id.button2) as Button

        tvDes.text =
            "Are you sure you want to remove ${member.name}?"
        btnProcced.setOnClickListener(View.OnClickListener {
            dialog.dismiss()

            viewModel.removeUserFromJointAccount(
                DeleteUserRequestModel(
                    jointAccountId,
                    member.user
                )
            )
        })
        btnCancel.setOnClickListener(View.OnClickListener {
            dialog.dismiss()

        })
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.show()
    }

    private fun observeData() {
        lifecycleScope.launch {
            viewModel.removeUserResponseFlow.collectLatest { response ->
                when (response.status) {
                    ApiCallStatus.LOADING -> {
                    }
                    ApiCallStatus.SUCCESS -> {
                        val data = response.data
                        if (data != null && data.success && data.status == 200) {
                            if (data.result != null) {
                                showProgress(true)
                               viewModel.signJoinAccountHash(data, "removeUser")
                            } else {
                                showProgress(false)
                                showToast(data.message)
                            }
                        } else {
                            showProgress(false)
                            showToast(data?.message!!)
                        }
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

        lifecycleScope.launch {
            viewModel.confirmationResponseFlow.collectLatest { response ->
                when (response.status) {
                    ApiCallStatus.LOADING -> {
                    }
                    ApiCallStatus.SUCCESS -> {
                        val data = response.data
                        if (data != null && data.success && data.status == 200) {
                            showProgress(false)
                            fetchDetailsOfAccount()
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
                                val memberList: ArrayList<Member> = ArrayList()
                                for (item in members) {
                                    if (item.isAccepted)
                                        if(item.isAdmin)
                                            memberList.add(0,item)
                                        else
                                            memberList.add(item)
                                }
                                this@JointAccountMemberListActivity.memberList = memberList
                                if (memberList.isNotEmpty()) {
                                    userListAdapter.updateList(memberList)
                                    binding.rvUsersList.visibility = View.VISIBLE
                                    binding.emptyList.visibility = View.GONE
                                } else {
                                    binding.rvUsersList.visibility = View.GONE
                                    binding.emptyList.visibility = View.VISIBLE
                                }
                                this@JointAccountMemberListActivity.name = name
                                populateValue()
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

        lifecycleScope.launch {
            viewModel.getPermissionListResponseFlow.collectLatest { response ->
                when (response.status) {
                    ApiCallStatus.LOADING -> {
                    }
                    ApiCallStatus.SUCCESS -> {
                        val data = response.data
                        if (data != null && data.success && data.status == 200) {
                            showProgress(false)
                            permissionList = response.data.result
                            memberOptionBottomSheet.getPermissionData(permissionList)
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

        lifecycleScope.launch {
            viewModel.getPermissionResponseFlow.collectLatest { response ->
                when (response.status) {
                    ApiCallStatus.LOADING -> {
                    }
                    ApiCallStatus.SUCCESS -> {
                        val data = response.data
                        if (data != null && data.success && data.status == 200) {
                            if (data.result != null) {
                                showProgress(true)
                                viewModel.signJoinAccountHash(response.data, "setPermission")
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

    private fun fetchDetailsOfAccount() {
        viewModel.fetchJointAccounts(jointAccountId!!)
    }

    private fun showProgress(status: Boolean) {
        if(status)
            binding.progressBar.visibility = View.VISIBLE
        else binding.progressBar.visibility = View.GONE
    }
}