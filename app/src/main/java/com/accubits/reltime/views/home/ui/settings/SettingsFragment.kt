package com.accubits.reltime.views.home.ui.settings

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.accubits.reltime.R
import com.accubits.reltime.activity.contacts.ContactActivity
import com.accubits.reltime.activity.myAccounts.MyAccountsActivity
import com.accubits.reltime.activity.settings.SettingsFAQActivity
import com.accubits.reltime.activity.settings.SettingsNotificationAndSecurityActivity
import com.accubits.reltime.activity.settings.SettingsProfileActivity
import com.accubits.reltime.activity.v2.support.SupportActivity
import com.accubits.reltime.activity.v2.welcomeScreen.WelcomeScreen
import com.accubits.reltime.api.ApiCallStatus
import com.accubits.reltime.databinding.SettingsFragmentv2Binding
import com.accubits.reltime.helpers.PreferenceManager
import com.accubits.reltime.utils.Extensions.openActivity
import com.accubits.reltime.utils.Extensions.showToast
import com.accubits.reltime.views.about.AboutActivity
import com.accubits.reltime.views.privacy.PrivacyActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.lang.reflect.Type
import javax.inject.Inject

@AndroidEntryPoint
class SettingsFragment : Fragment() {

    companion object {
        fun newInstance() = SettingsFragment()
    }

    @Inject
    lateinit var preferenceManager: PreferenceManager
    lateinit var viewModel: SettingsViewModel
    private lateinit var fragmentSettingsFragmentBinding: SettingsFragmentv2Binding
    private var suggestionList = ArrayList<String>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentSettingsFragmentBinding =
            SettingsFragmentv2Binding.inflate(inflater, container, false)
        val root: View = fragmentSettingsFragmentBinding.root


        fragmentSettingsFragmentBinding.rootLogout.setOnClickListener {
            viewModel.doLogout(preferenceManager.getUserId())
            updateUi()

        }
        fragmentSettingsFragmentBinding.rootProfile.setOnClickListener {
            val intent = Intent(activity, SettingsProfileActivity::class.java)
            startActivity(intent)
        }
        fragmentSettingsFragmentBinding.rootFaq.setOnClickListener {
            val intent = Intent(activity, SettingsFAQActivity::class.java)
            startActivity(intent)
        }
        fragmentSettingsFragmentBinding.rootAccount.setOnClickListener {
            if (!preferenceManager.getMomic())
                requireContext().showToast(resources.getString(R.string.wallet_not_available_error))
            else requireActivity().openActivity(MyAccountsActivity::class.java)
        }
        fragmentSettingsFragmentBinding.rootNotification.setOnClickListener {
            val intent = Intent(activity, SettingsNotificationAndSecurityActivity::class.java)
            startActivity(intent)
        }
        fragmentSettingsFragmentBinding.rootAbout.setOnClickListener {
            val intent = Intent(activity, AboutActivity::class.java)
            startActivity(intent)
        }

        fragmentSettingsFragmentBinding.rootInvite.setOnClickListener {
            if (!preferenceManager.getMomic())
                requireContext().showToast(resources.getString(R.string.wallet_not_available_error))
            else requireActivity().openActivity(ContactActivity::class.java)
        }
        fragmentSettingsFragmentBinding.rootHelpSupport.setOnClickListener {
            val intent = Intent(activity, SupportActivity::class.java)
            activity?.startActivity(intent)
        }
        fragmentSettingsFragmentBinding.rootPrivacyPolicy.setOnClickListener {
            val intent = Intent(context, PrivacyActivity::class.java)
            intent.putExtra(PrivacyActivity.URL, PrivacyActivity.PRIVACY_POLICY)
            activity?.startActivity(intent)
        }

        val list = preferenceManager.getSuggestionList() ?: ""
        if (list.isNotEmpty()) {
            val gson = Gson()
            val type: Type = object : TypeToken<List<String>?>() {}.type
            suggestionList = gson.fromJson(list, type)
        }



        return root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[SettingsViewModel::class.java]
        viewModel.getAppVersion()
        appVersion()
    }

    private fun appVersion() {
        lifecycleScope.launch {
            viewModel.versionFlow.collectLatest {
                fragmentSettingsFragmentBinding.tvVersion.text = it
            }
        }
    }

    private fun updateUi() {
        lifecycleScope.launch {
            viewModel.logoutModel.collectLatest { response ->
                when (response.status) {
                    ApiCallStatus.LOADING -> {
                        fragmentSettingsFragmentBinding.progressBar.visibility = View.VISIBLE
                        fragmentSettingsFragmentBinding.scrollView.visibility = View.GONE
                    }
                    ApiCallStatus.SUCCESS -> {
                        fragmentSettingsFragmentBinding.progressBar.visibility = View.GONE
                        fragmentSettingsFragmentBinding.scrollView.visibility = View.VISIBLE
                        if (response.data!!.status == 200) {
                            preferenceManager.setLoggedIn(false)
                            preferenceManager.clearPrefs(
                                preferenceManager.getPrivateKey(),
                                preferenceManager.getPublicKey(), false
                            )
                            preferenceManager.setSuggestionList(suggestionList)
                            val intent = Intent(activity, WelcomeScreen::class.java)
                            startActivity(intent)
                            activity?.finishAffinity()

                        } else {
                            Toast.makeText(
                                requireContext(),
                                response.data.message,
                                Toast.LENGTH_LONG
                            )
                                .show()
                        }
                    }
                    ApiCallStatus.ERROR -> {
                        fragmentSettingsFragmentBinding.progressBar.visibility = View.GONE
                        fragmentSettingsFragmentBinding.scrollView.visibility = View.VISIBLE
                    }
                    else -> {

                    }
                }
            }
        }
    }

}
