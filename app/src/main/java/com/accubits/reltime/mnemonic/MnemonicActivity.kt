package com.accubits.reltime.mnemonic

import android.app.Dialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.accubits.reltime.R
import com.accubits.reltime.databinding.ActivityMnemonicBinding
import com.accubits.reltime.helpers.DeCryptor
import com.accubits.reltime.helpers.EnCryptor
import com.accubits.reltime.helpers.PreferenceManager
import com.accubits.reltime.mnemonic.adapter.MnemonicAdapter
import com.accubits.reltime.utils.cryptoutils.BitcoinUtils
import dagger.hilt.android.AndroidEntryPoint
import org.bitcoinj.crypto.*
import org.bitcoinj.crypto.MnemonicException.MnemonicLengthException
import org.bitcoinj.wallet.DeterministicSeed
import org.web3j.crypto.ECKeyPair
import org.web3j.crypto.Wallet
import org.web3j.crypto.WalletFile
import org.web3j.utils.Numeric
import java.security.SecureRandom
import javax.inject.Inject


@AndroidEntryPoint
class MnemonicActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMnemonicBinding

    @Inject
    lateinit var bitcoinUtils: BitcoinUtils

    @Inject
    lateinit var preferenceManager: PreferenceManager

    @Inject
    lateinit var encryptor: EnCryptor

    @Inject
    lateinit var decryptor: DeCryptor

    private lateinit var privateKey: String
    private lateinit var publicKey: String
    private lateinit var btcPrivateKey: String
    private lateinit var btcPublicKey: String
    private var isCopied = false
    var words: MutableList<String> = mutableListOf()
    private val mnemonicAdapter: MnemonicAdapter by lazy { MnemonicAdapter() }
    var data = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMnemonicBinding.inflate(layoutInflater)
        setContentView(binding.root)
        onCreateMnemonicWallet()
        binding.ivBack.setOnClickListener {
            onBackPressed()
        }
        binding.blurView.setOnClickListener {
            showDocumentBottomSheet()
        }
        binding.tvCopy.setOnClickListener {


            val clipboard: ClipboardManager =
                getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("label", data)
            clipboard.setPrimaryClip(clip)
            isCopied = true
            Toast.makeText(
                this,
                resources.getString(R.string.code_copied_to_clipboard),
                Toast.LENGTH_LONG
            ).show()
        }
        binding.btnContinue.setOnClickListener {

            val memonicsArrayList = ArrayList<String>()
            memonicsArrayList.addAll(words)
            var intent = Intent(this, ConfirmRecoveryKeyActivity::class.java)
            intent.putExtra("mnemonics", data)
            intent.putExtra("privateKey", privateKey)
            intent.putExtra("publicKey", publicKey)
            intent.putExtra("btcPublicKey", btcPublicKey)
            intent.putExtra("btcPrivateKey", btcPrivateKey)
            startActivity(intent)
        }
    }


    private fun createWallet(words: List<String>): WalletFile? {
        val seeds = MnemonicCode.toSeed(words, "")
        val masterPrivateKey1 = HDKeyDerivation.createMasterPrivateKey(seeds)
        val bip44Path = "M/44H/60H/0H/0"
        val keyPath = HDUtils.parsePath(bip44Path)
        val hierarchy = DeterministicHierarchy(masterPrivateKey1)
        val walletKey = hierarchy.deriveChild(keyPath, true, true, ChildNumber.ZERO)
        val masterPrivateKey = HDKeyDerivation.createMasterPrivateKey(seeds)
        val deterministicHierarchy = DeterministicHierarchy(masterPrivateKey)

        walletKey.pubKey
        Log.d("pub", walletKey.pubKey.toString())
        walletKey.privKeyBytes
        walletKey.privKeyBytes33
        val ecKeyPair = ECKeyPair.create(walletKey.privKey)
        val data = Numeric.toHexStringNoPrefix(walletKey.privKey)

        Log.d("data1!!", data)
        privateKey = data
        return Wallet.createLight("", ecKeyPair)
    }

    fun onCreateMnemonicWallet() {
        try {
            words = createMnemonics()

            data = words.joinToString(separator = " ")
            val createWallet = createWallet(words)
            publicKey = "0x" + createWallet!!.address

            val bitcoinWallet = bitcoinUtils.createBitcoinWallet(data)
            btcPublicKey = bitcoinWallet.currentReceiveAddress().toString()
            btcPrivateKey = bitcoinWallet.currentReceiveKey().privateKeyAsHex

            mnemonicAdapter.setData(words)
            binding.rvMnemonics.apply {
                adapter = mnemonicAdapter
                layoutManager = GridLayoutManager(this@MnemonicActivity, MnemonicAdapter.SPAN_COUNT)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @Throws(MnemonicLengthException::class)
    private fun createMnemonics(): MutableList<String> {
        val entropy = ByteArray(DeterministicSeed.DEFAULT_SEED_ENTROPY_BITS / 8)
        SecureRandom().nextBytes(entropy)
        return MnemonicCode.INSTANCE.toMnemonic(entropy)
    }

    fun showDocumentBottomSheet() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.memonic_bottom_sheet)
        val btn = dialog.findViewById<Button>(R.id.btnContinue)
        btn.setOnClickListener {
            mnemonicAdapter.enableBlurView(true)
            binding.blurView.visibility = View.GONE
            binding.tvCopy.visibility = View.VISIBLE
            binding.tvWarningTitle.visibility = View.VISIBLE
            binding.tvWarningDesc.visibility = View.VISIBLE
            binding.btnContinue.visibility = View.VISIBLE
            dialog.dismiss()
        }

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
}