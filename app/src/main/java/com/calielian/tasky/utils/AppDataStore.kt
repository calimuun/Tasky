package com.calielian.tasky.utils

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
}