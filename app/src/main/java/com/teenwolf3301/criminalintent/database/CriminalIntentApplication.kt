package com.teenwolf3301.criminalintent.database

import android.app.Application

class CriminalIntentApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        CrimeRepository.initialize(this)
    }
}