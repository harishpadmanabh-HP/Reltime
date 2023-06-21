package com.accubits.reltime.activity.signup.model.response.ui

data class UserSignupResponseUI(
    val success: Boolean,
    val error: String? = null,
    val message: String,
    val tokenRefresh: Boolean = false,
    val serverIssue: Boolean = false
)
