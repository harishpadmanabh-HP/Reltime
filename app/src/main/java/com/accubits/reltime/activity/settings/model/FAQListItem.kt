package com.accubits.reltime.activity.settings.model

import com.accubits.reltime.R

data class FAQListItem(
    val title: String = R.string.settings_faq_subtitle.toString(),
    val content: String = R.string.settings_faq_content_sample.toString()
)