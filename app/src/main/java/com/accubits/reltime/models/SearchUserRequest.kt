package com.accubits.reltime.models

import androidx.annotation.Keep

@Keep
data class SearchUserRequest(
    val phone_number: String
)