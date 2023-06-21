package com.accubits.reltime.utils

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.sp

class TextSizeTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        return TransformedText(
            buildAnnotatedStringWithColors(text.toString()),
            OffsetMapping.Identity
        )
    }
}


fun buildAnnotatedStringWithColors(text: String): AnnotatedString {

    val words: List<String> = text.split(".")// splits by whitespace

    val builder = AnnotatedString.Builder()
    words.forEachIndexed { index, s ->
        if (index == 0)
            builder.append(s)
        else
            builder.withStyle(style = SpanStyle(fontSize = 18.sp)) {
                append(".$s")
            }

    }
    return builder.toAnnotatedString()
}

