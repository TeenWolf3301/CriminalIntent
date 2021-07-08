package com.teenwolf3301.criminalintent.ui.screens.crime

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.text.Editable
import android.text.TextWatcher
import android.text.format.DateFormat
import android.view.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentResultListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import coil.load
import coil.transform.CircleCropTransformation
import com.teenwolf3301.criminalintent.R
import com.teenwolf3301.criminalintent.databinding.FragmentCrimeBinding
import com.teenwolf3301.criminalintent.model.CrimeViewModel
import com.teenwolf3301.criminalintent.ui.screens.dialogs.DatePickerFragment
import com.teenwolf3301.criminalintent.ui.screens.dialogs.TimePickerFragment
import com.teenwolf3301.criminalintent.utility.*
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.util.*

@AndroidEntryPoint
class CrimeFragment : Fragment(R.layout.fragment_crime), FragmentResultListener {

    private lateinit var photoFile: File
    private lateinit var photoUri: Uri

    private var _binding: FragmentCrimeBinding? = null
    private val binding get() = _binding!!
    private val crimeDetailViewModel: CrimeViewModel by viewModels()
    private val resultContactLauncher =
        registerForActivityResult(ActivityResultContracts.PickContact()) { contactUri ->
            val queryFields = arrayOf(ContactsContract.Contacts.DISPLAY_NAME)
            val cursor = requireActivity().contentResolver
                .query(contactUri!!, queryFields, null, null, null)
            cursor?.use {
                if (it.count == 0) {
                    return@use
                }

                it.moveToFirst()
                val suspect = it.getString(0)
                crimeDetailViewModel.crimeSuspect = suspect
                binding.pickCrimeSuspectBtn.text = crimeDetailViewModel.crimeSuspect
            }
        }
    private val resultPhotoLauncher =
        registerForActivityResult(ActivityResultContracts.TakePicture()) {
            if (it) binding.crimeImage.load(photoUri) {
                crossfade(true)
                crossfade(1000)
                transformations(CircleCropTransformation())
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCrimeBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val filesDir = requireActivity().applicationContext.filesDir

        val titleWatcher = object : TextWatcher {

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                crimeDetailViewModel.crimeTitle = s.toString()
            }

            override fun afterTextChanged(s: Editable?) {}
        }

        binding.apply {
            crimeTitleEditText.setText(crimeDetailViewModel.crimeTitle)
            crimeTitleEditText.addTextChangedListener(titleWatcher)

            val path = crimeDetailViewModel.crimePhotoFileName
            photoFile = File(filesDir, path)
            photoUri = FileProvider.getUriForFile(
                requireActivity(),
                "com.teenwolf3301.criminalintent.fileprovider",
                photoFile
            )
            if (photoFile.exists()) {
                crimeImage.apply {
                    load(photoUri) {
                        crossfade(true)
                        crossfade(1000)
                        transformations(CircleCropTransformation())
                    }
                    setOnClickListener {
                        showPreviewPhotoDialog()
                    }
                }
            }

            crimeImageBtn.setOnClickListener {
                resultPhotoLauncher.launch(photoUri)
            }

            crimeDateBtn.apply{
                text = crimeDetailViewModel.crimeDate.toString()
                setOnClickListener { showDatePickerDialog() }
            }

            crimeSolvedCheckBox.apply {
                isChecked = crimeDetailViewModel.crimeSolved
                jumpDrawablesToCurrentState()
                setOnCheckedChangeListener { _, isChecked ->
                    crimeDetailViewModel.crimeSolved = isChecked
                }
            }

            pickCrimeSuspectBtn.apply {
                setOnClickListener { resultContactLauncher.launch(null) }
                text =
                    if (crimeDetailViewModel.crimeSuspect.isBlank()) "Choose suspect"
                    else crimeDetailViewModel.crimeSuspect
            }

            sendCrimeReportBtn.setOnClickListener { sendCrimeReport() }

            crimeSaveBtn.setOnClickListener {
                if (crimeDetailViewModel.crimeTitle.isBlank()) {
                    showToast("Fill the title!")
                } else {
                    crimeDetailViewModel.onSaveClick()
                    findNavController().popBackStack()
                }
            }
        }

        childFragmentManager.setFragmentResultListener(REQUEST_DATE, viewLifecycleOwner, this)
        childFragmentManager.setFragmentResultListener(REQUEST_TIME, viewLifecycleOwner, this)

        // Show delete item in menu only in Crime Update
        if (crimeDetailViewModel.crime != null) setHasOptionsMenu(true)
    }

    override fun onFragmentResult(requestKey: String, result: Bundle) {
        when (requestKey) {
            REQUEST_DATE -> {
                crimeDetailViewModel.crimeDate = DatePickerFragment.getSelectedDate(result)
                showTimePickerDialog()
            }
            REQUEST_TIME -> {
                crimeDetailViewModel.crimeDate = TimePickerFragment.getSelectedTime(result)
                binding.crimeDateBtn.text = crimeDetailViewModel.crimeDate.toString()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.fragment_crime_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.delete_crime -> {
                deleteCrime()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun deleteCrime() {
        val builder = AlertDialog.Builder(requireContext())
        builder.apply {
            setPositiveButton("Yes") { _, _ ->
                crimeDetailViewModel.onDeleteClick()
                if (photoFile.exists()) photoFile.delete()
                findNavController().popBackStack()
                showToast("Successfully removed ${crimeDetailViewModel.crimeTitle} crime")
            }
            setNegativeButton("No") { _, _ -> }
            setTitle("Delete crime?")
            setMessage("Are you sure you want to delete ${crimeDetailViewModel.crimeTitle} crime?")
            create().show()
        }
    }

    private fun showPreviewPhotoDialog() {
        // TODO
    }

    private fun showDatePickerDialog() {
        DatePickerFragment
            .newInstance(crimeDetailViewModel.crimeDate, REQUEST_DATE)
            .show(childFragmentManager, REQUEST_DATE)
    }

    private fun showTimePickerDialog() {
        TimePickerFragment
            .newInstance(crimeDetailViewModel.crimeDate, REQUEST_TIME)
            .show(childFragmentManager, REQUEST_TIME)
    }

    private fun createCrimeReport(): String {
        val solvedString = if (crimeDetailViewModel.crimeSolved) {
            getString(R.string.crime_report_solved)
        } else getString(R.string.crime_report_unsolved)

        val dateString = DateFormat.format(DATE_FORMAT, crimeDetailViewModel.crimeDate).toString()

        val suspectString = if (crimeDetailViewModel.crimeSuspect.isBlank()) {
            getString(R.string.crime_report_no_suspect)
        } else getString(R.string.crime_report_suspect, crimeDetailViewModel.crimeSuspect)

        return getString(
            R.string.crime_report,
            crimeDetailViewModel.crimeTitle,
            dateString,
            solvedString,
            suspectString
        )
    }

    private fun sendCrimeReport() {
        Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, createCrimeReport())
            putExtra(Intent.EXTRA_SUBJECT, getString(R.string.crime_report_subject))
        }.also {
            startActivity(it)
        }
    }
}