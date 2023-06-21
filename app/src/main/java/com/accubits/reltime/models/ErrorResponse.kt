package com.accubits.reltime.models

data class ErrorResponse(
    val serverIssue: Boolean?,
    val errorResponseCode: Int?,
    val neededAuthTokenRefresh: Boolean?,
    val otherMessage: String?
)
