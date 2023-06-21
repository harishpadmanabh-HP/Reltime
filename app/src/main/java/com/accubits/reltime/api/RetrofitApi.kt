package com.accubits.reltime.api

import com.accubits.reltime.activity.addBankAccount.model.AddBankAccountResponse
import com.accubits.reltime.activity.addBankAccount.model.BankListResponse
import com.accubits.reltime.activity.biometricLogin.model.LoginPINCreateRequest
import com.accubits.reltime.activity.contacts.model.ContactRequestModel
import com.accubits.reltime.activity.contacts.model.ContactResponseModel
import com.accubits.reltime.activity.jointAccount.model.*
import com.accubits.reltime.activity.jointAccount.model.singleJointAccountModel.SingleJointAccountModel
import com.accubits.reltime.activity.kyc.model.KYCResponseModel
import com.accubits.reltime.activity.myAccounts.model.MyAccountsListModel
import com.accubits.reltime.activity.rto.model.RtoP2PRequestModel
import com.accubits.reltime.activity.rto.model.RtoP2PResponseModel
import com.accubits.reltime.activity.rto.model.RtoSignTransactionResponseModel
import com.accubits.reltime.activity.settings.model.CloseRequestHome
import com.accubits.reltime.activity.settings.model.EmailPhoneUpdationResponseModel
import com.accubits.reltime.activity.settings.model.LoginBiometricStatusRequest
import com.accubits.reltime.activity.settings.model.PhoneEmailModel
import com.accubits.reltime.activity.signup.model.body.OtpData
import com.accubits.reltime.activity.signup.model.body.ValidationUser
import com.accubits.reltime.activity.signup.model.body.userData
import com.accubits.reltime.activity.signup.model.response.server.ValidationResponse
import com.accubits.reltime.activity.signup.model.response.server.userSignupResponse
import com.accubits.reltime.activity.v2.common.walletpicker.model.WalletAddressValidationRequest
import com.accubits.reltime.activity.v2.lending.model.LendingCalculationRequest
import com.accubits.reltime.activity.v2.lending.model.LendngCalculationResponse
import com.accubits.reltime.activity.v2.signUp.model.SignUpRequestModel
import com.accubits.reltime.activity.v2.splash.model.InitDataResponse
import com.accubits.reltime.activity.v2.wallet.home.model.WalletHomeSuccessModel
import com.accubits.reltime.activity.v2.wallet.myaccounts.model.ExternalAccountListResponse
import com.accubits.reltime.activity.v2.wallet.swap.model.CryptoConversionRequest
import com.accubits.reltime.activity.v2.wallet.swap.model.CryptoConversionResponse
import com.accubits.reltime.activity.v2.wallet.swap.model.CurrencyListResponse
import com.accubits.reltime.activity.v2.wallet.swap.model.SwapApprovalRequest
import com.accubits.reltime.activity.withdraw.model.*
import com.accubits.reltime.models.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

interface RetrofitApi {

    @POST("auth/validation/")
    suspend fun validateUser(
        @Body validationUser: ValidationUser
    ): ValidationResponse

    @POST("auth/signup/")
    suspend fun postUserData(
        @Body userData: userData
    ): userSignupResponse

    @POST("auth/signup_verify/")
    suspend fun validateOTPV2(
        @Body otpData: OtpData
    ): OtpVerifySuccessModel

    @POST("auth/signup_otp/")
    suspend fun regenerateOtpV2(
        @Header("deviceId") deviceId: String,
        @Body otpData: OtpData
    ): OtpVerifySuccessModel

    @POST("auth/signup/v2/")
    suspend fun signUpV2(
        @Body signUpRequestModel: SignUpRequestModel
    ): LoginSuccessModel

    @POST("auth/new_password/")
    suspend fun doSetNewPassword(
        @Body newPasswordRequestModel: NewPasswordRequestModel
    ): CommonModel

