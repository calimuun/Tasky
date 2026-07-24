package com.calimuun.tasky.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.calimuun.tasky.database.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BootReceiver: BroadcastReceiver() {
	override fun onReceive(context: Context, intent: Intent) {
		if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
			val pendingResult = goAsync()

			CoroutineScope(Dispatchers.IO).launch {
				try {
					val db = AppDatabase.getDatabase(context)
					AlarmScheduler.rescheduleNext(context, db)
				} finally {
					pendingResult.finish()
				}
			}
		}
	}
}