package com.teenwolf3301.criminalintent.utility

import android.widget.Toast

fun showToast(message: String) {
    Toast.makeText(APP_ACTIVITY, message, Toast.LENGTH_SHORT).show()
}