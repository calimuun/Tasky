package com.calimuun.tasky.database

import androidx.room.TypeConverter
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

/*
* This is the custom type converter class defined on AppDatabase
*
* SQLite doesn't have a DATE or DATETIME type, so we need to convert to string and back to LocalDate or LocalTime
* */
class Converters {

	private val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE
	private val timeFormatter = DateTimeFormatter.ISO_LOCAL_TIME

	// LocalDate converters
	@TypeConverter
	fun fromLocalDate(value: String?): LocalDate? {
		return value?.let { LocalDate.parse(it, dateFormatter) }
	}

	@TypeConverter
	fun toLocalDate(date: LocalDate?): String? {
		return date?.format(dateFormatter)
	}

	// LocalTime converters
	@TypeConverter
	fun fromLocalTime(value: String?): LocalTime? {
		return value?.let { LocalTime.parse(it, timeFormatter) }
	}

	@TypeConverter
	fun toLocalTime(time: LocalTime?): String? {
		return time?.format(timeFormatter)
	}
}