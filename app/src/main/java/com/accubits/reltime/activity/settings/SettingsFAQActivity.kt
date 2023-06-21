package com.accubits.reltime.activity.settings

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.accubits.reltime.R
import com.accubits.reltime.activity.settings.adapter.FAQListAdapter
import com.accubits.reltime.activity.settings.adapter.FaqTagAdapter
import com.accubits.reltime.activity.settings.viewmodel.FaqViewModel
import com.accubits.reltime.databinding.ActivitySettingsFaqactivityBinding
import com.accubits.reltime.helpers.Utils
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SettingsFAQActivity : AppCompatActivity() {
    lateinit var binder: ActivitySettingsFaqactivityBinding

    @Inject
    lateinit var faqadapter: FaqTagAdapter
    private val viewModel by viewModels<FaqViewModel>()
    private val adapter = FAQListAdapter()
    private var id: Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binder = ActivitySettingsFaqactivityBinding.inflate(layoutInflater)
        setContentView(binder.root)
        if (Utils.isNetworkAvailable(this)!!) {
            binder.progressBar.visibility = View.VISIBLE
            viewModel.getAllTag()

        } else {
            Toast.makeText(this, getString(R.string.please_check_net), Toast.LENGTH_LONG).show()
        }
        viewModel.faqSuccessModel.observe(this) { faqList ->
            binder.progressBar.visibility = View.GONE
            faqList.result?.let { adapter.setItems(it) }
            binder.lvFaqlist.adapter = adapter
            binder.tvEmpty.visibility = if(adapter.count==0) View.VISIBLE else View.GONE
        }
        viewModel.tagSuccessModel.observe(this) { taglist ->
            taglist.result?.let {
                binder.progressBar.visibility = View.GONE
                if (taglist.result != null && taglist.result!!.isNotEmpty()) {
                    id = taglist.result!![0].id
                    binder.progressBar.visibility = View.VISIBLE
                    viewModel.getAllFaq(id.toString())
                    faqadapter.setItems(taglist.result!!)
                    binder.hsView.adapter = faqadapter

                    binder.hsView.visibility = View.VISIBLE
                    binder.tvEmpty.visibility = View.GONE
                } else {
                    binder.hsView.visibility = View.GONE
                    binder.tvEmpty.visibility = View.VISIBLE
                }
            }
        }
        binder.ivBack.setOnClickListener {
            finish()
        }
        binder.hsView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        faqadapter.setOnItemClickListener { it ->
            binder.progressBar.visibility = View.VISIBLE
            binder.tvEmpty.visibility =View.GONE
            viewModel.getAllFaq(it.id.toString())
        }
    }
}