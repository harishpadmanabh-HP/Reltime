package com.accubits.reltime.base

import androidx.annotation.UiThread
import com.accubits.reltime.base.OnErrorBtnCallback

interface BaseView {

    @UiThread
    fun onInflateLayout(): Int

    @UiThread
    fun onInflateToolbarLayout(): Int

    @UiThread
    fun showEmptyView(tag: Any?, message: String)

    @UiThread
    fun showError(tag: Any?, error: String)

    @UiThread
    fun showErrorWithErrorCode(tag: Any?, error: String, code: Int)

    @UiThread
    fun showError(tag: Any?, error: String,btnText: String, btnCallback: OnErrorBtnCallback?)

    @UiThread
    fun showProgress(tag: Any?, message: String = "Loading...")

    @UiThread
    fun hideProgress(tag: Any?)

    @UiThread
    fun showSuccess(message: String = "")

    @UiThread
    fun showNotFoundError(message: String = "")

    @UiThread
    fun showUnAuthorized(message: String = "")

    @UiThread
    fun showNoInternetConnection()
}