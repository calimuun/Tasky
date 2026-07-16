package com.calimuun.tasky

import android.annotation.SuppressLint
import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.PredictiveBackControl
import com.calimuun.tasky.database.AppDatabase
import com.google.android.material.color.DynamicColors

/*
* An Application class is a class that represents the application as a whole
* It is used here for storing an instance of the database,
* initiate Material You (Material 3) integration,
* enable the Predictive Back functionality
* and create a notification channel
* */
class App: Application() {
	val database: AppDatabase
		get() = AppDatabase.getDatabase(this)

	@OptIn(PredictiveBackControl::class)
	override fun onCreate() {
		super.onCreate()
		DynamicColors.applyToActivitiesIfAvailable(this)
		FragmentManager.enablePredictiveBack(true)

		createNotificationChannel(getString(R.string.task_title), getString(R.string.notification_task_channel_description))
	}

	@SuppressLint("ServiceCast")
	private fun createNotificationChannel(name: String, description: String) {
		val channel = NotificationChannel("tasky_notifications", name, NotificationManager.IMPORTANCE_HIGH).apply {
			this.description = description
		}

		(getSystemService(NOTIFICATION_SERVICE) as NotificationManager).createNotificationChannel(channel)
	}

}