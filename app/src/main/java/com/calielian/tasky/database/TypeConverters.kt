package com.calielian.tasky.database

import androidx.room.TypeConverter
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class TypeConverters {

	private val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE
	private val timeFormatter = DateTimeFormatter.ISO_LOCAL_TIME

	// Conversores para LocalDate
	@TypeConverter
	fun fromLocalDate(value: String?): LocalDate? {
		return value?.let { LocalDate.parse(it, dateFormatter) }
	}

	@TypeConverter
	fun toLocalDate(date: LocalDate?): String? {
		return date?.format(dateFormatter)
	}

	// Conversores para LocalTime
	@TypeConverter
	fun fromLocalTime(value: String?): LocalTime? {
		return value?.let { LocalTime.parse(it, timeFormatter) }
	}

	@TypeConverter
	fun toLocalTime(time: LocalTime?): String? {
		return time?.format(timeFormatter)
	}
}