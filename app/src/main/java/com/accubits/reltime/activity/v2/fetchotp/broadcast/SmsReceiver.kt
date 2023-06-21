package com.accubits.reltime.activity.v2.fetchotp.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.telephony.SmsMessage
import com.accubits.reltime.activity.v2.fetchotp.interfaces.SmsListener
import com.google.android.gms.auth.api.phone.SmsRetriever
import java.util.regex.Matcher
import java.util.regex.Pattern
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.Status


class SmsReceiver : BroadcastReceiver() {


    var b: Boolean? = null
    var abcd: String? = null
    var xyz: String? = null
    companion object {
        var mListener: SmsListener? = null
        fun bindListener(listener: SmsListener) {
            mListener = listener
        }
    }
    override fun onReceive(p0: Context?, intent: Intent?) {
        if (intent?.action == SmsRetriever.SMS_RETRIEVED_ACTION) {
            val extras = intent?.extras
            val status = extras!![SmsRetriever.EXTRA_STATUS] as Status?
            when (status!!.statusCode) {
                CommonStatusCodes.SUCCESS -> {
                    val sms = extras[SmsRetriever.EXTRA_SMS_MESSAGE] as String?
                    sms?.let {
                         val p = Pattern.compile("[0-9]{6}+") //check a pattern with only digit
//                        val p = Pattern.compile("\\d+")
                        val m = p.matcher(it)
                        if (m.find()) {
                            val otp = m.group()
                            if (mListener != null) {
                                mListener!!.messageReceived(otp)
                            }
                        }
                    }
                }
            }
        }
    }




}