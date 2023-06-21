package com.accubits.reltime.helpers

import android.os.Build
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import com.accubits.reltime.constants.ReltimeConstants
import java.io.IOException
import java.security.*
import java.security.cert.CertificateException
import javax.crypto.*
import javax.crypto.spec.GCMParameterSpec

class DeCryptor {
    private val TRANSFORMATION = "AES/GCM/NoPadding"
    private val ANDROID_KEY_STORE = "AndroidKeyStore"

    private var keyStore: KeyStore? = null

    @Throws(
        CertificateException::class,
        NoSuchAlgorithmException::class,
        KeyStoreException::class,
        IOException::class
    )
    fun DeCryptor() {
        initKeyStore()
    }

    @Throws(
        KeyStoreException::class,
        CertificateException::class,
        NoSuchAlgorithmException::class,
        IOException::class
    )
    private fun initKeyStore() {
        keyStore = KeyStore.getInstance(ANDROID_KEY_STORE)
        keyStore!!.load(null)
    }

    @Throws(
        UnrecoverableEntryException::class,
        NoSuchAlgorithmException::class,
        KeyStoreException::class,
        NoSuchProviderException::class,
        NoSuchPaddingException::class,
        InvalidKeyException::class,
        IOException::class,
        BadPaddingException::class,
        IllegalBlockSizeException::class,
        InvalidAlgorithmParameterException::class
    )
    fun decryptData(alias: String, encryptedData: ByteArray?, encryptionIv: ByteArray?): String? {
        val cipher: Cipher = Cipher.getInstance(TRANSFORMATION)
        val spec = GCMParameterSpec(128, encryptionIv)
        cipher.init(Cipher.DECRYPT_MODE, getSecretKey(alias), spec)
        return String(cipher.doFinal(encryptedData), Charsets.UTF_8)
    }

    @Throws(
        NoSuchAlgorithmException::class,
        UnrecoverableEntryException::class,
        KeyStoreException::class
    )
    private fun getSecretKey(alias: String): SecretKey? {
        return (keyStore?.getEntry(alias, null) as KeyStore.SecretKeyEntry).secretKey
    }

    fun decryptData(encryptedData: ByteArray, iv: ByteArray): String {
        return try {
            val cipher = Cipher.getInstance(TRANSFORMATION)
            val spec = GCMParameterSpec(128, iv)
            cipher.init(Cipher.DECRYPT_MODE, getSecretKet(ReltimeConstants.RELTIME), spec)
            val result = cipher.doFinal(encryptedData).toString(Charsets.ISO_8859_1)
            //Timber.i("$TAG decrypted data $result")
            result
        } catch (e: Exception) {
            //  Timber.e("$TAG decryptData error may string was not encrypted", e)
            encryptedData.toString()
        }
    }

    private fun getSecretKet(alias: String): Key {

        if (keyStore!!.containsAlias(alias)) {
            // Try for existing key
            return keyStore!!.getKey(alias, null)
        } else {
            // Key is not present, create new one.
            val keyGenerator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val kGenerator =
                    KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEY_STORE)
                val specs = KeyGenParameterSpec
                    .Builder(
                        alias,
                        KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
                    )
                    .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                    .build()
                kGenerator.init(specs)
                kGenerator
            } else {
                KeyGenerator.getInstance(ANDROID_KEY_STORE)
            }
            return keyGenerator.generateKey()
        }
    }

}