    @POST("wallet/new_mpin/")
    suspend fun doSetNewMpin(
        @Header("Authorization") token: String,
        @Body newMpinRequestModel: NewMpinRequestModel
    ): CommonModel

    @GET("auth/profile/")
    suspend fun getProfile(
        @Header("Authorization") token: String
    ): UserSuccessModel

    @GET("tag/")
    suspend fun getTag(): TagSuccessModel

    @GET("faq/")
    suspend fun getFaq(@Query("tag") tagId: String): FaqSuccessModel

    @PATCH("auth/profile/")
    suspend fun setAddress(
        @Header("Authorization") token: String,
        @Body updateAddressRequestModel: UpdateAddressRequestModel
    ): CommonModel

    @POST("wallet/mpin/")
    suspend fun createMPIN(
        @Header("Authorization") token: String,
        @Body requstModelForMpinCreation: RequstModelForMpinCreation
    ): CommonModel

    @GET("wallet/mpin/")
    suspend fun checkMpinCreated(
        @Header("Authorization") token: String
    ): CheckMpinSuccessModel

    @POST("wallet/mpin/verify/")
    suspend fun validateMpin(
        @Header("Authorization") token: String,
        @Body requestMpinValidateRequestModel: MpinValidateRequestModel
    ): CommonModel

    @PATCH("wallet/change/")
    suspend fun doEnableOrDisableBiometric(
        @Header("Authorization") token: String,
        @Body bioMetricRequest: BioMetricRequest
    ): CommonModel

    @POST("wallet/create_intent/")
    suspend fun getIntentApi(
        @Header("Authorization") token: String,
        @Body stripeIntentApiRequest: StripeIntentApiRequest
    ): StripeIntentSuccessModel

    @POST("wallet/payment_detail/")
    suspend fun getReccipet(
        @Header("Authorization") token: String,
        @Body reccipetApiRequest: ReccipetApiRequest
    ): TrasactionSuccessModel

    @GET("wallet/dashboard_details/")
    suspend fun getWalletDetails(
        @Header("Authorization") token: String
    ): WalletSuccessModel

    @GET("wallet/wallet_details/")
    suspend fun getWalletAmountDetails(
        @Header("Authorization") token: String
    ): WalletAmountSuccessModel

    @GET("wallet/card/")
    suspend fun getCardDetails(
        @Header("Authorization") token: String
    ): CardSuccessModel

    @POST("wallet/lending/")
    suspend fun doLending(
        @Header("Authorization") token: String,
        @Body reccipetApiRequest: LendingRequestModel
    ): LendingSuccessModel

    @PATCH("wallet/lending/")
    suspend fun doUpdateLendItem(
        @Header("Authorization") token: String,
        @Body reccipetApiRequest: LendingRequestModel
    ): LendingSuccessModel

    @GET("wallet/lending/")
    suspend fun getLendingList(
        @Header("Authorization") token: String,
        @Query("search") data: String
    ): LendingTokenSuccessModel

    @GET("wallet/lending_filter/")
    suspend fun getSearchLendingList(
        @Header("Authorization") token: String,
        @Query("search") data: String
    ): SearchTokenModel

    @GET("wallet/lending_filter/")
    suspend fun getSearchTokenLendingList(
        @Header("Authorization") token: String,
        @Query("page") page: String,
        @Query("amount_from") amountFrom: String,
        @Query("amount_to") amount_to: String,
        @Query("date_from") date_from: String,
        @Query("date_to") date_to: String,
        @Query("installment") instalment: String,
        @Query("search") search: String,
    ): SearchTokenModel

    @GET("auth/customer_transactions/")
    suspend fun getTransactionHistory(
        @Header("Authorization") token: String,
        @Query("page") page: String,
        @Query("limit") limit: String,
        @Query("search") search: String
    ): TransactionHistorySuccessModel

    @GET("wallet/v1/transaction/")
    suspend fun getTransactionHistoryV1(
        @Header("Authorization") token: String,
        @Query("page") page: String,
        @Query("limit") limit: String,
        @Query("search") search: String
    ): TransactionHistorySuccessModel

