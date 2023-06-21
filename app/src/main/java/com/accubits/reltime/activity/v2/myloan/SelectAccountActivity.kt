package com.accubits.reltime.activity.v2.myloan

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.viewModels
import com.accubits.reltime.R
import com.accubits.reltime.databinding.ActivityMyLoanBinding
import com.accubits.reltime.databinding.ActivitySelectAccount2Binding
import com.accubits.reltime.models.GetLoanAccountSuccessModel
import dagger.hilt.android.AndroidEntryPoint
import smartadapter.SmartRecyclerAdapter
import smartadapter.viewevent.listener.OnCustomViewEventListener

@AndroidEntryPoint
class SelectAccountActivity : AppCompatActivity() {

    private val viewModel by viewModels<MyLoanViewModel>()
    private lateinit var dataBinding: ActivitySelectAccount2Binding
    var accountList = mutableListOf<GetLoanAccountSuccessModel.AccountModel>()

    lateinit var adapter: SmartRecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dataBinding = ActivitySelectAccount2Binding.inflate(layoutInflater)
        setContentView(dataBinding.root)

        if (intent.hasExtra("data")) {
            var list =
                intent.getParcelableArrayListExtra<GetLoanAccountSuccessModel.AccountModel>("data")
            //Toast.makeText(this,"size:::::::${list?.size}",Toast.LENGTH_LONG).show()

            accountList.addAll(list!!)
        }
        adapter = SmartRecyclerAdapter
            .items(accountList)
            .map(GetLoanAccountSuccessModel.AccountModel::class, LoanAccountsViewHolder::class)
            .add(OnCustomViewEventListener { event ->
                var selectedAccount =
                    event.adapter.getItem(event.position) as GetLoanAccountSuccessModel.AccountModel
                adapter.getItems().forEach {

                    if ((it as GetLoanAccountSuccessModel.AccountModel).id == selectedAccount.id)
                        it.isSelected = true
                    else
                        it.isSelected = false

                }
                adapter.notifyDataSetChanged()
            })
            .into<SmartRecyclerAdapter>(dataBinding.rvAccounts)

        dataBinding.btnPay.setOnClickListener {
            val intent = Intent()
            accountList.forEach {
                if (it.isSelected) {
                    intent.putExtra("selectedAccount", it)
                }

            }

            setResult(RESULT_OK, intent);
            finish();

        }
    }
}