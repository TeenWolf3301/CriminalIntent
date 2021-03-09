package com.teenwolf3301.criminalintent.ui.screens.datepicker

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.DatePicker
import androidx.fragment.app.DialogFragment
import com.teenwolf3301.criminalintent.utility.ARG_DATE
import com.teenwolf3301.criminalintent.utility.ARG_REQUEST_CODE
import com.teenwolf3301.criminalintent.utility.REQUEST_DATE
import java.util.*

class DatePickerFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val dateListener =
            DatePickerDialog.OnDateSetListener { _: DatePicker, year: Int, month: Int, day: Int ->
                val resultDate: Date = GregorianCalendar(year, month, day).time

                val result = Bundle().apply {
                    putSerializable(REQUEST_DATE, resultDate)
                }

                val resultRequestCode = requireArguments().getString(ARG_REQUEST_CODE, "")
                parentFragmentManager.setFragmentResult(resultRequestCode, result)
            }


        val date = arguments?.getSerializable(ARG_DATE) as Date
        val calendar = Calendar.getInstance()
        calendar.time = date
        val initialYear = calendar.get(Calendar.YEAR)
        val initialMonth = calendar.get(Calendar.MONTH)
        val initialDay = calendar.get(Calendar.DAY_OF_MONTH)

        return DatePickerDialog(
            requireContext(),
            dateListener,
            initialYear, initialMonth, initialDay
        )
    }

    companion object {
        fun newInstance(date: Date, requestCode: String): DatePickerFragment {
            val args = Bundle().apply {
                putSerializable(ARG_DATE, date)
                putString(ARG_REQUEST_CODE, requestCode)
            }

            return DatePickerFragment().apply {
                arguments = args
            }
        }

        fun getSelectedDate(result: Bundle) = result.getSerializable(REQUEST_DATE) as Date
    }
}