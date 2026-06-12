package com.haiku.app.api

data class ClaudeMessage(
    val role: String,
    val content: String
)

data class ClaudeRequest(
    val model: String = "claude-haiku-4-5-20251001",
    val max_tokens: Int = 80,
    val system: String,
    val messages: List<ClaudeMessage>
)

data class ContentBlock(
    val type: String,
    val text: String
)

data class ClaudeResponse(
    val content: List<ContentBlock>
) {
    fun text(): String = content.firstOrNull { it.type == "text" }?.text?.trim() ?: ""
}
