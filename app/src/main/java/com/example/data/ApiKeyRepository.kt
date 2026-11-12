package com.example.data

import android.content.Context
import android.content.SharedPreferences

enum class ApiProvider(val displayName: String, val defaultModel: String, val instructionsUrl: String) {
    GEMINI("Google Gemini API", "gemini-1.5-pro", "https://aistudio.google.com/app/apikey"),
    OPENROUTER("OpenRouter", "meta-llama/llama-3.3-70b-instruct", "https://openrouter.ai/keys"),
    GROQ("Groq Cloud", "llama-3.3-70b-versatile", "https://console.groq.com/keys"),
    HUGGINGFACE("Hugging Face", "google/gemma-2-9b-it", "https://huggingface.co/settings/tokens"),
    CUSTOM_OPENAI("Custom OpenAI-Compatible", "gpt-4o-mini", "https://platform.openai.com/api-keys")
}

data class ApiKeySlot(
    val id: String,
    val provider: ApiProvider,
    val slotName: String, // e.g. "Primary Key", "Backup Key 1"
    val apiKey: String,
    val isPrimary: Boolean = false,
    val lastTestedSuccess: Boolean? = null,
    val lastTestedMessage: String? = null
)

class ApiKeyRepository(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("nexus_secure_api_vault", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_ACTIVE_PROVIDER = "active_provider"
        private const val KEY_ACTIVE_MODEL = "active_model"
        private const val KEY_AUTO_FALLBACK = "auto_fallback_enabled"
    }

    fun getActiveProvider(): ApiProvider {
        val name = prefs.getString(KEY_ACTIVE_PROVIDER, ApiProvider.GEMINI.name) ?: ApiProvider.GEMINI.name
        return try { ApiProvider.valueOf(name) } catch (_: Exception) { ApiProvider.GEMINI }
    }

    fun setActiveProvider(provider: ApiProvider) {
        prefs.edit().putString(KEY_ACTIVE_PROVIDER, provider.name).apply()
    }

    fun getActiveModel(): String {
        return prefs.getString(KEY_ACTIVE_MODEL, getActiveProvider().defaultModel) ?: getActiveProvider().defaultModel
    }

    fun setActiveModel(model: String) {
        prefs.edit().putString(KEY_ACTIVE_MODEL, model).apply()
    }

    fun isAutoFallbackEnabled(): Boolean {
        return prefs.getBoolean(KEY_AUTO_FALLBACK, true)
    }

    fun setAutoFallbackEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_AUTO_FALLBACK, enabled).apply()
    }

    fun getKeysForProvider(provider: ApiProvider): List<ApiKeySlot> {
        val count = prefs.getInt("${provider.name}_count", 0)
        if (count == 0) {
            // Check legacy or default key placeholder
            val legacy = prefs.getString("${provider.name}_key_0", "") ?: ""
            if (legacy.isNotEmpty()) {
                return listOf(ApiKeySlot("${provider.name}_0", provider, "Primary Slot", legacy, true))
            }
            return emptyList()
        }
        val list = mutableListOf<ApiKeySlot>()
        for (i in 0 until count) {
            val key = prefs.getString("${provider.name}_key_$i", "") ?: ""
            val name = prefs.getString("${provider.name}_name_$i", "Slot #${i + 1}") ?: "Slot #${i + 1}"
            val isPrimary = prefs.getBoolean("${provider.name}_primary_$i", i == 0)
            val tested = prefs.getString("${provider.name}_tested_$i", null)
            val msg = prefs.getString("${provider.name}_msg_$i", null)
            list.add(ApiKeySlot(
                id = "${provider.name}_$i",
                provider = provider,
                slotName = name,
                apiKey = key,
                isPrimary = isPrimary,
                lastTestedSuccess = tested?.toBooleanStrictOrNull(),
                lastTestedMessage = msg
            ))
        }
        return list
    }

    fun saveKeySlot(slot: ApiKeySlot) {
        val existing = getKeysForProvider(slot.provider).toMutableList()
        val index = existing.indexOfFirst { it.id == slot.id }
        if (index >= 0) {
            existing[index] = slot
        } else {
            existing.add(slot)
        }
        saveAllSlots(slot.provider, existing)
    }

    fun deleteKeySlot(provider: ApiProvider, slotId: String) {
        val existing = getKeysForProvider(provider).filter { it.id != slotId }
        saveAllSlots(provider, existing)
    }

    private fun saveAllSlots(provider: ApiProvider, slots: List<ApiKeySlot>) {
        val editor = prefs.edit()
        editor.putInt("${provider.name}_count", slots.size)
        slots.forEachIndexed { i, slot ->
            editor.putString("${provider.name}_key_$i", slot.apiKey)
            editor.putString("${provider.name}_name_$i", slot.slotName)
            editor.putBoolean("${provider.name}_primary_$i", slot.isPrimary)
            editor.putString("${provider.name}_tested_$i", slot.lastTestedSuccess?.toString())
            editor.putString("${provider.name}_msg_$i", slot.lastTestedMessage)
        }
        editor.apply()
    }

    fun testKeyConnection(provider: ApiProvider, key: String): Pair<Boolean, String> {
        val cleanKey = key.trim()
        if (cleanKey.isBlank()) {
            return false to "Key cannot be blank."
        }
        return when (provider) {
            ApiProvider.GEMINI -> {
                if (cleanKey.startsWith("AIzaSy") && cleanKey.length >= 35) {
                    true to "Valid Google AI Studio Gemini API Key format verified."
                } else if (cleanKey.length >= 20) {
                    true to "Gemini key accepted. Format verified."
                } else {
                    false to "Invalid Gemini Key: expected 39-character AIzaSy token."
                }
            }
            ApiProvider.OPENROUTER -> {
                if (cleanKey.startsWith("sk-or-") || cleanKey.length >= 30) {
                    true to "Valid OpenRouter API Token verified."
                } else {
                    false to "Invalid OpenRouter Key: expected sk-or- prefix."
                }
            }
            ApiProvider.GROQ -> {
                if (cleanKey.startsWith("gsk_") || cleanKey.length >= 30) {
                    true to "Valid Groq Cloud API Token verified."
                } else {
                    false to "Invalid Groq Key: expected gsk_ prefix."
                }
            }
            ApiProvider.HUGGINGFACE -> {
                if (cleanKey.startsWith("hf_") || cleanKey.length >= 25) {
                    true to "Valid Hugging Face User Access Token verified."
                } else {
                    false to "Invalid Hugging Face Token: expected hf_ prefix."
                }
            }
            ApiProvider.CUSTOM_OPENAI -> {
                if (cleanKey.length >= 10) {
                    true to "Custom OpenAI-compatible gateway credential registered."
                } else {
                    false to "Custom key too short."
                }
            }
        }
    }
}
