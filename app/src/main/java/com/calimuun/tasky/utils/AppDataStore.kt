package com.calimuun.tasky.utils

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore("settings")

class AppDataStore(val context: Context) {

	private val dataStore = context.dataStore

	companion object {
		val IS_FIRST_TIME = booleanPreferencesKey("is_first_time")
		val USERNAME = stringPreferencesKey("username")
		val IS_NOTIFICATION_PERMITTED = booleanPreferencesKey("is_notification_permitted")
		val DEFAULT_ALARM_TIME = stringPreferencesKey("default_alarm_time")
	}

	fun isFirstTime(): Flow<Boolean> = dataStore.data.map { preferences ->
		preferences[IS_FIRST_TIME] ?: true
	}

	suspend fun setFirstTime(value: Boolean) {
		dataStore.edit { preferences ->
			preferences[IS_FIRST_TIME] = value
		}
	}

	fun getUsername(): Flow<String> = dataStore.data.map { preferences ->
		preferences[USERNAME] ?: ""
	}

	suspend fun setUsername(value: String) {
		dataStore.edit { preferences ->
			preferences[USERNAME] = value
		}
	}

	fun isNotificationPermitted(): Flow<Boolean> = dataStore.data.map { preferences ->
		preferences[IS_NOTIFICATION_PERMITTED] ?: false
	}

	suspend fun setNotificationPermitted(value: Boolean) {
		dataStore.edit { preferences ->
			preferences[IS_NOTIFICATION_PERMITTED] = value
		}
	}

	fun getDefaultAlarmTime(): Flow<String> = dataStore.data.map { preferences ->
		preferences[DEFAULT_ALARM_TIME] ?: "09:00"
	}

	suspend fun setDefaultAlarmTime(value: String) {
		dataStore.edit { preferences ->
			preferences[DEFAULT_ALARM_TIME] = value
		}
	}
}