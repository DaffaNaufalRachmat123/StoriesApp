package com.storiesapp.core.local

import com.chibatching.kotpref.KotprefModel

object UserSession : KotprefModel() {
    const val LOGGED_IN = "LOGGED_IN"
    const val AUTH_TOKEN = "AUTH_TOKEN"
    const val USER_CREDENTIALS = "USER_CREDENTIALS"
    const val PAGE_NUMBER = "PAGE_NUMBER"
    const val IS_INDONESIA = "IS_INDONESIA"
    var authToken by stringPref(default = "", key = AUTH_TOKEN)
    var userCredentials by stringPref(default = "", key = USER_CREDENTIALS)
    var isLoggedIn by booleanPref(default = false, key = LOGGED_IN)
    var pageNumber by intPref(default = 1, key = PAGE_NUMBER)
    var isIndonesia by booleanPref(default = true , key = IS_INDONESIA)
    fun login(auth: String, userCredential: String) {
        UserSession.apply {
            isLoggedIn = true
            authToken = auth
            userCredentials = userCredential
        }
    }

    fun logout() {
        UserSession.apply {
            isLoggedIn = false
            authToken = ""
            userCredentials = ""
        }
    }

}