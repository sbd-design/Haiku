package com.haiku.app.api

import javax.inject.Inject
import javax.inject.Singleton

data class NotificationInput(
    val appName: String,
    val sender: String?,
    val text: String
)

data class GeneratedHaiku(
    val line1: String,
    val line2: String,
    val line3: String
)

@Singleton
class HaikuGenerator @Inject constructor(
    private val apiServiceProvider: ClaudeApiServiceProvider
) {
    companion object {
        private const val SYSTEM_PROMPT = """You are a haiku poet. Convert phone notifications into haikus with exactly 5-7-5 syllables across exactly 3 lines. Be poetic and evocative — capture the feeling, not just the content. Abstract the mundane into something beautiful. Reply with exactly 3 lines of the haiku and nothing else — no title, no explanation, no punctuation beyond what the poem needs."""
    }

    suspend fun generate(input: NotificationInput): GeneratedHaiku? {
        return try {
            val userText = buildString {
                append("App: ${input.appName}")
                if (!input.sender.isNullOrBlank()) append(" | From: ${input.sender}")
                append(" | Message: ${input.text}")
            }

            val response = apiServiceProvider.get().createMessage(
                ClaudeRequest(
                    system = SYSTEM_PROMPT,
                    messages = listOf(ClaudeMessage(role = "user", content = userText))
                )
            )

            parseHaiku(response.text())
        } catch (e: Exception) {
            null
        }
    }

    private fun parseHaiku(text: String): GeneratedHaiku? {
        val lines = text.lines().map { it.trim() }.filter { it.isNotBlank() }
        if (lines.size < 3) return null
        return GeneratedHaiku(
            line1 = lines[0],
            line2 = lines[1],
            line3 = lines[2]
        )
    }
}
