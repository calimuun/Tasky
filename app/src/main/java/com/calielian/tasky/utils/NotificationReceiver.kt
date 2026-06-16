package com.calielian.tasky.utils

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.calielian.tasky.R

class NotificationReceiver: BroadcastReceiver() {
	override fun onReceive(context: Context, intent: Intent) {
		val title = intent.getStringExtra("TITLE") ?: "Tarefa Pendente"
		val description = intent.getStringExtra("DESC") ?: "Você tem uma tarefa agora!"
		val notificationId = intent.getIntExtra("ID", 0)

		val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
		val channelId = "tasky_notifications"

		val notification = NotificationCompat.Builder(context, channelId)
			.setSmallIcon(R.drawable.app_icon_scaled)
			.setContentTitle(title)
			.setContentText(description)
			.setPriority(NotificationCompat.PRIORITY_HIGH)
			.setAutoCancel(true)
			.build()

		manager.notify(notificationId, notification)
	}
}