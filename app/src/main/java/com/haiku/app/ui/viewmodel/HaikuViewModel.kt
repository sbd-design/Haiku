package com.haiku.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.haiku.app.api.ClaudeApiServiceProvider
import com.haiku.app.api.ClaudeMessage
import com.haiku.app.api.ClaudeRequest
import com.haiku.app.data.HaikuRepository
import com.haiku.app.data.datastore.SettingsDataStore
import com.haiku.app.data.db.HaikuEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class ApiKeyState {
    object Untested : ApiKeyState()
    object Testing : ApiKeyState()
    object Valid : ApiKeyState()
    data class Invalid(val message: String) : ApiKeyState()
}

@HiltViewModel
class HaikuViewModel @Inject constructor(
    private val repository: HaikuRepository,
    private val settingsDataStore: SettingsDataStore,
    private val apiServiceProvider: ClaudeApiServiceProvider
) : ViewModel() {

    val haikus: StateFlow<List<HaikuEntity>> = repository.allHaikus
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val latestHaiku: StateFlow<HaikuEntity?> = repository.latestHaiku
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    val apiKey: StateFlow<String> = settingsDataStore.apiKey
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), "")

    val blockedPackages: StateFlow<Set<String>> = settingsDataStore.blockedPackages
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptySet())

    private val _apiKeyState = MutableStateFlow<ApiKeyState>(ApiKeyState.Untested)
    val apiKeyState: StateFlow<ApiKeyState> = _apiKeyState.asStateFlow()

    fun saveApiKey(key: String) {
        viewModelScope.launch {
            settingsDataStore.setApiKey(key)
            _apiKeyState.value = ApiKeyState.Untested
        }
    }

    fun testApiKey() {
        viewModelScope.launch {
            _apiKeyState.value = ApiKeyState.Testing
            try {
                apiServiceProvider.get().createMessage(
                    ClaudeRequest(
                        system = "Reply with exactly: ok",
                        messages = listOf(ClaudeMessage("user", "ping")),
                        max_tokens = 10
                    )
                )
                _apiKeyState.value = ApiKeyState.Valid
            } catch (e: Exception) {
                _apiKeyState.value = ApiKeyState.Invalid(e.message ?: "Unknown error")
            }
        }
    }

    fun toggleBlockedPackage(packageName: String, blocked: Boolean) {
        viewModelScope.launch {
            settingsDataStore.toggleBlockedPackage(packageName, blocked)
        }
    }

    fun deleteHaiku(id: Long) {
        viewModelScope.launch {
            repository.deleteHaiku(id)
        }
    }
}
