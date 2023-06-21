package com.accubits.reltime.activity.v2.transfer

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.accubits.reltime.R
import com.accubits.reltime.activity.v2.transfer.adapter.RecentTransactionAccountsAdapter
import com.accubits.reltime.activity.v2.transfer.pagingSource.TransferTransactionPagingV2Source
import com.accubits.reltime.activity.v2.transfer.viewmodel.TransferViewModel
import com.accubits.reltime.activity.v2.wallet.qrCode.QrCodeV2Activity
import com.accubits.reltime.databinding.ActivityTransferBinding
import com.accubits.reltime.helpers.Utils
import com.accubits.reltime.models.RowData
import com.accubits.reltime.utils.Extensions.openActivity
import com.accubits.reltime.utils.Extensions.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class TransferActivity : AppCompatActivity(),
    RecentTransactionAccountsAdapter.OnItemClickListener {
    private lateinit var binding: ActivityTransferBinding
    private val viewModel by viewModels<TransferViewModel>()

    private lateinit var recentTransacitionAccountsAdapter: RecentTransactionAccountsAdapter

    @Inject
    lateinit var transactionPagingSource: TransferTransactionPagingV2Source

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTransferBinding.inflate(layoutInflater)
        setContentView(binding.root)

        recentTransacitionAccountsAdapter = RecentTransactionAccountsAdapter(this, this)
        action()
        getTransactionData()
        handlePagination()

    }

    private fun handlePagination() {

        binding.rvRecentTransaction.layoutManager =
            GridLayoutManager(this, 4, LinearLayoutManager.VERTICAL, false)

        if (Utils.isNetworkAvailable(this)!!) {
            binding.rvRecentTransaction.adapter = recentTransacitionAccountsAdapter
            transactionPagingSource.setData(
                "",
            )
            getTransactionData()
        } else {
            Toast.makeText(
                this,
                getString(R.string.please_check_net),
                Toast.LENGTH_LONG
            ).show()
        }

        recentTransacitionAccountsAdapter.addLoadStateListener {

        }

        transactionPagingSource.getCountItem {

            if (it == 0) {
                binding.tvRecentTransactions.visibility = View.GONE
                binding.rvRecentTransaction.visibility = View.GONE
            } else {
                binding.tvRecentTransactions.visibility = View.VISIBLE
                binding.rvRecentTransaction.visibility = View.VISIBLE

            }
        }
    }

    private fun getTransactionData() {
        lifecycleScope.launch {
            viewModel.getPagedTransactionData().collectLatest {
                recentTransacitionAccountsAdapter.submitData(it)
            }
        }
    }


    private fun action() {
        binding.ivBack.setOnClickListener {
            finish()
        }

        binding.ivPhoneNumber.setOnClickListener {
            openActivity(PhoneNumberActivity::class.java)
        }
        binding.ivScanQr.setOnClickListener {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                openActivity(QrCodeV2Activity::class.java) {
                    this.putBoolean(QrCodeV2Activity.WALLET_DETAIL, false)
                }
            } else {
                cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
        binding.ivWallet.setOnClickListener {
            openActivity(WalletAddressActivity::class.java)

        }
        binding.ivEmail.setOnClickListener {
            openActivity(EmailAddressActivity::class.java)

        }

    }

    override fun OnRecentItemClickListener(rowData: RowData) {
        openActivity(SendAmountActivity::class.java) {
            putString(TransferObject.RECEIVER, rowData.mobile)
            putString(TransferObject.NAME, rowData.userName)
            putString(TransferObject.USERID, rowData.userId.toString())
            putString(TransferObject.PROFILE_IMAGE, rowData.userImage)
            putString(TransferObject.CONTACT_TYPE, Utils.TransferContactType.PHONE.contactType)

        }
    }

    private val cameraPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            openActivity(QrCodeV2Activity::class.java) {
                this.putBoolean(QrCodeV2Activity.WALLET_DETAIL, false)
            }
        } else showToast(resources.getString(R.string.permission_needed_to_continue))
    }
}