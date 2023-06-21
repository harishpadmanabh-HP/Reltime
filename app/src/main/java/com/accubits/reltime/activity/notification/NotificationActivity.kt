package com.accubits.reltime.activity.notification

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.accubits.reltime.R
import com.accubits.reltime.activity.jointAccount.ViewJointActivity
import com.accubits.reltime.activity.settings.viewmodel.NotificationViewModel
import com.accubits.reltime.activity.v2.loan.LendingDetailActivity1
import com.accubits.reltime.api.ApiCallStatus
import com.accubits.reltime.constants.ReltimeConstants
import com.accubits.reltime.databinding.ActivityNotificationBinding
import com.accubits.reltime.helpers.PreferenceManager
import com.accubits.reltime.models.RowNotificationModel
import com.accubits.reltime.utils.Extensions.openActivity
import com.accubits.reltime.utils.Extensions.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import smartadapter.SmartEndlessScrollRecyclerAdapter
import smartadapter.viewevent.listener.OnClickEventListener
import smartadapter.viewevent.model.ViewEvent
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class NotificationActivity : AppCompatActivity() {

    companion object {
        const val general: Int = 0
        const val jointAccountInvite: Int = 1
        const val joinAccountUpdates: Int = 2
        const val loanTransactions: Int = 3
        const val transfer: Int = 4
        const val deposit: Int = 5
        const val withdraw: Int = 6
    }

    private val viewModel by viewModels<NotificationViewModel>()
    private lateinit var notificationBinding: ActivityNotificationBinding

    @Inject
    lateinit var preferenceManager: PreferenceManager

    lateinit var adapter: SmartEndlessScrollRecyclerAdapter
    var currentPage: Int = 0;
    var dataList = mutableListOf<Any>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        notificationBinding = ActivityNotificationBinding.inflate(layoutInflater)

        setContentView(notificationBinding.root)

        adapter = SmartEndlessScrollRecyclerAdapter
            .items(dataList)
            .setEndlessScrollEnabled(true)
            .setAutoLoadMoreEnabled(true)
            .setOnLoadMoreListener { adapter, loadMoreViewHolder ->
                viewModel.getNotificationList(preferenceManager.getApiKey(), "${currentPage + 1}")
                updateUi()
            }
            .setLoadMoreLayoutResource(R.layout.custom_loadmore_view)
            .map(RowNotificationModel::class, NotificationItemViewHolder::class)
            .map(String::class, NotificationHeaderViewHolder::class)
            .add(OnClickEventListener { event: ViewEvent.OnClick ->
                if (adapter.getItem(event.position) is RowNotificationModel) {
                    val selectedItem = adapter.getItem(event.position) as RowNotificationModel
                    if (selectedItem.isJointAccountRemoved)
                        showToast(resources.getString(R.string.this_joint_account_not_available))
                    else if (selectedItem.type == jointAccountInvite && !selectedItem.isAccepted) {
                        val intent = Intent(
                            this@NotificationActivity,
                            NotificationDetailsActivity::class.java
                        )
                        intent.putExtra("NotificationData", selectedItem)
                        startActivity(intent)
                    } else if (selectedItem.type == joinAccountUpdates) {
                        selectedItem.data?.joint_account_id?.let {
                            val intent = Intent(
                                this@NotificationActivity,
                                ViewJointActivity::class.java
                            )
                            intent.putExtra(
                                "jointAccountId",
                                selectedItem.data?.joint_account_id
                            )
                            startActivity(intent)
                        }
                    } else if (selectedItem.type == loanTransactions) {
                        selectedItem.data?.loan_id?.let {
                            val intent = Intent(
                                this@NotificationActivity,
                                LendingDetailActivity1::class.java
                            )
                            intent.putExtra(ReltimeConstants.TransactionId, it.toString())
                            startActivity(intent)
                        }
                    } else if (selectedItem.type == transfer) {
                        showWalletPage()
                    } else if (selectedItem.type == deposit) {
                        showWalletPage()
                    } else if (selectedItem.type == withdraw) {
                        showWalletPage()
                    }

                    if (!selectedItem.isRead) {
                        viewModel.setReadAllNotification(
                            token = preferenceManager.getApiKey(),
                            notificationData = selectedItem,
                            position = event.position
                        )
                        updateUIToRead()
                    }
                }
            }).into(notificationBinding.notificationList)

        notificationBinding.ivBack.setOnClickListener {
            onBackPressed()
        }
        notificationBinding.tvMarkAsRead.setOnClickListener {
            viewModel.setReadAllNotification(
                token = preferenceManager.getApiKey(),
                notificationData = null,
                position = null
            )
            updateUIToRead()
        }
    }

    private fun showWalletPage() {
        openActivity(WalletV2Activity::class.java) {
            this.putString(WalletV2Activity.TITLE, resources.getString(R.string.wallet))
        }
    }

    override fun onResume() {
        super.onResume()
        currentPage = 0
        adapter.clear()
        adapter.isEndlessScrollEnabled = true

        /*
          if (com.accubits.reltime.helpers.Utils.isNetworkAvailable(this)!!) {
              viewModel.getNotificationList(preferenceManager.getApiKey(),"${currentPage+1}")
              updateUi()
          } else {
              Toast.makeText(this, getString(R.string.please_check_net), Toast.LENGTH_LONG).show()
          }*/
    }

    private fun updateUi() {
        lifecycleScope.launch {

            viewModel.notificationModel.collectLatest { response ->
                when (response.status) {
                    ApiCallStatus.LOADING -> {
                        //     notificationBinding.progressBar.visibility = View.VISIBLE
                    }
                    ApiCallStatus.SUCCESS -> {
                        notificationBinding.progressBar.visibility = View.GONE

                        if (response.data!!.status == 200) {
                            // dataList.clear()
                            if (response.data.result.num_pages == response.data.result.current_page) {
                                adapter.isEndlessScrollEnabled = false
                            }
                            if (!response.data.result.row.isNullOrEmpty()) {
                                currentPage++;
                                val parser: DateFormat =
                                    SimpleDateFormat("dd MMM yyyy HH:mm:ss", Locale.ENGLISH)
                                val endDateParser: DateFormat = SimpleDateFormat("dd MM yyyy")
                                var districtList = response.data.result.row!!.distinctBy {
                                    endDateParser.format(parser.parse(it.created_at))
                                }

                                districtList.forEach { dis ->
                                    val parser: DateFormat =
                                        SimpleDateFormat("dd MMM yyyy HH:mm:ss", Locale.ENGLISH)
                                    val endDateParser: DateFormat = SimpleDateFormat("dd MM yyyy")
                                    var date = endDateParser.format(parser.parse(dis.created_at));
                                    var filteredData = response.data.result.row!!.filter { fil ->
                                        date.equals(endDateParser.format(parser.parse(fil.created_at)))
                                    }

                                    var exsistingContains = dataList.filter { ct ->
                                        if (ct is String) {
                                            date.equals(endDateParser.format(parser.parse(ct)))
                                        } else {
                                            false
                                        }

                                    }
                                    if (exsistingContains.isNullOrEmpty()) {
                                        dataList.add(dis.created_at)
                                    }
                                    dataList.addAll(filteredData)


                                }
                                var unReadNotificationList = dataList.filter { isRead ->
                                    if (isRead is RowNotificationModel) {
                                        !isRead.isRead
                                    } else {
                                        false
                                    }
                                }
                                notificationBinding.notificationList.visibility = View.VISIBLE
                                notificationBinding.tvEmpty.visibility = View.GONE
                                if (unReadNotificationList.isNullOrEmpty()) {
                                    notificationBinding.tvMarkAsRead.visibility = View.GONE
                                } else {
                                    notificationBinding.tvMarkAsRead.visibility = View.VISIBLE
                                }
                                adapter.setItems(dataList)
                            } else {
                                notificationBinding.tvMarkAsRead.visibility = View.GONE
                                notificationBinding.notificationList.visibility = View.GONE
                                notificationBinding.tvEmpty.visibility = View.VISIBLE
                            }
                        } else {
                            Toast.makeText(
                                this@NotificationActivity,
                                response.data.message,
                                Toast.LENGTH_LONG
                            )
                                .show()
                        }
                    }
                    ApiCallStatus.ERROR -> {
                        notificationBinding.progressBar.visibility = View.GONE
                        /*Toast.makeText(
                            activity,
                            getString(R.string.some_thing_went_wrong),
                            Toast.LENGTH_LONG
                        )
                            .show()*/
                    }
                    else -> {}
                }
            }
        }
    }

    private fun updateUIToRead() {
        lifecycleScope.launch {
            viewModel.notificationReadModel.collectLatest { response ->
                when (response.status) {
                    ApiCallStatus.LOADING -> {
                        notificationBinding.progressBar.visibility = View.VISIBLE
                        notificationBinding.notificationList.visibility = View.GONE
                    }
                    ApiCallStatus.SUCCESS -> {
                        notificationBinding.progressBar.visibility = View.GONE
                        notificationBinding.notificationList.visibility = View.VISIBLE
                        if (response.data!!.status == 200) {
                            if (response.data.notificationData == null) {
                                if (com.accubits.reltime.helpers.Utils.isNetworkAvailable(
                                        applicationContext
                                    )!!
                                ) {
                                    adapter.getItems().forEach {
                                        if (it is RowNotificationModel) {
                                            it.isRead = true
                                        }
                                    }
                                    adapter.notifyDataSetChanged()
                                }
                            } else {
                                val updateModel = adapter.getItem(response.data.position!!)
                                val data = updateModel as RowNotificationModel
                                data.isRead = true
                                adapter.replaceItem(response.data.position!!, data)
                            }

                            /*else {

                                var updateModel = adapter.getItem(response.data.position!!)
                                var data = updateModel as RowNotificationModel
                                data.isRead = true
                                adapter.replaceItem(response.data.position!!, data)
                                if (updateModel.type == jointAccountInvite) {
                                    var intent = Intent(
                                        this@NotificationActivity,
                                        NotificationDetailsActivity::class.java
                                    )
                                    intent.putExtra("NotificationData", updateModel)
                                    startActivity(intent)
                                } else if (updateModel.type == joinAccountUpdates) {
                                    var intent = Intent(
                                        this@NotificationActivity,
                                        ViewJointActivity::class.java
                                    )
                                    intent.putExtra(
                                        "jointAccountId",
                                        updateModel.data?.joint_account_id
                                    )
                                    startActivity(intent)
                                } else if (updateModel.type == loanTransactions) {
                                    updateModel.data?.loan_id?.let {
                                        val intent = Intent(this@NotificationActivity, LendingDetailActivity1::class.java)
                                        intent.putExtra(ReltimeConstants.TransactionId, it.toString())
                                        startActivity(intent)
                                    }
                                } else if (updateModel.type == transfer) {
//                                    val intent = Intent(
//                                        this@NotificationActivity,
//                                        TransactionHistoryActivity::class.java
//                                    )
//                                    startActivity(intent)
                                    showWalletPage()
                                } else if (updateModel.type == deposit) {
//                                    val intent = Intent(
//                                        this@NotificationActivity,
//                                        TransactionHistoryActivity::class.java
//                                    )
//                                    startActivity(intent)
                                    showWalletPage()
                                } else if (updateModel.type == withdraw) {
//                                    val intent = Intent(
//                                        this@NotificationActivity,
//                                        TransactionHistoryActivity::class.java
//                                    )
//                                    startActivity(intent)
                                    showWalletPage()
                                } else {
                                    var unReadNotificationList = dataList.filter { isRead ->
                                        if (isRead is RowNotificationModel) {
                                            !isRead.isRead
                                        } else {
                                            false
                                        }
                                    }
                                    if (unReadNotificationList.isNullOrEmpty()) {
                                        notificationBinding.tvMarkAsRead.visibility = View.GONE
                                    } else {
                                        notificationBinding.tvMarkAsRead.visibility = View.VISIBLE
                                    }
                                }
                            }*/

                        } else {
                            notificationBinding.notificationList.visibility = View.VISIBLE
                            Toast.makeText(
                                this@NotificationActivity,
                                response.data.message,
                                Toast.LENGTH_LONG
                            )
                                .show()
                        }
                    }
                    ApiCallStatus.ERROR -> {
                        notificationBinding.progressBar.visibility = View.GONE

                    }
                    else -> {}
                }
            }
        }
    }
}