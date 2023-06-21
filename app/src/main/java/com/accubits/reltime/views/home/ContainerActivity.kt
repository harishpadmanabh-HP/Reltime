package com.accubits.reltime.views.home

import android.Manifest
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.accubits.reltime.R
import com.accubits.reltime.activity.v2.wallet.qrCode.QrCodeV2Activity
import com.accubits.reltime.constants.ReltimeConstants
import com.accubits.reltime.databinding.ActivityContainerBinding
import com.accubits.reltime.helpers.PreferenceManager
import com.accubits.reltime.utils.Extensions.openActivity
import com.accubits.reltime.utils.Extensions.showToast
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class ContainerActivity : AppCompatActivity() {
    @Inject
    lateinit var preferenceManager: PreferenceManager
    lateinit var binding: ActivityContainerBinding

    companion object {
        const val EXTRA_IS_FIRST_LOGIN = "first_login"
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityContainerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        preferenceManager.setBiometricCompleted(true)
        val navView: BottomNavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_activity_bottom_navigation)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        navView.setupWithNavController(navController)
        if (intent.getStringExtra(ReltimeConstants.To).equals(ReltimeConstants.WALLET)) {
            lifecycleScope.launch {
                getCallback().collect {
                    navView.selectedItemId = R.id.wallet
                }
            }
        }
        binding.floatingActionButton.setOnClickListener {
            if (preferenceManager.getMomic()) {
                if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.CAMERA
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    openActivity(QrCodeV2Activity::class.java)
                } else {
                    cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                }
            } else {
                showDialog()
            }
        }

        if (!preferenceManager.getMomic()) {
            binding.navView.menu.findItem(R.id.wallet).isCheckable = true
            binding.navView.menu.findItem(R.id.loans).isCheckable = false
            //  binding.navView.menu.findItem(R.id.settings).isCheckable = false

//            binding.navView.menu.findItem(R.id.wallet).setOnMenuItemClickListener(object : MenuItem.OnMenuItemClickListener{
//
//                override fun onMenuItemClick(p0: MenuItem?): Boolean {
//                    showDialog()
//                    return true
//                }
//            })
            binding.navView.menu.findItem(R.id.loans)
                .setOnMenuItemClickListener(object : MenuItem.OnMenuItemClickListener {

                    override fun onMenuItemClick(p0: MenuItem?): Boolean {
                        showDialog()
                        return true
                    }
                })
            /*  binding.navView.menu.findItem(R.id.settings).setOnMenuItemClickListener(object : MenuItem.OnMenuItemClickListener{

                  override fun onMenuItemClick(p0: MenuItem?): Boolean {
                      showDialog()
                      return true
                  }
              })*/
        } else {

            binding.navView.menu.findItem(R.id.wallet).isCheckable = true
            binding.navView.menu.findItem(R.id.loans).isCheckable = true
            // binding.navView.menu.findItem(R.id.settings).isCheckable = true
        }
    }

    val cameraPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            openActivity(QrCodeV2Activity::class.java)
        }
        else
            showToast(resources.getString(R.string.permission_needed_to_continue))
    }

    fun getCallback(): Flow<String> = flow {
        delay(1000)
        emit("data")
    }

    private fun displayPopupWindow(anchorView: View) {
        val popupView: View = layoutInflater.inflate(R.layout.home_popup, null)
        // create the popup window
        val width = LinearLayout.LayoutParams.MATCH_PARENT
        val height = LinearLayout.LayoutParams.MATCH_PARENT
        val popupWindow = PopupWindow(popupView, width, height, true)

        popupWindow.isOutsideTouchable = true
        popupWindow.isFocusable = true
        popupWindow.setBackgroundDrawable(null)
        popupWindow.showAsDropDown(anchorView)


    }

    fun showDialog() {
        val dialog = Dialog(this@ContainerActivity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setContentView(com.accubits.reltime.R.layout.alert_dialog_one)
        val text = dialog.findViewById(com.accubits.reltime.R.id.tv_des) as TextView
//        text.text = msg
        val dialogButton: Button = dialog.findViewById(com.accubits.reltime.R.id.button2) as Button
        dialogButton.setOnClickListener(View.OnClickListener { dialog.dismiss() })
        dialog.show()
    }
}