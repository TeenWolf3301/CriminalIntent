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
import androidx.activity.addCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentResultListener
import androidx.fragment.app.viewModels
import coil.load
import coil.transform.CircleCropTransformation
import com.teenwolf3301.criminalintent.R
import com.teenwolf3301.criminalintent.databinding.FragmentCrimeBinding
import com.teenwolf3301.criminalintent.model.Crime
import com.teenwolf3301.criminalintent.model.CrimeViewModel
import com.teenwolf3301.criminalintent.ui.screens.datepicker.DatePickerFragment
import com.teenwolf3301.criminalintent.ui.screens.timepicker.TimePickerFragment
import com.teenwolf3301.criminalintent.utility.*
import java.io.File
import java.util.*

class CrimeFragment : Fragment(), FragmentResultListener {

    private lateinit var crime: Crime
    private lateinit var photoFile: File
    private lateinit var photoUri: Uri

    private var _binding: FragmentCrimeBinding? = null

    private val crimeDetailViewModel: CrimeViewModel by viewModels()
    private val binding get() = _binding!!
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
                crime.suspect = suspect
                updateUI()
            }
        }
    private val resultPhotoLauncher =
        registerForActivityResult(ActivityResultContracts.TakePicture()) {
            if (it) updatePhotoView()
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        _binding = FragmentCrimeBinding.inflate(layoutInflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val crimeId = arguments?.getSerializable(ARG_CRIME_ID) as UUID
        crimeDetailViewModel.loadCrime(crimeId)
        APP_ACTIVITY.title = "Crime"

        crimeDetailViewModel.crimeLiveData.observe(viewLifecycleOwner) { crimeLD ->
            crimeLD?.let {
                crime = it
                photoFile = crimeDetailViewModel.getPhotoFile(it)
                photoUri = FileProvider.getUriForFile(
                    requireActivity(),
                    "com.teenwolf3301.criminalintent.fileprovider",
                    photoFile
                )
                updateUI()
            }
        }

        val titleWatcher = object : TextWatcher {

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                crime.title = s.toString()
            }

            override fun afterTextChanged(s: Editable?) {}
        }

        binding.apply {
            crimeTitleEditText.addTextChangedListener(titleWatcher)
            crimeDateBtn.setOnClickListener { datePickerDialog() }
            crimeSolvedCheckBox.apply {
                setOnCheckedChangeListener { _, isChecked ->
                    crime.isSolved = isChecked
                }
            }

            sendCrimeReportBtn.setOnClickListener { sendCrimeReport() }

            // TODO Btn to call a suspect
            pickCrimeSuspectBtn.setOnClickListener { resultContactLauncher.launch(null) }

            crimeImageBtn.setOnClickListener { resultPhotoLauncher.launch(photoUri) }

            crimeSaveBtn.setOnClickListener { updateCrime() }
        }

        childFragmentManager.setFragmentResultListener(REQUEST_DATE, viewLifecycleOwner, this)
        childFragmentManager.setFragmentResultListener(REQUEST_TIME, viewLifecycleOwner, this)

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            parentFragmentManager.popBackStack()
        }

        setHasOptionsMenu(true)
    }

    override fun onFragmentResult(requestKey: String, result: Bundle) {
        when (requestKey) {
            REQUEST_DATE -> {
                crime.date = DatePickerFragment.getSelectedDate(result)
                timePickerDialog()
            }
            REQUEST_TIME -> {
                crime.date = TimePickerFragment.getSelectedTime(result)
                updateUI()
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
                crimeDetailViewModel.deleteCrime(crime)
                if (photoFile.exists()) photoFile.delete()
                parentFragmentManager.popBackStack()
                showToast("Successfully removed ${crime.title} crime")
            }
            setNegativeButton("No") { _, _ -> }
            setTitle("Delete crime?")
            setMessage("Are you sure you want to delete ${crime.title} crime?")
            create().show()
        }
    }

    private fun datePickerDialog() {
        DatePickerFragment
            .newInstance(crime.date, REQUEST_DATE)
            .show(childFragmentManager, REQUEST_DATE)
    }

    private fun timePickerDialog() {
        TimePickerFragment
            .newInstance(crime.date, REQUEST_TIME)
            .show(childFragmentManager, REQUEST_TIME)
    }

    private fun updateUI() {
        binding.apply {
            crimeTitleEditText.setText(crime.title)
            crimeDateBtn.text = crime.date.toString()
            crimeSolvedCheckBox.apply {
                isChecked = crime.isSolved
                jumpDrawablesToCurrentState()
            }
            if (crime.suspect.isNotEmpty()) {
                pickCrimeSuspectBtn.text = crime.suspect
            }
        }
        updatePhotoView()
    }

    private fun updatePhotoView() {
        // TODO Output of the full-size image in DialogFragment
        // TODO Efficient loading of image preview
        if (photoFile.exists()) {
            binding.crimeImage.load(photoUri) {
                crossfade(true)
                crossfade(1000)
                transformations(CircleCropTransformation())
            }
        }
    }

    private fun updateCrime() {
        if (crime.title.isEmpty()) {
            showToast("Fill the title!")
        } else {
            crimeDetailViewModel.saveCrime(crime)
            parentFragmentManager.popBackStack()
            showToast("Crime ${crime.title} updated!")
        }
    }

    private fun createCrimeReport(): String {
        val solvedString = if (crime.isSolved) {
            getString(R.string.crime_report_solved)
        } else getString(R.string.crime_report_unsolved)

        val dateString = DateFormat.format(DATE_FORMAT, crime.date).toString()

        val suspectString = if (crime.suspect.isBlank()) {
            getString(R.string.crime_report_no_suspect)
        } else getString(R.string.crime_report_suspect, crime.suspect)

        return getString(
            R.string.crime_report,
            crime.title,
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

    companion object {
        fun newInstance(crimeId: UUID): CrimeFragment {
            val args = Bundle().apply {
                putSerializable(ARG_CRIME_ID, crimeId)
            }
            return CrimeFragment().apply {
                arguments = args
            }
        }
    }
}