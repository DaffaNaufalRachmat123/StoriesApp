package com.storiesapp.common.extension

import android.app.Activity
import android.app.ActivityManager
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.content.res.Resources
import android.graphics.Outline
import android.os.Build
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.app.ActivityManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.content.getSystemService
import androidx.core.view.setPadding
import androidx.core.widget.ImageViewCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieProperty
import com.airbnb.lottie.model.KeyPath
import com.airbnb.lottie.value.LottieValueCallback
import com.github.ajalt.timberkt.d
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar
import com.storiesapp.common.R
import com.storiesapp.common.view.model.LottieDialogType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.HttpException
import java.net.SocketTimeoutException
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

fun Boolean?.orFalse(): Boolean = this ?: false
fun Throwable.errorMessage(): String {
    val msg: String

    when (this) {
        is HttpException -> {
            d { "Error Type : HttpException" }
            val responseBody = this.response()?.errorBody()
            val code = response()?.code()
            msg = responseBody.getErrorMessage()
            println("HttpException checkApiError onError Code : $code : $msg")

        }
        is SocketTimeoutException -> {
            msg = "Timeout, Coba lagi"
        }
        else -> {
            d { "Error Type : $message"}
            msg = if (message == null || message?.startsWith("Unable to resolve host").orFalse() || message == "null" || message?.startsWith("Failed to connect to").orFalse()
                || message?.startsWith("Write error: ssl").orFalse() || message?.startsWith("Value <!DOCTYPE ").orFalse()
            ) {
                "Tidak ada koneksi jaringan"
            } else {
                message ?: "Terjadi kesalahan, silahkan coba lagi"
            }


        }
    }
    println("ApiOnError : $msg")
    return msg
}

fun ResponseBody?.getErrorMessage(): String {
    return try {
        val jsonObject = JSONObject(this?.string() ?: "")
        println("jsonObjectError : $jsonObject")
        val errorMsg: String = jsonObject.toString()
        return errorMsg
    } catch (e: JSONException) {
        e.message.toString()
    }
}

fun View.snackBar(text: CharSequence, duration: Int = Snackbar.LENGTH_SHORT, init: Snackbar.() -> Unit = {}): Snackbar {
    val snack = Snackbar.make(this, text, duration)
    snack.init()
    snack.show()
    return snack
}

fun Activity.snackBar(text: CharSequence, duration: Int = Snackbar.LENGTH_SHORT, init: Snackbar.() -> Unit = {}): Snackbar {
    val v = this.findViewById<View>(android.R.id.content)
    return v.snackBar(text, duration, init)
}

fun View.hide() {
    visibility = View.GONE
}

fun View.invisible(){
    visibility = View.INVISIBLE
}

fun View.show() {
    visibility = View.VISIBLE
}

fun Intent.clearTask(): Intent = apply { addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK) }

fun Intent.newTask(): Intent = apply { addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) }

fun <T> Collection<T>.toArrayList(): ArrayList<T> {
    return ArrayList(this)
}

val Int.dp: Int
    get() = (this / Resources.getSystem().displayMetrics.density).toInt()

val Float.dp: Float
    get() = (Resources.getSystem().displayMetrics.density * this)

fun Context?.isLowRamDevice(): Boolean {
    val activityManager = this?.getSystemService<ActivityManager>()
    return if (activityManager != null)
        ActivityManagerCompat.isLowRamDevice(activityManager)
    else false
}
inline val Float.dpToPx: Float
    get() = this * Resources.getSystem().displayMetrics.density

inline val Int.dpToPx: Int
    get() = toFloat().dpToPx.toInt()

fun View.changeBackground(context : Context , @DrawableRes resource : Int){
    this.background = ContextCompat.getDrawable(context , resource)
}
private fun createDialog(
    context: Context,
    view: View,
    themeStyle: Int
): Dialog {
    val dialog = Dialog(context, themeStyle)
    dialog.setContentView(view)
    dialog.window?.setBackgroundDrawableResource(R.drawable.lottie_dialog_bg_rounded_corner_dialog)
    val window = dialog.window
    val attributesParams = window!!.attributes
    attributesParams.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND
    attributesParams.dimAmount = 0.5f
    window.attributes = attributesParams
    window.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)

    return dialog
}

private fun createBottomSheet(
    context: Context,
    view: View
): Dialog {
    val theme = R.style.Theme_LottieDialog_BottomSheet_DayNight
    return BottomSheetDialog(context, theme).apply { setContentView(view) }
}

internal fun inflateView(
    context: Context,
    layoutInflater: LayoutInflater,
    @LayoutRes layout: Int,
    dialogType: LottieDialogType,
    theme: Int
): Pair<Dialog, View> {
    @Suppress("InflateParams")
    val view = layoutInflater.inflate(layout, null)
    val dialog = when (dialogType) {
        LottieDialogType.DIALOG -> createDialog(context, view, theme)
        LottieDialogType.BOTTOM_SHEET -> createBottomSheet(context, view)
    }
    return Pair(dialog, view)
}
fun Context.resolveThemeAttribute(@AttrRes resId: Int): TypedValue =
    TypedValue().apply { theme.resolveAttribute(resId, this, true) }

@ColorInt
fun Context.resolveThemeColor(@AttrRes attrId: Int): Int = resolveThemeAttribute(attrId).data

