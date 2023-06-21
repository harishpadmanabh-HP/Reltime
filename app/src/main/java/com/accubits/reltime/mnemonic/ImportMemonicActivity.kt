package com.accubits.reltime.mnemonic

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.accubits.reltime.R
import com.accubits.reltime.api.ApiCallStatus
import com.accubits.reltime.databinding.ActivityImportMemonicBinding
import com.accubits.reltime.helpers.EnCryptor
import com.accubits.reltime.helpers.PreferenceManager
import com.accubits.reltime.helpers.Utils
import com.accubits.reltime.models.PublicKeyRequestModel
import com.accubits.reltime.utils.Extensions.openActivity
import com.accubits.reltime.utils.Extensions.showToast
import com.accubits.reltime.utils.cryptoutils.BitcoinUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.bitcoinj.crypto.*
import org.web3j.crypto.ECKeyPair
import org.web3j.crypto.Wallet
import org.web3j.crypto.WalletFile
import org.web3j.utils.Numeric
import javax.inject.Inject


@AndroidEntryPoint
class ImportMemonicActivity : AppCompatActivity() {
    lateinit var activitymemonicBing: ActivityImportMemonicBinding

    @Inject
    lateinit var bitcoinUtils: BitcoinUtils
    @Inject
    lateinit var preferenceManager: PreferenceManager
    private lateinit var privateKey: String
    private lateinit var publicKey: String

    @Inject
    lateinit var encryptor: EnCryptor
    private val viewModel by viewModels<ImportViewModel>()

    var btcPublicKey :String= ""
    var btcPrivateKey :String= ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activitymemonicBing = ActivityImportMemonicBinding.inflate(layoutInflater)
        setContentView(activitymemonicBing.root)
        activitymemonicBing.btMnemonic.setOnClickListener {
            if (activitymemonicBing.edSearch.text.toString().trim() != "") {
                onImportMnemonics()
            } else {
                Toast.makeText(this, "Enter the memonic", Toast.LENGTH_LONG).show()
            }
        }

        activitymemonicBing.ivBack.setOnClickListener {
            onBackPressed()
        }

/*        activitymemonicBing.edSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
//                TODO("Not yet implemented")
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (p3 == 0) {
                    activitymemonicBing.btMnemonic.visibility = View.GONE
                } else {
                    activitymemonicBing.btMnemonic.visibility = View.VISIBLE
                }
            }

            override fun afterTextChanged(p0: Editable?) {
//                TODO("Not yet implemented")
            }
        })*/
        observeResponse()
    }

    fun onImportMnemonics() {
        val words: String = activitymemonicBing.edSearch.text.toString().trim { it <= ' ' }
        val split = words.split(" ").toTypedArray()
        //val split = words.split(",").toTypedArray()
        val mnemonics = ArrayList<String>()
        for (s in split) {
            mnemonics.add(s.trim { it <= ' ' })
        }
        val wallet = createWallet(mnemonics)

        val bitcoinWallet = bitcoinUtils.createBitcoinWallet(mnemonics.joinToString(separator = " "))

        if (preferenceManager.getPublicKeyFromLogin() == "0x" + wallet!!.address) {
            publicKey = "0x" + wallet.address
            btcPublicKey = bitcoinWallet.currentReceiveAddress().toString()
            btcPrivateKey = bitcoinWallet.currentReceiveKey().privateKeyAsHex
            activitymemonicBing.tvWrongWord3.visibility = View.GONE
            activitymemonicBing.constraintLayout24.setBackgroundResource(R.drawable.rectangle_without_line)

            if (Utils.isNetworkAvailable(this@ImportMemonicActivity)!!) {
                viewModel.doRestorePublicKey(
                    preferenceManager.getApiKey(),
                    PublicKeyRequestModel(publicKey, publicKey,btcPublicKey)
                )
            }
        } else {
            activitymemonicBing.tvWrongWord3.visibility = View.VISIBLE
            activitymemonicBing.constraintLayout24.setBackgroundResource(R.drawable.error_bg)
        }
    }

    private fun createWallet(words: List<String>): WalletFile? {
        val seeds = MnemonicCode.toSeed(words, "")
        val masterPrivateKey1 = HDKeyDerivation.createMasterPrivateKey(seeds)
        val bip44Path = "M/44H/60H/0H/0"
        val keyPath = HDUtils.parsePath(bip44Path)
        val hierarchy = DeterministicHierarchy(masterPrivateKey1)
        val walletKey = hierarchy.deriveChild(keyPath, true, true, ChildNumber.ZERO)

        val ecKeyPair = ECKeyPair.create(walletKey.privKey)
        val data = Numeric.toHexStringNoPrefix(walletKey.privKey)
        privateKey = data
        return Wallet.createLight("", ecKeyPair)
    }

    private fun observeResponse() {
        lifecycleScope.launch {

            viewModel.importSuccessModel.collectLatest { response ->
                when (response.status) {
                    ApiCallStatus.LOADING -> {
                        activitymemonicBing.progressBar.visibility = View.VISIBLE
                    }
                    ApiCallStatus.SUCCESS -> {
                        activitymemonicBing.progressBar.visibility = View.GONE
                        val responseOk = response.data?.status == 200 && response.data.success
                        if (responseOk){
                            preferenceManager.setMpin(true)
                            preferenceManager.setMemonic(true)
                            preferenceManager.setPrivateKey(privateKey)
                            preferenceManager.setPublickKey(publicKey)

                            preferenceManager.setBTCPublickKey(btcPublicKey)
                            preferenceManager.setBTCPrivateKey(btcPrivateKey)

                            openActivity(WalletRestoreSuccessActivity::class.java,shouldFinish = true)
                        }
                        else response.data?.message?.let { showToast(it) }

                    }
                    ApiCallStatus.ERROR -> {
                        activitymemonicBing.progressBar.visibility = View.GONE
                        showToast(response.errorMessage)
                    }
                    else -> {}
                }
            }
        }
    }

}