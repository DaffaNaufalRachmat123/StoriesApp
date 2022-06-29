package com.storiesapp.common.view

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.annotation.RawRes
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.airbnb.lottie.LottieAnimationView
import com.storiesapp.common.R
import com.storiesapp.common.extension.*
import com.storiesapp.common.view.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job

data class LottieLoadingDialog(
    var type: LottieDialogType = LottieDialogType.DIALOG,
    var theme: LottieDialogTheme = LottieDialogTheme.DAY_NIGHT,
    var timeout: Boolean = true,
    internal var job: ((DialogInterface, CoroutineScope) -> Job)? = null,
    internal var image: LottieDialogImage? = null,
    internal var animation: LottieDialogAnimation? = null,
    internal var title: LottieDialogText = LottieDialogText(),
    internal var cancelAbility: LottieDialogCancelOption = LottieDialogCancelOption(),
    internal var onShowListener: LottieDialogOnShowListener = LottieDialogOnShowListener(),
    internal var onDismissListener: LottieDialogOnDismissListener = LottieDialogOnDismissListener(),
    internal var onCancelListener: LottieDialogOnCancelListener = LottieDialogOnCancelListener(),
    internal var onTimeoutListener: LottieDialogOnTimeoutListener = LottieDialogOnTimeoutListener()
) {
    val jobs = mutableListOf<Job>()

    operator fun invoke(
            dialog: Dialog,
            view: View,
            coroutineScope: CoroutineScope,
            useInsideFragment: Boolean = false
    ): Dialog {
        val illustrationLayout: FrameLayout = view.findViewById(R.id.lottie_dialog_illustration_layout)
        val illustrationAnim: LottieAnimationView = view.findViewById(R.id.lottie_dialog_view_anim)
        val illustrationImage: ImageView = view.findViewById(R.id.lottie_dialog_view_image)
        val tvTitle: TextView = view.findViewById(R.id.lottie_dialog_tv_title)

        illustrationAnim.setFillTint(dialog.context.getResColor(R.color.material_color_blue_1))
        illustrationAnim.setStrokeTint(dialog.context.getResColor(R.color.material_color_blue_1))
        animation?.invoke(illustrationLayout, illustrationAnim, null, dialog, type) ?: run {
            illustrationAnim.gone()
            image?.invoke(illustrationLayout, illustrationImage, null, dialog, type) ?: run {
                illustrationImage.gone()
                illustrationLayout.gone()
            }
        }
        title(tvTitle.apply {
            if (isOreoMR1 && type == LottieDialogType.BOTTOM_SHEET && dialog.context.hasSoftNavigationKeys) {
                addNavigationBarPadding()
            }
        })

        title(tvTitle.apply {
            if (isOreoMR1 && type == LottieDialogType.BOTTOM_SHEET && dialog.context.hasSoftNavigationKeys) {
                context.navigationBarHeight?.let {
                    setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom + it)
                }
            }
        })
        cancelAbility(dialog)
        onShowListener(dialog)

        job?.let { jobs.add(it(dialog, coroutineScope)) }

        if (timeout) {
            onTimeoutListener(dialog, 3000, coroutineScope)?.let { job ->
                jobs.add(job)
            }
        }

        if (isOreoMR1) {
            view.systemUiVisibility =
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
        }


        if (!useInsideFragment) {
            onDismissListener.invoke(dialog, jobs)
            onCancelListener(dialog)
            dialog.show()
        }

        return dialog
    }

    companion object {
        internal fun create(
                context: Context,
                layoutInflater: LayoutInflater,
                coroutineScope: CoroutineScope,
                vararg builders: LottieLoadingDialog.() -> Unit
        ): Dialog {
            val lottieDialog = LottieLoadingDialog()
            builders.forEach { lottieDialog.apply(it) }

            val layout = when (lottieDialog.type) {
                LottieDialogType.DIALOG -> R.layout.lottie_dialog_loading
                LottieDialogType.BOTTOM_SHEET -> R.layout.lottie_dialog_loading_bottom_sheet
            }
            val (dialog, view) = inflateView(
                    context, layoutInflater, layout,
                    lottieDialog.type,
                    R.style.LottieDialogTheme_Dialog
            )

            return lottieDialog.invoke(dialog, view, coroutineScope)
        }
    }
}

