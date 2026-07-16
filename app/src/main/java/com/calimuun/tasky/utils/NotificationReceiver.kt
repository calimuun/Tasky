package com.calimuun.tasky.utils

import android.Manifest
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.calimuun.tasky.R

/*
* This class receives the notifications from the AlarmScheduler and builds them to send to the user
* */
class NotificationReceiver: BroadcastReceiver() {
	override fun onReceive(context: Context, intent: Intent) {
		val title = intent.getStringExtra("TITLE")
		val description = intent.getStringExtra("DESC")
		val notificationId = intent.getIntExtra("ID", 0)

		val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
		val channelId = "tasky_notifications"

		// if the user is on Android 13+, verifies if the app has notifications permission
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
			if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
				return
			}
		}

		val notification = NotificationCompat.Builder(context, channelId)
			.setSmallIcon(R.drawable.ic_launcher_foreground)
			.setContentTitle(title)
			.setContentText(description)
			.setPriority(NotificationCompat.PRIORITY_HIGH)
			.setAutoCancel(true)
			.build()

		manager.notify(notificationId, notification)
	}
}