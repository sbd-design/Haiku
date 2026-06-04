package com.haiku.app.ui.screens

import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.haiku.app.ui.viewmodel.ApiKeyState
import com.haiku.app.ui.viewmodel.HaikuViewModel

@Composable
fun OnboardingScreen(
    hasNotificationAccess: Boolean,
    onComplete: () -> Unit,
    viewModel: HaikuViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val apiKey by viewModel.apiKey.collectAsStateWithLifecycle()
    val apiKeyState by viewModel.apiKeyState.collectAsStateWithLifecycle()
    var keyInput by remember(apiKey) { mutableStateOf(apiKey) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "haiku",
            style = MaterialTheme.typography.displayLarge,
            fontStyle = FontStyle.Italic
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "every notification,\na poem",
            style = MaterialTheme.typography.bodyMedium,
            fontStyle = FontStyle.Italic,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(56.dp))

        // Step 1: Notification Access
        StepLabel(
            number = "1",
            text = "Allow notification access",
            done = hasNotificationAccess
        )
        Spacer(modifier = Modifier.height(8.dp))
        if (!hasNotificationAccess) {
            OutlinedButton(
                onClick = {
                    context.startActivity(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS))
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Open Settings")
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Step 2: API Key
        StepLabel(
            number = "2",
            text = "Enter your Claude API key",
            done = apiKeyState == ApiKeyState.Valid
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = keyInput,
            onValueChange = { keyInput = it },
            label = { Text("sk-ant-…") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(onDone = {
                viewModel.saveApiKey(keyInput)
                viewModel.testApiKey()
            }),
            modifier = Modifier.fillMaxWidth(),
            isError = apiKeyState is ApiKeyState.Invalid
        )

        if (apiKeyState is ApiKeyState.Invalid) {
            Text(
                text = (apiKeyState as ApiKeyState.Invalid).message,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (apiKeyState == ApiKeyState.Testing) {
            CircularProgressIndicator()
        } else {
            OutlinedButton(
                onClick = {
                    viewModel.saveApiKey(keyInput)
                    viewModel.testApiKey()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Test & Save Key")
            }
        }

        Spacer(modifier = Modifier.height(48.dp))

        Button(
            onClick = onComplete,
            enabled = hasNotificationAccess && apiKeyState == ApiKeyState.Valid,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Begin")
        }
    }
}

@Composable
private fun StepLabel(number: String, text: String, done: Boolean) {
    val color = if (done) MaterialTheme.colorScheme.secondary
    else MaterialTheme.colorScheme.onSurface
    Text(
        text = if (done) "✓ $text" else "$number. $text",
        style = MaterialTheme.typography.bodyMedium,
        color = color,
        modifier = Modifier.fillMaxWidth()
    )
}
