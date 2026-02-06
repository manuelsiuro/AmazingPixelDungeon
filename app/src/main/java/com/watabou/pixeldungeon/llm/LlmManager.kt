package com.watabou.pixeldungeon.llm

import android.app.ActivityManager
import android.content.Context
import com.google.mediapipe.tasks.genai.llminference.LlmInference
import com.watabou.noosa.Game
import com.watabou.pixeldungeon.PixelDungeon
import java.util.concurrent.Executors

object LlmManager {

    enum class ModelState { NOT_DOWNLOADED, DOWNLOADED, LOADING, READY, ERROR }

    @Volatile
    var state: ModelState = ModelState.NOT_DOWNLOADED
        private set

    private var inference: LlmInference? = null
    private val executor = Executors.newSingleThreadExecutor()

    private const val MIN_RAM_MB = 3072 // 3 GB minimum

    fun isAvailable(): Boolean =
        PixelDungeon.llmEnabled() && state == ModelState.READY

    fun hasEnoughMemory(): Boolean {
        val context = Game.instance ?: return false
        val am = context.getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager ?: return false
        val memInfo = ActivityManager.MemoryInfo()
        am.getMemoryInfo(memInfo)
        val totalMb = memInfo.totalMem / (1024 * 1024)
        return totalMb >= MIN_RAM_MB
    }

    fun checkModelState() {
        val selectedId = PixelDungeon.llmSelectedModel()
        val model = LlmConfig.AVAILABLE_MODELS.find { it.id == selectedId }
        state = when {
            model == null -> ModelState.NOT_DOWNLOADED
            LlmDownloadManager.isDownloaded(model) -> ModelState.DOWNLOADED
            else -> ModelState.NOT_DOWNLOADED
        }
    }

    fun loadModel(onComplete: (Boolean) -> Unit = {}) {
        if (state == ModelState.LOADING || state == ModelState.READY) {
            onComplete(state == ModelState.READY)
            return
        }

        if (!hasEnoughMemory()) {
            state = ModelState.ERROR
            onComplete(false)
            return
        }

        val selectedId = PixelDungeon.llmSelectedModel().ifEmpty {
            LlmConfig.AVAILABLE_MODELS.firstOrNull()?.id ?: ""
        }
        val model = LlmConfig.AVAILABLE_MODELS.find { it.id == selectedId }
        if (model == null || !LlmDownloadManager.isDownloaded(model)) {
            state = ModelState.NOT_DOWNLOADED
            onComplete(false)
            return
        }

        val modelPath = LlmDownloadManager.modelPath(model)
        if (modelPath == null) {
            state = ModelState.NOT_DOWNLOADED
            onComplete(false)
            return
        }

        state = ModelState.LOADING
        executor.submit {
            try {
                val context = Game.instance
                if (context == null) {
                    state = ModelState.ERROR
                    onComplete(false)
                    return@submit
                }

                val options = LlmInference.LlmInferenceOptions.builder()
                    .setModelPath(modelPath)
                    .setMaxTopK(64)
                    .setMaxTokens(1024)
                    .build()

                inference = LlmInference.createFromOptions(context, options)
                state = ModelState.READY

                // Auto-select model if none selected
                if (PixelDungeon.llmSelectedModel().isEmpty()) {
                    PixelDungeon.llmSelectedModel(model.id)
                }

                onComplete(true)
            } catch (e: Exception) {
                state = ModelState.ERROR
                PixelDungeon.reportException(e)
                onComplete(false)
            }
        }
    }

    fun unloadModel() {
        try {
            inference?.close()
        } catch (e: Exception) {
            PixelDungeon.reportException(e)
        }
        inference = null
        if (state == ModelState.READY) {
            state = ModelState.DOWNLOADED
        }
    }

    fun generateText(prompt: String, maxTokens: Int = LlmConfig.DEFAULT_MAX_TOKENS, onResult: (String?) -> Unit) {
        if (!isAvailable()) {
            onResult(null)
            return
        }
        executor.submit {
            val result = generateTextSync(prompt, maxTokens)
            onResult(result)
        }
    }

    fun generateTextSync(prompt: String, maxTokens: Int = LlmConfig.DEFAULT_MAX_TOKENS): String? {
        val llm = inference ?: return null
        return try {
            val result = llm.generateResponse(prompt)
            result?.trim()
        } catch (e: Exception) {
            PixelDungeon.reportException(e)
            null
        }
    }
}