    @GET("wallet/commission/")
    suspend fun getCommissionData(@Header("Authorization") token: String): CommissionSuccessModel

    @GET("wallet/contact_list/")
    suspend fun getContactList(@Header("Authorization") token: String): ContactListSuccessModel


    @GET("wallet/all_lending/v2/")
    suspend fun getAllLending(
        @Header("Authorization") token: String,
        @Query("page") page: String,
        @Query("amount_from") amount_from: String,
        @Query("amount_to") amount_to: String,
        @Query("installments_from") installments_from: String,
        @Query("installments_to") installments_to: String,
        @Query("interest_rate") interest_rate: String,
        @Query("no_collateral") no_collateral: String,
        @Query("collateral_from") collateral_from: String,
        @Query("collateral_to") collateral_to: String,
        @Query("amount_sort") amount_sort: String,
        @Query("interest_rate_sort") interest_rate_sort: String,
        @Query("loan_term_sort") loan_term_sort: String,
        @Query("search") search: String
    ): AllLendingSuccessModel

    @GET("auth/loan_transactions/")
    suspend fun getLendingHistory(
        @Header("Authorization") token: String,
        @Query("id") id: String
    ): AllLendingSuccessModel

    @GET("wallet/borrowing/")
    suspend fun getAllBorrowings(
        @Header("Authorization") token: String,
        @Query("page") page: String,
        @Query("amount_from") amountFrom: String,
        @Query("amount_to") amount_to: String,
        @Query("date_from") date_from: String,
        @Query("date_to") date_to: String,
        @Query("installment") instalment: String,
        @Query("search") search: String
    ): MyBorrowingsSuccessModel


    @GET("wallet/borrowing/")
    suspend fun getAllBorrowingsNew(
        @Header("Authorization") token: String,
        @Query("page") page: String,
        @Query("amount_from") amountFrom: String,
        @Query("amount_to") amount_to: String,
        @Query("date_from") date_from: String,
        @Query("date_to") date_to: String,
        @Query("installment") instalment: String,
        @Query("search") search: String
    ): MyBorrowingsSuccessModel

    @GET("auth/loan_transactions/")
    suspend fun getPaymentHistory(
        @Header("Authorization") token: String,
        @Query("page") page: String,
        @Query("id") id: String,
    ): PaymentHistorySuccessModel

    @GET("wallet/borrowing/")
    suspend fun getBorrowing(
        @Header("Authorization") token: String,
    ): MyBorrowingsSuccessModel

    @POST("wallet/borrowing/")
    suspend fun doBorrowRequest(
        @Header("Authorization") token: String,
        @Body borrowRequestModel: BorrowRequestModel
    ): TransactionApprovalSuccessModel

    @POST("management/public_key/")
    suspend fun doUploadPublicKey(
        @Header("Authorization") token: String,
        @Body publicKeyRequestModel: PublicKeyRequestModel
    ): CommonModel

    @POST("management/restore_key/")
    suspend fun doRestorePublicKey(
        @Header("Authorization") token: String,
        @Body publicKeyRequestModel: PublicKeyRequestModel
    ): CommonModel

    @GET("wallet/borrowing/")
    suspend fun getMyBorrowingsForItem(
        @Header("Authorization") token: String,
        @Query("id") id: String
    ): MyBorrowSuccessModel

    @GET("wallet/payment_accounts/")
    suspend fun getAccountToPayLoanList(
        @Header("Authorization") token: String
    ): GetLoanAccountSuccessModel


    @POST("wallet/loan_payment/")
    suspend fun doCloseLoan(
        @Header("Authorization") token: String,
        @Body requestLoanUpdateRequest: RequestLoanUpdateRequest
    ): TransactionApprovalSuccessModel


    @POST("wallet/loan_payment/v2/")
    suspend fun doCloseLoanV2(
        @Header("Authorization") token: String,
        @Body request: NewInstallementRequest
    ): TransactionApprovalSuccessModel

