package com.accubits.reltime.activity.v2.fetchotp.interfaces

interface SmsListener {
    fun messageReceived(messageText: String?)
}