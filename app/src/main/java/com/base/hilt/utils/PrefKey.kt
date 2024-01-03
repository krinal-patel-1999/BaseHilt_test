package com.base.hilt.utils

import com.base.hilt.BuildConfig


/**
 * PrefKeys Name
 */
object PrefKey {
    const val PREFERENCE_NAME = BuildConfig.APPLICATION_ID
    const val EN_CODE = "en"
    const val AR_CODE = "ar"
    const val SELECTED_LANGUAGE = "selected_language"

    const val ACCESS_TOKEN = "access_token"
    const val ID = "id"
    const val EMAIL = "email"
    const val IS_LOGGED_IN = "isLoggedIn"
}
