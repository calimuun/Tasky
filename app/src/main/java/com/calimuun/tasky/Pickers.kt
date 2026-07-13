package com.calimuun.tasky

import android.text.format.DateFormat
import androidx.fragment.app.Fragment
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneOffset

class Pickers {
	companion object {
		fun showDatePicker(fragment: Fragment, onDateSelected: (LocalDate) -> Unit) {
			val constraintBuilder = CalendarConstraints.Builder()
				.setValidator(DateValidatorPointForward.now())

			val datePicker = MaterialDatePicker.Builder.datePicker()
				.setTitleText(fragment.getString(R.string.date_picker_title))
				.setSelection(MaterialDatePicker.todayInUtcMilliseconds())
				.setCalendarConstraints(constraintBuilder.build())
				.build()

			datePicker.addOnPositiveButtonClickListener { selection ->
				val date = Instant.ofEpochMilli(selection)
					.atZone(ZoneOffset.UTC)
					.toLocalDate()

				onDateSelected(date)
			}

			datePicker.show(fragment.parentFragmentManager, "date_picker")
		}

		fun showTimePicker(fragment: Fragment, defaultTime: LocalTime = LocalTime.now(), onTimeSelected: (LocalTime) -> Unit) {
			val clockFormat = if (DateFormat.is24HourFormat(fragment.requireContext())) TimeFormat.CLOCK_24H else TimeFormat.CLOCK_12H

			val timePicker = MaterialTimePicker.Builder()
				.setTitleText(fragment.getString(R.string.time_picker_title))
				.setTimeFormat(clockFormat)
				.setInputMode(MaterialTimePicker.INPUT_MODE_CLOCK)
				.setHour(defaultTime.hour)
				.setMinute(defaultTime.minute)
				.build()

			timePicker.addOnPositiveButtonClickListener {
				val time = LocalTime.of(timePicker.hour, timePicker.minute)
				onTimeSelected(time)
			}

			timePicker.show(fragment.parentFragmentManager, "time_picker")
		}
	}
}