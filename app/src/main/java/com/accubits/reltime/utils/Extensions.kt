package com.accubits.reltime.utils

import android.annotation.SuppressLint
import android.app.*
import android.content.*
import android.graphics.*
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.telephony.TelephonyManager
import android.text.InputFilter
import android.util.Patterns
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ComponentActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.text.buildSpannedString
import androidx.core.text.color
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.accubits.reltime.R
import com.accubits.reltime.activity.contacts.model.ContactData
import com.accubits.reltime.activity.myAccounts.model.*
import com.accubits.reltime.activity.region.model.Country
import com.accubits.reltime.constants.ReltimeConstants
import com.accubits.reltime.constants.ReltimeConstants.DIALOG_CANCEL
import com.accubits.reltime.constants.ReltimeConstants.DIALOG_NEGATIVE
import com.accubits.reltime.constants.ReltimeConstants.DIALOG_POSITIVE
import com.accubits.reltime.helpers.Utils
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*


object Extensions {

    fun String.isValidEmail(): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(this).matches()
    }

    fun String.isValidAmount(): Boolean {
        var verified = true
        var decimalPointFound = false
        // ""             "."                      "1299."
        if (isEmpty() || length == 1 && this == "." || !last().isDigit()) {
            return false
        }
        forEach {
            if (!it.isDigit()) {
                if (it == '.') {
                    if (!decimalPointFound) decimalPointFound = true
                    else verified = false
                } else {
                    verified = false
                }
            }
        }
        return if (verified) {
            toFloat() != 0f
        } else {
            false
        }
    }

    fun Context.showToast(message: String?) {
        message?.let {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }
    }

    fun String.removeAllSpaces(): String {
        var num = this.replace("(", "")
        num = num.replace(")", "")
        num = num.replace("-", "")
        return num.replace(" ", "")
    }

    fun createCharacterListMap(contactList: ArrayList<ContactData>?): HashMap<Int, Char>? {
        contactList?.apply {
            return if (isNotEmpty()) {
                val characterMap = HashMap<Int, Char>()
                var currentChar = ""
                forEachIndexed { index, contactData ->
                    val firstChar = contactData.contactName[0].toString()
                    if (firstChar != currentChar) {
                        currentChar = firstChar
                        characterMap[index] = contactData.contactName[0]
                    }
                }
                characterMap
            } else {
                null
            }
        }
        return null
    }


    fun View.makeVisibility(visibility: Boolean) {
        if (visibility) {
            this.visibility = View.VISIBLE
        } else {
            this.visibility = View.INVISIBLE
        }
    }

    fun String.monthYearFormatter(): String {

        return if (length == 2) {
            if (last() == '/') {
                "0${first()}/"
            } else {
                "$this/"
            }
        } else {
            if (length > 5) {
                this.substring(startIndex = 0, endIndex = 5)
            } else {
                this
            }
        }
    }

    fun String.createInitials(): String {
        return if (this.length > 1)
            this.substring(0, 2).uppercase(Locale.getDefault())
        else this.substring(0, 1).uppercase(Locale.getDefault())
        /*return this.split(' ').mapNotNull { it.firstOrNull()?.toString() }
            .reduce { acc, s -> acc + s }.uppercase(Locale.getDefault())*/
    }

    inline fun <T> sdkBelow29(sdkBelow29: () -> T): T? {
        return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            sdkBelow29()
        } else null
    }

    fun Context.getProgressDialog(message: String = "Uploading documents, Please wait.."): ProgressDialog {
        val progress = ProgressDialog(this)
        progress.setMessage(message)
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        progress.isIndeterminate = true
        progress.setCancelable(false)
        return progress
    }

    fun Context.showQRCodeDetailsBottomSheet(publicKey: String, title: String? = null) {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.bottom_sheet_qrcode_wallet)
        val tvTitle = dialog.findViewById<TextView>(R.id.tvTitle)
        val tvWallet = dialog.findViewById<TextView>(R.id.tvWalletAddress)
        val tvCopyAddress = dialog.findViewById<TextView>(R.id.tvCopyAddress)
        val tvSendAddress = dialog.findViewById<TextView>(R.id.tvSendAddress)
        title?.let { tvTitle.text = it }
        tvCopyAddress.setOnClickListener {
            copyToClipBoard(resources.getString(R.string.wallet_address_), publicKey)
        }
        tvSendAddress.setOnClickListener {
            shareTextViaApps(publicKey)
        }
        tvWallet.text = publicKey
        dialog.apply {
            show()
            window?.apply {
                setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                attributes.windowAnimations = R.style.BottomDialogAnimation
                setGravity(Gravity.BOTTOM)
            }
        }

    }

    fun String.convertDate(): String {
        var index = this.lastIndexOf(":")
        var finalDate = this.substring(
            0, index
        ) + this.substring(index + 1)
        println(finalDate)
        var indexSSS = finalDate.lastIndexOf(".")
        var indexPlus = finalDate.lastIndexOf("+")

        var moreFInal = finalDate.substring(
            0, indexSSS
        ) + finalDate.substring(indexPlus, finalDate.lastIndex + 1)
        print(moreFInal)
        val parser: DateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ssZ", Locale.ENGLISH)
        val endDateParser: DateFormat = SimpleDateFormat("dd/MMM/yyyy")
        var createdDate = endDateParser.format(parser.parse(moreFInal))

        return createdDate
    }


    fun Context.copyToClipBoard(label: String, message: String) {
        val clipboard: ClipboardManager? =
            getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager?
        val clip = ClipData.newPlainText(label, message)
        clipboard?.apply {
            setPrimaryClip(clip)
            showToast(resources.getString(R.string.n_copied_to_clipboard, label))
        }
    }

    fun Context.shareTextViaApps(message: String) {
        val sendIntent = Intent()
        sendIntent.action = Intent.ACTION_SEND
        sendIntent.putExtra(
            Intent.EXTRA_TEXT, message
        )
        sendIntent.type = "text/plain"
        startActivity(sendIntent)
    }

    fun Context.openDatePicker(onDateSelected: (String) -> Unit) {
        val year: Int
        val month: Int
        val day: Int

        val calendar = Calendar.getInstance()
        year = calendar.get(Calendar.YEAR)
        month = calendar.get(Calendar.MONTH)
        day = calendar.get(Calendar.DAY_OF_MONTH)
        calendar.time = Date()

        val datePickerDialog = DatePickerDialog(
            this, { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
                val mMonth = month.plus(1)
                val newDayOfMonth = if (dayOfMonth < 10) "0$dayOfMonth" else "$dayOfMonth"
                val newMonth = if (mMonth < 10) "0${mMonth}" else "$mMonth"
                onDateSelected(
                    "$year-$newMonth-$newDayOfMonth"
                )
            }, year, month, day
        )
        datePickerDialog.show()
    }

    fun <R> Context.clearStackAndOpenActivity(it: Class<R>, extras: Bundle.() -> Unit = {}) {
        val intent = Intent(this, it)
        intent.putExtras(Bundle().apply(extras))
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        this.findActivity()?.let {
            it.finish()
        }
    }

    fun <T> Context.openActivity(
        it: Class<T>, shouldFinish: Boolean = false, extras: Bundle.() -> Unit = {}
    ) {
        val intent = Intent(this, it)
        intent.putExtras(Bundle().apply(extras))
        startActivity(intent)
        if (shouldFinish) this.findActivity()?.let {
            it.finish()
        }
    }

    fun Context.findActivity(): Activity? = when (this) {
        is AppCompatActivity -> this
        is ComponentActivity -> this
        is ContextWrapper -> baseContext.findActivity()
        else -> null
    }

    fun ImageView.loadImageWithUrl(imageUrl: String?, onError: (status: Boolean) -> Unit) {
        try {
            if (imageUrl != null) {
                if (context != null) {
                    Glide.with(context).load(imageUrl).listener(object : RequestListener<Drawable> {
                        override fun onLoadFailed(
                            p0: GlideException?, p1: Any?, p2: Target<Drawable>?, p3: Boolean
                        ): Boolean {
                            onError(true)
                            return false
                        }

                        override fun onResourceReady(
                            p0: Drawable?,
                            p1: Any?,
                            p2: Target<Drawable>?,
                            p3: DataSource?,
                            p4: Boolean
                        ): Boolean {
                            //do something when picture already loaded
                            onError(false)
                            return false
                        }
                    }).into(this)
                }
            } else {
                onError(true)
            }

        } catch (e: Exception) {
            e.printStackTrace()
            onError(true)
        }
    }

    fun Context.isNetworkAvailable(): Boolean {
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
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


    fun EditText.acceptOnlyAmount() {
        val filter = InputFilter { source, start, end, dest, dstart, dend ->
            for (i in start until end) {
                if ((!Character.isDigit(source[i]) && source[i].toString() != ".") || (source[i].toString() == "." && text.contains(
                        "."
                    ))
                ) {
                    return@InputFilter ""
                }
            }
            null
        }
        filters = arrayOf(filter)
    }

    fun View.setButtonEnable(enable: Boolean) {
        isEnabled = enable
        alpha = if (enable) 1f else .5f
    }

    fun Activity.hideSoftKeyboard() {
        val view: View? = this.currentFocus
        if (view != null) {
            val imm: InputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    fun ReltimeAccount.getAccountId(): String {
        return when (this) {
            is BankAccount -> id.toString()
            is Card -> cardNumber
            is JointAccount -> id.toString()
            is RTO -> coinCode + userAddress
            is CryptoWallet -> coinCode + address
            else -> ""
        }
    }

    fun ReltimeAccount.accountWithdrawType(): String {
        return when (this) {
            is BankAccount -> "bankAccounts"
            is JointAccount -> "jointAccounts"
            is RTO -> "wallets"
            is CryptoWallet -> type ?: "wallets"
            else -> ""
        }
    }

    fun ReltimeAccount.id(): Int {
        return when (this) {
            is BankAccount -> id
            is JointAccount -> id
            is RTO -> id
            is CryptoWallet -> id
            else -> 0
        }
    }

    fun ReltimeAccount.getCoinCode(): String {
        return when (this) {
            is JointAccount -> coinCode
            is RTO -> coinCode
            is CryptoWallet -> coinCode
            else -> ""
        }
    }

    fun ReltimeAccount.getSymbolWithCoinCode(): String {
        return when (this) {
            is RTO -> if (symbol == null) coinCode.convertRTOtoEURO()
            else "$coinCode (${symbol})".convertRTOtoEURO()
            is JointAccount -> if (symbol == null) coinCode.convertRTOtoEURO()
            else "$coinCode (${symbol})".convertRTOtoEURO()
            is CryptoWallet -> if (symbol == null) coinCode
            else "$coinCode (${symbol})"
            else -> ""
        }
    }

    fun ReltimeAccount.getAccountBalanceWithCoinCode() = when (this) {
        is RTO -> if (symbol == null) {
            "$coinCode $balance".convertRTOtoEURO()
        } else {
            "$coinCode (${symbol}) $balance".convertRTOtoEURO()
        }
        is Card -> cardName
        is BankAccount -> "---"
        is JointAccount -> if (symbol == null) {
            "$coinCode $rtoBalance".convertRTOtoEURO()
        } else {
            "$coinCode (${symbol}) $rtoBalance".convertRTOtoEURO()
        }
        is CryptoWallet -> if (symbol == null) {
            "$coinCode $balance"
        } else {
            "$coinCode (${symbol}) $balance"
        }
        else -> ""
    }

    fun ReltimeAccount.getAddress(): String {
        return when (this) {
            is BankAccount -> id.toString()
            is JointAccount -> address
            is RTO -> userAddress
            is CryptoWallet -> address
            else -> ""
        }
    }

    fun ReltimeAccount.getAccountName(): String {
        return when (this) {
            is BankAccount -> "$accountNumber ($swiftCode)"
            is JointAccount -> "$name (Joint Account)"
            is RTO -> description
            is CryptoWallet -> coin_name
            else -> ""
        }
    }

    fun ReltimeAccount.getAccountBalance(): String {
        return when (this) {
            is BankAccount -> ""
            is JointAccount -> rtoBalance
            is RTO -> balance
            is CryptoWallet -> balance
            else -> ""
        }
    }

    fun ReltimeAccount.getStatistics(): String? {
        return when (this) {
            is JointAccount -> statistics
            is RTO -> statistics
            is CryptoWallet -> statistics
            else -> null
        }
    }

    fun Activity.showAlertDialog(
        icon: Int? = null,
        title: String? = null,
        description: String? = null,
        positiveButton: String? = null,
        negativeButton: String? = null,
        cancelable: Boolean = true,
        onItemClicked: (DialogInterface, Int) -> Unit
    ) {

        val builder = AlertDialog.Builder(this, R.style.DialogTheme)
        builder.apply {
            icon?.let { setIcon(it) }
            title?.let { setTitle(it) }
            description?.let { setMessage(it) }
            setCancelable(cancelable)
            positiveButton?.let {
                setPositiveButton(
                    it
                ) { dialog, id ->
                    onItemClicked.invoke(dialog, DIALOG_POSITIVE)
                }
            }
            negativeButton?.let {
                setNegativeButton(
                    it
                ) { dialog, id ->
                    onItemClicked.invoke(dialog, DIALOG_NEGATIVE)
                }
            }
            setOnDismissListener {
                onItemClicked.invoke(it, DIALOG_CANCEL)
            }
        }
        builder.create()
        builder.show()
    }

    fun EditText.doSearchOnTextChange(
        onSearchTextChange: (
            text: CharSequence?, start: Int, before: Int, count: Int
        ) -> Unit = { _, _, _, _ -> }
    ) {
        doOnTextChanged { text1, start: Int, before: Int, count ->
            /*   postDelayed({
                   if ( text.length==start+count){
                       onSearchTextChange.invoke(text1.toString(),start,before,count)
                   }
               },400)*/
            onSearchTextChange.invoke(text1.toString(), start, before, count)
        }
    }

    fun Activity.getBitmapFromView(view: View, callback: (Bitmap) -> Unit) {
        window?.let { window ->
            val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
            val locationOfViewInWindow = IntArray(2)
            view.getLocationInWindow(locationOfViewInWindow)
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    PixelCopy.request(
                        window, Rect(
                            locationOfViewInWindow[0],
                            locationOfViewInWindow[1],
                            locationOfViewInWindow[0] + view.width,
                            locationOfViewInWindow[1] + view.height
                        ), bitmap, { copyResult ->
                            if (copyResult == PixelCopy.SUCCESS) {
                                callback(bitmap)
                            }
                            // possible to handle other result codes ...
                        }, Handler()
                    )
                } else {
                    callback(bitmap)
                }
            } catch (e: IllegalArgumentException) {
                // PixelCopy may throw IllegalArgumentException, make sure to handle it
                e.printStackTrace()
            }

        }
    }

    fun RecyclerView.setDivider(@DrawableRes drawableRes: Int) {
        val divider = DividerItemDecoration(
            this.context, DividerItemDecoration.VERTICAL
        )
        val drawable = ContextCompat.getDrawable(
            this.context, drawableRes
        )
        drawable?.let {
            divider.setDrawable(it)
            addItemDecoration(divider)
        }
    }


    fun Context.openSystemPermissionSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        val uri = Uri.fromParts("package", packageName, null);
        intent.data = uri;
        startActivity(intent);
    }


    fun Activity.shareReceipt(view: View, text: String? = null) {
        getBitmapFromView(view) { bitmapDetail ->
            val bitmapHeader = Utils.createTextBitmap(
                this,
                text,
                bitmapDetail.width,
                (bitmapDetail.height * .15).toInt()
            )
            Utils.combineImages(bitmapHeader, bitmapDetail)?.let {
                val imageFile = Utils.storeFile(this, it)
                val intent = Intent()
                intent.action = Intent.ACTION_SEND

                val uri = imageFile?.let { it1 ->
                    FileProvider.getUriForFile(
                        applicationContext, "$packageName.fileprovider", it1
                    )
                }
                intent.putExtra(Intent.EXTRA_STREAM, uri)
                // intent.putExtra(Intent.EXTRA_TEXT, text)
                intent.type = "image/*"
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                startActivity(intent)
            }
        }
    }

    fun Activity.getDefaultCountry(): Country? {
        val countryList = ReltimeConstants.countries
        if (countryList != null && countryList.isNotEmpty()) {
            val countryCodeValue =
                getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            val iso = countryCodeValue.networkCountryIso
            val code = countryList.find {
                it.countryISOCode.trim().equals(iso.trim(), ignoreCase = true)
            }
            return code
        }
        return null
    }


    fun EditText.allowOnlyAlphaNumericCharacters() {
        filters = arrayOf(
            InputFilter { src, start, end, dst, dstart, dend ->
                if (src.toString().matches(Regex("[a-zA-Z 0-9]+"))) {
                    src
                } else ""
            }
        )
    }

    fun TextView.markRequiredInRed() {
        text = buildSpannedString {
            append(text)
            color(Color.RED) { append(" *") } // Mind the space prefix.
        }
    }

    @SuppressLint("HardwareIds")
    fun Context.getDeviceId(): String {
        return Settings.Secure.getString(
            contentResolver,
            Settings.Secure.ANDROID_ID
        )
    }

    fun Context.openPlayStore(newPackageName: String? = null) {
        val mPackageName = newPackageName ?: packageName
        val uri =
            Uri.parse("market://details?id=$mPackageName")
        val goToMarket = Intent(Intent.ACTION_VIEW, uri)
        // To count with Play market backstack, After pressing back button,
        // to taken back to our application, we need to add following flags to intent.
        goToMarket.addFlags(
            Intent.FLAG_ACTIVITY_NO_HISTORY or
                    Intent.FLAG_ACTIVITY_NEW_DOCUMENT or
                    Intent.FLAG_ACTIVITY_MULTIPLE_TASK
        )
        goToMarket.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        try {
            startActivity(goToMarket)
        } catch (e: ActivityNotFoundException) {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=$mPackageName")
                )
            )
        }
    }

}

fun String.convertRTOtoEURO(): String {

    android.util.Log.e("convertRTOtoEURO", this)

    return if (this.contains("RTO")) {
        this.replace("RTO", "EURO")
    } else {
        this
    }
}

fun String.convertReltimeToNagra(): String {

    android.util.Log.e("convertReltimeToNagra", this)

    return if (this.contains("Reltime")) {
        this.replace("Reltime", "Nagra")
    } else {
        this
    }
}