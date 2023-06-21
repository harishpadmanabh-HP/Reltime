package com.accubits.reltime.activity.settings

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.accubits.reltime.R
import com.accubits.reltime.activity.settings.viewmodel.ProfileEditViewModel
import com.accubits.reltime.constants.ReltimeConstants
import com.accubits.reltime.databinding.ActivitySettingsEditAddressBinding
import com.accubits.reltime.helpers.PreferenceManager
import com.accubits.reltime.helpers.Utils
import com.accubits.reltime.models.UpdateAddressRequestModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SettingsEditAddressActivity : AppCompatActivity() {
    lateinit var binder: ActivitySettingsEditAddressBinding

    @Inject
    lateinit var preferenceManager: PreferenceManager
    private val viewmodel by viewModels<ProfileEditViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binder = ActivitySettingsEditAddressBinding.inflate(layoutInflater)
        setContentView(binder.root)
        binder.btSavechanges.setOnClickListener {
            doApicall()
        }
        binder.ivBack.setOnClickListener {
            finish()
        }
        binder.edAddressLabel1.setText(intent.getStringExtra(ReltimeConstants.ADDRESS1))
        binder.edAddressLabel2.setText(intent.getStringExtra(ReltimeConstants.ADDRESS2))
        binder.edCityLabel.setText(intent.getStringExtra(ReltimeConstants.CITY))
        binder.edStateLabel.setText(intent.getStringExtra(ReltimeConstants.STATE))
        binder.edAddressLabel1.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {}
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.toString() != "") {
                    binder.tvAdd1Error.visibility = View.GONE
                } else if (s.length > 200) {
                    binder.tvAdd1Error.visibility = View.VISIBLE
                    binder.tvAdd1Error.text = "Address1  shouldn't contain more than 200 characters"
                }
            }

        })
        binder.edAddressLabel2.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {}
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.toString() != "") {
                    binder.tvAdd2Error.visibility = View.GONE
                } else if (s.length > 200) {
                    binder.tvAdd2Error.visibility = View.VISIBLE
                    binder.tvAdd2Error.text = "Address2  shouldn't contain more than 200 characters"
                }
            }

        })
        binder.edCityLabel.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {}
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.toString() != "") {
                    binder.tvCityError.visibility = View.GONE
                } else if (s.length > 40) {
                    binder.tvCityError.visibility = View.VISIBLE
                    binder.tvCityError.text = "City name shouldn't contain more than 40 characters"
                }
            }

        })
        binder.edStateLabel.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {}
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.toString() != "") {
                    binder.tvStateError.visibility = View.GONE
                } else if (s.length > 40) {
                    binder.tvStateError.visibility = View.VISIBLE
                    binder.tvStateError.text =
                        "State name shouldn't contain more than 40 characters"
                }
            }

        })
        setObservers()
    }

    private fun doApicall() {
        if (Utils.isNetworkAvailable(this)!!) {
            if (binder.edAddressLabel1.text.toString()
                    .trim() != "" && binder.edAddressLabel2.text.toString()
                    .trim() != "" && binder.edCityLabel.text.toString()
                    .trim() != "" && binder.edStateLabel.text.toString().trim() != ""
            ) {
                if (binder.edAddressLabel1.text.toString().length < 200 && binder.edAddressLabel2.text.toString().length < 200 && binder.edCityLabel.text.toString().length < 40 && binder.edStateLabel.text.toString().length < 40) {
                    binder.tvAdd1Error.visibility = View.GONE
                    binder.tvAdd2Error.visibility = View.GONE
                    binder.tvCityError.visibility = View.GONE
                    binder.tvStateError.visibility = View.GONE
                    val updateAddressRequestModel = UpdateAddressRequestModel(
                        binder.edAddressLabel1.text.toString().trim(),
                        binder.edAddressLabel2.text.toString().trim(),
                        binder.edCityLabel.text.toString().trim(),
                        binder.edStateLabel.text.toString().trim()
                    )
                    viewmodel.setAddress(
                        preferenceManager.getApiKey(),
                        updateAddressRequestModel
                    )
                } else {
                    if (binder.edAddressLabel1.text.toString().length > 200) {
                        binder.tvAdd1Error.visibility = View.VISIBLE
                        binder.tvAdd1Error.text =
                            "Address1 shouldn't contain more than 200 characters"
                    }
                    if (binder.edAddressLabel2.text.toString().length > 200) {
                        binder.tvAdd2Error.visibility = View.VISIBLE
                        binder.tvAdd2Error.text =
                            "Address2 shouldn't contain more than 200 characters"
                    }
                    if (binder.edCityLabel.text.toString().length > 40) {
                        binder.tvCityError.visibility = View.VISIBLE
                        binder.tvCityError.text =
                            "City name shouldn't contain more than 40 characters"
                    }
                    if (binder.edStateLabel.text.toString().length > 40) {
                        binder.tvStateError.visibility = View.VISIBLE
                        binder.tvStateError.text =
                            "State name shouldn't contain more than 40 characters"
                    }
                }

            } else {
                if (binder.edAddressLabel1.text.toString() == "") {
                    binder.tvAdd1Error.visibility = View.VISIBLE
                    binder.tvAdd1Error.text = "Please enter the address 1"
                }

                if (binder.edAddressLabel2.text.toString() == "") {
                    binder.tvAdd2Error.visibility = View.VISIBLE
                    binder.tvAdd2Error.text = "Please enter the address 2"
                }
                if (binder.edCityLabel.text.toString() == "") {
                    binder.tvCityError.visibility = View.VISIBLE
                    binder.tvCityError.text = "Please enter the city name"
                }
                if (binder.edStateLabel.text.toString() == "") {
                    binder.tvStateError.visibility = View.VISIBLE
                    binder.tvStateError.text = "Please enter the state name"
                }

            }
        } else {
            Toast.makeText(this, getString(R.string.please_check_net), Toast.LENGTH_LONG).show()
        }
    }

    private fun setObservers() {
        viewmodel.commonModel.observe(this, { model ->
            var responseMessage: String = ReltimeConstants.EMPTY
            binder.progressBar.visibility = View.GONE
            if (model.success!!) {
                Toast.makeText(this, model.message, Toast.LENGTH_LONG).show()
                finish()
            } else {
                when (model.status) {
                    400 -> {
                        responseMessage = "Something went wrong"
                        Toast.makeText(this, responseMessage, Toast.LENGTH_LONG).show()
                    }
                    401 -> {
                        responseMessage = "Something went wrong"
                        Toast.makeText(this, responseMessage, Toast.LENGTH_LONG).show()
                    }
                }
            }

        })

    }
}