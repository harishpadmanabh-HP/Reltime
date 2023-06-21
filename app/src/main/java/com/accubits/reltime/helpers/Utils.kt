package com.accubits.reltime.helpers

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.ContentValues
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.*
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Environment
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.TextUtils
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import android.util.Log
import android.util.Patterns
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.accubits.reltime.BuildConfig
import com.accubits.reltime.R
import com.accubits.reltime.activity.myAccounts.model.*
import com.accubits.reltime.activity.rto.model.RtoP2PResponseModel
import com.accubits.reltime.activity.withdraw.model.AccountResult
import com.accubits.reltime.constants.ReltimeConstants
import com.accubits.reltime.helpers.dateTimeUtils.DateTimeStyle
import com.accubits.reltime.helpers.dateTimeUtils.DateTimeUtils.formatWithPattern
import com.accubits.reltime.helpers.dateTimeUtils.DateTimeUtils.formatWithStyle
import com.accubits.reltime.helpers.dateTimeUtils.DateTimeUtils.getPreviousWeekDate
import com.accubits.reltime.helpers.dateTimeUtils.DateTimeUtils.isToday
import com.accubits.reltime.helpers.dateTimeUtils.DateTimeUtils.isYesterday
import com.accubits.reltime.models.Data
import com.accubits.reltime.models.TransferResponseModel
import com.accubits.reltime.utils.Extensions.getAddress
import com.accubits.reltime.utils.convertRTOtoEURO
import com.accubits.reltime.utils.convertReltimeToNagra
import com.accubits.reltime.views.privacy.PrivacyActivity
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.i18n.phonenumbers.NumberParseException
import com.google.i18n.phonenumbers.PhoneNumberUtil
import org.web3j.crypto.Credentials
import org.web3j.crypto.RawTransaction
import org.web3j.crypto.TransactionEncoder
import org.web3j.utils.Numeric
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.security.*
import java.text.DateFormat
import java.text.NumberFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import javax.crypto.BadPaddingException
import javax.crypto.IllegalBlockSizeException
import javax.crypto.NoSuchPaddingException


object Utils {
    var validated: Boolean = false
    var FromDate: String? = ""
    var ToDateDis: String? = ""
    var FromDateDis: String? = ""

    var ToDate: String? = ""
    var LendId: String? = ""
    var rangeOne: String? = ""

    var rangeTwo: String? = ""

    var tenure: String? = ""


    //newBorrows Filter
    var amount_from = ""
    var amount_to = ""
    var installments_from = ""
    var installments_to = ""
    var interest_rate = ""
    var no_collateral = ""
    var collateral_from = ""
    var collateral_to = ""
    var amount_sort = ""
    var interest_rate_sort = ""
    var loan_term_sort = ""

    var borrow: Boolean = false

    //for staging
    var chainidStg: Long = 24242

    //for production
    var chainidProd: Long = 32323

    var transaction_status: String = ""
    var date_type: Int? = null

    var account_transaction_status: String = ""
    var account_date_type: Int? = null

    const val DATE_FORMAT_DEFAULT = "MMM dd, yyyy"
    const val TIME_FORMAT_DEFAULT = "hh:mm a"
    const val DATE_TIME_FORMAT_DEFAULT =
        "$DATE_FORMAT_DEFAULT, $TIME_FORMAT_DEFAULT" //"MMM dd, yyyy, hh:mm a"
    const val OTP_MAX_TIME = 90000L

    fun setStatusBarColour(context: Activity, color: Int) {
        context.window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        context.window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        context.window.statusBarColor = color
    }

    fun isValidEmail(target: CharSequence?): Boolean {
        return !TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(
            target.toString().trim()
        ).matches()
    }

    fun getDateCurrentTimeZone1(
        timestamp: Double,
        toFormat: String? = DATE_TIME_FORMAT_DEFAULT
    ): String? {
        try {
            val date = Date((timestamp * 1000).toLong())
            val sdf = SimpleDateFormat(toFormat)
            return sdf.format(date)
        } catch (e: java.lang.Exception) {
        }
        return ""
    }

