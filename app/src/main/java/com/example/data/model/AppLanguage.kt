package com.example.data.model

enum class AppLanguage(
    val code: String,
    val displayName: String,
    val flagEmoji: String
) {
    SPANISH("es", "Español", "🇪🇸"),
    ENGLISH("en", "English", "🇺🇸"),
    PORTUGUESE("pt", "Português", "🇧🇷"),
    FRENCH("fr", "Français", "🇫🇷"),
    JAPANESE("ja", "日本語", "🇯P");

    companion object {
        fun fromCode(code: String): AppLanguage =
            entries.find { it.code.equals(code, ignoreCase = true) } ?: SPANISH
    }
}
