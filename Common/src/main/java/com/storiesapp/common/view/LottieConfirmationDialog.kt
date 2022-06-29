package com.storiesapp.common.view

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.annotation.RawRes
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintSet
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.airbnb.lottie.LottieAnimationView
import com.storiesapp.common.R
import com.google.android.material.button.MaterialButton
import com.storiesapp.common.extension.*
import com.storiesapp.common.view.model.*
import kotlinx.coroutines.CoroutineScope

data class LottieConfirmationDialog(
    var type: LottieDialogType = LottieDialogType.BOTTOM_SHEET,
    var theme: LottieDialogTheme = LottieDialogTheme.DAY_NIGHT,
    internal var image: LottieDialogImage? = null,
    internal var animation: LottieDialogAnimation? = null,
    internal var title: LottieDialogText = LottieDialogText(),
    internal var content: LottieDialogText? = null,
    internal var positiveButton: LottieDialogButton = LottieDialogButton(textRes = android.R.string.ok),
    internal var negativeButton: LottieDialogButton? = null,
    internal var cancelAbility: LottieDialogCancelOption = LottieDialogCancelOption(),
    internal var onShowListener: LottieDialogOnShowListener = LottieDialogOnShowListener(),
    internal var onDismissListener: LottieDialogOnDismissListener = LottieDialogOnDismissListener(),
    internal var onCancelListener: LottieDialogOnCancelListener = LottieDialogOnCancelListener()
) {
    @SuppressWarnings("LongMethod", "NestedBlockDepth")
    operator fun invoke(
            dialog: Dialog,
            view: View,
            coroutineScope: CoroutineScope,
            useInsideFragment: Boolean = false
    ): Dialog {
        val illustrationLayout: FrameLayout = view.findViewById(R.id.lottie_dialog_illustration_layout)
        val illustrationAnim: LottieAnimationView = view.findViewById(R.id.lottie_dialog_view_anim)
        val illustrationImage: ImageView = view.findViewById(R.id.lottie_dialog_view_image)
        val btnClose: ImageButton = view.findViewById(R.id.lottie_dialog_btn_close)
        val tvTitle: TextView = view.findViewById(R.id.lottie_dialog_tv_title)
        val tvContent: TextView = view.findViewById(R.id.lottie_dialog_tv_content)
        val btnPositive: MaterialButton = view.findViewById(R.id.lottie_dialog_btn_positive)
        val btnNegative: MaterialButton = view.findViewById(R.id.lottie_dialog_btn_negative)

        animation?.let {
            it(illustrationLayout, illustrationAnim, btnClose, dialog, type)
            tvTitle.gravity = Gravity.CENTER
            tvContent.gravity = Gravity.CENTER
            if (negativeButton == null) {
                btnPositive.layoutParams.width = 0
                with(view.resources.getDimensionPixelSize(R.dimen.large)) {
                    btnPositive.constraintMarginStart = this
                    btnPositive.constraintMarginEnd = this
                }
            }
        } ?: run {
            illustrationAnim.gone()
            btnClose.gone()

            image?.let {
                it(illustrationLayout, illustrationImage, btnClose, dialog, type)
                tvTitle.gravity = Gravity.CENTER
                tvContent.gravity = Gravity.CENTER
                if (negativeButton == null) {
                    btnPositive.layoutParams.width = 0
                    with(view.resources.getDimensionPixelSize(R.dimen.large)) {
                        btnPositive.constraintMarginStart = this
                        btnPositive.constraintMarginEnd = this
                    }
                }
            } ?: run {
                illustrationImage.gone()
                btnClose.gone()
                illustrationLayout.gone()

                view.clearConstraint {
                    clear(R.id.lottie_dialog_btn_positive, ConstraintSet.START)
                    clear(R.id.lottie_dialog_btn_negative, ConstraintSet.START)
                }
                with(view.resources.getDimensionPixelSize(R.dimen.medium)) {
                    btnPositive.constraintMarginEnd = this
                    btnNegative.constraintMarginEnd = this
                }
            }
        }

        title(tvTitle)
        content?.invoke(tvContent) ?: tvContent.gone()
        positiveButton(dialog, btnPositive)
        negativeButton?.invoke(dialog, btnNegative)
        cancelAbility(dialog)
        onShowListener(dialog)

        if (!useInsideFragment) {
            onDismissListener(dialog)
            onCancelListener(dialog)
        }

        return dialog
    }

    companion object {
        internal fun create(
                context: Context,
                coroutineScope: CoroutineScope,
                layoutInflater: LayoutInflater,
                vararg builders: LottieConfirmationDialog.() -> Unit
        ): Dialog {
            val lottieDialog = LottieConfirmationDialog()

            builders.forEach { lottieDialog.apply(it) }

            val (dialog, view) = inflateView(
                    context,
                    layoutInflater,
                    R.layout.lottie_dialog_confirmation,
                    lottieDialog.type,
                    R.style.Theme_LottieDialog_DayNight
            )

            return lottieDialog(dialog, view, coroutineScope)
        }
    }
}

//region Builder Only Function

fun lottieConfirmationDialogBuilder(builder: LottieConfirmationDialog.() -> Unit) = builder

