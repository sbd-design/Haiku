package com.haiku.app.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import com.haiku.app.data.db.HaikuEntity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HaikuCard(
    haiku: HaikuEntity,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = haiku.line1,
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = haiku.line2,
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = haiku.line3,
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "${haiku.appName}  ·  ${formatTime(haiku.timestamp)}",
                style = MaterialTheme.typography.labelSmall,
                fontStyle = FontStyle.Italic,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun LargeHaikuDisplay(
    haiku: HaikuEntity,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.padding(horizontal = 32.dp)) {
        Text(
            text = haiku.line1,
            style = MaterialTheme.typography.displayMedium
        )
        Text(
            text = haiku.line2,
            style = MaterialTheme.typography.displayMedium
        )
        Text(
            text = haiku.line3,
            style = MaterialTheme.typography.displayMedium
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = haiku.appName,
            style = MaterialTheme.typography.labelSmall,
            fontStyle = FontStyle.Italic,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

private fun formatTime(timestamp: Long): String {
    val now = System.currentTimeMillis()
    val diff = now - timestamp
    return when {
        diff < 60_000 -> "just now"
        diff < 3_600_000 -> "${diff / 60_000}m ago"
        diff < 86_400_000 -> "${diff / 3_600_000}h ago"
        else -> SimpleDateFormat("MMM d", Locale.getDefault()).format(Date(timestamp))
    }
}
