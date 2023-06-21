package com.accubits.reltime.mnemonic

import android.util.Base64
import android.util.Log
import org.bitcoinj.core.*
import org.bitcoinj.core.Utils.HEX
import org.bitcoinj.crypto.*
import org.bitcoinj.params.TestNet3Params
import org.bitcoinj.script.Script
import org.bitcoinj.wallet.*
import org.web3j.utils.Numeric
import java.lang.String.join
import java.security.SecureRandom
import java.util.*


class BitcoinJTesting {
    val param: NetworkParameters = TestNet3Params.get()

    init {
        main()
        // mediumExample()
        // sign2()
    }


    fun main() {
       /* val mnemonic =
            "sister person pond inmate maximum butter govern mean race gesture topple depart"*/

    //    val mnemonic =  "chat ghost math love horn scale cousin because impose dress expand chase"
        val mnemonic =     "ripple scissors kick mammal hire column oak again sun offer wealth tomorrow wagon turn fatal"


        var seed: DeterministicSeed? = null
        try {
            seed = DeterministicSeed(mnemonic, null, "", Date().time)
        } catch (e: UnreadableWalletException) {
            e.printStackTrace()
        }

        //  val accountPath = BIP44Util.generatePath("m/44'/0'/0'/0/0")
        val accountPath = arrayListOf(
            ChildNumber(44, false), ChildNumber(1, false),
            ChildNumber(0, false)
        )
        val wallet = Wallet.fromSeed(param, seed, Script.ScriptType.P2PKH, accountPath)
        println(wallet)

        // derive 100 addresses along with private key
        val watchingKey: DeterministicKey = wallet.watchingKey
        Log.e("TAG", "PUBLIC KEY  " + wallet.currentReceiveAddress())

        /*  Log.e("TAG", "PRIVATE KEY  " + watchingKey.privateKeyAsHex)
          Log.e("TAG", "PUBLIC KEY  " + wallet.currentReceiveAddress())
          Log.e("TAG", "WiF  " + watchingKey.getPrivateKeyAsWiF(param))
          Log.e("TAG", "key  " + watchingKey.publicKeyAsHex)
          Log.e("TAG", "key  " + watchingKey.serializePubB58(param))
          Log.e("TAG", "key  " + wallet.currentReceiveKey())
          Log.e("TAG", "key serializePubB58  " + wallet.currentReceiveKey().serializePubB58(param))
          Log.e("TAG", "key serializePrivB58  " + wallet.currentReceiveKey().serializePrivB58(param))
  */

        // log(watchingKey)
        log(wallet.currentReceiveKey())




    }

    private fun log(watchingKey: DeterministicKey) {
        Log.e("TAG", "INT KEY pubKey " + Numeric.toHexStringNoPrefix(watchingKey.pubKey))
        Log.e("TAG", "INT KEY privKey " + Numeric.toHexStringNoPrefix(watchingKey.privKey))

        Log.e("TAG", "privateKeyAsHex  " + watchingKey.privateKeyAsHex)
        Log.e("TAG", "publicKeyAsHex  " + watchingKey.publicKeyAsHex)
        Log.e("TAG", "WiF  " + watchingKey.getPrivateKeyAsWiF(param))
        Log.e("TAG", "key  " + watchingKey.serializePubB58(param))
        Log.e("TAG", "key  " + watchingKey.serializePrivB58(param))

        Log.e("TAG", Address.fromKey(param,watchingKey,Script.ScriptType.P2PKH).toString())
        Log.e("TAG", "--\n  ")

    }



























































    /*   private fun sign(){
           val transaction = Transaction(param)
           val scriptBytes = byteArrayOf()
           val vinTxId: Sha256Hash = Sha256Hash.wrap(txId)
           val index=0L//
           val outpoint = TransactionOutPoint(param, index, vinTxId)
           val value: Coin? = null
           val input = TransactionInput(param, transaction, scriptBytes, outpoint, value)
           transaction.addInput(input)


           val outPoint = TransactionOutPoint(param, utxo.getIndex(), utxo.getHash())
           val privKeyBytes = HEX.decode("privKeyAsHex")
           val ecKey = ECKey.fromPrivate(privKeyBytes)

           transaction.addSignedInput(outPoint, utxo.getScript(), ecKey, Transaction.SigHash.ALL, true)

       }*/

