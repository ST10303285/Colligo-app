package com.varsitycollege.st10303285.colligoapp

import android.app.Application
import com.varsitycollege.st10303285.colligoapp.LocaleHelper

class ColligoApp : Application() {
    override fun onCreate() {
        // apply language early
        LocaleHelper.setLocale(this, LocaleHelper.getSavedLanguage(this))
        super.onCreate()
    }

    // Also override attachBaseContext to wrap base context on some devices (optional)
    override fun attachBaseContext(base: android.content.Context) {
        val localeWrapped = LocaleHelper.setLocale(base, LocaleHelper.getSavedLanguage(base))
        super.attachBaseContext(localeWrapped)
    }
}