package com.accubits.reltime.repository

import android.util.Log
import com.accubits.reltime.activity.biometricLogin.model.LoginPINCreateRequest
import com.accubits.reltime.activity.contacts.model.ContactRequestModel
import com.accubits.reltime.activity.jointAccount.model.*
import com.accubits.reltime.activity.kyc.model.KYCData
import com.accubits.reltime.activity.rto.model.RtoP2PRequestModel
import com.accubits.reltime.activity.settings.model.CloseRequestHome
import com.accubits.reltime.activity.settings.model.LoginBiometricStatusRequest
import com.accubits.reltime.activity.settings.model.PhoneEmailModel
import com.accubits.reltime.activity.signup.model.body.OtpData
import com.accubits.reltime.activity.signup.model.body.userData
import com.accubits.reltime.activity.signup.model.body.ValidationUser
import com.accubits.reltime.activity.signup.model.response.server.ValidationResponse
import com.accubits.reltime.activity.signup.model.response.server.userSignupResponse
import com.accubits.reltime.activity.v2.common.walletpicker.model.WalletAddressValidationRequest
import com.accubits.reltime.activity.v2.lending.model.LendingCalculationRequest
import com.accubits.reltime.activity.v2.signUp.model.SignUpRequestModel
import com.accubits.reltime.activity.v2.wallet.swap.model.CryptoConversionRequest
import com.accubits.reltime.activity.v2.wallet.swap.model.SwapApprovalRequest
import com.accubits.reltime.activity.withdraw.model.JointAccountWithdrawRequestNew
import com.accubits.reltime.api.ApiCallStatus
import com.accubits.reltime.api.ApiMapper
import com.accubits.reltime.api.RetrofitApi
import com.accubits.reltime.models.*
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import retrofit2.http.Body
import retrofit2.http.Header


private const val TAG = "ReltimeRepository"