    private fun sign2() {
        // message (hash) to be signed with private key
        val msg = "9734f9ce9770692eae78794112df63b9aa9bb402da30fa302b28263c43115ff0"

        // an example of WiF for private key (taken from 'Mastering Bitcoin')
        //  val wif = "KxFC1jmwwCoACiCAWZ3eXa96mBM6tb3TYzGmf6YwgdGWZgawvrtJ"

        // creating a key object from WiF
        /*  val dpk = DumpedPrivateKey.fromBase58(null, wif)
          val key = dpk.key*/

        val privKeyBytes =
            HEX.decode("dc7437f44d807a9c958e9b3db6b4c447ef4f0cf2508cbd41d5471331e7740b75")
        val key = ECKey.fromPrivate(privKeyBytes)

        // checking our key object
        // val main: NetworkParameters = MainNetParams.get()
        //   val check = key.getPrivateKeyAsWiF(param)
        //   println(wif == check) // true

        // creating Sha object from string
        val hash = Sha256Hash.wrap(msg)

        // creating signature
        val sig: ECKey.ECDSASignature = key.sign(hash)

        // encoding
        val res: ByteArray = sig.encodeToDER()

        // converting to hex
        //  val hex: String = DatatypeConverter.printHexBinary(res)
        val hex = Base64.encodeToString(res, 16)

        Log.e("Log", "sigendTransiction        \n" + hex)

        //   Log.e("Log", "decrypttx      " + Hex.decode(sig.encodeToDER()))
        println(hex) // 304502210081B528...

    }


    /*
    *
    *

    fun hexStringToByteArray(s: String): ByteArray {
        val len = s.length
        val data = ByteArray(len / 2)
        var i = 0
        while (i < len) {
            data[i / 2] = ((s[i].digitToIntOrNull(16) ?: -1 shl 4) + s[i + 1].digitToIntOrNull(16)!!
                ?: -1).toByte()
            i += 2
        }
        return data
    }
    private fun sign4(){
        val b = msg.toByteArray()//getBytes(StandardCharsets.UTF_8)
        val transaction=Transaction(param,hexStringToByteArray(msg))
        Log.e("Log",""+Transaction(param).inputs.size+"")
        Log.e("Log",""+transaction.inputs.size+"")
        for (i in 0 until transaction.inputs.size) {
            val transactionInput: TransactionInput = transaction.getInput(i.toLong())
            val privKeyBytes = HEX.decode(privateKey)
            val ecKey = ECKey.fromPrivate(privKeyBytes)
            /* val scriptPubKey = ScriptBuilder.createOutputScript(
                 LegacyAddress.fromBase58(
                     param,
                     mUTXOs.get(i).getAddress()
                 )
             )
             val hash: Sha256Hash =
                 transaction.hashForSignature(i, scriptPubKey, Transaction.SigHash.ALL, true)
             val ecSig = ecKey.sign(hash)
             val txSig = TransactionSignature(ecSig, Transaction.SigHash.ALL, true)
             if (scriptPubKey.isSentToRawPubKey) {
                 transactionInput.scriptSig = ScriptBuilder.createInputScript(txSig)
             } else {
                 if (!scriptPubKey.isSentToAddress) {
                   //  throw ScriptException("Don\'t know how to sign for this kind of scriptPubKey: $scriptPubKey")
                 }
                 transactionInput.scriptSig = ScriptBuilder.createInputScript(txSig, ecKey)
             }*/
        }
    }


   private fun sign3(){
           val transaction=Transaction(param)
           for (i in 0 until transaction.inputs.size) {
               val transactionInput: TransactionInput = transaction.getInput(i.toLong())
               val addressFromUtxo: String = mUTXOs.get(i).getAddress()
               val privKeyBytes: ByteArray = getPrivKeyBitesForAddress(addressFromUtxo)
               val ecKey = ECKey.fromPrivate(privKeyBytes)
               val scriptPubKey = ScriptBuilder.createOutputScript(
                   LegacyAddress.fromBase58(
                       param,
                       mUTXOs.get(i).getAddress()
                   )
               /*Address.fromBase58(
                       param,
                       mUTXOs.get(i).getAddress()
                   )*/
               )
               val hash: Sha256Hash =
                   transaction.hashForSignature(i, scriptPubKey, Transaction.SigHash.ALL, false)
               val ecSig = ecKey.sign(hash)
               val txSig = TransactionSignature(ecSig, Transaction.SigHash.ALL, false)
               transactionInput.scriptSig = ScriptBuilder.createInputScript(txSig, ecKey)
           }

   //serialization and broadcasting
           val bytesRawTransaction: ByteArray = transaction.bitcoinSerialize()
           val rawTransaction = HEX.encode(bytesRawTransaction)
           broadcastTx(rawTransaction)
       }
