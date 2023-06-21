package com.accubits.reltime.mnemonic

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.accubits.reltime.R
import com.accubits.reltime.databinding.ActivityEntryMemonicBinding
import com.accubits.reltime.helpers.PreferenceManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class EntryMemonicActivity : AppCompatActivity() {
    @Inject
    lateinit var preferenceHelper: PreferenceManager
    private lateinit var activityEntryMemonicBing: ActivityEntryMemonicBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityEntryMemonicBing = ActivityEntryMemonicBinding.inflate(layoutInflater)
        setContentView(activityEntryMemonicBing.root)
        if (!preferenceHelper.getMnemonicCreated())
            activityEntryMemonicBing.btImport.visibility=View.GONE
        activityEntryMemonicBing.btCreateMemonic.setOnClickListener {
            if (preferenceHelper.getMnemonicCreated())
            createAlert()
            else {
                val intent = Intent(this, MnemonicActivity::class.java)
                startActivity(intent)
            }
        }
        activityEntryMemonicBing.btImport.setOnClickListener {
            val intent = Intent(this, ImportMemonicActivity::class.java)
            startActivity(intent)
        }

    }

    private fun createAlert() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.popup_window)
        val btnCancel: Button = dialog.findViewById(R.id.button) as Button
        val tvHeading: TextView = dialog.findViewById(R.id.tv_heading) as TextView
        val tvDes: TextView = dialog.findViewById(R.id.tv_des) as TextView
        val btnProcced: Button = dialog.findViewById(R.id.button2) as Button
        // if button is clicked, close the custom dialog
        // if button is clicked, close the custom dialog
        tvHeading.text = "Create New"
        tvDes.text =
            "If you are an existing member kindly restore the wallet. Creating a new wallet might lose your old one. Continue anyway? "
        btnProcced.setOnClickListener(View.OnClickListener {

            dialog.dismiss()
            //doAcceptOrReject(true, rowMyBorrowings)
            val intent = Intent(this, MnemonicActivity::class.java)
            startActivity(intent)
        })
        btnCancel.setOnClickListener(View.OnClickListener {
            dialog.dismiss()

        })
        dialog.show()
    }
}