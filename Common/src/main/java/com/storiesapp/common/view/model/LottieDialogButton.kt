package com.storiesapp.common.view.model

import android.app.Dialog
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.google.android.material.button.MaterialButton
import com.storiesapp.common.extension.click
import com.storiesapp.common.extension.setTextRes
import com.storiesapp.common.extension.visible

data class LottieDialogButton(
        var text: CharSequence? = null,
        @StringRes
        var textRes: Int? = null,
        @DrawableRes
        var iconRes: Int? = null,
        @MaterialButton.IconGravity
        var iconGravity: Int? = null,
        var actionDelay: Long? = null,
        internal var onClickListener: ((Dialog) -> Unit)? = null
) {
    internal operator fun invoke(
            dialog: Dialog,
            button: MaterialButton
    ) {
        textRes?.let { button.setTextRes(it) }
        text?.let { button.text = it }
        iconRes?.let { button.setIconResource(it) }
        iconGravity?.let { button.iconGravity = it }

        with(button) {
            click {
                onClickListener?.let {
                    it(dialog)
                }
                dialog.dismiss()
            }
            visible()
        }
    }
}

fun LottieDialogButton.onClick(function: (Dialog) -> Unit) {
    onClickListener = function
}