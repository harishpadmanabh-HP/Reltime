package com.accubits.reltime.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class BioMetricRequest(
    @Expose
    @SerializedName("biometrics_enabled")
    var biometrics_enabled: Boolean
)
