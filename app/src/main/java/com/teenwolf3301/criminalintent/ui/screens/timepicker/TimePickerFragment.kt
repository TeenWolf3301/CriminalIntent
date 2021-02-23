package com.teenwolf3301.criminalintent.ui.screens.timepicker

import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.TimePicker
import androidx.fragment.app.DialogFragment
import com.teenwolf3301.criminalintent.utility.ARG_DATE
import com.teenwolf3301.criminalintent.utility.ARG_REQUEST_CODE
import com.teenwolf3301.criminalintent.utility.REQUEST_TIME
import java.util.*

class TimePickerFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val date = arguments?.getSerializable(ARG_DATE) as Date
        val calendar = Calendar.getInstance()
        calendar.time = date
        val initialYear = calendar.get(Calendar.YEAR)
        val initialMonth = calendar.get(Calendar.MONTH)
        val initialDay = calendar.get(Calendar.DAY_OF_MONTH)
        val initialHour = calendar.get(Calendar.HOUR_OF_DAY)
        val initialMinute = calendar.get(Calendar.MINUTE)

        val timeListener =
            TimePickerDialog.OnTimeSetListener { _: TimePicker, hour: Int, minute: Int ->
                val resultDate: Date = GregorianCalendar(
                    initialYear,
                    initialMonth,
                    initialDay,
                    hour, minute
                ).time

                val result = Bundle().apply {
                    putSerializable(REQUEST_TIME, resultDate)
                }

                val resultRequestCode = requireArguments().getString(ARG_REQUEST_CODE, "")
                parentFragmentManager.setFragmentResult(resultRequestCode, result)
            }

        return TimePickerDialog(
            requireContext(),
            timeListener,
            initialHour,
            initialMinute,
            true
        )
    }

    companion object {
        fun newInstance(date: Date, requestCode: String): TimePickerFragment {
            val args = Bundle().apply {
                putSerializable(ARG_DATE, date)
                putString(ARG_REQUEST_CODE, requestCode)
            }

            return TimePickerFragment().apply {
                arguments = args
            }
        }

        fun getSelectedTime(result: Bundle) = result.getSerializable(REQUEST_TIME) as Date
    }
}