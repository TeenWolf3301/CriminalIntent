package com.teenwolf3301.criminalintent.ui.screens.crime

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.text.Editable
import android.text.TextWatcher
import android.text.format.DateFormat
import android.view.*
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentResultListener
import androidx.lifecycle.ViewModelProvider
import com.teenwolf3301.criminalintent.R
import com.teenwolf3301.criminalintent.databinding.FragmentCrimeBinding
import com.teenwolf3301.criminalintent.model.Crime
import com.teenwolf3301.criminalintent.model.CrimeViewModel
import com.teenwolf3301.criminalintent.ui.screens.datepicker.DatePickerFragment
import com.teenwolf3301.criminalintent.ui.screens.timepicker.TimePickerFragment
import com.teenwolf3301.criminalintent.utility.*
import java.util.*

class CrimeFragment : Fragment(), FragmentResultListener {

    private lateinit var crime: Crime
    private lateinit var titleField: EditText
    private lateinit var dateButton: Button
    private lateinit var sendReportButton: Button
    private lateinit var pickSuspectButton: Button
    private lateinit var solvedCheckBox: CheckBox

    private var _binding: FragmentCrimeBinding? = null

    private val crimeDetailViewModel: CrimeViewModel by lazy {
        ViewModelProvider(this).get(CrimeViewModel::class.java)
    }
    private val binding get() = _binding!!
    private val resultLauncher = registerForActivityResult(StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            val contactUri: Uri? = data?.data
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

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        crime = Crime()
        val crimeId = arguments?.getSerializable(ARG_CRIME_ID) as UUID
        crimeDetailViewModel.loadCrime(crimeId)

        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        _binding = FragmentCrimeBinding.inflate(layoutInflater, container, false)
        val view = binding.root

        titleField = binding.crimeTitleEditText
        dateButton = binding.crimeDateButton
        sendReportButton = binding.crimeReportButton
        pickSuspectButton = binding.crimeSuspectButton
        solvedCheckBox = binding.crimeSolvedCheckBox

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        crimeDetailViewModel.crimeLiveData.observe(
            viewLifecycleOwner,
            { crimeLD ->
                crimeLD?.let {
                    this.crime = crimeLD
                    updateUI()
                }
            }
        )

        childFragmentManager.setFragmentResultListener(REQUEST_DATE, viewLifecycleOwner, this)
        childFragmentManager.setFragmentResultListener(REQUEST_TIME, viewLifecycleOwner, this)
    }

    override fun onStart() {
        super.onStart()

        APP_ACTIVITY.title = "Crime"

        val titleWatcher = object : TextWatcher {

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                crime.title = s.toString()
            }

            override fun afterTextChanged(s: Editable?) {}
        }

        titleField.addTextChangedListener(titleWatcher)

        dateButton.setOnClickListener { datePickerDialog() }

        solvedCheckBox.apply {
            setOnCheckedChangeListener { _, isChecked ->
                crime.isSolved = isChecked
            }
        }

        sendReportButton.setOnClickListener { sendCrimeReport() }

        pickSuspectButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI)
            resultLauncher.launch(intent)
        }
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
            R.id.save_crime -> {
                updateCrime()
                true
            }
            else -> super.onOptionsItemSelected(item)
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
        titleField.setText(crime.title)
        dateButton.text = crime.date.toString()
        solvedCheckBox.apply {
            isChecked = crime.isSolved
            jumpDrawablesToCurrentState()
        }
        if (crime.suspect.isNotEmpty()) {
            pickSuspectButton.text = crime.suspect
        }
    }

    private fun updateCrime() {
        if (crime.title.isEmpty()) {
            showToast("Fill the title")
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