    fun isNetworkAvailable(application: Context): Boolean? {
        val connectivityManager =
            application.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val nw = connectivityManager.activeNetwork ?: return false
            val actNw = connectivityManager.getNetworkCapabilities(nw)
            actNw != null && (actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) || actNw.hasTransport(
                NetworkCapabilities.TRANSPORT_CELLULAR
            ) || actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) || actNw.hasTransport(
                NetworkCapabilities.TRANSPORT_BLUETOOTH
            ))
        } else {
            val nwInfo = connectivityManager.activeNetworkInfo
            nwInfo != null && nwInfo.isConnected
        }
    }

    fun convertToEmoji(countryCode: String): String {
        val firstLetter = Character.codePointAt(countryCode, 0) - 0x41 + 0x1F1E6
        val secondLetter = Character.codePointAt(countryCode, 1) - 0x41 + 0x1F1E6
        return String(Character.toChars(firstLetter)) + String(Character.toChars(secondLetter))
    }

    fun getFormattedAmount(price: String?): String {
        if (price == null || price.isEmpty()) {
            return ""
        }
        return try {
            val format: NumberFormat = NumberFormat.getNumberInstance()
            format.maximumFractionDigits = 0
            format.minimumFractionDigits = 0
            "${format.format(price.toDouble())}"
        } catch (ex: Exception) {
            ""
        }
    }

    class NumericKeyBoardTransformationMethod :
        PasswordTransformationMethod() {
        override fun getTransformation(source: CharSequence, view: View): CharSequence {
            return source
        }
    }

    fun decryptText(decryptor: DeCryptor, enCryptor: EnCryptor): String {
        try {


            /*Log.d(
                "final!!",
                decryptor.decryptData(ReltimeConstants.RELTIME, enCryptor.getEncryption(), enCryptor.getIv())
                    .toString()
            )*/
            return if (enCryptor.getEncryption() != null) {
                decryptor.decryptData(
                    ReltimeConstants.RELTIME,
                    enCryptor.getEncryption(),
                    enCryptor.getIv()
                )
                    .toString()
            } else {
                ""
            }

        } catch (e: UnrecoverableEntryException) {
            Log.e(ContentValues.TAG, "decryptData() called with: " + e.message, e)
        } catch (e: NoSuchAlgorithmException) {
            Log.e(ContentValues.TAG, "decryptData() called with: " + e.message, e)
        } catch (e: KeyStoreException) {
            Log.e(ContentValues.TAG, "decryptData() called with: " + e.message, e)
        } catch (e: NoSuchPaddingException) {
            Log.e(ContentValues.TAG, "decryptData() called with: " + e.message, e)
        } catch (e: NoSuchProviderException) {
            Log.e(ContentValues.TAG, "decryptData() called with: " + e.message, e)
        } catch (e: IOException) {
            Log.e(ContentValues.TAG, "decryptData() called with: " + e.message, e)
        } catch (e: InvalidKeyException) {
            Log.e(ContentValues.TAG, "decryptData() called with: " + e.message, e)
        } catch (e: IllegalBlockSizeException) {
            e.printStackTrace()
        } catch (e: BadPaddingException) {
            e.printStackTrace()
        } catch (e: InvalidAlgorithmParameterException) {
            e.printStackTrace()
        }
        return ""
    }

    fun getKeyHasForTransaction(
        data: Data,
        preferenceManager: PreferenceManager,
        chainId: Long = BuildConfig.CHAIN_ID
    ): String {
        val rawTransaction = RawTransaction.createTransaction(
            data.txnBuild.nonce.replace("0x", "")
                .toBigInteger(radix = 16),
            data.txnBuild.gasPrice.replace("0x", "")
                .toBigInteger(radix = 16),
            data.txnBuild.gasLimit.replace("0x", "")
                .toBigInteger(radix = 16),
            data.txnBuild.to,
            data.txnBuild.value.replace("0x", "")
                .toBigInteger(radix = 16),
            data.txnBuild.data
        )

        val key = preferenceManager.getPrivateKey()
        val a = Credentials.create(key)
        val signedMessage = TransactionEncoder.signMessage(
            rawTransaction, chainId,
            a
        )
        return Numeric.toHexString(signedMessage)
    }

    fun getKeyHasForTransferTransaction(
        data: TransferResponseModel.Data.Data,
        preferenceManager: PreferenceManager
    ): String {
        val rawTransaction = RawTransaction.createTransaction(
            data.txnBuild.nonce.replace("0x", "")
                .toBigInteger(radix = 16),
            data.txnBuild.gasPrice.replace("0x", "")
                .toBigInteger(radix = 16),
            data.txnBuild.gasLimit.replace("0x", "")
                .toBigInteger(radix = 16),
            data.txnBuild.to,
            data.txnBuild.value.replace("0x", "")
                .toBigInteger(radix = 16),
            data.txnBuild.data
        )
        val key = preferenceManager.getPrivateKey()
        val a = Credentials.create(key)
        val signedMessage = TransactionEncoder.signMessage(
            rawTransaction, BuildConfig.CHAIN_ID,
            a
        )
        return Numeric.toHexString(signedMessage)
    }


    fun getKeyHasForTransactionSecond(
        data: RtoP2PResponseModel.Result,
        preferenceManager: PreferenceManager
    ): String {
        var rawTransaction = RawTransaction.createTransaction(
            data.txnBuild.nonce.replace("0x", "")
                .toBigInteger(radix = 16),
            data.txnBuild.gasPrice.replace("0x", "")
                .toBigInteger(radix = 16),
            data.txnBuild.gasLimit.replace("0x", "")
                .toBigInteger(radix = 16),
            data.txnBuild.to,
            data.txnBuild.value.replace("0x", "")
                .toBigInteger(radix = 16),
            data.txnBuild.data
        )
        var signedMessage = TransactionEncoder.signMessage(
            rawTransaction, BuildConfig.CHAIN_ID,
            Credentials.create(preferenceManager.getPrivateKey())
        )
        return Numeric.toHexString(signedMessage)
    }

    /* fun toDuration(duration: Long): String? {
         val res = StringBuffer()

         val current: Long = System.currentTimeMillis()
         val temp = duration / current
         if (temp > 0) {
             res.append(temp).append(" ").append(current)
                 .append(if (temp != 1L) "s" else "").append(" ago")
         }

         return if ("" == res.toString()) "0 seconds ago" else res.toString()
     }*/

    fun toDuration(duration: Long): String? {
        val res = StringBuffer()
        for (i in times.indices) {
            val current = times[i]
            val temp = duration / current
            if (temp > 0) {
                res.append(temp).append(" ").append(timesString[i])
                    .append(if (temp != 1L) "s" else "").append(" ago")
                break
            }
        }
        return if ("" == res.toString()) "0 seconds ago" else res.toString()
    }

    val times: List<Long> = listOf(
        TimeUnit.DAYS.toMillis(365),
        TimeUnit.DAYS.toMillis(30),
        TimeUnit.DAYS.toMillis(1),
        TimeUnit.HOURS.toMillis(1),
        TimeUnit.MINUTES.toMillis(1),
        TimeUnit.SECONDS.toMillis(1)
    )
    val timesString = listOf("year", "mon", "day", "hr", "min", "sec")

    fun getDatesInString(date: String): String? {
        //AppLog.d(TAG, "getDatesInString() called with: date = [$date]")
        val parser: DateFormat = SimpleDateFormat("dd MMM yyyy hh:mm:ss", Locale.ENGLISH)
        val date1 = parser.parse(date)
        return if (isToday(date1)) {
            "Today"
        } else if (isYesterday(date1)) {
            "Yesterday"
        } else if (getPreviousWeekDate(Calendar.getInstance().time).time < date1!!.time) {
            formatWithPattern(date1, "EEEE")
        } else {
            formatWithStyle(date1, DateTimeStyle.MEDIUM)
        }
    }

    fun storeFile(context: Context, bitmap: Bitmap): File? {
        val now = Date()
        var imgPath = android.text.format.DateFormat.format("yyyy-MM-dd_hh:mm:ss", now)

        // image naming and path  to include sd card  appending name you choose for file
        imgPath = imgPath.toString().replace(":", "-")
        Log.e("imgPath", context.resources.getString(R.string.app_name) + imgPath)
        val cw = ContextWrapper(context)
        val directory = cw.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val imageFile = File(
            directory!!.path,
            context.resources.getString(R.string.app_name) + imgPath.trim() + ".jpg"
        )
        val outputStream = FileOutputStream(imageFile)
        val quality = 100
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
        outputStream.flush()
        outputStream.close()
        return imageFile
    }

    fun getGreetingMessage(context: Context): String {
        val c = Calendar.getInstance()
        val timeOfDay = c.get(Calendar.HOUR_OF_DAY)

        return when (timeOfDay) {
            in 0..11 -> context.getString(R.string.good_morning)
            in 12..15 -> context.getString(R.string.good_afternoon)
            in 16..20 -> context.getString(R.string.good_evening)
            // in 21..23 -> context.getString(R.string.good_night)
            else -> context.getString(R.string.hello)
        }
    }

    fun setUpBiometricLoginFlags(preferenceManager: PreferenceManager) {
        preferenceManager.setLoginBiometricEnabled(false)
        preferenceManager.setLoginPINEnabled(false)
        preferenceManager.setLoginPINSetStatus(false)
    }

    fun onClearMnemonics(preferenceManager: PreferenceManager) {
        preferenceManager.setPublickKey("")
        preferenceManager.setPublicKeyFromLogin("")
        preferenceManager.setPrivateKey("")
        preferenceManager.setMemonic(false)
        preferenceManager.setBTCPrivateKey("")
        preferenceManager.setBTCPublickKey("")
    }

    fun setPasswordToggle(ibToggle: ImageView, etPassword: EditText) {
        ibToggle.setOnClickListener {
            if (etPassword.transformationMethod == PasswordTransformationMethod.getInstance()) {
                etPassword.transformationMethod =
                    HideReturnsTransformationMethod.getInstance()
                ibToggle.setImageDrawable(
                    ContextCompat.getDrawable(
                        ibToggle.context,
                        R.drawable.ic_eye
                    )
                )
            } else {
                etPassword.transformationMethod = PasswordTransformationMethod.getInstance()
                ibToggle.setImageDrawable(
                    ContextCompat.getDrawable(
                        ibToggle.context,
                        R.drawable.password_toggle_eye_close
                    )
                )
            }
            etPassword.setSelection(etPassword.text.length)
        }
    }

    fun createContactDisclosureAlert(
        context: Context,
        onItemClicked: (Int) -> Unit
    ) {
        val dialog = Dialog(context)
        dialog.setContentView(R.layout.alert_contact_disclosure)
        val btnCancel: Button = dialog.findViewById(R.id.button) as Button
        val btnProcced: Button = dialog.findViewById(R.id.button2) as Button
        val tvPrivacyPolicy: View = dialog.findViewById(R.id.tvPrivacyPolicy) as View
        dialog.setCancelable(false)
        btnCancel.setOnClickListener {
            dialog.dismiss()
            onItemClicked.invoke(ReltimeConstants.DIALOG_CANCEL)
        }
        btnProcced.setOnClickListener {
            dialog.dismiss()
            onItemClicked.invoke(ReltimeConstants.DIALOG_POSITIVE)
        }
        tvPrivacyPolicy.setOnClickListener {
            val intent = Intent(context, PrivacyActivity::class.java)
            intent.putExtra(PrivacyActivity.URL, PrivacyActivity.PRIVACY_POLICY)
            context.startActivity(intent)
        }
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.show()
    }

    fun getRTOAmount(double: Double) = "RTO (€) $double".convertRTOtoEURO()
    fun getRTOAmount(value: String) = "RTO (€) $value".convertRTOtoEURO()


    fun getProgressDialog(context: Context): AlertDialog {
        val dialog = AlertDialog.Builder(context).setCancelable(false)
            .setView(R.layout.progress_dialog)
            .create()

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return dialog
    }

    fun getCollateralValue(
        context: Context,
        collateralStatus: String,
        collateralAmount: String?,
        coin: String?
    ): String {
        return if (collateralStatus == "OFF")
            context.resources.getString(R.string.not_applicable)
        else {
            if (collateralAmount != null) // some cases it is not coming currently
                context.resources.getString(R.string.n_n, coin, collateralAmount)
            else
                context.resources.getString(R.string.applicable)
        }

    }

    fun handleBottomSheet(behavior: BottomSheetBehavior<*>, view: View) {

        behavior.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {

            override fun onStateChanged(bottomSheet: View, newState: Int) {


            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                if (slideOffset > 0.86) {
                    view.visibility = View.GONE

                } else {
                    view.visibility = View.VISIBLE

                }

            }
        })
    }

    fun getMembers(context: Context, int: Int): String = context.resources.getString(
        if (int <= 1) R.string.n_member else R.string.n_members,
        int.toString()
    )

    fun getAccountName(account: ReltimeAccount) = when (account) {
        is RTO -> account.displayText.convertRTOtoEURO().convertReltimeToNagra()
        is Card -> account.cardName
        is BankAccount -> account.bank_name
        is JointAccount -> account.name
        is CryptoWallet -> account.coin_name
        else -> ""
    }

    //get account number

    fun getAccountInfo(context: Context, account: ReltimeAccount) = when (account) {
        is RTO -> account.description.convertRTOtoEURO().convertReltimeToNagra()
        is Card -> account.cardName
        is BankAccount -> account.accountNumber
        is JointAccount -> getMembers(context = context, account.members)
        is CryptoWallet -> account.coin_name
        else -> ""
    }

    fun getAccountAddress(account: ReltimeAccount) = when (account) {
        is RTO -> account.userAddress.toString()
        is Card -> account.getAddress()
        is BankAccount -> account.getAddress()
        is JointAccount -> account.getAddress()
        is CryptoWallet -> account.getAddress()
        else -> ""
    }

    fun setAmountWithCoin(
        coinValue: String?,
        amountValue: String,
        sizeArray: Array<Float> = arrayOf(0.7f, 1f, 0.7f)
    ): SpannableStringBuilder {
        val coin = SpannableString(coinValue)
        val amount = SpannableString(amountValue)
        amount.setSpan(
            RelativeSizeSpan(sizeArray[0]),
            amountValue.indexOf(".") + 1,
            amountValue.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        coin.setSpan(
            StyleSpan(R.font.inter_regular),
            coin.indexOf(".") + 1,
            coin.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        coin.setSpan(
            RelativeSizeSpan(sizeArray[2]),
            0,
            coin.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        val amountOut = SpannableStringBuilder()
        amountOut.append(coin)
        amountOut.append(" $amount")
        return amountOut
    }


    fun firstLetter(input: String): String {
        val firstAlphabet = input[0].uppercaseChar()
        return firstAlphabet.toString()
    }

    fun Context.setUserImage(profileImage: String, imageView: ImageView) {
        Glide.with(this)
            .asGif()
            .load(profileImage)
            .into(imageView)
    }

    fun isValidPhoneLength(context: Context, phoneNumber: String, countryCode: String): Boolean {
        try {
            val phoneUtil = PhoneNumberUtil.getInstance(context)
            val number = phoneUtil.parse(phoneNumber, countryCode)
            return phoneUtil.isValidNumber(number)
        } catch (ex: NumberParseException) {
        }
        return false
    }

    fun buildAccountsList(accountResult: AccountResult): ArrayList<ReltimeAccount> {
        val accountList = ArrayList<ReltimeAccount>()
        accountResult.wallets?.rTO?.let {
            accountList.add(it)
        }
        accountResult.wallets?.rTC?.let { accountList.add(it) }
        accountResult.jointAccounts?.let { accountList.addAll(it) }
        accountResult.cryptoWallet?.let { accountList.addAll(it) }
        accountResult.bankAccounts?.let { accountList.addAll(it) }
        accountResult.cards?.let { accountList.addAll(it) }
        return accountList
    }

    enum class TransferContactType(val contactType: String) {
        PHONE("phone"),
        EMAIL("email"),
        WALLET("address")
    }

    fun setFormattedMonth(context: Context, value: String): String {
        val intVal = value.toIntOrNull()
        return intVal?.let {
            context.resources.getString(
                if (intVal < 2) R.string.n_month else R.string.n_months,
                value
            )
        } ?: ""
    }

    fun coinCodeWithSymbol(context: Context, coinCode: String, symbol: String?): String {
        return symbol?.let {
            context.resources.getString(R.string.n_c_n_c, coinCode, it).convertRTOtoEURO()
        } ?: coinCode.convertRTOtoEURO()
    }

    fun buildStyledAmount(context: Context? = null, amountValue: String): SpannableString {
        val firstPart = amountValue.split(".")[0]
        var firstPartLength = firstPart.length + 2
        val balanceString = SpannableString(
            context?.let {
                val largeText = it.resources.getString(
                    R.string.n_n,
                    it.resources.getString(
                        R.string.rto
                    ), firstPart
                )
                firstPartLength = largeText.length
                it.resources.getString(
                    R.string.n_n,
                    it.resources.getString(
                        R.string.rto
                    ), amountValue
                )
            } ?: amountValue

        )
        balanceString.setSpan(
            RelativeSizeSpan(1.6f),
            0,
            firstPartLength,
            0
        )
        return balanceString
    }

    fun hasPermissions(context: Context, permissions: Array<String>): Boolean =
        permissions.all {
            ActivityCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }

    fun createTextBitmap(context: Context,text:String?,width:Int,height:Int):Bitmap? {
        if (text==null)
            return null
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val scale: Float = context.resources.displayMetrics.density
        val canvas = Canvas(bitmap)
        canvas.drawColor(ContextCompat.getColor(context, R.color.blue2))
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.color = Color.WHITE
        paint.textSize = (18 * scale)
        paint.typeface = ResourcesCompat.getFont(context, R.font.inter_bold)
        val bounds = Rect()
        paint.getTextBounds(text, 0, text.length, bounds)
        val x = (bitmap.width - bounds.width()) / 2
        val y = (bitmap.height + bounds.height()) / 2

        canvas.drawText(text, x.toFloat(), y.toFloat(), paint)
        return bitmap
    }

    fun combineImages(
        c: Bitmap?,
        s: Bitmap
    ): Bitmap? {
        if (c==null)
            return s
        val cs: Bitmap?
        val width=s.width
        val height = c.height+s.height
        cs = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val comboImage = Canvas(cs)
        comboImage.drawBitmap(c, 0f, 0f, null)
        comboImage.drawBitmap(s, 0f, c.height.toFloat(), null)
       return cs
    }
}