    @POST("wallet/borrow_request/")
    suspend fun doUpdateBorrowRequest(
        @Header("Authorization") token: String,
        @Body borrowRequest: BorrowRequest
    ): TransactionApprovalSuccessModel

    @GET("wallet/borrow_request/")
    suspend fun getAlBorrowRequest(
        @Header("Authorization") token: String,
        @Query("page") page: String,
    ): BorrowRequestSuccessModel

    @POST("wallet/transaction_approval/")
    suspend fun getWalletTransactionApproval(
        @Header("Authorization") token: String,
        @Body transactionRequestModel: TransactionRequestModel
    ): TransactionApprovalSuccessModel

    @POST("wallet/signTxn/")
    suspend fun getSignTransaction(
        @Header("Authorization") token: String,
        @Body transactionRequestModel: SignTransactionRequest
    ): SiginInTransactionSuccessModel


    @DELETE("wallet/lending/{lending_id}/")
    suspend fun deleteLendItem(
        @Header("Authorization") token: String,
        @Path(value = "lending_id") url: String

    ): TransactionApprovalSuccessModel

    @GET("wallet/lending_details/")
    suspend fun getLendItem(
        @Header("Authorization") token: String,
        @Query("id") id: String
    ): MyBorrowingsSuccessModel

    @PATCH("wallet/confirmation/")
    suspend fun doAcceptOrRejectColatral(
        @Header("Authorization") token: String,
        @Body collatralRequestModel: CollatralRequestModel
    ): TransactionApprovalSuccessModel

    @POST("/wallet/borrow/")
    suspend fun doAcceptOrRejectBorrow(
        @Header("Authorization") token: String,
        @Body collatralRequestModel: NormalBorrowRequestModel
    ): TransactionApprovalSuccessModel

    @POST("wallet/transaction_approval/")
    suspend fun doWalletApproval(
        @Header("Authorization") token: String,
        @Body collatralRequestModel: WalletCollatral
    ): TransactionApprovalSuccessModel

    @POST("wallet/transaction_approval/")
    suspend fun doWalletApprovalForInsatallement(
        @Header("Authorization") token: String,
        @Body collatralRequestModel: InstallementRequest
    ): TransactionApprovalSuccessModel

    @POST("wallet/joint_account_approval/")
    suspend fun doJointAccountApprovalForInstallments(
        @Header("Authorization") token: String,
        @Body collatralRequestModel: InstallementRequest
    ): TransactionApprovalSuccessModel

    @POST("auth/contact_list/")
    suspend fun getRegisteredContactList(
        @Header("Authorization") token: String,
        @Body contactBody: ContactRequestModel
    ): ContactResponseModel

    @POST("wallet/p2p_transaction/")
    suspend fun sendRtoP2PRequestModel(
        @Header("Authorization") token: String,
        @Body rtoP2PRequestModel: RtoP2PRequestModel
    ): RtoP2PResponseModel

    @POST("wallet/check_joint_account/")
    suspend fun jointTransactionCheck(
        @Header("Authorization") token: String,
        @Body joinTransactionRequestModel: JointTransactionRequestModel
    ): JointAccountCheckSuccessModel

    @POST("wallet/transaction_approval/")
    suspend fun transactionApproval(
        @Header("Authorization") token: String,
        @Body rtoP2PRequestModel: TransactionApprovalRequestModel
    ): TransactionApprovalSuccessModel

    @POST("wallet/signTxn/")
    suspend fun signRtoTransaction(
        @Header("Authorization") token: String,
        @Body signTransactionRequest: SignTransactionRequest
    ): RtoSignTransactionResponseModel

    @Multipart
    @POST("auth/kyc/")
    suspend fun uploadDocument(
        @Header("Authorization") token: String,
        @Part imageData: MultipartBody.Part,
        @Part videoData: MultipartBody.Part,
        @Part kycDetails: MultipartBody.Part
    ): KYCResponseModel

