package com.haiku.app.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

@Singleton
class SettingsDataStore @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        private val API_KEY = stringPreferencesKey("claude_api_key")
        private val BLOCKED_PACKAGES = stringSetPreferencesKey("blocked_packages")
    }

    val apiKey: Flow<String> = context.dataStore.data.map { it[API_KEY] ?: "" }

    val blockedPackages: Flow<Set<String>> = context.dataStore.data.map {
        it[BLOCKED_PACKAGES] ?: emptySet()
    }

    suspend fun setApiKey(key: String) {
        context.dataStore.edit { it[API_KEY] = key }
    }

    suspend fun toggleBlockedPackage(packageName: String, blocked: Boolean) {
        context.dataStore.edit { prefs ->
            val current = prefs[BLOCKED_PACKAGES]?.toMutableSet() ?: mutableSetOf()
            if (blocked) current.add(packageName) else current.remove(packageName)
            prefs[BLOCKED_PACKAGES] = current
        }
    }
}
