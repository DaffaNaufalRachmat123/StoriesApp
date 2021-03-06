package com.storiesapp.common.view.model

import android.app.Dialog
import android.widget.FrameLayout
import android.widget.ImageButton
import androidx.annotation.AttrRes
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import androidx.annotation.RawRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.updatePadding
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieDrawable
import com.storiesapp.common.R
import com.storiesapp.common.extension.*

data class LottieDialogAnimation(
        @RawRes
        var fileRes: Int? = null,
        @ColorRes @AttrRes
        var bgColorRes: Int? = null,
        @DimenRes
        var heightRes: Int? = null,
        @DimenRes
        var paddingRes: Int? = null,
        @DimenRes
        var paddingTopRes: Int? = null,
        @DimenRes
        var paddingBottomRes: Int? = null,
        @DimenRes
        var paddingLeftRes: Int? = null,
        @DimenRes
        var paddingRightRes: Int? = null,
        var animationSpeed: Float = 1f,
        @LottieDrawable.RepeatMode
        var repeatMode: Int = LottieDrawable.RESTART,
        var showCloseButton: Boolean = true,
        @ColorRes @AttrRes
        var closeButtonColorRes: Int? = null
) {
    operator fun invoke(
            animationLayout: FrameLayout,
            animationView: LottieAnimationView,
            btnClose: ImageButton? = null,
            dialog: Dialog,
            type: LottieDialogType
    ) {
        fileRes?.let {
            animationView.show()
            animationView.setAnimation(it)
            animationView.speed      = animationSpeed
            animationView.repeatMode = repeatMode
        } ?: run {
            animationView.hide()
            btnClose?.hide()
            return
        }

        bgColorRes?.let {
            animationView.setBackgroundColorRes(it)
            if (type == LottieDialogType.BOTTOM_SHEET) {
                animationView.makeRoundedCornerOnTop(R.dimen.lottie_dialog_corner_radius_bottom_sheet)
            }
        }

        heightRes?.let {
            with(animationLayout.layoutParams as ConstraintLayout.LayoutParams) {
                height = animationLayout.resources.getDimensionPixelSize(it)
                animationLayout.layoutParams = this
            }
        }

        paddingRes?.let {
            animationView.setPaddingRes(it)
        } ?: run {
            with(animationView.resources) {
                animationView.updatePadding(
                        left   = paddingLeftRes?.let { getDimensionPixelSize(it) } ?: 0,
                        top    = paddingTopRes?.let { getDimensionPixelSize(it) } ?: 0,
                        right  = paddingRightRes?.let { getDimensionPixelSize(it) } ?: 0,
                        bottom = paddingBottomRes?.let { getDimensionPixelSize(it) } ?: 0
                )
            }
        }

        btnClose?.visibleOrGone { showCloseButton }
        btnClose?.apply {
            visibleOrGone { showCloseButton }
            closeButtonColorRes?.let { setImageTintList(it) }
            click { dialog.cancel() }
        }
    }
}
