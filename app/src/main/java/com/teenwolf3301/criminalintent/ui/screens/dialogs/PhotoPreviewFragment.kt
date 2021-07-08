package com.teenwolf3301.criminalintent.ui.screens.dialogs

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment

class PhotoPreviewFragment() : DialogFragment() {

    private var imageUri: String = ""

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState)
    }

    companion object {
        fun newInstance(imageUri: String): PhotoPreviewFragment = PhotoPreviewFragment().apply {
            arguments = Bundle().apply {
                putString("uri", imageUri)
            }
        }
    }
}