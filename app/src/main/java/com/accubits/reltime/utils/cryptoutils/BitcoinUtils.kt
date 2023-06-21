package com.accubits.reltime.utils.cryptoutils

import com.accubits.reltime.BuildConfig
import com.accubits.reltime.helpers.PreferenceManager
import com.accubits.reltime.models.Data
import com.accubits.reltime.models.Inputs
import com.accubits.reltime.models.Outputs
import org.bitcoinj.core.*
import org.bitcoinj.crypto.ChildNumber
import org.bitcoinj.crypto.TransactionSignature
import org.bitcoinj.params.MainNetParams
import org.bitcoinj.params.TestNet3Params
import org.bitcoinj.script.*
import org.bitcoinj.wallet.DeterministicSeed
import org.bitcoinj.wallet.UnreadableWalletException
import org.bitcoinj.wallet.Wallet
import org.web3j.utils.Numeric
import java.util.*
import kotlin.math.pow

class BitcoinUtils(val preferenceManager: PreferenceManager) {
    private val param: NetworkParameters by lazy {
        if (isMainNet())
            MainNetParams.get()
        else TestNet3Params.get()
    }

    private val accountPath by lazy {
        arrayListOf(
            ChildNumber(44, true), ChildNumber(if (isMainNet()) 0 else 1, true),
            ChildNumber(0, true)
        )
    }

    private var tx = Transaction(param)

    /**
    TESTNET: m/44'/1'/0'/0/0
    MAINNET: m/44'/0'/0'/0/0
     */
    private fun isMainNet() = BuildConfig.MAIN_NET

    init {
        Context(param)
    }

    fun createBitcoinWallet(mnemonics: String): Wallet {
        var seed: DeterministicSeed? = null
        try {
            seed = DeterministicSeed(mnemonics, null, "", Date().time)
        } catch (e: UnreadableWalletException) {
            e.printStackTrace()
        }

        return Wallet.fromSeed(param, seed, Script.ScriptType.P2PKH, accountPath)
    }

    fun signBtcTransaction(data: Data): String {
        tx = Transaction(param)

        val sourceAddress = Address.fromString(
            param, preferenceManager.getBTCPublicKey()
        )
        val privKeyBytes = Utils.HEX.decode(preferenceManager.getBTCPrivateKey())
        val ecKey = ECKey.fromPrivate(privKeyBytes)

        addInputsOutputsToTransaction(
            data.txnBuild.inputs,
            data.txnBuild.outputs
        )
        signInputsOfTransaction(sourceAddress, ecKey)

        tx.verify()
        tx.confidence.source = TransactionConfidence.Source.SELF
        tx.purpose = Transaction.Purpose.USER_PAYMENT
        return Utils.HEX.encode(tx.bitcoinSerialize())
    }

    private fun addInputsOutputsToTransaction(
        inputs: List<Inputs>?,
        outputs: List<Outputs>?
    ) {
        inputs?.forEach {
            val outPoint = TransactionOutPoint(param, it.tx_output_n, Sha256Hash.wrap(it.tx_hash))
            val transactionInput = TransactionInput(
                param,
                tx,
                Numeric.hexStringToByteArray(it.script),
                outPoint,
                Coin.valueOf(getSatoshis(it.value.toDouble()))
            )
            tx.addInput(transactionInput)
        }
        outputs?.forEach {
            tx.addOutput(
                Coin.valueOf(it.amount), Address.fromString(
                    param, it.address
                )
            )
        }
    }


    private fun signInputsOfTransaction(
        sourceAddress: Address,
        key: ECKey
    ) {
        for (i in 0 until tx.inputs.size) {
            val scriptPubKey = ScriptBuilder.createOutputScript(sourceAddress)
            val hash = tx.hashForSignature(i, scriptPubKey, Transaction.SigHash.ALL, true)
            val ecdsaSignature = key.sign(hash)
            val txSignature = TransactionSignature(ecdsaSignature, Transaction.SigHash.ALL, true)
            if (ScriptPattern.isP2PK(scriptPubKey)) {
                tx.getInput(i.toLong()).scriptSig = ScriptBuilder.createInputScript(txSignature)
            } else {
                if (!ScriptPattern.isP2PKH(scriptPubKey)) {
                    throw ScriptException(
                        ScriptError.SCRIPT_ERR_UNKNOWN_ERROR,
                        "Unable to sign this scrptPubKey: $scriptPubKey"
                    )
                }
                tx.getInput(i.toLong()).scriptSig =
                    ScriptBuilder.createInputScript(txSignature, key)
            }
        }
    }


/*
    fun signBtcTransaction(data: Data) {
        tx = Transaction(param)
        val unspents = data.txnBuild.inputs //getInputs()

        val sourceAddress = Address.fromString(
            param, preferenceManager.getBTCPublicKey()
        )
        val targetAddress = Address.fromString(
            param, "mk3TfdyuGZwzfq6QBLwjmfUxhoaBufzex3"
        )
        val privKeyBytes = Utils.HEX.decode(preferenceManager.getBTCPrivateKey())
        val ecKey = ECKey.fromPrivate(privKeyBytes)

        val sendAmount = 0.00000111

        tx.addOutput(
            Coin.valueOf(getSatoshis(sendAmount)),
            targetAddress
        )
        addInputsToTransaction(
            sourceAddress, unspents,
            getSatoshis(sendAmount)
        )
        signInputsOfTransaction(sourceAddress, ecKey)

        tx.verify()
        tx.confidence.source = TransactionConfidence.Source.SELF
        tx.purpose = Transaction.Purpose.USER_PAYMENT
        val rawTransaction = Utils.HEX.encode(tx.bitcoinSerialize())
        Log.e("TAG", rawTransaction)
    }
    private fun addInputsToTransaction(
        sourceAddress: Address,
        unspents: List<Inputs>?,
        amount: Long
    ) {
        val TX_FEE = 1000
        var gatheredAmount = 0L
        val requiredAmount = amount + TX_FEE
        unspents?.forEach {
            gatheredAmount += getSatoshis(it.value.toDouble())
            val outPoint = TransactionOutPoint(param, it.tx_output_n, Sha256Hash.wrap(it.tx_hash))
            val transactionInput = TransactionInput(
                param, tx, null,//Numeric.hexStringToByteArray(it.script_hex),
                outPoint, Coin.valueOf(getSatoshis(it.value.toDouble()))
            )
            tx.addInput(transactionInput)

            if (gatheredAmount >= requiredAmount) {
                return@forEach
            }
        }
        if (gatheredAmount > requiredAmount) {
            tx.addOutput(Coin.valueOf((gatheredAmount - requiredAmount)), sourceAddress)
        }
    }*/


    private fun getSatoshis(input: Double): Long {
        val a = input * 10.0.pow(8.0)
        return a.toLong()
    }

}