//endregion
//region Activity Extension

@Suppress("SpreadOperator")
fun AppCompatActivity.lottieConfirmationDialog(
    priority: Int = Int.MAX_VALUE,
    vararg builders: LottieConfirmationDialog.() -> Unit,
    builder: LottieConfirmationDialog.() -> Unit
) {
    LottieConfirmationDialog.create(
            this,
            lifecycleScope,
            layoutInflater,
            *builders,
            builder
    ).let { showLottieDialog(it, priority) }
}

//endregion
//region Fragment Extension

@Suppress("SpreadOperator")
fun Fragment.lottieConfirmationDialog(
        priority: Int = Int.MAX_VALUE,
        vararg builders: LottieConfirmationDialog.() -> Unit,
        builder: LottieConfirmationDialog.() -> Unit
) {
    LottieConfirmationDialog.create(
            requireContext(),
            viewLifecycleOwner.lifecycleScope,
            layoutInflater,
            *builders,
            builder
    ).let { showLottieDialog(it, priority) }
}

//endregion
//region Animation DSL

fun LottieConfirmationDialog.withAnimation(@RawRes lottieFileRes: Int) {
    if (animation == null) {
        animation = LottieDialogAnimation(lottieFileRes)
    }
    animation!!.fileRes = lottieFileRes
}

fun LottieConfirmationDialog.withAnimation(builder: LottieDialogAnimation.() -> Unit) {
    if (animation == null) {
        animation = LottieDialogAnimation()
    }
    animation!!.apply(builder)
}

fun LottieConfirmationDialog.withoutAnimation() {
    animation = null
}

//endregion
//region Image DSL

fun LottieConfirmationDialog.withImage(@DrawableRes imageRes: Int) {
    if (image == null) {
        image = LottieDialogImage(imageRes)
    }
    image!!.imageRes = imageRes
}

fun LottieConfirmationDialog.withImage(builder: LottieDialogImage.() -> Unit) {
    if (image == null) {
        image = LottieDialogImage()
    }
    image!!.apply(builder)
}

fun LottieConfirmationDialog.withoutImage() {
    image = null
}

//endregion
//region Text DSL

fun LottieConfirmationDialog.withTitle(@StringRes textRes: Int) {
    title.textRes = textRes
}

fun LottieConfirmationDialog.withTitle(text: CharSequence) {
    title.text = text
}

fun LottieConfirmationDialog.withTitle(builder: LottieDialogText.() -> Unit) {
    title.apply(builder)
}

fun LottieConfirmationDialog.withContent(@StringRes textRes: Int) {
    if (content == null) {
        content = LottieDialogText()
    }
    content!!.textRes = textRes
}

fun LottieConfirmationDialog.withContent(text: CharSequence) {
    if (content == null) {
        content = LottieDialogText()
    }
    content!!.text = text
}

fun LottieConfirmationDialog.withContent(builder: LottieDialogText.() -> Unit) {
    if (content == null) {
        content = LottieDialogText()
    }
    content!!.apply(builder)
}

fun LottieConfirmationDialog.withoutContent() {
    content = null
}

//endregion
//region Action Button DSL

fun LottieConfirmationDialog.withPositiveButton(@StringRes textRes: Int) {
    positiveButton.textRes = textRes
}

fun LottieConfirmationDialog.withPositiveButton(text: CharSequence) {
    positiveButton.text = text
}

fun LottieConfirmationDialog.withPositiveButton(builder: LottieDialogButton.() -> Unit) {
    positiveButton.apply(builder)
}

fun LottieConfirmationDialog.withNegativeButton(@StringRes textRes: Int) {
    if (negativeButton == null) {
        negativeButton = LottieDialogButton(textRes = textRes)
    }
    negativeButton!!.textRes = textRes
}

fun LottieConfirmationDialog.withNegativeButton(text: CharSequence) {
    if (negativeButton == null) {
        negativeButton = LottieDialogButton(textRes = android.R.string.cancel, text = text)
    }
    negativeButton!!.text = text
}

fun LottieConfirmationDialog.withNegativeButton(builder: LottieDialogButton.() -> Unit) {
    if (negativeButton == null) {
        negativeButton = LottieDialogButton(textRes = android.R.string.cancel)
    }
    negativeButton!!.apply(builder)
}

fun LottieConfirmationDialog.withoutNegativeButton() {
    negativeButton = null
}

//endregion
//region Cancel Option DSL

fun LottieConfirmationDialog.withCancelOption(builder: LottieDialogCancelOption.() -> Unit) {
    cancelAbility.apply(builder)
}

//endregion
//region Listener DSL

fun LottieConfirmationDialog.onShow(function: (DialogInterface) -> Unit) {
    onShowListener = LottieDialogOnShowListener(function)
}

fun LottieConfirmationDialog.onDismiss(function: (DialogInterface) -> Unit) {
    onDismissListener = LottieDialogOnDismissListener(function)
}

fun LottieConfirmationDialog.onCancel(function: (DialogInterface) -> Unit) {
    onCancelListener = LottieDialogOnCancelListener(function)
}

//endregion