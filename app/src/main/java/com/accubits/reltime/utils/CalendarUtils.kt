package com.accubits.reltime.utils

import java.util.*


object CalendarUtils {
    const val DATE_FORMAT = "dd-MM-yyyy"
    const val DATE_FORMAT_YYYY_MM_DD = "yyyy-MM-dd"
    const val DATE_FORMAT_YYYY_MM_DD_HH_MM_SS_SSS = "yyyy-MM-dd HH:mm:ss.SSS"
    const val DATE_FORMAT_YYYY_MM_DD_T_HH_MM_SS_SSS_Z = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"

    const val DATE_FORMAT_YYYY_MM_DD_T_HH_MM_Z = "yyyy-MM-dd'T'HH:mm'Z'"
    const val TIME_FORMAT = "hh:mm a"
    const val DATE_TIME_FORMAT = "dd-MM-yyyy hh:mm a"

    const val DATE = 1
    const val TIME = 2
    const val DATE_TIME = 3

    fun getCalendarInstance(default: String? = null, type: Int): Calendar {
        return Calendar.getInstance().apply {
            if (!default.isNullOrBlank()) {
                time = when (type) {
                    DATE -> default.toDate()
                    TIME -> default.toTime()
                    DATE_TIME -> default.toDateTime()
                    else -> default.toDate()
                }
            }
        }
//        return Calendar.getInstance().apply {
//            set(Calendar.HOUR_OF_DAY, 0)
//            set(Calendar.MINUTE, 0)
//            set(Calendar.SECOND, 0)
//            set(Calendar.MILLISECOND, 0)
//        }
    }
}