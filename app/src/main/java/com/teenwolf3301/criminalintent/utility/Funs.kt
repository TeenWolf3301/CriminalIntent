package com.teenwolf3301.criminalintent.utility

import android.widget.Toast
import com.teenwolf3301.criminalintent.R
import com.teenwolf3301.criminalintent.ui.screens.crime.CrimeFragment
import java.util.*

fun onCrimeSelected(crimeId: UUID) {
    val fragment = CrimeFragment.newInstance(crimeId)
    APP_ACTIVITY.supportFragmentManager
        .beginTransaction()
        .addToBackStack(null)
        .replace(R.id.frame_container, fragment)
        .commit()
}

fun showToast(message: String) {
    Toast.makeText(APP_ACTIVITY, message, Toast.LENGTH_SHORT).show()
}