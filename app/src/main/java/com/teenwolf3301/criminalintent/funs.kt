package com.teenwolf3301.criminalintent

import com.teenwolf3301.criminalintent.ui.screens.CrimeFragment
import java.util.*

lateinit var APP_ACTIVITY: MainActivity

fun onCrimeSelected(crimeId: UUID) {
    val fragment = CrimeFragment.newInstance(crimeId)
    APP_ACTIVITY.supportFragmentManager
        .beginTransaction()
        .addToBackStack(null)
        .replace(R.id.frame_container, fragment)
        .commit()
}