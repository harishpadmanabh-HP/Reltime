package com.accubits.reltime.api

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import retrofit2.Call

class ApiMapper<T>(var status: ApiCallStatus, val data: T?, val errorMessage: String?) {

    override fun hashCode(): Int {
        var result = status.hashCode()
        result = 31 * result + (data?.hashCode() ?: 0)
        return result
    }

    override fun equals(o: Any?): Boolean {
        if (this === o) {
            return true
        }
        if (o == null || javaClass != o.javaClass) {
            return false
        }
        return false
    }

    override fun toString(): String {
        return ("Resource{"
                + "status="
                + status
                + ", data="
                + data
                + '}'.toString())
    }

    companion object {

        fun <T> success(data: T?): ApiMapper<T> {
            return ApiMapper(ApiCallStatus.SUCCESS, data, null)
        }

        fun <T> error(
            msg: String, data: T?, callback: Call<T>?,
            errorMessage: String?
        ): ApiMapper<T> {
            return ApiMapper(ApiCallStatus.ERROR, data, errorMessage)
        }

        fun <T> loading(data: T?): ApiMapper<T> {
            return ApiMapper(ApiCallStatus.LOADING, data, null)
        }

        fun <T> empty(data: T?): ApiMapper<T> {
            return ApiMapper(ApiCallStatus.EMPTY, null, null)
        }
    }
}

/**
 * Class containing error response for the APIs.
 */
data class ErrorResponse(

    @SerializedName("Message")
    @Expose
    public var error: String? = null,
    @SerializedName("Description")
    @Expose
    var description: String? = null,
    @SerializedName("ErrorCode")
    var errorCode: Int = -1

) {
    /**
     * function to get error message from the server
     */
    fun getErrorMessage(): String? {
        val message: String? =
            if (!error.isNullOrEmpty()) {
                error
            } else if (!description.isNullOrEmpty()) {
                description
            } else {
                null
            }
        return message
    }
}

enum class ApiCallStatus {
    SUCCESS,
    ERROR,
    LOADING, EMPTY
}