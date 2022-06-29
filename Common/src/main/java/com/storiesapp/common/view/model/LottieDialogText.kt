package com.storiesapp.common.view.model

import android.widget.TextView
import androidx.annotation.StringRes
import com.storiesapp.common.extension.setTextRes

data class LottieDialogText(
        var text: CharSequence? = null,
        @StringRes
        var textRes: Int? = null
) {
    operator fun invoke(textView: TextView) {
        textRes ?.let { textView.setTextRes(it) }
        text    ?.let { textView.text     = it }
    }
}
