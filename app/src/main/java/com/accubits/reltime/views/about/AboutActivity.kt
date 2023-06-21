package com.accubits.reltime.views.about

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.accubits.reltime.databinding.ActivityAboutBinding

class AboutActivity : AppCompatActivity() {
    private lateinit var aboutBinding: ActivityAboutBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        aboutBinding = ActivityAboutBinding.inflate(layoutInflater)
        setContentView(aboutBinding.root)
        aboutBinding.ivBack.setOnClickListener {
            finish()
        }
    }
}