package com.accubits.reltime.activity.qrcode

import android.Manifest
import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Point
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.Display
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidmads.library.qrgenearator.QRGContents
import androidmads.library.qrgenearator.QRGEncoder
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.net.toFile
import androidx.lifecycle.lifecycleScope
import com.accubits.reltime.BuildConfig
import com.accubits.reltime.R
import com.accubits.reltime.helpers.PreferenceManager
import com.accubits.reltime.helpers.Utils
import com.accubits.reltime.utils.Extensions.copyToClipBoard
import com.google.zxing.WriterException
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import javax.inject.Inject


@AndroidEntryPoint
class QRCodeActivity : AppCompatActivity() {


    lateinit var ivBack: ImageView
    lateinit var ivQRCode: AppCompatImageView
    lateinit var tvWalletAddress: AppCompatTextView
    lateinit var ivCopy: AppCompatImageView
    lateinit var download: Button
    lateinit var share: Button
    lateinit var bitmap: Bitmap
    private val savePath = Environment.getExternalStorageDirectory().path + "/QRCode/"

    companion object {
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)

        const val QR_STRING = "qr_string"
        const val QR_TITLE = "title"
    }

    private lateinit var qrCodeValue: String
private lateinit var tvLabel:TextView
    @Inject
    lateinit var preferenceManager: PreferenceManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // dataBinding = ActivityQRCodeBinding.inflate(layoutInflater)
//
//        setContentView(ActivityQRCodeBinding.root)

        setContentView(R.layout.activity_qrcode)
        ivBack = findViewById(R.id.iv_back)
        ivQRCode = findViewById(R.id.ivQrCode)
        download = findViewById(R.id.bt_download)
        share = findViewById(R.id.bt_share)
        tvWalletAddress = findViewById(R.id.tv_wallet_id)
        ivCopy = findViewById(R.id.iv_copy)
        tvLabel = findViewById(R.id.tv_title)
        qrCodeValue = intent.getStringExtra(QR_STRING) ?: preferenceManager.getPublicKeyFromLogin()
        intent.getStringExtra(QR_TITLE)?.let {
            val tvTitle = findViewById<TextView>(R.id.tvTitle)

            tvTitle.text = resources.getText(R.string.my_wallet_id)
            tvLabel.text= resources.getText(R.string.my_wallet_address)
        }
        tvWalletAddress.text = qrCodeValue
        val manager = getSystemService(WINDOW_SERVICE) as WindowManager
        val display: Display = manager.defaultDisplay
        val point = Point()
        display.getSize(point)
        val width: Int = point.x
        val height: Int = point.y
        var smallerDimension = if (width < height) width else height
        smallerDimension = smallerDimension * 3 / 4
        val qrgEncoder = QRGEncoder(
            qrCodeValue,
            null,
            QRGContents.Type.TEXT,
            smallerDimension
        )
        qrgEncoder.colorBlack = resources.getColor(R.color.white90)
        qrgEncoder.colorWhite = resources.getColor(R.color.dark)
        try {
            // Getting QR-Code as Bitmap
            bitmap = qrgEncoder.bitmap
            // Setting Bitmap to ImageView
            ivQRCode.setImageBitmap(bitmap)
        } catch (e: WriterException) {
            e.printStackTrace()
        }
        ivBack.setOnClickListener { onBackPressed() }
        download.setOnClickListener {

//            onBtnSavePng()

            if (!checkPermission()) {
                onBtnSavePng()
            } else {
                if (checkPermission()) {
                    requestPermissionAndContinue()
                } else {
                    onBtnSavePng()
                }
            }

        }
        share.setOnClickListener {
          //  shareTextViaApps(qrCodeValue)

            val imageFile= Utils.storeFile(this, bitmap)
            val intent = Intent()
            intent.action = Intent.ACTION_SEND

            val uri= imageFile?.let { it1 ->
                FileProvider.getUriForFile(getApplicationContext(), getPackageName()+".fileprovider",
                    it1
                )
            }
            intent.putExtra(Intent.EXTRA_STREAM, uri);
            intent.putExtra(Intent.EXTRA_TEXT,qrCodeValue)
            intent.setType("image/*")
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            startActivity(intent)
        }
        ivCopy.setOnClickListener {
            copyToClipBoard(resources.getString(R.string.wallet_address_), qrCodeValue)
        }
    }

    fun onBtnSavePng() {
        try {
            val filename = "${System.currentTimeMillis()}.jpg"

            //Output stream
            var fos: OutputStream? = null

            //For devices running android >= Q
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                //getting the contentResolver
                contentResolver?.also { resolver ->

                    //Content resolver will process the contentvalues
                    val contentValues = ContentValues().apply {

                        //putting file information in content values
                        put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                        put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg")
                        put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DCIM)
                    }

                    //Inserting the contentValues to contentResolver and getting the Uri
                    val imageUri: Uri? =
                        resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

                    //Opening an outputstream with the Uri that we got
                    fos = imageUri?.let { resolver.openOutputStream(it) }
                }
            } else {
                //These for devices running on android < Q
                //So I don't think an explanation is needed here
                val imagesDir =
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
                val image = File(imagesDir, filename)
                fos = FileOutputStream(image)
                MediaScannerConnection.scanFile(this, arrayOf(imagesDir.path +"/"+filename ),  arrayOf("image/jpeg" ) , null)
            }

            fos?.use {
                //Finally writing the bitmap to the output stream that we opened
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
                Toast.makeText(this, "QR code downloaded successfully", Toast.LENGTH_LONG).show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(
                    applicationContext,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                lifecycleScope.launch {
                    onBtnSavePng()
                }
            } else {
                Toast.makeText(this, "Kindly allow permission", Toast.LENGTH_LONG).show()
            }
        }
    }


    private fun checkPermission(): Boolean {
        return (ContextCompat.checkSelfPermission(
            this,
            WRITE_EXTERNAL_STORAGE
        ) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(
            this,
            READ_EXTERNAL_STORAGE
        ) != PackageManager.PERMISSION_GRANTED)
    }

    private fun requestPermissionAndContinue() {
        if (ContextCompat.checkSelfPermission(
                this,
                WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
            && ContextCompat.checkSelfPermission(
                this,
                READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, WRITE_EXTERNAL_STORAGE)
                && ActivityCompat.shouldShowRequestPermissionRationale(this, READ_EXTERNAL_STORAGE)
            ) {

                ActivityCompat.requestPermissions(
                    this@QRCodeActivity, arrayOf(
                        WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE
                    ), REQUEST_CODE_PERMISSIONS
                )
            } else {
                ActivityCompat.requestPermissions(
                    this@QRCodeActivity, arrayOf(
                        WRITE_EXTERNAL_STORAGE,
                        READ_EXTERNAL_STORAGE
                    ), REQUEST_CODE_PERMISSIONS
                )
            }
        } else {
            Toast.makeText(this, "Kindly allow permission", Toast.LENGTH_LONG).show()
        }
    }

    private fun getBitmapFromView(bmp: Bitmap?): Uri? {
        var bmpUri: Uri? = null
        try {
            val file = File(this.externalCacheDir, System.currentTimeMillis().toString() + ".jpg")
            val out = FileOutputStream(file)
            bmp?.compress(Bitmap.CompressFormat.JPEG, 90, out)
            out.close()
           // bmpUri = Uri.fromFile(file)
            bmpUri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID+".fileprovider", file)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return bmpUri
    }
}