    @POST("generate_otp/")
    suspend fun generateOtp(
        @Header("Authorization") token: String, @Header("deviceId") deviceId: String,
        @Body model: PhoneEmailModel
    ): EmailPhoneUpdationResponseModel

    @PATCH("contact/update/")
    suspend fun updateEmailorPhone(
        @Header("Authorization") token: String,
        @Body model: PhoneEmailModel
    ): EmailPhoneUpdationResponseModel

    @POST("wallet/joint_account/")
    suspend fun createJointAccount(
        @Header("Authorization") token: String,
        @Body model: CreateJointAccountRequestModel
    ): CreateJointAccountTxnResponse

    @POST("wallet/manage_joint_account/")
    suspend fun editJointAccount(
        @Header("Authorization") token: String,
        @Body model: EditJointAccountRequestModel
    ): CreateJointAccountTxnResponse

    @HTTP(method = "DELETE", hasBody = true, path = "wallet/manage_joint_account/")
    suspend fun removeUserFromJointAccount(
        @Header("Authorization") token: String,
        @Body model: DeleteUserRequestModel
    ): CreateJointAccountTxnResponse

    @HTTP(method = "DELETE", hasBody = true, path = "wallet/joint_account/")
    suspend fun removeJointAccount(
        @Header("Authorization") token: String,
        @Body model: DeleteUserRequestModel
    ): CreateJointAccountTxnResponse

    @POST("wallet/joint_account_confirm/")
    suspend fun signJointAccountTransaction(
        @Header("Authorization") token: String,
        @Body model: SignJointAccountTxnRequestModel
    ): CreateJointAccountConfirmationResponse

    @GET("wallet/set_account_permission/")
    suspend fun getAccountPermission(
        @Header("Authorization") token: String,
    ): PermissionResponseModel

    @POST("wallet/set_account_permission/")
    suspend fun setAccountPermission(
        @Header("Authorization") token: String,
        @Body model: PermissionRequestModel
    ): CreateJointAccountTxnResponse

    @POST("auth/manage_widget/")
    suspend fun doCloseInHome(
        @Header("Authorization") token: String,
        @Body model: CloseRequestHome
    ): CloseSuccesModel

    @GET("auth/notifications/")
    suspend fun getNotificationList(
        @Header("Authorization") token: String,
        @Query("page") page: String,
    ): NotificationSuccessModel

    @GET("wallet/v1/my_account/")//my_account/")
    suspend fun getAccountList(
        @Header("Authorization") token: String,
    ): MyAccountsListModel

    @GET("wallet/card/{pk}/")
    suspend fun removeCard(
        @Header("Authorization") token: String,
        @Path(value = "pk") cardId: String
    ): MyAccountsListModel

    @GET("wallet/v1/fetch_joint_accounts/")
    suspend fun fetchJointAccounts(
        @Header("Authorization") token: String,
        @Query("id") accountId: Int,
    ): SingleJointAccountModel

    @POST("wallet/manage_invite/")
    suspend fun acceptRejectJointAccountRequest(
        @Header("Authorization") token: String,
        @Body request: AcceptRejectRequest
    ): CreateJointAccountTxnResponse

    @POST("wallet/add_checkout/")
    suspend fun walletAddCheckout(
        @Header("Authorization") token: String,
        @Body request: CheckoutApiRequest
    ): CheckoutSuccessModel

    @GET("wallet/add_checkout/")
    suspend fun walletGetCheckout(
        @Header("Authorization") token: String,
        @Query("id") id: Int
    ): CheckoutSuccessModel

    @GET("wallet/payment_statistics/")
    suspend fun convertCurrency(
        @Header("Authorization") token: String,
        @Query("coinCode") coinCode: String,
        @Query("amount") amount: String
    ): CurrencyConversionSuccessModel

