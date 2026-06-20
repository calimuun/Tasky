package com.calielian.tasky.utils

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.calielian.tasky.database.TaskEntity
import java.time.LocalDateTime
import java.time.ZoneId

object AlarmScheduler {

	fun schedule(context: Context, task: TaskEntity) {
		val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
			if (!alarmManager.canScheduleExactAlarms()) {
				return
			}
		}

		val intent = Intent(context, NotificationReceiver::class.java).apply {
			putExtra("ID", task.id)
			putExtra("TITLE", task.title)
			putExtra("DESC", task.description)
		}

		val pendingIntent = PendingIntent.getBroadcast(
			context, task.id, intent,
			PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
		)

		val zonedDateTime = LocalDateTime.of(task.date, task.time)
			.atZone(ZoneId.systemDefault())
		val timeInMillis = zonedDateTime.toInstant().toEpochMilli()

		// Agendar
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

	fun cancel(context: Context, taskId: Int) {
		// Lógica para cancelar se a tarefa for deletada ou concluída
	}
}