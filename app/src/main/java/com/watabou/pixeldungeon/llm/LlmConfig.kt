package com.watabou.pixeldungeon.llm

object LlmConfig {
    const val DEFAULT_MAX_TOKENS = 64
    const val DEFAULT_TEMPERATURE = 0.8f
    const val DEFAULT_TOP_K = 40
    const val INFERENCE_TIMEOUT_MS = 5000L
    const val CACHE_TTL_MS = 7L * 24 * 60 * 60 * 1000  // 7 days
    const val MAX_CACHE_ENTRIES = 1000
    const val MODEL_DIR = "llm_models"
    const val CACHE_DIR = "llm_cache"

    val AVAILABLE_MODELS = listOf(
        LlmModelInfo(
            id = "gemma-1b-4bit",
            displayName = "Gemma 3 1B (Recommended)",
            description = "557 MB - Fast, good quality",
            downloadUrl = "https://huggingface.co/litert-community/Gemma3-1B-IT/resolve/main/gemma3-1b-it-int4.task",
            fileSizeMb = 557,
            fileName = "gemma3-1b-it-int4.task"
        )
    )
}