    @GET("wallet/payment_statistics/v2/")
    suspend fun getPaymentStatistics(
        @Header("Authorization") token: String,
        @Query("coinCode") coinCode: String?,
        @Query("amount") amount: String,
        @Query("sourceCoinCode") sourceCoinCode: String?
    ): CurrencyConversionSuccessModel

    @POST("wallet/wyre/payment")
    suspend fun wyrePayment(
        @Header("Authorization") token: String,
        @Body request: SendWyreApiRequest
    ): SendWyreSuccessModel

    @POST("wallet/user-details-phone_number/")
    suspend fun searchUserContact(
        @Header("Authorization") token: String,
        @Body request: SearchUserRequest
    ): SearchUserResponseModel

    @POST("auth/notifications/")
    suspend fun setAllNotificationReaded(
        @Header("Authorization") token: String,
        @Body request: NotificationReadRequest
    ): NotificationReadSuccessResponse

    @POST("auth/logout/")
    suspend fun logout(
        @Body request: LogoutRequest
    ): NotificationReadSuccessResponse

    @GET("wallet/bank_list/")
    suspend fun getBankList(
        @Header("Authorization") token: String
    ): BankListResponse

    @POST("wallet/add/bank/")
    suspend fun postBankAccount(
        @Header("Authorization") token: String,
        @Body request: RequestBody
    ): AddBankAccountResponse

    @GET("wallet/withdraw_to_accounts/")
    suspend fun getWithdrawToAccounts(
        @Header("Authorization") token: String,
        @Query("withdrawFrom") withdrawFrom: String?
    ): AccountListResponse

    @GET("wallet/v1/withdraw_to_accounts/")
    suspend fun getV1WithdrawToAccounts(
        @Header("Authorization") token: String,
        @Query("withdrawFrom") withdrawFrom: String?,
        @Query("coinCode") coinCode: String?,
        @Query("address") address: String?
    ): AccountListResponse

    @GET("wallet/v1/withdraw_from_accounts/")//"wallet/withdraw_from_accounts/")
    suspend fun getWithdrawFromAccounts(
        @Header("Authorization") token: String,
    ): AccountListResponse

    @POST("wallet/create_checkout/")
    suspend fun createWyreDepositCheckout(
        @Header("Authorization") token: String,
        @Body request: WyreCheckoutRequest
    ): WyreCheckoutResponse

    @POST("wallet/check_status/")
    suspend fun checkWyreDepositStatus(
        @Header("Authorization") token: String,
        @Body request: WyreCheckoutStatusRequest
    ): WyreCheckoutStatusResponse

    @POST("auth/login_pin/")
    suspend fun createLoginPIN(
        @Header("Authorization") token: String,
        @Body request: LoginPINCreateRequest
    ): LoginSuccessModel

    //@POST("wallet/lending_calculation/")
    @POST("wallet/v1/lending_calculation/")
    suspend fun getLendingCalculation(
        @Header("Authorization") token: String,
        @Body request: LendingCalculationRequest
    ): LendngCalculationResponse

    @POST("wallet/crypto-address-validation/")
    suspend fun walletAddressValidation(
        @Header("Authorization") token: String,
        @Body request: WalletAddressValidationRequest
    ): SearchUserResponseModel


    @GET("wallet/crypto-address-validation/")
    suspend fun tokenCoins(
        @Header("Authorization") token: String,
    ): TokenCodeResponse

    @POST("wallet/user-details-email/")
    suspend fun emailAddressValidation(
        @Header("Authorization") token: String,
        @Body request: EmailVerificationRequest
    ): SearchUserResponseModel

    @POST("auth/biometric-login-update/")
    suspend fun changeLoginBiometricStatus(
        @Header("Authorization") token: String,
        @Body request: LoginBiometricStatusRequest
    ): CommonModel

    @GET("wallet/v1/account_from/")
    suspend fun getTransferAccountList(
        @Header("Authorization") token: String,
    ): MyAccountsListModel

