package com.storiesapp.common.view.model

import android.app.Dialog
import android.content.DialogInterface
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class LottieDialogOnShowListener(
    var onShowListener: ((DialogInterface) -> Unit)? = null
) {
    operator fun invoke(dialog: Dialog) {
        dialog.setOnShowListener(onShowListener)
    }
}

data class LottieDialogOnDismissListener(
    var onDismissListener: ((DialogInterface) -> Unit)? = null
) {
    operator fun invoke(dialog: Dialog, jobs: MutableList<Job> = mutableListOf()) {
        dialog.setOnDismissListener {
            jobs.forEach { job -> job.cancel() }
            jobs.clear()
            onDismissListener?.invoke(it)
        }
    }
}

data class LottieDialogOnCancelListener(
    var onCancelListener: ((DialogInterface) -> Unit)? = null
) {
    operator fun invoke(dialog: Dialog) {
        dialog.setOnCancelListener(onCancelListener)
    }
}

data class LottieDialogOnTimeoutListener(
    var onTimeoutListener: (() -> Unit)? = null
) {
    @Suppress("MagicNumber")
    operator fun invoke(
        dialog: Dialog,
        timeout: Long,
        coroutineScope: CoroutineScope
    ): Job? {
        val progressBarUpdateFrequency = 200

        return coroutineScope.launch {
            for (index in 1..progressBarUpdateFrequency) {
                val length = timeout / progressBarUpdateFrequency
                delay(length)
            }
            dialog.dismiss()
            onTimeoutListener?.invoke()
        }
    }
}
