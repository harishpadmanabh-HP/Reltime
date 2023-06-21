package com.accubits.reltime.activity.addBankAccount

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.emoji.text.EmojiCompat
import androidx.lifecycle.lifecycleScope
import com.accubits.reltime.R
import com.accubits.reltime.activity.addBankAccount.model.BankItem
import com.accubits.reltime.activity.addBankAccount.viewmodel.AddBankAccountViewModel
import com.accubits.reltime.activity.region.model.Country
import com.accubits.reltime.activity.region.viewmodel.RegionSelectionViewModel
import com.accubits.reltime.activity.settings.popup.CountryPopupDialog
import com.accubits.reltime.api.ApiCallStatus
import com.accubits.reltime.databinding.ActivityAddBankAccountBinding
import com.accubits.reltime.helpers.Utils
import com.accubits.reltime.utils.Extensions.allowOnlyAlphaNumericCharacters
import com.accubits.reltime.utils.Extensions.getDefaultCountry
import com.accubits.reltime.utils.Extensions.hideSoftKeyboard
import com.accubits.reltime.utils.Extensions.markRequiredInRed
import com.accubits.reltime.utils.Extensions.setButtonEnable
import com.accubits.reltime.utils.Extensions.showToast
import com.accubits.reltime.utils.customView.CustomSpinnerAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.io.InputStream
import java.util.*


@AndroidEntryPoint
class AddBankAccountActivity : AppCompatActivity() {
    private lateinit var binder: ActivityAddBankAccountBinding
    private val viewModel by viewModels<AddBankAccountViewModel>()
    private val bankList = arrayListOf<BankItem>()
    private lateinit var selectedCountry: Country

    private var filePickerIntent: Intent? = null
    private var activityResultLauncher: ActivityResultLauncher<Intent>? = null
    private var selectedFileUri: Uri? = null
    private var fileName: String? = null
    private val regionSelectionViewModel by viewModels<RegionSelectionViewModel>()

    private val adapter by lazy {
        ArrayAdapter(
            this,
            R.layout.spinner_item,
            bankList
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binder = ActivityAddBankAccountBinding.inflate(layoutInflater)
        setContentView(binder.root)

        init()
        setListeners()
        collectData()
        prepareRegisters()
        viewModel.getBankList()
        loadCountriesFromJson()
    }

    private fun init() {
        binder.tvBankTitle.markRequiredInRed()
        binder.tvIBAN.markRequiredInRed()
        binder.tvSwiftCode.markRequiredInRed()
        binder.tvAccountTypeTitle.markRequiredInRed()
        binder.tvFullName.markRequiredInRed()
        binder.tvPONumber.markRequiredInRed()
        binder.tvAddress.markRequiredInRed()
        binder.tvAddress2.markRequiredInRed()
        binder.tvPhoneNumber.markRequiredInRed()
        binder.tvBankStatementTitle.markRequiredInRed()

        binder.etIBAN.allowOnlyAlphaNumericCharacters()
        binder.etFullName.allowOnlyAlphaNumericCharacters()
        binder.tvBank.setAdapter(adapter)
        val spinnerAdapter = CustomSpinnerAdapter(
            this,
            R.layout.spinner_item,
            arrayListOf(
                resources.getString(R.string.account_type),
                resources.getString(R.string.account_type_savings),
                resources.getString(R.string.account_type_checking)
            )
        )
        binder.spinnerAccountType.adapter = spinnerAdapter
        spinnerAdapter.setDropDownViewResource(R.layout.spinner_item)

    }

    private fun loadCountriesFromJson() {
        regionSelectionViewModel.extractCountryFromJson(resources.openRawResource(R.raw.country_codes))
    }

    private fun collectData() {
        collectBankList()
        collectAddBankAccount()
        observerCountryList()
    }

    private fun setListeners() {
        binder.ivBack.setOnClickListener { onBackPressed() }
        binder.tvAccountType.setOnClickListener {
            binder.spinnerAccountType.performClick()
        }
        binder.spinnerAccountType.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    if (position != 0)
                        binder.tvAccountType.text = binder.spinnerAccountType.selectedItem as String
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                }
            }
        binder.tvEmoji.setOnClickListener {
            CountryPopupDialog {
                setCountry(it)
            }
        }
        binder.btnAddAccount.setOnClickListener {
            validateData()
        }

