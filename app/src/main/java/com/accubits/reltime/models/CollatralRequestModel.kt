package com.accubits.reltime.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class CollatralRequestModel(
    @Expose
    @SerializedName("proposal_id")
    var proposal_id: Int,
    @Expose
    @SerializedName("is_accepted")
    var is_accepted: Boolean,
    @Expose
    @SerializedName("collateralAmount")
    var collateralAmount: Double? ,

)
