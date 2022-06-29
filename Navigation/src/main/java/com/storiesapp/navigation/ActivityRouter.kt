@file:JvmName("ActivityRouter")
package com.storiesapp.navigation

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.annotation.AnimRes
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityOptionsCompat
import androidx.fragment.app.Fragment

private const val PACKAGE_NAME = "com.android.storiesapp"
private const val FEATURES = "com.storiesapp.features"
fun Context.intentTo(addressableActivity: AddressableActivity): Intent {
    return Intent(Intent.ACTION_VIEW)
            .setClassName(this, addressableActivity.className)
}

fun Context.startFeature(
    addressableActivity: AddressableActivity,
    enterResId: Int = android.R.anim.fade_in,
    exitResId: Int = android.R.anim.fade_out,
    options: Bundle? = null,
    body: Intent.() -> Unit) {

    val intent = intentTo(addressableActivity)
    intent.body()

    if (options == null) {
        val optionsCompat = ActivityOptionsCompat.makeCustomAnimation(this, enterResId, exitResId)
        ActivityCompat.startActivity(this, intent, optionsCompat.toBundle())
    } else {
        ActivityCompat.startActivity(this, intent, options)
    }
}

fun Context.startFeatureAnimation(
    activity: Activity,
    addressableActivity: AddressableActivity,
    view : View,
    options: Bundle? = null,
    body: Intent.() -> Unit) {

    val intent = intentTo(addressableActivity)
    intent.body()
    val activityOptionsCompat : ActivityOptionsCompat =
        ActivityOptionsCompat.makeCustomAnimation(this , android.R.anim.fade_in , android.R.anim.fade_out)
    startActivity(intent,activityOptionsCompat.toBundle())
}

fun Context.startFeatureForResult(
    activity : Activity,
    addressableActivity: AddressableActivity,
    requestCode : Int ,
    @AnimRes enterResId : Int = android.R.anim.fade_in,
    @AnimRes exitResId : Int = android.R.anim.fade_out,
    options : Bundle? = null ,
    body : Intent.() -> Unit){
    val intent = intentTo(addressableActivity)
    intent.body()
    if(options == null){
        val optionsCompat = ActivityOptionsCompat.makeCustomAnimation(this , enterResId , exitResId)
        ActivityCompat.startActivityForResult(activity , intent , requestCode , optionsCompat.toBundle())
    } else {
        ActivityCompat.startActivityForResult(activity , intent , requestCode , null)
    }
}
interface AddressableActivity {
    /**
     * The activity class name.
     */
    val className: String
}
object Activities {
    object LoginActivity : AddressableActivity {
        override val className = "$FEATURES.auth.LoginActivity"
    }
    object MainActivity : AddressableActivity {
        override val className = "$FEATURES.main.MainActivity"
    }
}