        binder.tvBankStatement.setOnClickListener {
            if (selectedFileUri != null) return@setOnClickListener
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                activityResultLauncher?.launch(filePickerIntent)
            } else {
                fileManagerPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }
        binder.ivClose.setOnClickListener { fileSelected(null) }
    }

    private fun collectBankList() {
        lifecycleScope.launch {
            viewModel.bankListResponseFlow.collectLatest { response ->
                when (response.status) {
                    ApiCallStatus.LOADING -> {
                        binder.progressBar.visibility = View.VISIBLE
                        binder.scrollView.visibility = View.GONE
                        binder.btnAddAccount.visibility = View.GONE
                    }
                    ApiCallStatus.SUCCESS -> {
                        binder.progressBar.visibility = View.GONE
                        val responseOk = response.data?.status == 200 && response.data.success
                        if (responseOk && response.data != null && response.data.result.bankNameList.isNotEmpty()) {
                            binder.scrollView.visibility = View.VISIBLE
                            binder.btnAddAccount.visibility = View.VISIBLE
                            binder.tvNoBanks.visibility = View.GONE
                            bankList.clear()
                            bankList.addAll(response.data.result.bankNameList)
                            adapter.notifyDataSetChanged()

                        } else {
                            binder.scrollView.visibility = View.GONE
                            binder.tvNoBanks.visibility = View.VISIBLE
                            binder.btnAddAccount.visibility = View.GONE
                            response.errorMessage?.let { binder.tvNoBanks.text = it }
                        }
                    }
                    ApiCallStatus.ERROR -> {
                        binder.progressBar.visibility = View.GONE
                        binder.tvNoBanks.visibility = View.VISIBLE
                        binder.scrollView.visibility = View.GONE
                        binder.btnAddAccount.visibility = View.GONE
                        response.errorMessage?.let { binder.tvNoBanks.text = it }
                    }
                    else -> {}
                }
            }
        }
    }

    private fun collectAddBankAccount() {
        lifecycleScope.launch {
            viewModel.addBankAccountRequestFlow.collectLatest { response ->
                when (response.status) {
                    ApiCallStatus.LOADING -> {
                        binder.progressBar.visibility = View.VISIBLE
                        binder.btnAddAccount.setButtonEnable(false)
                        binder.tvBankStatement.setButtonEnable(false)
                        binder.ivClose.setButtonEnable(false)
                    }
                    ApiCallStatus.SUCCESS -> {
                        binder.progressBar.visibility = View.GONE
                        binder.btnAddAccount.setButtonEnable(true)
                        binder.tvBankStatement.setButtonEnable(true)
                        binder.ivClose.setButtonEnable(true)
                        val responseOk = response.data?.status == 200 && response.data.success
                        if (responseOk && response.data != null) {
                            response.data.message?.let { showToast(it) }
                            finish()
                        } else if (response.data != null && !response.data.success) {
                            response.data.message?.let { showToast(it) }
                        } else {
                            response.errorMessage?.let { showToast(it) }
                        }
                    }
                    ApiCallStatus.ERROR -> {
                        binder.progressBar.visibility = View.GONE
                        binder.btnAddAccount.setButtonEnable(true)
                        binder.tvBankStatement.setButtonEnable(true)
                        binder.ivClose.setButtonEnable(true)
                        response.errorMessage?.let { showToast(it) }
                    }
                    else -> {}
                }
            }
        }
    }

    private fun validateData() {
        val documentInputStream = selectedFileUri?.let {
            contentResolver.openInputStream(it)
        }
        val bankName = binder.tvBank.text.toString().trim()
        val iban = binder.etIBAN.text.toString().trim()
        val swiftCode = binder.etSwiftCode.text.toString().trim()
        val accountType = binder.tvAccountType.text.toString()
        val fullName = binder.etFullName.text.toString()
        val pONumber = binder.etPONumber.text.toString()
        val address = binder.etAddress.text.toString()
        val address2 = binder.etAddress2.text.toString()
        val phone = binder.etPhone.text.toString().trim()

        when {
            bankName.isEmpty() -> showToast(resources.getString(R.string.activity_add_bank_account_error_select_bank_name))
            iban.isEmpty() -> showToast(resources.getString(R.string.activity_add_bank_account_error_please_enter_valid_iban))
            iban.length > 20 -> showToast(resources.getString(R.string.activity_add_bank_account_error_iban_length_validation))
            swiftCode.isEmpty() -> showToast(resources.getString(R.string.activity_add_bank_account_error_please_enter_valid_swift_code))
            accountType.isEmpty() -> showToast(resources.getString(R.string.activity_add_bank_account_error_please_select_your_account_type))
            fullName.isEmpty() -> showToast(resources.getString(R.string.activity_add_bank_account_error_please_enter_your_full_name))
            pONumber.isEmpty() -> showToast(resources.getString(R.string.activity_add_bank_account_error_please_enter_your_po_box_number))
            address.isEmpty() -> showToast(resources.getString(R.string.activity_add_bank_account_error_please_enter_your_address))
            address2.isEmpty() -> showToast(resources.getString(R.string.activity_add_bank_account_error_please_enter_your_address_2))
            !::selectedCountry.isInitialized -> showToast(resources.getString(R.string.activity_add_bank_account_error_select_country))
            !Utils.isValidPhoneLength(this,
                phone.trim(),
                selectedCountry.countryISOCode
            ) -> showToast(resources.getString(R.string.activity_add_bank_account_error_please_enter_valid_phone_number))
            // routingNumber.length < 5 || routingNumber.length > 17 -> showToast(resources.getString(R.string.activity_add_bank_account_error_please_enter_valid_routing_number))
            selectedFileUri == null || (documentInputStream == null) -> showToast(
                resources.getString(
                    R.string.kycin_selectdoc_label
                )
            )
            else -> callAddBankAccountAPI(documentInputStream)
        }
    }

    private fun callAddBankAccountAPI(documentInputStream: InputStream) {
        if (Utils.isNetworkAvailable(this)!!) {
            hideSoftKeyboard()
            viewModel.postBankAccount(
                bankName = binder.tvBank.text.toString(),
                accountNumber = binder.etIBAN.text.toString().trim(),
                swiftCode = binder.etSwiftCode.text.toString().trim(),
                fullName = binder.etFullName.text.toString(),
                poNumber = binder.etPONumber.text.toString(),
                address = binder.etAddress.text.toString(),
                address2 = binder.etAddress2.text.toString(),
                contactNumber = selectedCountry.dialCode + binder.etPhone.text.toString().trim(),
                accountTpe = binder.spinnerAccountType.selectedItem.toString()
                    .uppercase(Locale.ENGLISH),
                documentFile = documentInputStream.readBytes(),
                documentFileName = fileName!!
            )
        } else {
            Toast.makeText(this, getString(R.string.please_check_net), Toast.LENGTH_LONG).show()
        }
    }

    @SuppressLint("MissingPermission")
    val fileManagerPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            activityResultLauncher?.launch(filePickerIntent)
        } else {
            showToast("Permission needed to continue")
        }
    }

    private fun prepareRegisters() {
        if (activityResultLauncher == null) {
            activityResultLauncher =
                registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { activityResult ->
                    if (activityResult.resultCode == Activity.RESULT_OK) {
                        fileSelected(activityResult.data?.data)
                    }
                }
        }

        filePickerIntent = Intent.createChooser(Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "image/*"
            val mimeTypes = arrayOf("image/*", "application/pdf")
            putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false)
            putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
        }, resources.getString(R.string.select_file))
    }

    private fun fileSelected(file: Uri?) {
        selectedFileUri = file
        if (selectedFileUri != null) {
            selectedFileUri?.let {
                fileName = getFileName(it)
                binder.tvBankStatement.text = fileName
                binder.ivClose.visibility = View.VISIBLE
                binder.tvBankStatement.background =
                    resources.getDrawable(R.drawable.bg_rectangle_selected, null)
            }
        } else {
            fileName = null
            binder.tvBankStatement.text = resources.getString(R.string.click_here_to_upload)
            binder.ivClose.visibility = View.GONE
            binder.tvBankStatement.background =
                resources.getDrawable(R.drawable.bg_dotted_rectangle, null)
        }
    }

    private fun getFileName(uri: Uri): String {
        // Obtain a cursor with information regarding this uri
        val cursor = contentResolver.query(uri, null, null, null, null)
        return if (cursor != null) {
            if (cursor.count <= 0) {
                cursor.close()
                throw IllegalArgumentException("Can't obtain file name, cursor is empty")
            }
            cursor.moveToFirst()
            val fileName =
                cursor.getString(cursor.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME))
            cursor.close()
            fileName
        } else {
            "fileName.png"
        }
    }

    private fun observerCountryList() {
        regionSelectionViewModel.countryList.observe(this) {
            getDefaultCountry()?.let {code->
                if (!::selectedCountry.isInitialized) {
                    code.emojiString =
                        EmojiCompat.get().process(Utils.convertToEmoji(code.countryISOCode))
                            .toString() + " " + code.dialCode
                    setCountry(code)
                }
            }
        }
    }

    private fun setCountry(country: Country) {
        selectedCountry = country
        binder.tvEmoji.text =  country.dialCode//country.emojiString
    }
}