package com.accubits.reltime.constants

import com.accubits.reltime.activity.region.model.Country

object ReltimeConstants {
    object Tags {
        const val FLASHBAR = 1
        const val DIALOG = 2
        const val FULL_SCREEN = 3
    }
    const val PASSWORD_LENGTH_MIN_LIMIT = 6
    const val ZERO = 0
    const val ONE = 1
    const val TWO = 2
    const val THREE = 3
    const val FOUR = 4
    const val FIVE = 5
    const val TEN = 10
    const val RELTIME = "RELTIME"
    const val ANDROID="android"
    const val EMPTY = ""
    const val SPACE = " "
    const val EMAIL_LABEL = "email"
    const val PHONE_LABEL = "phone"
    const val COLLATRAL = "COLLATRAL"
    const val INSTALLMENT = "installment"
    const val STATE = "state"
    const val MPIN = "mpin"
    const val ADDRESS1 = "address1"
    const val ADDRESS2 = "address2"
    const val AMOUNT = "amount"
    const val ACTIVITY = "activity"
    const val FROM = "from"
    const val RETURN_VALUE = "return_value1"
    const val DirectLendingActivity = "DirectLendingActivity"
    const val SelectAccountActivity = "selectAccountActivity"
    const val SendRtoCashInputActivity = "SendRtoCashInputActivity"
    const val MpinValidateActivity = "MpinValidateActivity"
    const val CITY = "city"
    const val TransactionId = "transaction_id"
    const val DEBITFROM = "debitfrom"
    const val PAIDAMOUNT = "paidamount"
    const val AMOUNTLISTED = "amountlisted"
    const val YOUEARNED = "youearned"
    const val PURCHASED = "purchased"
    const val DATE = "date"
    const val TIME = "time"
    const val COUNT = "count"
    const val LENDING = "lending"
    const val DIRECT_LENDING = "directLending"
    const val INTEREST = "interest"
    const val To = "To"
    const val WALLET = "wallet"
    const val NAME = "name"
    val SPACE_MULTIPLIER = SPACE.repeat(FIVE)

    const val oneSecondInMillis = 1000L
    const val seconds = 60
    private const val secondInMillis = seconds * oneSecondInMillis // 60s -> 1 Minute
    const val OTP_COUNT_DOWN = FIVE * secondInMillis // 5 Minutes

    const val PASSWORD_REGEX =
        "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!*?])(?=\\S+$).{8,}$"
    const val PHONE_NUMBER_REGEX = "[0-9]+"

    var countries: ArrayList<Country>? = null

    //
    const val DIALOG_POSITIVE = 1
    const val DIALOG_NEGATIVE = 0
    const val DIALOG_CANCEL = -1

    const val WALLET_ERROR="wallet details not found"
}