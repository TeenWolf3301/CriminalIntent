package com.teenwolf3301.criminalintent.ui.screens.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import coil.load
import com.teenwolf3301.criminalintent.databinding.PhotoPreviewDialogBinding

class PhotoPreviewFragment() : DialogFragment() {

    private var imageUri = ""
    private var _binding: PhotoPreviewDialogBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        imageUri = arguments?.getString("uri") ?: ""
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = PhotoPreviewDialogBinding.inflate(inflater, container, false)
        dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)
        binding.apply {
            ivPhotoPreview.load(imageUri) {
                crossfade(true)
            }
            btnClose.setOnClickListener {
                dialog?.dismiss()
            }
        }
        return binding.root
    }

    override fun onStart() {
        super.onStart()

        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        fun newInstance(imageUri: String): PhotoPreviewFragment = PhotoPreviewFragment().apply {
            arguments = Bundle().apply {
                putString("uri", imageUri)
            }
        }
    }
}