fun View.setBackgroundColorRes(@ColorRes @AttrRes colorRes: Int) {
    setBackgroundColor(context.resolveThemeColor(colorRes))
}
fun View.makeRoundedCornerOnTop(@DimenRes radiusRes: Int) {
    val radius = resources.getDimensionPixelSize(radiusRes)
    outlineProvider = object : ViewOutlineProvider() {
        override fun getOutline(view: View, outline: Outline) {
            outline.setRoundRect(
                0, 0,
                view.width, view.height + radius,
                radius.toFloat()
            )
        }
    }
    clipToOutline = true
}
fun TextView.setTextRes(@StringRes textRes: Int) {
    text = resources.getText(textRes)
}
fun View.visible() {
    visibility = View.VISIBLE
}
fun View.gone() {
    visibility = View.GONE
}
fun View.visibleOrGone(visibleIf: () -> Boolean)      = apply {
    if (visibleIf()) visible() else gone()
}
private var <T : View> T.triggerDelay: Long
    get() = if (getTag(1123461123) != null) getTag(1123461123) as Long else -1
    set(value) {
        setTag(1123461123, value)
    }
private fun <T : View> T.clickEnable(): Boolean {
    var flag = false
    val currentClickTime = System.currentTimeMillis()
    if (currentClickTime - triggerLastTime >= triggerDelay) {
        triggerLastTime = currentClickTime
        flag = true
    }

    return flag
}
private var <T : View> T.triggerLastTime: Long
    get() = if (getTag(1123460103) != null) getTag(1123460103) as Long else 0
    set(value) {
        setTag(1123460103, value)
    }
fun <T : View> T.click(time: Long = 1000, block: (T) -> Unit) {
    triggerDelay = time
    setOnClickListener {
        if (clickEnable()) {
            @Suppress("UNCHECKED_CAST")
            block(it as T)
        }
    }
}
fun View.setPaddingRes(@DimenRes paddingRes: Int) {
    setPadding(resources.getDimensionPixelSize(paddingRes))
}
inline fun View.clearConstraint(function: ConstraintSet.() -> Unit) {
    with(ConstraintSet()) {
        clone(this@clearConstraint as ConstraintLayout)
        function()
        applyTo(this@clearConstraint)
    }
}

@ColorInt
fun Context.getResColor(@ColorRes resId: Int): Int = ContextCompat.getColor(this, resId)
val currentSdk = Build.VERSION.SDK_INT
val isMarshmallow = currentSdk >= Build.VERSION_CODES.M
val isOreoMR1 = currentSdk >= Build.VERSION_CODES.O_MR1
fun View.addNavigationBarPadding() {
    setPadding(
        paddingLeft,
        paddingTop,
        paddingRight,
        paddingBottom + resources.navBarHeightX
    )
}
inline val Resources.navBarHeightX: Int
    get() = with(getIdentifier("navigation_bar_height", "dimen", "android")) {
        if (this > 0) getDimensionPixelSize(this) else 0
    }
inline val Context.windowManager get() = getSystemService(Context.WINDOW_SERVICE) as WindowManager
inline val Context.hasSoftNavigationKeys: Boolean
    get() {
        val display = windowManager.defaultDisplay

        val realMetrics = DisplayMetrics()
        display.getRealMetrics(realMetrics)

        val displayMetrics = DisplayMetrics()
        display.getMetrics(displayMetrics)

        return realMetrics.widthPixels > displayMetrics.widthPixels ||
                realMetrics.heightPixels > displayMetrics.heightPixels
    }
inline val Context.navigationBarHeight
    get() = runCatching {
        resources.getIdentifier(
            "navigation_bar_height",
            "dimen",
            "android"
        ).run {
            resources.getDimensionPixelSize(this)
        }
    }.getOrNull()
fun Context.resolveColor(@ColorRes @AttrRes id: Int) = with(TypedValue()) {
    when {
        theme.resolveAttribute(id, this, true) -> data
        isMarshmallow -> getColor(id)
        else -> getResColor(id)
    }
}
fun LottieAnimationView.setFillTint(color: Int) =
    addValueCallback(KeyPath("**"), LottieProperty.COLOR, LottieValueCallback(color))

fun LottieAnimationView.setStrokeTint(color: Int) =
    addValueCallback(KeyPath("**"), LottieProperty.STROKE_COLOR, LottieValueCallback(color))


fun ImageView.setImageTintList(@ColorRes @AttrRes tintRes: Int) {
    val tintList = ColorStateList.valueOf(context.resolveColor(tintRes))
    ImageViewCompat.setImageTintList(this, tintList)
}
inline var View.constraintMarginStart: Int?
    get() = with(layoutParams as ConstraintLayout.LayoutParams) {
        marginStart
    }
    set(value) {
        with(layoutParams as ConstraintLayout.LayoutParams) {
            marginStart = value ?: 0
            layoutParams = this
        }
    }

inline var View.constraintMarginEnd: Int?
    get() = with(layoutParams as ConstraintLayout.LayoutParams) {
        marginEnd
    }
    set(value) {
        with(layoutParams as ConstraintLayout.LayoutParams) {
            marginEnd = value ?: 0
            layoutParams = this
        }
    }
