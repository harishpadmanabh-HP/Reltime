package com.accubits.reltime.utils

import android.util.Patterns
import org.json.JSONArray
import org.json.JSONObject
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

fun String.isValidEmail() = Patterns.EMAIL_ADDRESS.matcher(this).matches()
fun String.isValidPassword() = this.length in 6..14
fun String.formatJson(indentSpaces: Int? = 4): String? =
    if (indentSpaces == null) this else JSONObject(this).toString(indentSpaces)

fun String.twoDecimalPointFormat() : String?{
    val df = DecimalFormat("#.##")
    return df.format(this.toDouble())
}

fun Double.convertLongToTime(): String {
    val date = Date((this * 1000).toLong())
    val formatDateFormat = SimpleDateFormat("dd/MMM/yyyy",Locale.getDefault())
    val timeFormat = SimpleDateFormat("hh:mm aa",Locale.getDefault())
    return "${formatDateFormat.format(date)} at ${timeFormat.format(date)}"
}
fun String.formatJsonArray(indentSpaces: Int? = 4): String? =
    if (indentSpaces == null) this else JSONArray(this).toString(indentSpaces)

fun String.isTrimmedEmpty() = this.trim().isEmpty()

fun String.toDate(
    dateFormat: String = CalendarUtils.DATE_FORMAT
//    timeZone: TimeZone = TimeZone.getTimeZone("UTC")
): Date {
    val parser = SimpleDateFormat(dateFormat, Locale.getDefault())
//    parser.timeZone = timeZone
    return parser.parse(this)
}

fun String.toTime(
    dateFormat: String = CalendarUtils.TIME_FORMAT
//    timeZone: TimeZone = TimeZone.getTimeZone("UTC")
): Date {
    val parser = SimpleDateFormat(dateFormat, Locale.getDefault())
//    parser.timeZone = timeZone
    return parser.parse(this)
}

fun String.toDateTime(
    dateFormat: String = CalendarUtils.DATE_TIME_FORMAT
//    timeZone: TimeZone = TimeZone.getTimeZone("UTC")
): Date {
    val parser = SimpleDateFormat(dateFormat, Locale.getDefault())
//    parser.timeZone = timeZone
    return parser.parse(this)
}

fun String.toDATE_FORMAT_YYYY_MM_DD_T_HH_MM_Z(
    dateFormat: String = CalendarUtils.DATE_FORMAT_YYYY_MM_DD_T_HH_MM_Z,
    timeZone: TimeZone = TimeZone.getDefault()
): Date {
    val parser = SimpleDateFormat(dateFormat, Locale.getDefault())
    parser.timeZone = timeZone
    return parser.parse(this)
}

fun Date.formatDate(
    dateFormat: String = CalendarUtils.DATE_FORMAT/*,
    timeZone: TimeZone = TimeZone.getTimeZone("UTC")*/
): String {
    val formatter = SimpleDateFormat(dateFormat, Locale.getDefault())
//    formatter.timeZone = timeZone
    return formatter.format(this)
}

fun Date.formatDate_YYYY_MM_DD(
    dateFormat: String = CalendarUtils.DATE_FORMAT_YYYY_MM_DD/*,
    timeZone: TimeZone = TimeZone.getTimeZone("UTC")*/
): String {
    val formatter = SimpleDateFormat(dateFormat, Locale.getDefault())
//    formatter.timeZone = timeZone
    return formatter.format(this)
}

fun Date.formatDate_YYYY_MM_DD_HH_MM_SS_SSS(
    dateFormat: String = CalendarUtils.DATE_FORMAT_YYYY_MM_DD_HH_MM_SS_SSS/*,
    timeZone: TimeZone = TimeZone.getTimeZone("UTC")*/
): String {
    val formatter = SimpleDateFormat(dateFormat, Locale.getDefault())
//    formatter.timeZone = timeZone
    return formatter.format(this)
}

fun Date.formatDate_YYYY_MM_DD_T_HH_MM_Z(
    dateFormat: String = CalendarUtils.DATE_FORMAT_YYYY_MM_DD_T_HH_MM_Z,
    timeZone: TimeZone = TimeZone.getTimeZone("UTC")
): String {
    val formatter = SimpleDateFormat(dateFormat, Locale.getDefault())
    formatter.timeZone = timeZone
    return formatter.format(this)
}


fun Date.formatTime(
    dateFormat: String = CalendarUtils.TIME_FORMAT/*,
    timeZone: TimeZone = TimeZone.getTimeZone("UTC")*/
): String {
    val formatter = SimpleDateFormat(dateFormat, Locale.getDefault())
//    formatter.timeZone = timeZone
    return formatter.format(this)
}

fun Date.formatDateTime(
    dateFormat: String = CalendarUtils.DATE_TIME_FORMAT/*,
    timeZone: TimeZone = TimeZone.getTimeZone("UTC")*/
): String {
    val formatter = SimpleDateFormat(dateFormat, Locale.getDefault())
//    formatter.timeZone = timeZone
    return formatter.format(this)
}

fun String.isOverDue(): Boolean {
    val calendar = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }
    return this.toDate(dateFormat = CalendarUtils.DATE_FORMAT_YYYY_MM_DD).before(calendar.time)
}

fun String.isTodayDate(): Boolean {
    val calendar = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }
    return this.toDate(dateFormat = CalendarUtils.DATE_FORMAT_YYYY_MM_DD) == calendar.time
}

fun String.isNullString(): Boolean {
    return this == "null"
}


fun String?.valueOrNull() = if (this == "null") null else this
fun String.valueOrBlank() = if (this == "null") "" else this