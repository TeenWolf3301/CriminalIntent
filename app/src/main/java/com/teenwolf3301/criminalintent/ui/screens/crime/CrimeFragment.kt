package com.teenwolf3301.criminalintent.ui.screens.crime

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentResultListener
import androidx.lifecycle.ViewModelProvider
import com.teenwolf3301.criminalintent.databinding.FragmentCrimeBinding
import com.teenwolf3301.criminalintent.model.Crime
import com.teenwolf3301.criminalintent.model.CrimeViewModel
import com.teenwolf3301.criminalintent.ui.screens.datepicker.DatePickerFragment
import com.teenwolf3301.criminalintent.utility.ARG_CRIME_ID
import com.teenwolf3301.criminalintent.utility.REQUEST_DATE
import com.teenwolf3301.criminalintent.utility.showToast
import java.util.*

class CrimeFragment : Fragment(), FragmentResultListener {

    private lateinit var crime: Crime
    private lateinit var titleField: EditText
    private lateinit var dateButton: Button
    private lateinit var solvedCheckBox: CheckBox

    private var _binding: FragmentCrimeBinding? = null

    private val crimeDetailViewModel: CrimeViewModel by lazy {
        ViewModelProvider(this).get(CrimeViewModel::class.java)
    }
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        crime = Crime()
        val crimeId = arguments?.getSerializable(ARG_CRIME_ID) as UUID
        crimeDetailViewModel.loadCrime(crimeId)
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
        solvedCheckBox = binding.crimeSolvedCheckBox

        binding.saveButton.setOnClickListener {
            updateCrime()
        }

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

        parentFragmentManager.setFragmentResultListener(REQUEST_DATE, viewLifecycleOwner, this)
    }

    override fun onStart() {
        super.onStart()

        val titleWatcher = object : TextWatcher {

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                crime.title = s.toString()
            }

            override fun afterTextChanged(s: Editable?) {}
        }

        titleField.addTextChangedListener(titleWatcher)

        dateButton.setOnClickListener {
            DatePickerFragment
                .newInstance(crime.date, REQUEST_DATE)
                .show(parentFragmentManager, REQUEST_DATE)
        }

        solvedCheckBox.apply {
            setOnCheckedChangeListener { _, isChecked -> crime.isSolved = isChecked }
        }
    }

    override fun onFragmentResult(requestKey: String, result: Bundle) {
        when (requestKey) {
            REQUEST_DATE -> {
                crime.date = DatePickerFragment.getSelectedDate(result)
                updateUI()
            }
        }
    }

    private fun updateUI() {
        titleField.setText(crime.title)
        dateButton.text = crime.date.toString()
        solvedCheckBox.apply {
            isChecked = crime.isSolved
            jumpDrawablesToCurrentState()
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