//region Builder Only Function

fun lottieLoadingDialogBuilder(builder: LottieLoadingDialog.() -> Unit) = builder

//endregion
//region Activity Extension

@Suppress("SpreadOperator")
fun AppCompatActivity.lottieLoadingDialog(
        vararg builders: LottieLoadingDialog.() -> Unit,
        builder: LottieLoadingDialog.() -> Unit
) = LottieLoadingDialog.create(
        this,
        layoutInflater,
        lifecycleScope,
        *builders,
        builder
)

//endregion
//region Fragment Extension

@Suppress("SpreadOperator")
fun Fragment.lottieLoadingDialog(
        vararg builders: LottieLoadingDialog.() -> Unit,
        builder: LottieLoadingDialog.() -> Unit
) = LottieLoadingDialog.create(
        requireContext(),
        layoutInflater,
        viewLifecycleOwner.lifecycleScope,
        *builders,
        builder
)
//endregion
//region Job DSL

fun LottieLoadingDialog.withJob(block: (DialogInterface, CoroutineScope) -> Job) {
    job = block
}

fun LottieLoadingDialog.withoutJob() {
    job = null
}

//endregion
//region Animation DSL


fun LottieLoadingDialog.withAnimation(@RawRes lottieFileRes: Int) {
    if (animation == null) {
        animation = LottieDialogAnimation(lottieFileRes)
    }
    animation!!.fileRes = lottieFileRes
}

fun LottieLoadingDialog.withAnimation(builder: LottieDialogAnimation.() -> Unit) {
    if (animation == null) {
        animation = LottieDialogAnimation()
    }
    animation!!.apply(builder)
}

fun LottieLoadingDialog.withoutAnimation() {
    animation = null
}

//endregion
//region Image DSL

fun LottieLoadingDialog.withImage(@DrawableRes imageRes: Int) {
    if (image == null) {
        image = LottieDialogImage(imageRes)
    }
    image!!.imageRes = imageRes
}

fun LottieLoadingDialog.withImage(builder: LottieDialogImage.() -> Unit) {
    if (image == null) {
        image = LottieDialogImage()
    }
    image!!.apply(builder)
}

fun LottieLoadingDialog.withoutImage() {
    image = null
}

//endregion
//region Text DSL

fun LottieLoadingDialog.withTitle(@StringRes textRes: Int) {
    title.textRes = textRes
}

fun LottieLoadingDialog.withTitle(text: CharSequence) {
    title.text = text
}

fun LottieLoadingDialog.withTitle(builder: LottieDialogText.() -> Unit) {
    title.apply(builder)
}

//endregion
//region Cancel Option DSL

fun LottieLoadingDialog.withCancelOption(builder: LottieDialogCancelOption.() -> Unit) {
    cancelAbility.apply(builder)
}

//endregion
//region Listener DSL

fun LottieLoadingDialog.onShow(function: (DialogInterface) -> Unit) {
    onShowListener = LottieDialogOnShowListener(function)
}

fun LottieLoadingDialog.onDismiss(function: (DialogInterface) -> Unit) {
    onDismissListener = LottieDialogOnDismissListener(function)
}

fun LottieLoadingDialog.onCancel(function: (DialogInterface) -> Unit) {
    onCancelListener = LottieDialogOnCancelListener(function)
}

fun LottieLoadingDialog.onTimeout(function: () -> Unit) {
    onTimeoutListener = LottieDialogOnTimeoutListener(function)
}

//endregion
