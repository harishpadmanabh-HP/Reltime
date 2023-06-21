package com.accubits.reltime.activity.notification

import android.graphics.Typeface
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.accubits.reltime.R
import com.accubits.reltime.helpers.dateTimeUtils.DateTimeUtils
import com.accubits.reltime.models.RowNotificationModel
import com.accubits.reltime.utils.Extensions.createInitials
import com.accubits.reltime.utils.Extensions.loadImageWithUrl
import com.google.android.material.imageview.ShapeableImageView
import smartadapter.viewholder.SmartViewHolder
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*


open class NotificationItemViewHolder(parentView: ViewGroup) :
    SmartViewHolder<RowNotificationModel>(parentView, R.layout.item_notification) {

    // override lateinit var viewEventListner : OnViewEventListner
    init {
    }

    override fun bind(item: RowNotificationModel) {
        val tvTitle = itemView.findViewById(R.id.tv_notification_title) as TextView
        val tvNotificationTime = itemView.findViewById(R.id.tv_notification_time) as TextView
        val tvSeeMore = itemView.findViewById(R.id.tv_notification_see_more) as TextView
        val ivUserIcon = itemView.findViewById(R.id.ivUserIcon) as ShapeableImageView
        val tvLogChar = itemView.findViewById(R.id.tvLogoChar) as TextView
        val ivContainer = itemView.findViewById(R.id.ivContainer) as View
        val ivRead = itemView.findViewById(R.id.ivRead) as View

        tvTitle.text = item.message
        val parser: DateFormat = SimpleDateFormat("dd MMM yyyy HH:mm:ss", Locale.ENGLISH)
        val date: Date = parser.parse(item.created_at)

        if (item.isJointAccountRemoved) {
            activateView(
                ivContainer = ivContainer,
                tvTitle = tvTitle,
                tvNotificationTime = tvNotificationTime,
                tvSeeMore = tvSeeMore,
                false,
                Typeface.NORMAL
            )
            ivRead.visibility = View.GONE
        } else if (item.isRead) {
            activateView(
                ivContainer = ivContainer,
                tvTitle = tvTitle,
                tvNotificationTime = tvNotificationTime,
                tvSeeMore = tvSeeMore,
                true,
                Typeface.NORMAL
            )
            ivRead.visibility = View.GONE
        } else {
            activateView(
                ivContainer = ivContainer,
                tvTitle = tvTitle,
                tvNotificationTime = tvNotificationTime,
                tvSeeMore = tvSeeMore,
                true,
                Typeface.BOLD
            )
            ivRead.visibility = View.VISIBLE
        }

        tvNotificationTime.text = DateTimeUtils.getTimeAgo(itemView.context, date)
        if (item.type != 1) {
            tvSeeMore.visibility = View.GONE
        } else {
            if (item.isAccepted) {
                tvSeeMore.visibility = View.GONE
            } else {
                tvSeeMore.visibility = View.VISIBLE
            }
            //tvSeeMore.visibility = View.VISIBLE
        }
        ivUserIcon.visibility = View.GONE
        item.data?.let {
            tvLogChar.visibility = View.VISIBLE
            tvLogChar.text = it.created_by.createInitials()
        }
        if (!item.image.isNullOrEmpty()) {
            ivUserIcon.loadImageWithUrl(item.image) {
                if (it) {
                    ivUserIcon.visibility = View.VISIBLE
                    tvLogChar.visibility = View.GONE
                } else {
                    ivUserIcon.visibility = View.GONE
                    tvLogChar.visibility = View.VISIBLE
                }

            }
        }

    }

    private fun activateView(
        ivContainer: View,
        tvTitle: TextView,
        tvNotificationTime: TextView,
        tvSeeMore: TextView,
        toActivate: Boolean,
        style: Int
    ) {
        ivContainer.alpha = if (toActivate) 1f else .8f
        tvTitle.setTextColor(
            ContextCompat.getColor(
                tvTitle.context,
                if (toActivate) R.color.white else R.color.white60
            )
        )
        tvNotificationTime.setTextColor(
            ContextCompat.getColor(
                tvTitle.context,
                if (toActivate) R.color.white90 else R.color.white60
            )
        )
        tvSeeMore.setTextColor(
            ContextCompat.getColor(
                tvTitle.context,
                if (toActivate) R.color.white90 else R.color.white60
            )
        )
        tvTitle.setTypeface(null, style)
        tvTitle.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
    }
}