    @GET("wallet/user-recent-transaction/")
    suspend fun getTransferTransactionHistory(
        @Header("Authorization") token: String,
        @Query("page") page: String,
        @Query("limit") limit: String,
    ): TransferTransactionResponseModelX

    @GET("/wallet/v1/wallet/")
    suspend fun getAccountDetail(
        @Header("Authorization") token: String,
        @Query("id") id: Int,
        @Query("type") type: String
    ): AccountDetailResponseModel

    @POST("wallet/v1/transfer/")
    suspend fun performTransfer(
        @Header("Authorization") token: String,
        @Body request: TransferRequest
    ): TransferResponseModel

    @POST("wallet/v1/wallet_sign/")
    suspend fun walletSign(
        @Header("Authorization") token: String,
        @Body request: WalletSigninRequest
    ): WalletSignInModel

    @POST("wallet/transaction_approval/")
    suspend fun doTransferApproval(
        @Header("Authorization") token: String,
        @Body transferApprovalRequest: TransferApprovalRequest
    ): TransactionApprovalSuccessModel//TransferResponseModel

    @GET("wallet/v1/account/")
    suspend fun getWalletHomeDetails(
        @Header("Authorization") token: String
    ): WalletHomeSuccessModel

    @GET("wallet/v1/transaction/")
    suspend fun getTransactionHistoryV2(
        @Header("Authorization") token: String,
        @Query("page") page: String,
        @Query("limit") limit: Int?,
        @Query("search") search: String,
        @Query("account_id") account_id: Int?,
        @Query("type") type: String,
        @Query("status") status: String,
        // @Query("date_range") date_range: Int?
    ): TransactionHistorySuccessModelV2

    @GET("wallet/v1/list/")
    suspend fun getSwapCryptoCurrencyList(
        @Header("Authorization") token: String,
        @Query("coinCode") coinCode: String? = null
    ): CurrencyListResponse

    @POST("wallet/v1/convert/")
    suspend fun getCryptoConversionStatistics(
        @Header("Authorization") token: String,
        @Body request: CryptoConversionRequest
    ): CryptoConversionResponse

    @POST("wallet/v1/swap_approval/")
    suspend fun swapApproval(
        @Header("Authorization") token: String,
        @Body request: SwapApprovalRequest
    ): TransactionApprovalSuccessModel//RtoP2PResponseModel

    @POST("wallet/v1/swap/")
    suspend fun doSwap(
        @Header("Authorization") token: String,
        @Body request: SwapApprovalRequest
    ): TransactionApprovalSuccessModel

    @GET("wallet/v1/bridging/list/")
    suspend fun getBridgeTokenList(
        @Header("Authorization") token: String
    ): CurrencyListResponse

    @GET("wallet/v1/bridging/list/")
    suspend fun getExternalAccountsList(
        @Header("Authorization") token: String
    ): ExternalAccountListResponse

    @POST("wallet/v1/bridging/")
    suspend fun doBridge(
        @Header("Authorization") token: String,
        @Body request: SwapApprovalRequest
    ): TransactionApprovalSuccessModel

    @POST("wallet/v1/move/")
    suspend fun doMove(
        @Header("Authorization") token: String,
        @Body request: JointAccountWithdrawRequestNew
    ): JointAccountWithdrawResponse

    @GET("wallet/v1/deposit_account/")
    suspend fun getDepositAccounts(
        @Header("Authorization") token: String,
    ): AccountListResponse

    @GET("initial_data/")
    suspend fun getInitData(
    ): InitDataResponse

    @POST("auth/otp_generate/")
    suspend fun otpGenerate(
        @Header("Authorization") token: String?,
        @Header("deviceId") deviceId: String,
        @Body forgotRequestModel: ForgotRequestModel
    ): ForgotSuccessModel

    @POST("auth/verify/")
    suspend fun doVerifyEmail(
        @Header("Authorization") token: String?, @Body verifyRequestEmail: VerifyRequestEmail
    ): OtpVerifySuccessModel

}

