package com.accubits.reltime.activity.kyc.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class KYCResponseModel(
    @SerializedName("message")
    val message: String,
    @SerializedName("result")
    val result: Result,
    @SerializedName("status")
    val status: Int,
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("video_verification")
    val videoVerification: VideoVerification
) {
    @Keep
    data class Result(
        @SerializedName("created_at")
        val createdAt: String,
        @SerializedName("date_of_birth")
        val dateOfBirth: String,
        @SerializedName("document")
        val document: String,
        @SerializedName("document_id")
        val documentId: String,
        @SerializedName("document_s3_url")
        val documentS3Url: String,
        @SerializedName("document_type")
        val documentType: String,
        @SerializedName("document_verification")
        val documentVerification: Boolean,
        @SerializedName("expired_date")
        val expiredDate: String,
        @SerializedName("full_name")
        val fullName: String,
        @SerializedName("issued_date")
        val issuedDate: String,
        @SerializedName("status")
        val status: String,
        @SerializedName("user")
        val user: Int,
        @SerializedName("video")
        val video: String,
        @SerializedName("video_s3_url")
        val videoS3Url: String,
        @SerializedName("video_verification")
        val videoVerification: Boolean
    )

    @Keep
    data class VideoVerification(
        @SerializedName("confidence")
        val confidence: String,
        @SerializedName("message")
        val message: String,
        @SerializedName("result")
        val result: String,
        @SerializedName("status code")
        val statusCode: Int,
        @SerializedName("vector")
        val vector: List<Any>
    )
}