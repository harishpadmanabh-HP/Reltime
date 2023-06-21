package com.accubits.reltime.activity.notification

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.accubits.reltime.R
import com.accubits.reltime.activity.myAccounts.MyAccountsActivity
import com.accubits.reltime.activity.settings.viewmodel.NotificationDetailsViewModel
import com.accubits.reltime.api.ApiCallStatus
import com.accubits.reltime.databinding.ActivityNotificationDetailsBinding
import com.accubits.reltime.helpers.PreferenceManager
import com.accubits.reltime.helpers.Utils.DATE_TIME_FORMAT_DEFAULT
import com.accubits.reltime.models.AcceptRejectRequest
import com.accubits.reltime.models.DataNotificationModel
import com.accubits.reltime.models.RowNotificationModel
import com.accubits.reltime.utils.Extensions.convertDate
import com.accubits.reltime.utils.Extensions.createInitials
import com.accubits.reltime.utils.Extensions.loadImageWithUrl
import com.accubits.reltime.utils.Extensions.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class NotificationDetailsActivity : AppCompatActivity() {

    private val viewModel by viewModels<NotificationDetailsViewModel>()
    private lateinit var notificationBinding: ActivityNotificationDetailsBinding
    private var notificationData: RowNotificationModel? = null
    private var jointAccountNotificationData: DataNotificationModel? = null

    @Inject
    lateinit var preferenceManager: PreferenceManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        notificationBinding = ActivityNotificationDetailsBinding.inflate(layoutInflater)

        setContentView(notificationBinding.root)
        if (intent.hasExtra("NotificationData")) {
            notificationData = intent.getParcelableExtra("NotificationData")
            jointAccountNotificationData = notificationData?.data
            jointAccountNotificationData?.notificationMessage = notificationData?.message
        } else if (intent.hasExtra("notification")) {
            jointAccountNotificationData = intent.getParcelableExtra("notification")
        }

        notificationBinding.tvAccountName.text =
            "Account Name: ${jointAccountNotificationData?.name}"
        notificationBinding.tvCreatedBy.text =
            "Created By: ${jointAccountNotificationData?.created_by}"
        notificationData?.let {
            if (it.isAccepted) {
                notificationBinding.btAccept.visibility = View.GONE
                notificationBinding.btReject.visibility = View.GONE
            }
        }
        var createdDate: String? = ""
        try {
            val parser: DateFormat = SimpleDateFormat("dd MMM yyyy HH:mm:ss",Locale.ENGLISH)
            val endDateParser: DateFormat = SimpleDateFormat(DATE_TIME_FORMAT_DEFAULT)
            createdDate =
                endDateParser.format(parser.parse(jointAccountNotificationData?.created_at))
        } catch (e: Exception) {
            try {

                createdDate = jointAccountNotificationData?.created_at?.convertDate()

            } catch (ea: Exception) {
                createdDate = jointAccountNotificationData?.created_at
                ea.printStackTrace()
            }

            e.printStackTrace()
        }
        notificationBinding.tvCreatedDate.text =resources.getString(R.string.n_n_,resources.getString(R.string.created_date),createdDate)
        notificationBinding.tvNotificationTitle.text =
            jointAccountNotificationData?.notificationMessage

        if (notificationData?.image.isNullOrEmpty() || notificationData?.image.equals("NA")) {
            notificationBinding.ivUserIcon.visibility = View.GONE
            notificationBinding.tvLogoChar.visibility = View.VISIBLE
            notificationBinding.tvLogoChar.text =
                jointAccountNotificationData?.created_by?.createInitials()
        } else {
            notificationBinding.ivUserIcon.loadImageWithUrl(notificationData?.image) {
                if (it) {
                    notificationBinding.ivUserIcon.visibility = View.VISIBLE
                    notificationBinding.tvLogoChar.visibility = View.GONE
                } else {
                    notificationBinding.ivUserIcon.visibility = View.GONE
                    notificationBinding.tvLogoChar.visibility = View.VISIBLE
                    notificationBinding.tvLogoChar.text =
                        notificationData?.data?.created_by?.createInitials()
                }

            }
        }


        notificationBinding.btAccept.setOnClickListener {
            if (!preferenceManager.getMomic())
                showToast(resources.getString(R.string.wallet_not_available_error))
            else {
                viewModel.getAcceptRejectJointAccountRequest(
                    preferenceManager.getApiKey(),
                    AcceptRejectRequest(
                        isRejected = null,
                        isAccepted = true,
                        requestId = jointAccountNotificationData?.request_id,
                        jointAccountId = jointAccountNotificationData?.joint_account_id
                    )
                )
                updateUi()
            }
        }
        notificationBinding.btReject.setOnClickListener {
            if (!preferenceManager.getMomic())
                showToast(resources.getString(R.string.wallet_not_available_error))
            else {
                viewModel.getAcceptRejectJointAccountRequest(
                    preferenceManager.getApiKey(),
                    AcceptRejectRequest(
                        isRejected = true,
                        isAccepted = null,
                        requestId = jointAccountNotificationData?.request_id,
                        jointAccountId = jointAccountNotificationData?.joint_account_id
                    )
                )
                updateUi()
            }
        }

        notificationBinding.ivBack.setOnClickListener {
            onBackPressed()
        }
    }

    private fun updateUi() {
        lifecycleScope.launch {
            viewModel.acceptRejectModelModel.collectLatest { response ->
                when (response.status) {
                    ApiCallStatus.LOADING -> {
                        notificationBinding.progressBar.visibility = View.VISIBLE
                    }
                    ApiCallStatus.SUCCESS -> {
                        notificationBinding.progressBar.visibility = View.GONE
                        val data = response.data
                        if (data != null && data.success && data.status == 200) {
                            viewModel.signJoinAccountHash(data)
                            updateUIForConfirmation()
                        } else showToast(data?.message)
                    }
                    ApiCallStatus.ERROR -> {
                        notificationBinding.progressBar.visibility = View.GONE

                    }
                    else -> {}
                }
            }
        }
    }

    fun updateUIForConfirmation() {
        lifecycleScope.launch {
            viewModel.confirmationResponseFlow.collectLatest { response ->
                when (response.status) {
                    ApiCallStatus.LOADING -> {
                        notificationBinding.progressBar.visibility = View.VISIBLE
                    }
                    ApiCallStatus.SUCCESS -> {
                        notificationBinding.progressBar.visibility = View.GONE
                        val data = response.data
                        if (data?.success == true && data?.status == 200) {
                            var intent = Intent(
                                this@NotificationDetailsActivity,
                                MyAccountsActivity::class.java
                            )
                            startActivity(intent)
                            finish()
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