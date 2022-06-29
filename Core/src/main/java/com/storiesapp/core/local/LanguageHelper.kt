package com.storiesapp.core.local

import android.content.Context
import com.storiesapp.core.Constant
import java.util.*

object LanguageHelper {
    fun changeLocale(context: Context, isIndonesia : Boolean) {
        UserSession.isIndonesia = isIndonesia
        val locale = Locale(if(isIndonesia) "in" else "en")
        Locale.setDefault(locale)
        val conf = context.resources.configuration
        conf.setLocale(locale)
        conf.setLayoutDirection(conf.locales[0])
        context.resources.updateConfiguration(conf, context.resources.displayMetrics)
    }
    fun getLanguage(context: Context?): String {
        return if(UserSession.isIndonesia) "ID" else "EN"
    }
}
