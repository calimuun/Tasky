package com.calimuun.tasky.utils

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.calimuun.tasky.database.RoutineEntity
import com.calimuun.tasky.database.TaskEntity
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime

/*
* This object is responsible for scheduling notifications
* */
object AlarmScheduler {

	private const val ACTION_TASK = "com.calimuun.tasky.TASK_NOTIFICATION"
	private const val ACTION_ROUTINE = "com.calimuun.tasky.ROUTINE_NOTIFICATION"

	fun scheduleTask(context: Context, task: TaskEntity) {
		val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

		// if the user is on Android 12+, verifies if the app can schedule exact alarms (has alarms permission)
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
			if (!alarmManager.canScheduleExactAlarms()) {
				return
			}
		}

		val intent = Intent(context, NotificationReceiver::class.java).apply {
			action = ACTION_TASK
			putExtra("ID", task.id)
			putExtra("TITLE", task.title)
			putExtra("DESC", task.description)
		}

		val pendingIntent = PendingIntent.getBroadcast(
			context, task.id, intent,
			PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
		)

		val zonedDateTime = LocalDateTime.of(task.date, task.time).atZone(ZoneId.systemDefault())

		if (zonedDateTime.isBefore(ZonedDateTime.now())) return

		val timeInMillis = zonedDateTime.toInstant().toEpochMilli()

		try {
			alarmManager.setExactAndAllowWhileIdle(
				AlarmManager.RTC_WAKEUP,
				timeInMillis,
				pendingIntent
			)
		} catch (e: SecurityException) {
			e.printStackTrace()
		}
	}

	fun scheduleRoutine(context: Context, routine: RoutineEntity) {
		val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
			if (!alarmManager.canScheduleExactAlarms()) {
				return
			}
		}

		val intent = Intent(context, NotificationReceiver::class.java).apply {
			action = ACTION_ROUTINE
			putExtra("ID", routine.id)
			putExtra("TITLE", routine.title)
			putExtra("DESC", routine.description)
		}

		val pendingIntent = PendingIntent.getBroadcast(
			context, routine.id, intent,
			PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
		)

		var zonedDateTime = LocalDateTime.of(routine.date, routine.time).atZone(ZoneId.systemDefault())

		if (zonedDateTime.isBefore(ZonedDateTime.now())) zonedDateTime = zonedDateTime.plusDays(1)

		val timeInMillis = zonedDateTime.toInstant().toEpochMilli()

		try {
			alarmManager.setExactAndAllowWhileIdle(
				AlarmManager.RTC_WAKEUP,
				timeInMillis,
				pendingIntent
			)
		} catch (e: SecurityException) {
			e.printStackTrace()
		}
	}

	fun cancelTask(context: Context, taskId: Int) {
		val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

		val intent = Intent(context, NotificationReceiver::class.java).apply {
			action = ACTION_TASK
		}

		val pendingIntent = PendingIntent.getBroadcast(
			context, taskId, intent,
			PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
		)

		if (pendingIntent != null) {
			alarmManager.cancel(pendingIntent)
			pendingIntent.cancel()
		}
	}

	fun cancelRoutine(context: Context, routineId: Int) {
		val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

		val intent = Intent(context, NotificationReceiver::class.java).apply {
			action = ACTION_ROUTINE
		}

		val pendingIntent = PendingIntent.getBroadcast(
			context, routineId, intent,
			PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
		)

		if (pendingIntent != null) {
			alarmManager.cancel(pendingIntent)
			pendingIntent.cancel()
		}
	}
}