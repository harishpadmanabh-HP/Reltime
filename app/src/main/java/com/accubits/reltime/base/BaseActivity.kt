package com.accubits.reltime.base

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModel
import com.accubits.reltime.R
import com.accubits.reltime.constants.ReltimeConstants
import com.accubits.reltime.databinding.ActivityBaseBinding
import com.accubits.reltime.databinding.ActivityBiometricEnableBinding
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint


abstract class BaseActivity<B : ViewDataBinding, V : ViewModel> : AppCompatActivity(),BaseView {

    protected lateinit var dataBinding: B


    private lateinit var baseView: ActivityBaseBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        baseView = ActivityBaseBinding.inflate(layoutInflater)


        dataBinding = DataBindingUtil.inflate(layoutInflater, onInflateLayout(), baseView.rootView, true)
      //  DataBindingUtil.inflate(layoutInflater, onInflateToolbarLayout(), baseView.toolbarView, true)
        dataBinding.root.let { DataBindingUtil.bind<B>(it) }
        dataBinding.lifecycleOwner = this
        setContentView(baseView.root)
    }

    override fun showEmptyView(tag: Any?, message: String) {
        baseView.rootView.visibility = View.VISIBLE
        when (tag) {
            ReltimeConstants.Tags.FLASHBAR -> {
           /*     Flashbar.Builder(requireActivity())
                    .gravity(Flashbar.Gravity.BOTTOM)
                    .duration(Flashbar.DURATION_LONG)
                    .message(emptyMessage)
                    .build().show()*/
            }
            ReltimeConstants.Tags.FULL_SCREEN -> {
                baseView.rootView.visibility = View.GONE

                baseView.stateView.setLEMStateViews(showEmptyView = true, emptyMessage = message)
            }
            ReltimeConstants.Tags.DIALOG -> {

            }
            else -> {
                Toast.makeText(this, message, Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun showError(tag: Any?, error: String) {
        baseView.rootView.visibility = View.VISIBLE

        when (tag) {

            ReltimeConstants.Tags.FULL_SCREEN -> {
                baseView.rootView.visibility = View.GONE

                baseView.stateView.setLEMStateViews(showErrorView = true, errorMessage = error)
            }
            ReltimeConstants.Tags.FLASHBAR -> {
              /*  Flashbar.Builder(requireActivity())
                    .gravity(Flashbar.Gravity.TOP)
                    .title("")
                    .message(error)
                    .backgroundColor(resources.getColor(R.color.failure_color, null))
                    .messageTypeface(
                        ResourcesCompat.getFont(
                            requireContext(),
                            R.font.roboto_regular
                        )!!
                    )
                    .duration(Flashbar.DURATION_LONG)
                    .showOverlay()
                    .overlayBlockable()
                    .build().show()*/

            }
            ReltimeConstants.Tags.DIALOG->{
              //  dialogProvider.showErrorDialog(childFragmentManager, error)
            }
            else -> {
                Toast.makeText(this, error, Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun showError(
        tag: Any?,
        error: String,
        btnText: String,
        btnCallback: OnErrorBtnCallback?
    ) {
        baseView.rootView.visibility = View.VISIBLE
        when (tag) {

            ReltimeConstants.Tags.FULL_SCREEN -> {
                baseView.rootView.visibility = View.GONE
                baseView.stateView.setLEMStateViews(
                    showErrorView = true,
                    errorMessage = error,
                    errorBtnText = btnText,
                    errorCallback = btnCallback
                )
            }
            ReltimeConstants.Tags.DIALOG -> {
             //   dialogProvider.showErrorDialog(childFragmentManager, error, btnText, btnCallback)
            }
            ReltimeConstants.Tags.FLASHBAR->{
                /*  Flashbar.Builder(requireActivity())
                   .gravity(Flashbar.Gravity.TOP)
                   .title("")
                   .message(error)
                   .backgroundColor(resources.getColor(R.color.failure_color,null))
                   .messageTypeface(
                       ResourcesCompat.getFont(
                           requireContext(),
                           R.font.roboto_regular
                       )!!
                   )
                   .primaryActionText(btnText)
                   .primaryActionTapListener(object : Flashbar.OnActionTapListener {
                       override fun onActionTapped(bar: Flashbar) {
                           btnCallback?.onErrorBtnClicked()
                           bar.dismiss()
                       }
                   })
                   .duration(Flashbar.DURATION_LONG)
                   .build().show()*/
            }
            else -> {
                Toast.makeText(this, error, Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun showErrorWithErrorCode(tag: Any?, error: String, code: Int) {

    }

    override fun showProgress(tag: Any?, message: String) {

    }

    override fun hideProgress(tag: Any?) {

    }

    override fun showSuccess(message: String) {

    }

    override fun showNotFoundError(message: String) {

    }

    override fun showUnAuthorized(message: String) {

    }

    override fun showNoInternetConnection() {

    }
}