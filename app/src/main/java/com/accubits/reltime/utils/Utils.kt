package com.accubits.reltime.utils

import com.accubits.reltime.constants.ReltimeConstants
import java.util.regex.Pattern

object Utils {
    private const val TAG = "Utils"

    enum class Credentials {
        EMAIL, PHONE
    }

    val String.isValidPasswordFormat: () -> Boolean
        get() = {
            Pattern.compile(ReltimeConstants.PASSWORD_REGEX).matcher(this).matches()
        }

    val String.areNumbers: () -> Boolean
        get() = { Pattern.compile(ReltimeConstants.PHONE_NUMBER_REGEX).matcher(this).matches() }


}