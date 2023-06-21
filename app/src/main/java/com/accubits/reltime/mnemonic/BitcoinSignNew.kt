package com.accubits.reltime.mnemonic

import android.util.Log
import androidx.annotation.NonNull
import com.accubits.reltime.models.Inputs
import com.google.gson.Gson
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import org.bitcoinj.core.*
import org.bitcoinj.core.Utils.HEX
import org.bitcoinj.crypto.TransactionSignature
import org.bitcoinj.params.TestNet3Params
import org.bitcoinj.script.ScriptBuilder
import org.bitcoinj.script.ScriptError
import org.bitcoinj.script.ScriptException
import org.bitcoinj.script.ScriptPattern
import org.web3j.utils.Numeric.hexStringToByteArray
import java.util.*


class BitcoinSignNew() {
    val param: NetworkParameters = TestNet3Params.get()
    val privKeyHex =
"94eae01e10bdd0e4fb8ac01256df14d1d1aa0b95a916052534fad0a448971904"
     val tx :Transaction =Transaction(param)

    init {
        Context(param)
        buildTransaction()
    }




    private fun buildTransaction() {
        val unspents= arrayListOf(
           /* Unspents(
                txid="765ba709ecd05d5736c0351b6eb103bebad93070fd1454c27f365a839f719a2d",
                output_no = 1,
                script_asm= "OP_DUP OP_HASH160 bea9272e2abd46c98e8b61b8eb5c0e31641cd283 OP_EQUALVERIFY OP_CHECKSIG",
                script_hex="76a914bea9272e2abd46c98e8b61b8eb5c0e31641cd28388ac",
                value="0.01411551",
                confirmations=0,
                time = 1664540315
            )*/
            Unspents(
                txid="026282accfdfda38418a253282060b934594b5815d5fe667e4b09b307cb5bc8b",
                output_no = 1,
                script_asm= "OP_DUP OP_HASH160 bea9272e2abd46c98e8b61b8eb5c0e31641cd283 OP_EQUALVERIFY OP_CHECKSIG",
                script_hex="76a914bea9272e2abd46c98e8b61b8eb5c0e31641cd28388ac",
                value="0.01410428",
                confirmations=1,
                time = 1664540530
            )
        )

        val sourceAddress = Address.fromString(
            param,"mxu5KTjBomgh83uWhNL8WwcmwQnXay6rso"
        )
        val targetAddress = Address.fromString(
            param,"mk3TfdyuGZwzfq6QBLwjmfUxhoaBufzex3"//"mxFEkamjV2oamDPJUH67F4h7DDKNEqBQUj"
        )
        val privKeyBytes = HEX.decode(privKeyHex)
        val ecKey = ECKey.fromPrivate(privKeyBytes)

val sendAmount=0.00000123

        tx.addOutput(Coin.valueOf(getSatoshis(sendAmount)), targetAddress)
        addInputsToTransaction(sourceAddress,  unspents, getSatoshis(sendAmount))
        signInputsOfTransaction(sourceAddress, ecKey)

        tx.verify()
        tx.confidence.source = TransactionConfidence.Source.SELF
        tx.purpose = Transaction.Purpose.USER_PAYMENT
        // val valueToSend = byteArrayToHexString(tx.bitcoinSerialize())
        val rawTransaction = HEX.encode(tx.bitcoinSerialize())


        Log.e("TAG", rawTransaction)

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
    private fun addInputsToTransaction(sourceAddress:Address,unspents:List<Unspents>, amount:Long){
     val TX_FEE=1000
        var gatheredAmount = 0L
        val requiredAmount = amount + TX_FEE
        unspents.forEach {
            gatheredAmount += getSatoshis(it.value.toDouble())
            val outPoint = TransactionOutPoint(param, it.output_no, Sha256Hash.wrap(it.txid))
            val transactionInput = TransactionInput(param, tx, hexStringToByteArray(it.script_hex),
                outPoint, Coin.valueOf(getSatoshis(it.value.toDouble())))
            tx.addInput(transactionInput)

            if (gatheredAmount >= requiredAmount) {
                return@forEach
            }
        }
        if (gatheredAmount > requiredAmount) {
            //return change to sender, in real life it should use different address
            tx.addOutput(Coin.valueOf((gatheredAmount - requiredAmount)), sourceAddress)
        }

    }
}

private fun getSatoshis(input: Double):Long{
    val a=input*Math.pow(10.0, 8.0)
    Log.e("TAG",""+a)
   return a.toLong()
}



fun getInputs():List<Unspents>{
  val txs=  Gson().fromJson("{\n" +
          "    \"network\": \"BTCTEST\",\n" +
          "    \"address\": \"n2H4gC3jbUVpNeLuVfjVLa9RzQCRn7x6b3\",\n" +
          "    \"txs\": [\n" +
          "      {\n" +
          "        \"txid\": \"f883a6ffa10b3f87268d35bd19f20ab04e6c6ca647bcc07d199e5d527da920d4\",\n" +
          "        \"output_no\": 1,\n" +
          "        \"script_asm\": \"OP_DUP OP_HASH160 e3ba25288bc3d54598adae4b41236c3b731824f0 OP_EQUALVERIFY OP_CHECKSIG\",\n" +
          "        \"script_hex\": \"76a914e3ba25288bc3d54598adae4b41236c3b731824f088ac\",\n" +
          "        \"value\": \"0.03360623\",\n" +
          "        \"confirmations\": 1,\n" +
          "        \"time\": 1666379634\n" +
          "      }\n" +
          "    ]\n" +
          "  }", Txs::class.java)
return txs.txs
}

data class Txs(
    @Expose
    @SerializedName("txs")
    val txs: List<Unspents>
)















/*

val str =
        "01000000028bcf82619e93eb92cc78d5bb0b7b22d6ee419820428402e721fbac1386c9673f0000000000ffffffffd50ea1cd97af5295cddcd4f0c7c0d531fed5683bde80af45ede25a0571c31add0100000000ffffffff02087f0300000000001976a9146fb643fe63bcc6c352899298d6072cc5d4178c0f88acfa6b0100000000001976a914196e59ee4c151e742af69bca308fc1b4445c577288ac00000000"


  private fun sign() {
        val x = HEX.decode(str)

        val transaction = Transaction(param, x)



        for (i in transaction.inputs.indices) {
            val transactionInput = transaction.getInput(i.toLong())
            val privKeyBytes = HEX.decode(privKeyHex)
            val ecKey = ECKey.fromPrivate(privKeyBytes)
            val a =
                arrayOf("mwuDs2ekfAVTgzAKHjSpJmnwCysmBQyzZ1", "mwuDs2ekfAVTgzAKHjSpJmnwCysmBQyzZ1")
            val scriptPubKey = ScriptBuilder.createOutputScript(
                Address.fromString(
                    param,
                    a[i]// mUTXOs.get(i).getAddress()
                )
            )
            val hash = transaction.hashForSignature(i, scriptPubKey, Transaction.SigHash.ALL, true)
            val ecSig = ecKey.sign(hash)
            val txSig = TransactionSignature(ecSig, Transaction.SigHash.ALL, true)
            if (scriptPubKey.isSentToRawPubKey) {
                transactionInput.scriptSig = ScriptBuilder.createInputScript(txSig)
            } else {
                if (!scriptPubKey.isSentToAddress) {
                    // throw ScriptException("Don\'t know how to sign for this kind of scriptPubKey: $scriptPubKey")
                }
                transactionInput.scriptSig = ScriptBuilder.createInputScript(txSig, ecKey)
            }
        }

//serialization and broadcasting
        val bytesRawTransaction: ByteArray = transaction.bitcoinSerialize()
        val rawTransaction = HEX.encode(bytesRawTransaction)
        // broadcastTx(rawTransaction)
        Log.e("TAG", rawTransaction)
    }*/