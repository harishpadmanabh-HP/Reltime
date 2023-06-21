package com.accubits.reltime.mnemonic

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.accubits.reltime.R
import com.accubits.reltime.api.ApiCallStatus
import com.accubits.reltime.databinding.ActivityConfirmRecoveryKeyBinding
import com.accubits.reltime.helpers.PreferenceManager
import com.accubits.reltime.helpers.Utils
import com.accubits.reltime.models.PublicKeyRequestModel
import com.accubits.reltime.utils.Extensions.markRequiredInRed
import com.accubits.reltime.utils.Extensions.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class ConfirmRecoveryKeyActivity : AppCompatActivity() {

    private lateinit var binding: ActivityConfirmRecoveryKeyBinding
    @Inject
    lateinit var preferenceManager: PreferenceManager

    private val viewModel by viewModels<ImportViewModel>()
    var memonics :String= ""
    var publicKey :String= ""
    var privateKey :String= ""
    val memonicsList: ArrayList<String> = ArrayList<String>()

    var btcPublicKey :String= ""
    var btcPrivateKey :String= ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityConfirmRecoveryKeyBinding.inflate(layoutInflater)
        setContentView(binding.root)
        memonics= intent.getStringExtra("mnemonics")!!
        publicKey= intent.getStringExtra("publicKey")!!
        privateKey= intent.getStringExtra("privateKey")!!

        btcPublicKey= intent.getStringExtra("btcPublicKey")!!
        btcPrivateKey= intent.getStringExtra("btcPrivateKey")!!

        binding.tvWordThree.markRequiredInRed()
        binding.tvWordSix.markRequiredInRed()
        binding.tvWordTen.markRequiredInRed()
        binding.tvWordEleven.markRequiredInRed()


        memonicsList.addAll(memonics.split(" "))

        binding.apply {
            ivBack.setOnClickListener {
                onBackPressed()
            }
            actWordThree.addTextChangedListener(object:TextWatcher{
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {

                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                   if(s?.toString().equals(memonicsList[2])==true){
                       actWordThree.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_rightg, 0);
                       tvWrongWord3.visibility=View.GONE
                   }else{
                       actWordThree.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_wrong, 0);
                      tvWrongWord3.visibility=View.VISIBLE

                   }
                    enableContinueButton()

                   }

                override fun afterTextChanged(s: Editable?) {

                }
            })

            actWordSix.addTextChangedListener(object:TextWatcher{
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {

                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if(s?.toString().equals(memonicsList[5])==true){
                        actWordSix.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_rightg, 0);
                        tvWrongWord6.visibility=View.GONE
                    }else{
                        actWordSix.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_wrong, 0);
                       tvWrongWord6.visibility=View.VISIBLE
                    }
                    enableContinueButton()
                }

                override fun afterTextChanged(s: Editable?) {

                }
            })

            actWordTen.addTextChangedListener(object:TextWatcher{
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {

                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if(s?.toString().equals(memonicsList[9])){
                        actWordTen.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_rightg, 0);
                         tvWrongWord10.visibility=View.GONE
                    }else{
                        actWordTen.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_wrong, 0);
                        tvWrongWord10.visibility=View.VISIBLE
                    }

                    enableContinueButton()
                }

                override fun afterTextChanged(s: Editable?) {

                }
            })

            actWordEleven.addTextChangedListener(object:TextWatcher{
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {

                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if(s?.toString().equals(memonicsList[10])){
                        actWordEleven.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_rightg, 0);
                        tvWrongWord11.visibility=View.GONE
                    }else{
                        actWordEleven.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_wrong, 0);
                       tvWrongWord11.visibility=View.VISIBLE
                    }
                    enableContinueButton()
                }

                override fun afterTextChanged(s: Editable?) {

                }
            })

            btnContinue.setOnClickListener {
                if (Utils.isNetworkAvailable(this@ConfirmRecoveryKeyActivity)!!) {
                    viewModel.doUpdateKey(
                        preferenceManager.getApiKey(),
                        PublicKeyRequestModel(publicKey, publicKey,btcPublicKey)
                    )
                    updateUiForMpin()
                }
            }
        }
    }
    private fun updateUiForMpin() {
        lifecycleScope.launch {

            viewModel.importSuccessModel.collectLatest { response ->
                when (response.status) {
                    ApiCallStatus.LOADING -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }
                    ApiCallStatus.SUCCESS -> {
                        binding.progressBar.visibility = View.GONE
                        val responseOk = response.data?.status == 200 && response.data.success
                        if (responseOk){
                            preferenceManager.setPublickKey(publicKey)
                            preferenceManager.setPublicKeyFromLogin(publicKey)
                            preferenceManager.setPrivateKey(privateKey)
                            preferenceManager.setMemonic(true)

                            preferenceManager.setBTCPrivateKey(btcPrivateKey)
                            preferenceManager.setBTCPublickKey(btcPublicKey)
                            //encryptText()

                            val intent = Intent(this@ConfirmRecoveryKeyActivity, WalletRestoreSuccessActivity::class.java)
                            intent.putExtra("walletCreated",true)
                           intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                            startActivity(intent)
                        }
                        else response.data?.message?.let { showToast(it) }

                    }
                    ApiCallStatus.ERROR -> {
                        binding.progressBar.visibility = View.GONE
                    }
                    else -> {}
                }
            }
        }
    }

    fun enableContinueButton(){
        binding.apply {
            (actWordThree.text.toString().equals(memonicsList[2])
                    && actWordSix.text.toString().equals(memonicsList[5])
                    && actWordTen.text.toString().equals(memonicsList[9])
                    && actWordEleven.text.toString().equals(memonicsList[10])).also { btnContinue.isEnabled = it }
        }


    }
}