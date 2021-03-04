package com.teenwolf3301.criminalintent.utility

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Point
import android.widget.Toast
import com.teenwolf3301.criminalintent.R
import com.teenwolf3301.criminalintent.ui.screens.crime.CrimeFragment
import java.util.*
import kotlin.math.roundToInt

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

fun getScaledBitmap(path: String, destWidth: Int, destHeight: Int): Bitmap {
    var options = BitmapFactory.Options()
    options.inJustDecodeBounds = true
    BitmapFactory.decodeFile(path, options)

    val srcWidth = options.outWidth.toFloat()
    val srcHeight = options.outHeight.toFloat()

    var inSampleSize = 1
    if (srcHeight > destHeight || srcWidth > destWidth) {
        val heightScale = srcHeight / destHeight
        val widthScale = srcWidth / destWidth

        val sampleScale = if (heightScale > widthScale) heightScale else widthScale
        inSampleSize = sampleScale.roundToInt()
    }

    options = BitmapFactory.Options()
    options.inSampleSize = inSampleSize

    return BitmapFactory.decodeFile(path, options)
}

fun getScaledBitmap(path: String, activity: Activity): Bitmap {
    val size = Point()
    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
        activity.display!!.getRealSize(size)
    } else {
        @Suppress("DEPRECATION")
        activity.windowManager.defaultDisplay.getRealSize(size)
    }

    return getScaledBitmap(path, size.x, size.y)
}