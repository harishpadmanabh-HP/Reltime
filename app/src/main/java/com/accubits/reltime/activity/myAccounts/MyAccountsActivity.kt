package com.accubits.reltime.activity.myAccounts

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.accubits.reltime.R
import com.accubits.reltime.activity.jointAccount.EditJointActivity
import com.accubits.reltime.activity.jointAccount.ViewJointActivity
import com.accubits.reltime.activity.myAccounts.adapter.MyAccountListAdapter
import com.accubits.reltime.activity.myAccounts.model.JointAccount
import com.accubits.reltime.activity.qrcode.QRCodeActivity
import com.accubits.reltime.activity.settings.MyAccountStartActivity
import com.accubits.reltime.activity.v2.wallet.myaccounts.viewmodel.MyAccountsViewModel
import com.accubits.reltime.api.ApiCallStatus
import com.accubits.reltime.databinding.ActivityMyAccountsBinding
import com.accubits.reltime.helpers.Utils
import com.accubits.reltime.utils.Extensions.openActivity
import com.accubits.reltime.utils.Extensions.showQRCodeDetailsBottomSheet
import com.accubits.reltime.utils.Extensions.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MyAccountsActivity : AppCompatActivity(), ItemClickListener, View.OnClickListener {

    private lateinit var binder: ActivityMyAccountsBinding
    private var myAccountAdapter: MyAccountListAdapter? = null
    private val viewModel by viewModels<MyAccountsViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binder = ActivityMyAccountsBinding.inflate(layoutInflater)
        setContentView(binder.root)
        setListeners(); setAdapter()
        collectAccountList()
        fetchAccounts()
    }

    private fun collectAccountList() {
        lifecycleScope.launch {
            viewModel.internalResponseFlow.collectLatest { response ->
                when (response.status) {
                    ApiCallStatus.LOADING -> {
                        binder.progressBar.visibility = View.VISIBLE
                        binder.rvAccountsList.visibility = View.GONE
                        binder.tvNoAccounts.visibility = View.GONE
                    }
                    ApiCallStatus.SUCCESS -> {
                        binder.progressBar.visibility = View.GONE
                        val responseOk = response.data?.status == 200 && response.data.success
                        if (response.data != null && responseOk) {
                            val accounts = Utils.buildAccountsList(response.data.result)
                            if (accounts.isEmpty()) {
                                binder.rvAccountsList.visibility = View.GONE
                                binder.tvNoAccounts.visibility = View.VISIBLE
                                return@collectLatest
                            }
                            myAccountAdapter?.submitList(accounts)
                            binder.tvNoAccounts.visibility = View.GONE
                            binder.rvAccountsList.visibility = View.VISIBLE
                        } else {
                            binder.tvNoAccounts.visibility = View.VISIBLE
                            binder.rvAccountsList.visibility = View.GONE
                        }
                    }

                    ApiCallStatus.ERROR -> {
                        binder.progressBar.visibility = View.GONE
                        binder.tvNoAccounts.visibility = View.VISIBLE
                        binder.rvAccountsList.visibility = View.GONE
                    }
                }
            }
        }
    }

    private fun fetchAccounts() {
        viewModel.getAccountList()
    }

    override fun onResume() {
        super.onResume()
        collectAccountList()
        fetchAccounts()
    }

    private fun setAdapter() {
        myAccountAdapter = MyAccountListAdapter(this)
        binder.rvAccountsList.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(this@MyAccountsActivity)
            adapter = myAccountAdapter
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.tv_add_account -> {
                openActivity(MyAccountStartActivity::class.java)
            }
            R.id.iv_back -> {
                finish()
            }
        }
    }

    private fun setListeners() {
        binder.tvAddAccount.setOnClickListener(this)
        binder.ivBack.setOnClickListener(this)
    }


    private fun displayPopupWindow(anchorView: View) {
        val popupView: View = layoutInflater.inflate(R.layout.popup_add_account, null)
        // create the popup window
        val width = LinearLayout.LayoutParams.WRAP_CONTENT
        val height = LinearLayout.LayoutParams.WRAP_CONTENT
        val popupWindow = PopupWindow(popupView, width, height, true)

        popupWindow.isOutsideTouchable = true
        popupWindow.isFocusable = true
        popupWindow.setBackgroundDrawable(null)
        popupWindow.showAsDropDown(anchorView)

        var tvRemove = popupView.findViewById<TextView>(R.id.tv_remove)
        var tvDefault = popupView.findViewById<TextView>(R.id.tv_make_Default)
        tvDefault.setOnClickListener {
            popupWindow.dismiss()
        }
        tvRemove.setOnClickListener {
            popupWindow.dismiss()
        }
    }

    override fun onAccountItemSelected(
        whichAccount: String,
        selectedAccount: Any,
        itemPosition: Int
    ) {
        when (whichAccount) {
            "jointAccount" -> {
//                openActivity(ViewJointActivity::class.java) {
//                    putSerializable("jointAccount", selectedAccount as JointAccount)
//                }
                val i = Intent(this, ViewJointActivity::class.java)
                i.putExtra("jointAccount", selectedAccount as JointAccount)
                i.putExtra("jointAccountItemPosition", itemPosition)
                startActivity(i)
            }
            "bankAccount" -> {

            }
            "card" -> {

            }
        }
    }

    override fun onJointAccountEdit(selectedJointAccount: JointAccount) {
        openActivity(EditJointActivity::class.java) {
            putInt("jointAccount", selectedJointAccount.id)
        }
    }

    override fun onQRSelect(accountAddress: String, title: String?) {
        //  this.showQRCodeDetailsBottomSheet(accountAddress)
        val intent = Intent(this, QRCodeActivity::class.java)
        intent.putExtra(QRCodeActivity.QR_STRING, accountAddress)
        intent.putExtra(QRCodeActivity.QR_TITLE, title)
        startActivity(intent)
    }

    override fun onCopySelect(accountAddress: String, title: String?) {
        this.showQRCodeDetailsBottomSheet(accountAddress, title)
    }

    override fun onDelete(whichAccount: String, selectedAccount: Any, itemPosition: Int) {
//        collectRemoveCardRespons(itemPosition)
//        viewModel.removeCard(
//           itemPosition.toString()
//        )
        showToast(resources.getString(R.string.this_feature_will_available_soon))
    }
}


interface ItemClickListener {
    fun onAccountItemSelected(whichAccount: String, selectedAccount: Any, itemPosition: Int)
    fun onJointAccountEdit(selectedAccount: JointAccount)
    fun onQRSelect(accountAddress: String, title: String?)
    fun onCopySelect(accountAddress: String, title: String?)
    fun onDelete(whichAccount: String, selectedAccount: Any, itemPosition: Int)
}