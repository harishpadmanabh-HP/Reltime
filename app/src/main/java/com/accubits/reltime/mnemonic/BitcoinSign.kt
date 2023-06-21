package com.accubits.reltime.mnemonic

import android.util.Log
import androidx.annotation.NonNull
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


class BitcoinSign() {
    val param: NetworkParameters = TestNet3Params.get()
    val privKeyHex =
"a4a583f9d378e2924aa013bae27eb1bad758dbfc4113f72c88f3fc7dd8d6889c"
        //"4f749fa7bff8c9dc8d3398293c9b3d5df66d5ef94c758fc7cdf694ae48faa9a0"
        //"8122b1c845037f01b5f45466579478d4470b2164617033ee2a6fea0005ec938d"
        //"dc7437f44d807a9c958e9b3db6b4c447ef4f0cf2508cbd41d5471331e7740b75"
     val tx :Transaction =Transaction(param)

    init {
        Context(param)
        buildTransaction()
    }




    private fun buildTransaction() {
        val unspents= arrayListOf(
       /*     Unspents(
            txid="80f0b91d7d96a23125882bafd26bb8f54511d9c334a0c88f3c59d329aa5db92f",
            output_no = 1,
            script_asm= "OP_DUP OP_HASH160 552c087610ee64199552f40c5c45867290c9f12a OP_EQUALVERIFY OP_CHECKSIG",
            script_hex="76a914552c087610ee64199552f40c5c45867290c9f12a88ac",
            value="0.00068000",
            confirmations=2,
            time = 1663747884
        ),
                Unspents(
                txid="ea9558d2eb307aeef9c5b227f5a34d4506d8eb7a42eb314b368e05769f6b49f8",
            output_no = 0,
            script_asm= "OP_DUP OP_HASH160 552c087610ee64199552f40c5c45867290c9f12a OP_EQUALVERIFY OP_CHECKSIG",
            script_hex="76a914552c087610ee64199552f40c5c45867290c9f12a88ac",
            value="0.00093910",
            confirmations=2,
            time = 1663747884
        )*/
          /*  Unspents(
                txid="9db42da3e24a42eb150ce30cbe0548938b1ad90b6439557d04f4ee5a044e991b",
                output_no = 1,
                script_asm= "OP_DUP OP_HASH160 bfc8309abdc17846dcf966848bcfcf8a2e8bee44 OP_EQUALVERIFY OP_CHECKSIG",
                script_hex="76a914bfc8309abdc17846dcf966848bcfcf8a2e8bee4488ac",
                value="0.01245619",
                confirmations=1,
                time = 1663765860
            )*/
          /*  Unspents(
                txid="f20655fc4f9ec580e9c7f1cfaef2bee36ac8e747ed8797e629a4be4e11f004e2",
                output_no = 0,
                script_asm= "OP_DUP OP_HASH160 205046f0e759f631a91019348b2fa9e30e0e44d0 OP_EQUALVERIFY OP_CHECKSIG",
                script_hex="76a914205046f0e759f631a91019348b2fa9e30e0e44d088ac",
                value="0.00010000",
                confirmations=1,
                time = 1663765860
            )*/
            Unspents(
                txid="6143c9b8ea9ad3b9712032704817adaaf9a4b43ed3395683257bbb0713adff12",
                output_no = 0,
                script_asm= "OP_DUP OP_HASH160 154ad668968804782b4002c60519a6ddd536abc1 OP_EQUALVERIFY OP_CHECKSIG",
                script_hex="76a914154ad668968804782b4002c60519a6ddd536abc188ac",
                value="0.00010000",
                confirmations=399,
                time = 1663859512
            ),
            Unspents(
                txid="6b18b7b5b74945019bdac3032815773fd9e48946dc16d74fe52bfe6829e2ff0c",
                output_no = 1,
                script_asm= "OP_DUP OP_HASH160 154ad668968804782b4002c60519a6ddd536abc1 OP_EQUALVERIFY OP_CHECKSIG",
                script_hex="76a914154ad668968804782b4002c60519a6ddd536abc188ac",
                value="0.01804686",
                confirmations=0,
                time = 1664048687
            )
        )


        val sourceAddress = Address.fromString(
            param,"mhTY8tJsH7hfn9gg1FkcBsg89oFdaSBcsN"//"miTp5wibvC7PWF32hqppoKkqs5G4uxNuqD"//"my11B8SKfn46a9YbjYY8B3j8WNZz8nEF3G"//"moHJUq5F7QG5triQai3xdKjrxzivwd1puf"
        )
        val targetAddress = Address.fromString(
            param,"mwuDs2ekfAVTgzAKHjSpJmnwCysmBQyzZ1"
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

data class Unspents(val txid:String,val output_no:Long,val script_asm:String,val script_hex:String,val value:String,val confirmations:Int,
val time:Long)


















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