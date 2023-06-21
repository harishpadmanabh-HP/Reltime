package com.accubits.reltime.activity.jointAccount

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.accubits.reltime.R
import com.accubits.reltime.activity.jointAccount.adapter.ViewJointAccountUserListAdapter
import com.accubits.reltime.activity.jointAccount.model.DeleteUserRequestModel
import com.accubits.reltime.activity.jointAccount.model.singleJointAccountModel.Member
import com.accubits.reltime.activity.jointAccount.model.singleJointAccountModel.Result
import com.accubits.reltime.activity.jointAccount.viewmodel.ViewJointAccountViewModel
import com.accubits.reltime.activity.myAccounts.model.JointAccount
import com.accubits.reltime.api.ApiCallStatus
import com.accubits.reltime.databinding.ActivityViewJointBinding
import com.accubits.reltime.helpers.Utils
import com.accubits.reltime.utils.Extensions.openActivity
import com.accubits.reltime.utils.Extensions.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class ViewJointActivity : AppCompatActivity(), View.OnClickListener {

    lateinit var binder: ActivityViewJointBinding
    private val viewModel by viewModels<ViewJointAccountViewModel>()
    private lateinit var jointAccount: JointAccount
    private var jointAccountId: Int? = null
    private lateinit var userListAdapter: ViewJointAccountUserListAdapter
    var itemPosition = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binder = ActivityViewJointBinding.inflate(layoutInflater)
        setContentView(binder.root)
        if (intent.hasExtra("jointAccount")) {
            jointAccount = intent.getSerializableExtra("jointAccount") as JointAccount
            itemPosition = intent.getIntExtra("jointAccountItemPosition", -1)
            jointAccountId = jointAccount.id
        } else if (intent.hasExtra("jointAccountId")) {
            jointAccountId = intent.getIntExtra("jointAccountId", 0)
        }
        setListeners()
        setupAdapters()
        fetchDetailsOfAccount()
        collectDetailsFlow()
    }

    private fun collectDetailsFlow() {
        lifecycleScope.launch {
            viewModel.jointAccountDetailsFlow.collectLatest { responseModel ->
                when (responseModel.status) {
                    ApiCallStatus.LOADING -> {
                        binder.svContainer.visibility = View.GONE
                        binder.tvError.visibility = View.GONE
                        binder.progressBar.visibility = View.VISIBLE
                    }
                    ApiCallStatus.SUCCESS -> {
                        binder.progressBar.visibility = View.GONE
                        val responseOk = responseModel.data?.result != null &&
                                responseModel.data.status == 200 && responseModel.data.success
                        if (responseOk) {
                            binder.svContainer.visibility = View.VISIBLE
                            val jointAccountDetails = responseModel.data?.result
                            jointAccountDetails?.apply {
                                val memberList: MutableList<Member> = ArrayList()
                                members.forEach { item ->
                                    if (item.isAccepted)
                                        if (item.isAdmin)
                                            memberList.add(0, item)
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
                            binder.progressBar.visibility = View.GONE
                            binder.tvError.visibility = View.VISIBLE
                            showToast(
                                responseModel.data?.message
                            )
                        }
                    }
                    ApiCallStatus.ERROR -> {
                        binder.tvError.visibility = View.VISIBLE
                        showToast(
                            responseModel.errorMessage
                        )
                    }
                    else -> {}
                }
            }
        }
    }

    private fun showProgress(show: Boolean) {
        if (show) {
            binder.progressBar.visibility = View.VISIBLE
            binder.rvUsersList.visibility = View.INVISIBLE
        } else {
            binder.rvUsersList.visibility = View.VISIBLE
            binder.progressBar.visibility = View.INVISIBLE
        }
    }

    private fun collectRemoveUser() {
        lifecycleScope.launch {
            viewModel.removeAccountResponseFlow.collectLatest { response ->
                when (response.status) {
                    ApiCallStatus.LOADING -> {
                        showProgress(true)
                    }
                    ApiCallStatus.SUCCESS -> {
                        val data = response.data
                        if (data != null && data.success && data.status == 200) {
                            if (data.result != null)
                                viewModel.signJoinAccountHash(data, "removeJointAccount")
                            else {
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
                                ?: "Some error occurred while removing account"
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
            viewModel.confirmationResponseFlow.collectLatest { response ->
                when (response.status) {
                    ApiCallStatus.LOADING -> {
                        showProgress(true)
                    }
                    ApiCallStatus.SUCCESS -> {
                        val data = response.data
                        if (data != null && data.success && data.status == 200) {
                            showProgress(false)
                            val returnIntent = Intent()
                            returnIntent.putExtra("itemPosition", itemPosition)
                            setResult(RESULT_OK, returnIntent)
                            finish()
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

    private fun updateUi(result: Result) {
        binder.apply {
            edAccountName.setText(result.name)
//            val splitDate = result.createdAt.split(" ")
//            edCreatedDate.setText(splitDate[0])
            edCreatedDate.setText(
                Utils.getDateCurrentTimeZone1(
                    result.createdAt,
                    Utils.DATE_FORMAT_DEFAULT
                )
            )
            edRtoBalance.setText(result.rtoBalance)
            edWalletAddress.setText(result.address)
            if (result.isAdmin) {
                ivDelete.visibility = View.VISIBLE
                btEditDetails.visibility = View.VISIBLE
            }
        }
    }

    private fun setupAdapters() {
        userListAdapter = ViewJointAccountUserListAdapter()
        binder.rvUsersList.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(this@ViewJointActivity)
            adapter = userListAdapter
        }
    }

    private fun fetchDetailsOfAccount() {
        viewModel.fetchJointAccounts(jointAccountId!!)
    }

    private fun setListeners() {
        binder.apply {
            ivBack.setOnClickListener(this@ViewJointActivity)
            btEditDetails.setOnClickListener(this@ViewJointActivity)
            ivDelete.setOnClickListener(this@ViewJointActivity)
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.iv_back -> {
                finish()
            }
            R.id.btEditDetails -> {
                openActivity(EditJointActivity::class.java) {
                    putSerializable("jointAccount", jointAccountId)
                }
            }
            R.id.iv_delete -> {
                createAlert()
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
        tvDes.text = resources.getString(R.string.edit_joint_account_delete_account_message)
        btnProcced.setOnClickListener(View.OnClickListener {
            dialog.dismiss()
            collectRemoveUser()
            collectResponse()
            viewModel.removeJointAccount(
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

    override fun onResume() {
        super.onResume()
        fetchDetailsOfAccount()
    }
}