*/


    private fun x() {
        // Here we restore our wallet from a seed with no passphrase. Also have a look at the BackupToMnemonicSeed.java example that shows how to backup a wallet by creating a mnemonic sentence.
        // Here we restore our wallet from a seed with no passphrase. Also have a look at the BackupToMnemonicSeed.java example that shows how to backup a wallet by creating a mnemonic sentence.


        // val seedCode = "arrive radio wrap runway minute own embark correct divorce suggest enter fiber"
        //"yard impulse luxury drive today throw farm pepper survey wreck glass federal"
        val seedCode = "q w e r t y u i a d c b z"
        val passphrase = ""
        val creationtime = 1409478661L

        val seed = DeterministicSeed(seedCode, null, passphrase, creationtime)
        val param: NetworkParameters = TestNet3Params.get()

// The wallet class provides a easy fromSeed() function that loads a new wallet from a given seed.

// The wallet class provides a easy fromSeed() function that loads a new wallet from a given seed.
        val wallet: Wallet? = Wallet.fromSeed(param, seed, Script.ScriptType.P2PKH)


        val key = ECKey()
        val pubAddress = LegacyAddress.fromScriptHash(wallet!!.networkParameters, key.pubKeyHash)
        val privKey: DumpedPrivateKey = key.getPrivateKeyEncoded(wallet.networkParameters)
        Log.e(
            "TAG",
            "Public address: " + pubAddress.toBase58()
                .toString() + "; Private key: " + privKey.toBase58()
        )

     }




    fun mediumExample() {
        val entropyLen = 8
        val entropy = ByteArray(entropyLen)
        val random = SecureRandom()
        random.nextBytes(entropy)

        val words = MnemonicCode.INSTANCE.toMnemonic(entropy)
        val mnemonic: String = join(" ", words)

        val passphrase = ""
        val seed = MnemonicCode.toSeed(words, passphrase)

        val masterKey = HDKeyDerivation.createMasterPrivateKey(seed);
        val bip44Path = "44H/0H/0H"
        val keyPath: List<ChildNumber> = HDUtils.parsePath(bip44Path)

        val hierarchy = DeterministicHierarchy(masterKey)
        val walletKey = hierarchy[keyPath, false, true]

        Log.e("TAG", "key  " + walletKey.privateKeyAsHex)
        Log.e("TAG", "key  " + walletKey.publicKeyAsHex)
        Log.e("TAG", "key  " + walletKey.serializePubB58(param))
        Log.e("TAG", "key  " + walletKey.serializePrivB58(param))

    }


}


/*
*
     fun create(){
        val params: NetworkParameters = TestNet3Params.get()
        val wallet = Wallet.createDeterministic(params, Script.ScriptType.P2PKH)

        val seed = wallet.keyChainSeed
        Log.e("TAG","seed: ${seed.mnemonicCode}")
    }

      try {
            val key =
                if (base58PrivateKeyString.length == 51 || base58PrivateKeyString.length == 52) {
                    val dumpedPrivateKey =
                        DumpedPrivateKey.fromBase58(param, base58PrivateKeyString)
                    dumpedPrivateKey.key
                } else {
                    val privKey: BigInteger = Base58.decodeToBigInteger(base58PrivateKeyString)
                    ECKey.fromPrivate(privKey)
                }
            val publicKey = key.publicKeyAsHex
            Log.e("TAG", "publicKey  " + publicKey)

        } catch (e: Exception) {
            Log.e("TAG", "error " + e.message)
        }
*/