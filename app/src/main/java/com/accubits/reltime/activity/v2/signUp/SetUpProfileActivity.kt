package com.accubits.reltime.activity.v2.signUp

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.KeyEvent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.accubits.reltime.R
import com.accubits.reltime.databinding.ActivitySetUpProfileBinding
import com.accubits.reltime.utils.Extensions.markRequiredInRed
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SetUpProfileActivity : AppCompatActivity() {
    private lateinit var binder: ActivitySetUpProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binder = ActivitySetUpProfileBinding.inflate(layoutInflater)
        setContentView(binder.root)

        init()
        setListeners()
    }

    private fun init(){
        binder.tvFirstName.markRequiredInRed()
        binder.tvLastName.markRequiredInRed()
        binder.tvEmail.markRequiredInRed()
    }

    private fun setListeners() {
        binder.ibBack.setOnClickListener { onBackPressed() }
        binder.etEmail.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN &&
                keyCode == KeyEvent.KEYCODE_ENTER
            ) {
                validate()
                true
            } else false
        }
        binder.btnNext.setOnClickListener { validate() }
    }

    private fun validate() {
        if (binder.etFirstName.text.toString().isEmpty()) {
            binder.tvErrorFirstName.visibility = View.VISIBLE
            return
        } else binder.tvErrorFirstName.visibility = View.GONE
        if (binder.etLastName.text.toString().isEmpty()) {
            binder.tvErrorLastName.visibility = View.VISIBLE
            return
        } else binder.tvErrorLastName.visibility = View.GONE
        if (binder.etEmail.text.toString().isEmpty()) {
            binder.tvErrorEmail.text = resources.getString(R.string.please_enter_your_email)
            binder.tvErrorEmail.visibility = View.VISIBLE
            return
        } else if (!Patterns.EMAIL_ADDRESS.matcher(binder.etEmail.text.toString()).matches()) {
            binder.tvErrorEmail.text = resources.getString(R.string.please_enter_a_valid_email)
            binder.tvErrorEmail.visibility = View.VISIBLE
            return
        } else binder.tvErrorEmail.visibility = View.GONE

        goToConfirmPage()
    }

    private fun goToConfirmPage() {
        val intents = Intent(this, SetUpProfileConfirmActivity::class.java)
        intents.putExtra(SetUpProfileConfirmActivity.EXTRA_PHONE,intent.getStringExtra(SetUpProfileConfirmActivity.EXTRA_PHONE))
        intents.putExtra(SetUpProfileConfirmActivity.EXTRA_LOCATION,intent.getStringExtra(SetUpProfileConfirmActivity.EXTRA_LOCATION))
        intents.putExtra(SetUpProfileConfirmActivity.EXTRA_FIRST_NAME,binder.etFirstName.text.toString())
        intents.putExtra(SetUpProfileConfirmActivity.EXTRA_LAST_NAME,binder.etLastName.text.toString())
        intents.putExtra(SetUpProfileConfirmActivity.EXTRA_EMAIL,binder.etEmail.text.toString())
        startActivity(intents)
    }

}