package com.calielian.tasky

import android.app.Application
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.PredictiveBackControl
import com.calielian.tasky.database.AppDatabase
import com.google.android.material.color.DynamicColors

class App: Application() {
	val database: AppDatabase by lazy { AppDatabase.getDatabase(this) }

	@OptIn(PredictiveBackControl::class)
	override fun onCreate() {
		super.onCreate()
		DynamicColors.applyToActivitiesIfAvailable(this)
		FragmentManager.enablePredictiveBack(true)
	}
}