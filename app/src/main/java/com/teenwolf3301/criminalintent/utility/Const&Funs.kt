package com.teenwolf3301.criminalintent.utility

import android.widget.Toast
import com.teenwolf3301.criminalintent.ui.MainActivity

lateinit var APP_ACTIVITY: MainActivity

const val REQUEST_DATE = "DialogDate"
const val REQUEST_TIME = "DialogTime"
const val SHOW_PREVIEW = "PhotoPreview"

const val ARG_DATE = "date"
const val ARG_REQUEST_CODE = "requestCode"

const val DATE_FORMAT = "EEEE, MMM dd, yyyy"

fun showToast(message: String) {
    Toast.makeText(APP_ACTIVITY, message, Toast.LENGTH_SHORT).show()
}