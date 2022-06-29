package com.storiesapp.common.view.model

import android.app.Dialog

data class LottieDialogCancelOption(
    var onBackPressed: Boolean = false,
    var onTouchOutside: Boolean = false
) {
    operator fun invoke(dialog: Dialog) {
        dialog.setCancelable(onBackPressed)
        dialog.setCanceledOnTouchOutside(onTouchOutside)
    }
}