class ReltimeRepository constructor(
    private val retrofitApi: RetrofitApi,
    private val gson: Gson
) {

    suspend fun validateUser(validationUser: ValidationUser) = try {
        retrofitApi.validateUser(validationUser)
    } catch (httpException: HttpException) {
        val errorException = ErrorResponse(
            serverIssue = true,
            errorResponseCode = 0,
            neededAuthTokenRefresh = false,
            otherMessage = null
        )
        ValidationResponse(
            false,
            null,
            null,
            null,
            null,
            errorException
        )
    }

    suspend fun validateUserNew(validationUser: ValidationUser) =
        try {
            val response = retrofitApi.validateUser(validationUser)
            ApiMapper(ApiCallStatus.SUCCESS, response, null)
        } catch (ex: HttpException) {
            ApiMapper(ApiCallStatus.ERROR, null, ex.message)
        }

    suspend fun validateOTPV2(otpData: OtpData) = try {
        val response = retrofitApi.validateOTPV2(otpData)
        ApiMapper(ApiCallStatus.SUCCESS, response, null)
    } catch (ex: HttpException) {
        ApiMapper(ApiCallStatus.ERROR, null, ex.message)
    }

    suspend fun regenerateOtpV2(otpData: OtpData,deviceId:String) = try {
        val response = retrofitApi.regenerateOtpV2(deviceId,otpData)
        ApiMapper(ApiCallStatus.SUCCESS, response, null)
    } catch (ex: HttpException) {
        ApiMapper(ApiCallStatus.ERROR, null, ex.message)
    }

    suspend fun signUpV2(signUpRequestModel: SignUpRequestModel) = try {
        val response = retrofitApi.signUpV2(signUpRequestModel)
        ApiMapper(ApiCallStatus.SUCCESS, response, null)
    } catch (ex: HttpException) {
        ApiMapper(ApiCallStatus.ERROR, null, ex.message)
    }


    suspend fun registerUser(userData: userData): userSignupResponse = try {
        retrofitApi.postUserData(userData)
    } catch (httpException: HttpException) {
        val errorBody = httpException.response()?.errorBody()?.string()         //gson.fromJson(errorBody, userSignupResponse::class.java)
        val errorException = ErrorResponse(
            serverIssue = true,
            errorResponseCode = 0,
            neededAuthTokenRefresh = false,
            otherMessage = null
        )
        userSignupResponse(
            false,
            null, null, null, errorException
        )
    }

    suspend fun doSetNewPassword(newPasswordRequestModel: NewPasswordRequestModel) = try {
        retrofitApi.doSetNewPassword(newPasswordRequestModel)
    } catch (httpException: HttpException) {
        val errorBody = httpException.response()?.errorBody()?.string()
        CommonModel(false, null, null, null, true)
    }

    suspend fun doSetNewMpin(token: String, newMpinRequestModel: NewMpinRequestModel) = try {
        retrofitApi.doSetNewMpin(token, newMpinRequestModel)
    } catch (httpException: HttpException) {
        val errorBody = httpException.response()?.errorBody()?.string()
        CommonModel(false, null, null, null, true)
    }

    suspend fun getProfile(token: String) = try {
        retrofitApi.getProfile(token)
    } catch (httpException: HttpException) {
        val errorBody = httpException.response()?.errorBody()?.string()
        UserSuccessModel(false, null, null)
    }

    suspend fun getAllTag() = try {
        retrofitApi.getTag()
    } catch (httpException: HttpException) {
        val errorBody = httpException.response()?.errorBody()?.string()
        TagSuccessModel(false, null, "something went wrong", null)
    }

    suspend fun getAllFaq(tagId: String) = try {
        retrofitApi.getFaq(tagId)
    } catch (httpException: HttpException) {
        val errorBody = httpException.response()?.errorBody()?.string()
        FaqSuccessModel(false, null, "something went wrong", null)
    }

    suspend fun dosetAddress(token: String, updateAddressRequestModel: UpdateAddressRequestModel) =
        try {
            retrofitApi.setAddress(token, updateAddressRequestModel)
        } catch (httpException: HttpException) {
            val errorBody = httpException.response()?.errorBody()?.string()
            CommonModel(false, null, null, null, true)
        }

    suspend fun checkMpinCreated(token: String) = try {
        val response = retrofitApi.checkMpinCreated(token)
        ApiMapper(ApiCallStatus.SUCCESS, response, null)
    } catch (ex: HttpException) {
        ApiMapper(ApiCallStatus.ERROR, null, ex.message)
    }

    suspend fun createMpin(token: String, requestMpinCreation: RequstModelForMpinCreation) = try {
        val response = retrofitApi.createMPIN(token, requestMpinCreation)
        ApiMapper(ApiCallStatus.SUCCESS, response, null)
    } catch (ex: HttpException) {
        ApiMapper(ApiCallStatus.ERROR, null, ex.message)
    }

    suspend fun validateMpin(
        token: String,
        requestMpinValidateRequestModel: MpinValidateRequestModel
    ) = try {
        val response = retrofitApi.validateMpin(token, requestMpinValidateRequestModel)
        ApiMapper(ApiCallStatus.SUCCESS, response, null)
    } catch (ex: HttpException) {
        ApiMapper(ApiCallStatus.ERROR, null, ex.message)
    }

    suspend fun doEnableOrDisableBiometric(token: String, bioMetricRequest: BioMetricRequest) =
        try {
            val response = retrofitApi.doEnableOrDisableBiometric(token, bioMetricRequest)
            ApiMapper(ApiCallStatus.SUCCESS, response, null)
        } catch (ex: HttpException) {
            ApiMapper(ApiCallStatus.ERROR, null, ex.message)
        }

    suspend fun changeLoginBiometricStatus(token: String, request: LoginBiometricStatusRequest) =
        try {
            val response = retrofitApi.changeLoginBiometricStatus(token, request)
            ApiMapper(ApiCallStatus.SUCCESS, response, null)
        } catch (ex: HttpException) {
            ApiMapper(ApiCallStatus.ERROR, null, ex.message)
        }

    suspend fun getIntentApi(token: String, stripeIntentApiRequest: StripeIntentApiRequest) = try {
        val response = retrofitApi.getIntentApi(token, stripeIntentApiRequest)
        ApiMapper(ApiCallStatus.SUCCESS, response, null)
    } catch (ex: HttpException) {
        ApiMapper(ApiCallStatus.ERROR, null, ex.message)
    }

    suspend fun setTransactionToServer(token: String, reccipetApiRequest: ReccipetApiRequest) =
        try {
            val response = retrofitApi.getReccipet(token, reccipetApiRequest)
            ApiMapper(ApiCallStatus.SUCCESS, response, null)
        } catch (ex: HttpException) {
            ApiMapper(ApiCallStatus.ERROR, null, ex.message)
        }

    suspend fun getWalletDetails(token: String) = try {
        val response = retrofitApi.getWalletDetails(token)
        ApiMapper(ApiCallStatus.SUCCESS, response, null)
    } catch (ex: HttpException) {
        ApiMapper(ApiCallStatus.ERROR, null, ex.message)
    }

    suspend fun getWalletAmountDetails(token: String) = try {
        val response = retrofitApi.getWalletAmountDetails(token)
        ApiMapper(ApiCallStatus.SUCCESS, response, null)
    } catch (ex: HttpException) {
        ApiMapper(ApiCallStatus.ERROR, null, ex.message)
    }

    suspend fun getCardDetails(token: String) = try {
        val response = retrofitApi.getCardDetails(token)
        ApiMapper(ApiCallStatus.SUCCESS, response, null)
    } catch (ex: HttpException) {
        ApiMapper(ApiCallStatus.ERROR, null, ex.message)
    }

    suspend fun doLendingRequest(token: String, reccipetApiRequest: LendingRequestModel) = try {
        val response = retrofitApi.doLending(token, reccipetApiRequest)
        ApiMapper(ApiCallStatus.SUCCESS, response, null)
    } catch (ex: HttpException) {
        ApiMapper(ApiCallStatus.ERROR, null, ex.message)
    }

    suspend fun doUpdateLendingRequest(token: String, reccipetApiRequest: LendingRequestModel) =
        try {
            val response = retrofitApi.doUpdateLendItem(token, reccipetApiRequest)
            ApiMapper(ApiCallStatus.SUCCESS, response, null)
        } catch (ex: HttpException) {
            ApiMapper(ApiCallStatus.ERROR, null, ex.message)
        }

    suspend fun getLendingList(token: String, search: String) = try {
        val response = retrofitApi.getLendingList(token, search)
        ApiMapper(ApiCallStatus.SUCCESS, response, null)
    } catch (ex: HttpException) {
        ApiMapper(ApiCallStatus.ERROR, null, ex.message)
    }

    /* suspend fun getSearchLendingList(token: String, search: String,page:String) = try {
         val response = retrofitApi.getSearchTokenLendingList(token, search,page)
         ApiMapper(ApiCallStatus.SUCCESS, response, null)
     } catch (ex: HttpException) {
         ApiMapper(ApiCallStatus.ERROR, null, ex.message)
     }*/

    suspend fun getPaginatedToken(
        token: String,
        search: String,
        page: String,
        amountFrom: String,
        amount_to: String,
        date_from: String,
        date_to: String,
        instalment: String
    ) =
        retrofitApi.getSearchTokenLendingList(
            token,
            page,
            amountFrom,
            amount_to,
            date_from,
            date_to,
            instalment,
            search
        )


    suspend fun getPaginatedTransaction(
        token: String,
        search: String,
        page: String,
        limit: String
    ) =
        retrofitApi.getTransactionHistory(
            token,
            page,
            limit,
            search
        )

    suspend fun getTransactionHistoryV1(
        token: String,
        search: String,
        page: String,
        limit: String
    ) =
        retrofitApi.getTransactionHistoryV1(
            token,
            page,
            limit,
            search
        )


    suspend fun getPaginatedTransferTransaction(
        token: String,
        page: String,
        limit: String
    ) =
        retrofitApi.getTransferTransactionHistory(
            token,
            page,
            limit

        )


    suspend fun getPaginatedAllLending(
        token: String,
        search: String,
        page: String,
        amount_from: String,
        amount_to: String,
        installments_from: String,
        installments_to: String,
        interest_rate: String,
        no_collateral: String,
        collateral_from: String,
        collateral_to: String,
        amount_sort: String,
        interest_rate_sort: String,
        loan_term_sort: String,
    ) =
        retrofitApi.getAllLending(
            token,
            page,
            amount_from,
            amount_to,
            installments_from,
            installments_to,
            interest_rate,
            no_collateral,
            collateral_from,
            collateral_to,
            amount_sort,
            interest_rate_sort,
            loan_term_sort,
            search
        )

    suspend fun getLendingHistory(token: String, id: String) =
        retrofitApi.getLendingHistory(token, id)


    suspend fun getAllPaginatedBorrowings(
        token: String,
        search: String,
        page: String,
        amountFrom: String,
        amount_to: String,
        date_from: String,
        date_to: String,
        instalment: String
    ) =
        retrofitApi.getAllBorrowings(
            token,
            page,
            amountFrom,
            amount_to,
            date_from,
            date_to,
            instalment,
            search
        )

    suspend fun getAllBorrowingsNew(
        token: String,
        search: String,
        page: String,
        amountFrom: String,
        amount_to: String,
        date_from: String,
        date_to: String,
        instalment: String
    ) = try {
        val response = retrofitApi.getAllBorrowingsNew(
            token,
            page,
            amountFrom,
            amount_to,
            date_from,
            date_to,
            instalment,
            search
        )
        ApiMapper(ApiCallStatus.SUCCESS, response, null)
    } catch (ex: HttpException) {
        ApiMapper(ApiCallStatus.ERROR, null, ex.message)
    }

    suspend fun getPaymentHistory(
        token: String,
        page: String,
        id: String,
    ) = try {
        val response = retrofitApi.getPaymentHistory(
            token,
            page,
            id
        )
        ApiMapper(ApiCallStatus.SUCCESS, response, null)
    } catch (ex: HttpException) {
        ApiMapper(ApiCallStatus.ERROR, null, ex.message)
    }

    suspend fun getAllPaginatedBorrowRequest(token: String, page: String) =
        retrofitApi.getAlBorrowRequest(token, page)


    suspend fun getBorrowing(token: String) = try {
        val response = retrofitApi.getBorrowing(token)
        ApiMapper(ApiCallStatus.SUCCESS, response, null)
    } catch (ex: HttpException) {
        ApiMapper(ApiCallStatus.ERROR, null, ex.message)
    }

    suspend fun getTransactionApproval(
        token: String,
        transactionRequestModel: TransactionRequestModel
    ) = try {
        val response = retrofitApi.getWalletTransactionApproval(token, transactionRequestModel)
        ApiMapper(ApiCallStatus.SUCCESS, response, null)
    } catch (ex: HttpException) {
        ApiMapper(ApiCallStatus.ERROR, null, ex.message)
    }

    suspend fun doTransactionSiginInApi(
        token: String,
        signTransactionRequest: SignTransactionRequest
    ) = try {
        val response = retrofitApi.getSignTransaction(token, signTransactionRequest)
        ApiMapper(ApiCallStatus.SUCCESS, response, null)
    } catch (ex: HttpException) {
        ApiMapper(ApiCallStatus.ERROR, null, ex.message)
    }

    suspend fun deleteLendItem(token: String, url: String) = try {
        val response = retrofitApi.deleteLendItem(token, url)
        ApiMapper(ApiCallStatus.SUCCESS, response, null)
    } catch (ex: HttpException) {
        ApiMapper(ApiCallStatus.ERROR, null, ex.message)
    }

    suspend fun getLendItem(token: String, id: String) = try {
        val response = retrofitApi.getLendItem(token, id)
        ApiMapper(ApiCallStatus.SUCCESS, response, null)
    } catch (ex: HttpException) {
        ApiMapper(ApiCallStatus.ERROR, null, ex.message)
    }


    suspend fun getCommissionData(token: String) = try {
        val response = retrofitApi.getCommissionData(token)
        ApiMapper(ApiCallStatus.SUCCESS, response, null)
    } catch (ex: HttpException) {
        ApiMapper(ApiCallStatus.ERROR, null, ex.message)
    }

    suspend fun postRejectOrAcceptBorrow(token: String, borrowRequestModel: BorrowRequest) = try {
        val response = retrofitApi.doUpdateBorrowRequest(token, borrowRequestModel)
        ApiMapper(ApiCallStatus.SUCCESS, response, null)
    } catch (ex: HttpException) {
        ApiMapper(ApiCallStatus.ERROR, null, ex.message)
    }

    suspend fun postBorrowRequest(token: String, borrowRequestModel: BorrowRequestModel) = try {
        val response = retrofitApi.doBorrowRequest(token, borrowRequestModel)
        ApiMapper(ApiCallStatus.SUCCESS, response, null)
    } catch (ex: HttpException) {
        ApiMapper(ApiCallStatus.ERROR, null, ex.message)
    }

    suspend fun postPublicKey(token: String, publicKeyRequestModel: PublicKeyRequestModel) = try {
        val response = retrofitApi.doUploadPublicKey(token, publicKeyRequestModel)
        ApiMapper(ApiCallStatus.SUCCESS, response, null)
    } catch (ex: HttpException) {
        ApiMapper(ApiCallStatus.ERROR, null, ex.message)
    }

    suspend fun doRestorePublicKey(token: String, publicKeyRequestModel: PublicKeyRequestModel) = try {
        val response = retrofitApi.doRestorePublicKey(token, publicKeyRequestModel)
        ApiMapper(ApiCallStatus.SUCCESS, response, null)
    } catch (ex: HttpException) {
        ApiMapper(ApiCallStatus.ERROR, null, ex.message)
    }

    suspend fun postLoanClose(token: String, closeLoanRequest: RequestLoanUpdateRequest) = try {
        val response = retrofitApi.doCloseLoan(token, closeLoanRequest)
        ApiMapper(ApiCallStatus.SUCCESS, response, null)
    } catch (ex: HttpException) {
        ApiMapper(ApiCallStatus.ERROR, null, ex.message)
    }

    suspend fun postLoanCloseV2(token: String, closeLoanRequest: NewInstallementRequest) = try {
        val response = retrofitApi.doCloseLoanV2(token, closeLoanRequest)
        ApiMapper(ApiCallStatus.SUCCESS, response, null)
    } catch (ex: HttpException) {
        ApiMapper(ApiCallStatus.ERROR, null, ex.message)
    }

    suspend fun doAcceptOrRejectCollatral(
        token: String,
        collatralRequestModel: CollatralRequestModel
    ) = try {
        val response = retrofitApi.doAcceptOrRejectColatral(token, collatralRequestModel)
        ApiMapper(ApiCallStatus.SUCCESS, response, null)
    } catch (ex: HttpException) {
        ApiMapper(ApiCallStatus.ERROR, null, ex.message)
    }

    suspend fun doAcceptOrRejectBorrow(
        token: String,
        collatralRequestModel: NormalBorrowRequestModel
    ) = try {
        val response = retrofitApi.doAcceptOrRejectBorrow(token, collatralRequestModel)
        ApiMapper(ApiCallStatus.SUCCESS, response, null)
    } catch (ex: HttpException) {
        ApiMapper(ApiCallStatus.ERROR, null, ex.message)
    }

    suspend fun doWalletCollatral(token: String, collatralRequestModel: WalletCollatral) = try {
        val response = retrofitApi.doWalletApproval(token, collatralRequestModel)
        ApiMapper(ApiCallStatus.SUCCESS, response, null)
    } catch (ex: HttpException) {
        ApiMapper(ApiCallStatus.ERROR, null, ex.message)
    }

    suspend fun doInstallmentAndCloseLoan(
        token: String,
        collatralRequestModel: InstallementRequest
    ) = try {
        val response = retrofitApi.doWalletApprovalForInsatallement(token, collatralRequestModel)
        ApiMapper(ApiCallStatus.SUCCESS, response, null)
    } catch (ex: HttpException) {
        ApiMapper(ApiCallStatus.ERROR, null, ex.message)
    }

    suspend fun doJointAccountApprovalForInstallments(
        token: String,
        collatralRequestModel: InstallementRequest
    ) = try {
        val response =
            retrofitApi.doJointAccountApprovalForInstallments(token, collatralRequestModel)
        ApiMapper(ApiCallStatus.SUCCESS, response, null)
    } catch (ex: HttpException) {
        ApiMapper(ApiCallStatus.ERROR, null, ex.message)
    }


    suspend fun getMyBorrowingItem(token: String, id: String) = try {
        val response = retrofitApi.getMyBorrowingsForItem(token, id)
        ApiMapper(ApiCallStatus.SUCCESS, response, null)
    } catch (ex: HttpException) {
        ApiMapper(ApiCallStatus.ERROR, null, ex.message)
    }

    suspend fun getAccountToPayLoanList(token: String) = try {
        val response = retrofitApi.getAccountToPayLoanList(token)
        ApiMapper(ApiCallStatus.SUCCESS, response, null)
    } catch (ex: HttpException) {
        ApiMapper(ApiCallStatus.ERROR, null, ex.message)
    }

    suspend fun getContactList(token: String) = try {
        val response = retrofitApi.getContactList(token)
        ApiMapper(ApiCallStatus.SUCCESS, response, null)
    } catch (ex: HttpException) {
        ApiMapper(ApiCallStatus.ERROR, null, ex.message)
    }

    suspend fun getRegisteredContactList(token: String, contactRequestModel: ContactRequestModel) =
        try {
            val response = retrofitApi.getRegisteredContactList(token, contactRequestModel)
            ApiMapper(ApiCallStatus.SUCCESS, response, null)
        } catch (ex: HttpException) {
            ApiMapper(ApiCallStatus.ERROR, null, ex.message)
        }

    suspend fun sendRtoP2PRequestModel(token: String, rtoP2PRequestModel: RtoP2PRequestModel) =
        try {
            val response = retrofitApi.sendRtoP2PRequestModel(token, rtoP2PRequestModel)
            ApiMapper(ApiCallStatus.SUCCESS, response, null)
        } catch (ex: HttpException) {
            ApiMapper(ApiCallStatus.ERROR, null, ex.message)
        }

    suspend fun jointTransactionCheck(
        token: String,
        joinTransactionRequestModel: JointTransactionRequestModel
    ) =
        try {
            val response = retrofitApi.jointTransactionCheck(token, joinTransactionRequestModel)
            ApiMapper(ApiCallStatus.SUCCESS, response, null)
        } catch (ex: HttpException) {
            ApiMapper(ApiCallStatus.ERROR, null, ex.message)
        }

    suspend fun transactionApproval(
        token: String,
        rtoP2PRequestModel: TransactionApprovalRequestModel
    ) =
        try {
            val response = retrofitApi.transactionApproval(token, rtoP2PRequestModel)
            ApiMapper(ApiCallStatus.SUCCESS, response, null)
        } catch (ex: HttpException) {
            ApiMapper(ApiCallStatus.ERROR, null, ex.message)
        }

    suspend fun signRtoTransaction(token: String, signTransactionRequest: SignTransactionRequest) =
        try {
            val response = retrofitApi.signRtoTransaction(token, signTransactionRequest)
            ApiMapper(ApiCallStatus.SUCCESS, response, null)
        } catch (ex: HttpException) {
            ApiMapper(ApiCallStatus.ERROR, null, ex.message)
        }

    suspend fun uploadDocument(
        token: String,
        documentByteArray: ByteArray,
        documentFileName: String,
        videoFile: ByteArray,
        videoFileName: String,
        kycData: KYCData.KycDetails
    ) =
        try {
            val fileMediaType = "multipart/form-data".toMediaTypeOrNull()
            val jsonMediaType = "application/json; charset=utf-8".toMediaTypeOrNull()

            val kycDataJsonString = Gson().toJson(kycData)
            val requestBodyDocumentFile = documentByteArray.toRequestBody(fileMediaType)
            val requestVideoFile = videoFile.toRequestBody("*/*".toMediaTypeOrNull())

            val requestBodyKycData = kycDataJsonString.toRequestBody(jsonMediaType)


            val multiPartImage =
                MultipartBody.Part.createFormData(
                    "document",
                    documentFileName,
                    requestBodyDocumentFile
                )
            val multiPartVideo =
                MultipartBody.Part.createFormData("video", videoFileName, requestVideoFile)
            val multiPartKyc =
                MultipartBody.Part.createFormData("kyc_details", null, requestBodyKycData)

            val response =
                retrofitApi.uploadDocument(token, multiPartImage, multiPartVideo, multiPartKyc)
            ApiMapper(ApiCallStatus.SUCCESS, response, null)
        } catch (ex: Exception) {
            ApiMapper(ApiCallStatus.ERROR, null, ex.message)
        }

    suspend fun generateOtp(
        token: String,
        model: PhoneEmailModel,deviceId:String
    ) =
        try {
            val response = retrofitApi.generateOtp(token,deviceId, model)
            ApiMapper(ApiCallStatus.SUCCESS, response, null)
        } catch (ex: Exception) {
            ApiMapper(ApiCallStatus.ERROR, null, ex.message)
        }

    suspend fun updateEmailorPhone(token: String, phoneEmailModel: PhoneEmailModel) = try {
        val response = retrofitApi.updateEmailorPhone(token, phoneEmailModel)
        ApiMapper(ApiCallStatus.SUCCESS, response, null)
    } catch (ex: Exception) {
        ApiMapper(ApiCallStatus.ERROR, null, ex.message)
    }

    suspend fun createJointAccount(token: String, requestData: CreateJointAccountRequestModel) =
        try {
            val response = retrofitApi.createJointAccount(token, requestData)
            ApiMapper(ApiCallStatus.SUCCESS, response, null)
        } catch (ex: Exception) {
            ApiMapper(ApiCallStatus.ERROR, null, ex.message)
        }

    suspend fun editJointAccount(token: String, requestData: EditJointAccountRequestModel) =
        try {
            val response = retrofitApi.editJointAccount(token, requestData)
            ApiMapper(ApiCallStatus.SUCCESS, response, null)
        } catch (ex: Exception) {
            ApiMapper(ApiCallStatus.ERROR, null, ex.message)
        }

    suspend fun removeUserFromJointAccount(token: String, requestData: DeleteUserRequestModel) =
        try {
            val response = retrofitApi.removeUserFromJointAccount(token, requestData)
            ApiMapper(ApiCallStatus.SUCCESS, response, null)
        } catch (ex: Exception) {
            ApiMapper(ApiCallStatus.ERROR, null, ex.message)
        }

    suspend fun removeJointAccount(token: String, requestData: DeleteUserRequestModel) =
        try {
            val response = retrofitApi.removeJointAccount(token, requestData)
            ApiMapper(ApiCallStatus.SUCCESS, response, null)
        } catch (ex: Exception) {
            ApiMapper(ApiCallStatus.ERROR, null, ex.message)
        }

    suspend fun setAccountPermission(token: String, requestData: PermissionRequestModel) =
        try {
            val response = retrofitApi.setAccountPermission(token, requestData)
            ApiMapper(ApiCallStatus.SUCCESS, response, null)
        } catch (ex: Exception) {
            ApiMapper(ApiCallStatus.ERROR, null, ex.message)
        }

    suspend fun getAccountPermission(token: String) =
        try {
            val response = retrofitApi.getAccountPermission(token)
            ApiMapper(ApiCallStatus.SUCCESS, response, null)
        } catch (ex: Exception) {
            ApiMapper(ApiCallStatus.ERROR, null, ex.message)
        }

    suspend fun signJointAccountTransaction(
        token: String,
        requestData: SignJointAccountTxnRequestModel
    ) =
        try {
            val response = retrofitApi.signJointAccountTransaction(token, requestData)
            ApiMapper(ApiCallStatus.SUCCESS, response, null)
        } catch (ex: Exception) {
            ApiMapper(ApiCallStatus.ERROR, null, ex.message)
        }

    suspend fun doCloseHome(
        token: String,
        requestData: CloseRequestHome
    ) =
        try {
            val response = retrofitApi.doCloseInHome(token, requestData)
            ApiMapper(ApiCallStatus.SUCCESS, response, null)
        } catch (ex: Exception) {
            ApiMapper(ApiCallStatus.ERROR, null, ex.message)
        }


    suspend fun getNotificationList(token: String, page: String) = try {
        val response = retrofitApi.getNotificationList(token = token, page = page)
        ApiMapper(ApiCallStatus.SUCCESS, response, null)
    } catch (ex: HttpException) {
        ApiMapper(ApiCallStatus.ERROR, null, ex.message)
    }

    suspend fun getAccountList(token: String) = try {
        val response = retrofitApi.getAccountList(token)
        ApiMapper(ApiCallStatus.SUCCESS, response, null)
    } catch (ex: Exception) {
        ApiMapper(ApiCallStatus.ERROR, null, ex.message)
    }

    suspend fun removeCard(token: String, cardId: String) = try {
        val response = retrofitApi.removeCard(token, cardId)
        ApiMapper(ApiCallStatus.SUCCESS, response, null)
    } catch (ex: Exception) {
        ApiMapper(ApiCallStatus.ERROR, null, ex.message)
    }

    suspend fun fetchJointAccounts(token: String, accountId: Int) = try {
        val response = retrofitApi.fetchJointAccounts(token, accountId)
        ApiMapper(ApiCallStatus.SUCCESS, response, null)
    } catch (ex: Exception) {
        ApiMapper(ApiCallStatus.ERROR, null, ex.message)
    }

    suspend fun acceptRejectJointAccount(token: String, request: AcceptRejectRequest) = try {
        val response = retrofitApi.acceptRejectJointAccountRequest(token, request)
        ApiMapper(ApiCallStatus.SUCCESS, response, null)
    } catch (ex: HttpException) {
        ApiMapper(ApiCallStatus.ERROR, null, ex.message)
    }

    suspend fun walletAddCheckout(token: String, request: CheckoutApiRequest) = try {
        val response = retrofitApi.walletAddCheckout(token, request)
        ApiMapper(ApiCallStatus.SUCCESS, response, null)
    } catch (ex: HttpException) {
        ApiMapper(ApiCallStatus.ERROR, null, ex.message)
    }

    suspend fun walletGetCheckout(token: String, id: Int) = try {
        val response = retrofitApi.walletGetCheckout(token, id)
        ApiMapper(ApiCallStatus.SUCCESS, response, null)
    } catch (ex: HttpException) {
        ApiMapper(ApiCallStatus.ERROR, null, ex.message)
    }

    suspend fun convertCurrency(token: String, coinCode: String, amount: String) = try {
        val response = retrofitApi.convertCurrency(token, coinCode, amount)
        ApiMapper(ApiCallStatus.SUCCESS, response, null)
    } catch (ex: HttpException) {
        ApiMapper(ApiCallStatus.ERROR, null, ex.message)
    }

    suspend fun getPaymentStatistics(
        token: String,
        coinCode: String?,
        amount: String,
        sourceCoinCode: String?
    ) = try {
        val response = retrofitApi.getPaymentStatistics(token, coinCode, amount, sourceCoinCode)
        ApiMapper(ApiCallStatus.SUCCESS, response, null)
    } catch (ex: HttpException) {
        ApiMapper(ApiCallStatus.ERROR, null, ex.message)
    }

    suspend fun wyrePayment(token: String, request: SendWyreApiRequest) = try {
        val response = retrofitApi.wyrePayment(token, request)
        ApiMapper(ApiCallStatus.SUCCESS, response, null)
    } catch (ex: HttpException) {
        ApiMapper(ApiCallStatus.ERROR, null, ex.message)
    }

    suspend fun setAllNotificationRead(token: String, request: NotificationReadRequest) = try {
        val response = retrofitApi.setAllNotificationReaded(token, request)
        ApiMapper(ApiCallStatus.SUCCESS, response, null)
    } catch (ex: HttpException) {
        ApiMapper(ApiCallStatus.ERROR, null, ex.message)
    }

    suspend fun logout(request: LogoutRequest) = try {
        val response = retrofitApi.logout(request)
        ApiMapper(ApiCallStatus.SUCCESS, response, null)
    } catch (ex: HttpException) {
        ApiMapper(ApiCallStatus.ERROR, null, ex.message)
    }

    suspend fun getBankList(token: String) = try {
        val response = retrofitApi.getBankList(token)
        ApiMapper(ApiCallStatus.SUCCESS, response, null)
    } catch (ex: Exception) {
        ApiMapper(ApiCallStatus.ERROR, null, ex.message)
    }

    suspend fun postBankAccount(
        token: String, bankName: String? = null, accountNumber: String? = null,
        fullName: String? = null,
        swiftCode: String? = null,
        poNumber: String? = null,
        address: String? = null,
        address2: String? = null,
        contactNumber: String? = null,
        accountTpe: String? = null, documentByteArray: ByteArray, documentFileName: String
    ) = try {

        try {
            val fileMediaType = "multipart/form-data".toMediaTypeOrNull()
            val requestBodyDocumentFile = documentByteArray.toRequestBody(fileMediaType)
            val builder: MultipartBody.Builder = MultipartBody.Builder().setType(MultipartBody.FORM)
            builder.addFormDataPart("bank_name", bankName!!)
                .addFormDataPart("account_number", accountNumber!!)
                .addFormDataPart("full_name", fullName!!)
                .addFormDataPart("swift_code", swiftCode!!)
                .addFormDataPart("address", address!!)
                .addFormDataPart("address2", address2!!)
                .addFormDataPart("address3", poNumber!!)
                .addFormDataPart("contact_number", contactNumber!!)
                .addFormDataPart("account_type", accountTpe!!)

            builder.addFormDataPart("statement", documentFileName, requestBodyDocumentFile)

            val requestBody: RequestBody = builder.build()

            val response = retrofitApi.postBankAccount(
                token, requestBody
            )
            ApiMapper(ApiCallStatus.SUCCESS, response, null)

        } catch (ex: Exception) {
            ApiMapper(ApiCallStatus.ERROR, null, ex.message)
        }


    } catch (ex: Exception) {
        ApiMapper(ApiCallStatus.ERROR, null, ex.message)
    }


//    suspend fun postBankAccount(token: String, request: PostBankAccountRequest) = try {
//        val response = retrofitApi.postBankAccount(token, request)
//        ApiMapper(ApiCallStatus.SUCCESS, response, null)
//    } catch (ex: Exception) {
//        ApiMapper(ApiCallStatus.ERROR, null, ex.message)
//    }

    suspend fun getWithdrawToAccounts(token: String, withdrawFrom: String?) = try {
        val response = retrofitApi.getWithdrawToAccounts(token, withdrawFrom)
        ApiMapper(ApiCallStatus.SUCCESS, response, null)
    } catch (ex: Exception) {
        ApiMapper(ApiCallStatus.ERROR, null, ex.message)
    }

    suspend fun getV1WithdrawToAccounts(token: String, withdrawFrom: String?,
                                         coinCode: String?,
                                         address: String?) = try {
        val response = retrofitApi.getV1WithdrawToAccounts(token=token, withdrawFrom=withdrawFrom, coinCode = coinCode,address=address)
        ApiMapper(ApiCallStatus.SUCCESS, response, null)
    } catch (ex: Exception) {
        ApiMapper(ApiCallStatus.ERROR, null, ex.message)
    }

    suspend fun getWithdrawFromAccounts(token: String) = try {
        val response = retrofitApi.getWithdrawFromAccounts(token)
        ApiMapper(ApiCallStatus.SUCCESS, response, null)
    } catch (ex: Exception) {
        ApiMapper(ApiCallStatus.ERROR, null, ex.message)
    }

    suspend fun createWyreDepositCheckout(token: String, request: WyreCheckoutRequest) =
        try {
            val response = retrofitApi.createWyreDepositCheckout(token, request)
            ApiMapper(ApiCallStatus.SUCCESS, response, null)
        } catch (ex: HttpException) {
            ApiMapper(ApiCallStatus.ERROR, null, ex.message)
        }

    suspend fun checkWyreDepositStatus(token: String, request: WyreCheckoutStatusRequest) =
        try {
            val response = retrofitApi.checkWyreDepositStatus(token, request)
            ApiMapper(ApiCallStatus.SUCCESS, response, null)
        } catch (ex: HttpException) {
            ApiMapper(ApiCallStatus.ERROR, null, ex.message)
        }

    suspend fun createLoginPIN(token: String, request: LoginPINCreateRequest) =
        try {
            val response = retrofitApi.createLoginPIN(token, request)
            ApiMapper(ApiCallStatus.SUCCESS, response, null)
        } catch (ex: HttpException) {
            ApiMapper(ApiCallStatus.ERROR, null, ex.message)
        }

    suspend fun getLendingCalculation(token: String, request: LendingCalculationRequest) = try {
        val response = retrofitApi.getLendingCalculation(token, request)
        ApiMapper(ApiCallStatus.SUCCESS, response, null)
    } catch (ex: HttpException) {
        ApiMapper(ApiCallStatus.ERROR, null, ex.message)
    }

    suspend fun walletAddressValidation(token: String, request: WalletAddressValidationRequest) =
        try {
            val response = retrofitApi.walletAddressValidation(token, request)
            ApiMapper(ApiCallStatus.SUCCESS, response, null)
        } catch (ex: HttpException) {
            ApiMapper(ApiCallStatus.ERROR, null, ex.message)
        }

    suspend fun getCoinTokens(token: String) =
        try {
            val response = retrofitApi.tokenCoins(token)
            ApiMapper(ApiCallStatus.SUCCESS, response, null)
        } catch (ex: HttpException) {
            ApiMapper(ApiCallStatus.ERROR, null, ex.message)
        }

    suspend fun searchUserContact(token: String, request: SearchUserRequest) = try {
        val response = retrofitApi.searchUserContact(token, request)
        ApiMapper(ApiCallStatus.SUCCESS, response, null)
    } catch (ex: HttpException) {
        ApiMapper(ApiCallStatus.ERROR, null, ex.message)
    }


    suspend fun emailVerification(token: String, request: EmailVerificationRequest) = try {
        val response = retrofitApi.emailAddressValidation(token, request)
        ApiMapper(ApiCallStatus.SUCCESS, response, null)
    } catch (ex: HttpException) {
        ApiMapper(ApiCallStatus.ERROR, null, ex.message)
    }


    suspend fun getTransferAccountList(token: String) = try {
        val response = retrofitApi.getTransferAccountList(token)
        ApiMapper(ApiCallStatus.SUCCESS, response, null)
    } catch (ex: Exception) {
        ApiMapper(ApiCallStatus.ERROR, null, ex.message)
    }

    suspend fun getDepositAccounts(token: String) = try {
        val response = retrofitApi.getDepositAccounts(token)
        ApiMapper(ApiCallStatus.SUCCESS, response, null)
    } catch (ex: Exception) {
        ApiMapper(ApiCallStatus.ERROR, null, ex.message)
    }

    suspend fun getAccountDetail(token: String, id: Int, type: String) = try {
        val response = retrofitApi.getAccountDetail(token, id, type)
        ApiMapper(ApiCallStatus.SUCCESS, response, null)
    } catch (ex: Exception) {
        ApiMapper(ApiCallStatus.ERROR, null, ex.message)
    }

    suspend fun performTransfer(token: String, request: TransferRequest) = try {
        val response = retrofitApi.performTransfer(token, request)
        ApiMapper(ApiCallStatus.SUCCESS, response, null)
    } catch (ex: Exception) {
        ApiMapper(ApiCallStatus.ERROR, null, ex.message)
    }

    suspend fun performWalletSigin(token: String, request: WalletSigninRequest) = try {
        val response = retrofitApi.walletSign(token, request)
        ApiMapper(ApiCallStatus.SUCCESS, response, null)
    } catch (ex: Exception) {
        ApiMapper(ApiCallStatus.ERROR, null, ex.message)
    }

    suspend fun performTransferApproval(token: String, request: TransferApprovalRequest) = try {
        val response = retrofitApi.doTransferApproval(token, request)
        ApiMapper(ApiCallStatus.SUCCESS, response, null)
    } catch (ex: Exception) {
        ApiMapper(ApiCallStatus.ERROR, null, ex.message)
    }

    suspend fun getWalletHomeDetails(token: String) = try {
        val response = retrofitApi.getWalletHomeDetails(token)
        ApiMapper(ApiCallStatus.SUCCESS, response, null)
    } catch (ex: HttpException) {
        ApiMapper(ApiCallStatus.ERROR, null, ex.message)
    }

    suspend fun getHomeTransferList(token: String, page: String, limit: String) = try {
        val response = retrofitApi.getTransferTransactionHistory(
            token,
            page,
            limit
        )
        ApiMapper(ApiCallStatus.SUCCESS, response, null)
    } catch (ex: HttpException) {
        ApiMapper(ApiCallStatus.ERROR, null, ex.message)
    }


    suspend fun getTransactionHistoryV2(
        token: String,
        search: String,
        page: String,
        limit: Int?,
        account_id: Int?,
        type: String,
        status: String,
       // date_range: Int?
    ) =
        retrofitApi.getTransactionHistoryV2(
           token =  token,
            page= page,
            limit=limit,
            search= search,
            account_id=account_id,
            type=type,
            status=status,
          //  date_range
        )

    suspend fun getCurrencyList(token: String, coinCode: String? = null) = try {
        val response = retrofitApi.getSwapCryptoCurrencyList(token, coinCode)
        ApiMapper(ApiCallStatus.SUCCESS, response, null)
    } catch (ex: HttpException) {
        ApiMapper(ApiCallStatus.ERROR, null, ex.message)
    }

    suspend fun getCryptoConversionStatistics(token: String, request: CryptoConversionRequest) =
        try {
            val response = retrofitApi.getCryptoConversionStatistics(token, request)
            ApiMapper(ApiCallStatus.SUCCESS, response, null)
        } catch (ex: HttpException) {
            ApiMapper(ApiCallStatus.ERROR, null, ex.message)
        }

    suspend fun swapApproval(token: String, request: SwapApprovalRequest) = try {
        val response = retrofitApi.swapApproval(token, request)
        ApiMapper(ApiCallStatus.SUCCESS, response, null)
    } catch (ex: HttpException) {
        ApiMapper(ApiCallStatus.ERROR, null, ex.message)
    }

    suspend fun doSwap(token: String, request: SwapApprovalRequest) = try {
        val response = retrofitApi.doSwap(token, request)
        ApiMapper(ApiCallStatus.SUCCESS, response, null)
    } catch (ex: HttpException) {
        ApiMapper(ApiCallStatus.ERROR, null, ex.message)
    }

    suspend fun getBridgeTokenList(token: String) =
        try {
            val response = retrofitApi.getBridgeTokenList(token)
            ApiMapper(ApiCallStatus.SUCCESS, response, null)
        } catch (ex: HttpException) {
            ApiMapper(ApiCallStatus.ERROR, null, ex.message)
        }

    suspend fun getExternalAccountsList(token: String) =
        try {
            val response = retrofitApi.getExternalAccountsList(token)
            ApiMapper(ApiCallStatus.SUCCESS, response, null)
        } catch (ex: HttpException) {
            ApiMapper(ApiCallStatus.ERROR, null, ex.message)
        }

    suspend fun doBridge(token: String, request: SwapApprovalRequest) = try {
        val response = retrofitApi.doBridge(token, request)
        ApiMapper(ApiCallStatus.SUCCESS, response, null)
    } catch (ex: HttpException) {
        ApiMapper(ApiCallStatus.ERROR, null, ex.message)
    }

    suspend fun doMove(token: String, request: JointAccountWithdrawRequestNew) =
        try {
            val response = retrofitApi.doMove(token, request)
            ApiMapper(ApiCallStatus.SUCCESS, response, null)
        } catch (ex: HttpException) {
            ApiMapper(ApiCallStatus.ERROR, null, ex.message)
        }

    suspend fun getInitData() = try {
        val response = retrofitApi.getInitData()
        ApiMapper(ApiCallStatus.SUCCESS, response, null)
    } catch (ex: Exception) {
        ApiMapper(ApiCallStatus.ERROR, null, ex.message)
    }

    suspend fun otpGenerate(token: String?, deviceId: String, forgotRequestModel: ForgotRequestModel) = try {
        val response = retrofitApi.otpGenerate(token=token,deviceId=deviceId,forgotRequestModel=forgotRequestModel)
        ApiMapper(ApiCallStatus.SUCCESS, response, null)
    } catch (ex: Exception) {
        ApiMapper(ApiCallStatus.ERROR, null, ex.message)
    }

    suspend fun doVerifyEmail(token: String?,verifyRequestEmail: VerifyRequestEmail) = try {
        val response = retrofitApi.doVerifyEmail(token=token,verifyRequestEmail=verifyRequestEmail)
        ApiMapper(ApiCallStatus.SUCCESS, response, null)
    } catch (ex: Exception) {
        ApiMapper(ApiCallStatus.ERROR, null, ex.message)
    }
}
