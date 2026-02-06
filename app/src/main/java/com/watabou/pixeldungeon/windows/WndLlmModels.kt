package com.watabou.pixeldungeon.windows

import com.watabou.noosa.BitmapTextMultiline
import com.watabou.pixeldungeon.PixelDungeon
import com.watabou.pixeldungeon.llm.LlmConfig
import com.watabou.pixeldungeon.llm.LlmDownloadManager
import com.watabou.pixeldungeon.llm.LlmResponseCache
import com.watabou.pixeldungeon.scenes.PixelScene
import com.watabou.pixeldungeon.ui.RedButton
import com.watabou.pixeldungeon.ui.ProgressBar
import com.watabou.pixeldungeon.ui.Window

class WndLlmModels : Window() {

    private val progressBars = mutableMapOf<String, ProgressBar>()
    private val actionButtons = mutableMapOf<String, RedButton>()
    private val statusLabels = mutableMapOf<String, BitmapTextMultiline>()

    init {
        val tfTitle = PixelScene.createMultiline("AI Model Manager", 9f)
        tfTitle.hardlight(TITLE_COLOR)
        tfTitle.maxWidth = WIDTH - MARGIN * 2
        tfTitle.measure()
        tfTitle.x = MARGIN.toFloat()
        tfTitle.y = MARGIN.toFloat()
        add(tfTitle)

        var pos = tfTitle.y + tfTitle.height() + MARGIN * 2

        for (model in LlmConfig.AVAILABLE_MODELS) {
            val tfName = PixelScene.createMultiline(model.displayName, 8f)
            tfName.maxWidth = WIDTH - MARGIN * 2
            tfName.measure()
            tfName.x = MARGIN.toFloat()
            tfName.y = pos
            add(tfName)
            pos += tfName.height() + 2

            val tfDesc = PixelScene.createMultiline(model.description, 6f)
            tfDesc.maxWidth = WIDTH - MARGIN * 2
            tfDesc.measure()
            tfDesc.x = MARGIN.toFloat()
            tfDesc.y = pos
            add(tfDesc)
            pos += tfDesc.height() + 4

            val progressBar = ProgressBar()
            progressBar.setRect(MARGIN.toFloat(), pos, (WIDTH - MARGIN * 2).toFloat(), PROGRESS_HEIGHT.toFloat())
            progressBar.visible = false
            add(progressBar)
            progressBars[model.id] = progressBar
            pos += PROGRESS_HEIGHT + 2

            val isDownloaded = LlmDownloadManager.isDownloaded(model)
            val btn = object : RedButton(if (isDownloaded) "Delete" else "Download") {
                override fun onClick() {
                    onModelAction(model.id)
                }
            }
            btn.setRect(MARGIN.toFloat(), pos, (WIDTH - MARGIN * 2).toFloat(), BTN_HEIGHT.toFloat())
            add(btn)
            actionButtons[model.id] = btn
            pos += BTN_HEIGHT + MARGIN
        }

        // Storage info
        pos += MARGIN
        val storageMb = LlmDownloadManager.storageSizeMb()
        val cacheMb = LlmResponseCache.diskSizeMb()
        val tfStorage = PixelScene.createMultiline("Storage: ${storageMb.toInt()} MB models, ${cacheMb.toInt()} MB cache", 6f)
        tfStorage.maxWidth = WIDTH - MARGIN * 2
        tfStorage.measure()
        tfStorage.x = MARGIN.toFloat()
        tfStorage.y = pos
        add(tfStorage)
        pos += tfStorage.height() + 4

        val btnClearCache = object : RedButton("Clear Cache") {
            override fun onClick() {
                LlmResponseCache.clearAll()
                hide()
                com.watabou.noosa.Game.scene()?.add(WndLlmModels())
            }
        }
        btnClearCache.setRect(MARGIN.toFloat(), pos, (WIDTH - MARGIN * 2).toFloat(), BTN_HEIGHT.toFloat())
        add(btnClearCache)
        pos += BTN_HEIGHT + MARGIN

        resize(WIDTH, pos.toInt())
    }

    private fun onModelAction(modelId: String) {
        val model = LlmConfig.AVAILABLE_MODELS.find { it.id == modelId } ?: return
        if (LlmDownloadManager.isDownloaded(model)) {
            hide()
            com.watabou.noosa.Game.scene()?.add(object : WndOptions(
                "Delete Model",
                "Delete ${model.displayName}? This will free ${model.fileSizeMb} MB.",
                "Delete", "Cancel"
            ) {
                override fun onSelect(index: Int) {
                    if (index == 0) {
                        LlmDownloadManager.deleteModel(model)
                    }
                    com.watabou.noosa.Game.scene()?.add(WndLlmModels())
                }
            })
        } else if (LlmDownloadManager.state == LlmDownloadManager.State.DOWNLOADING) {
            LlmDownloadManager.cancel()
            updateModelUI(modelId)
        } else {
            startDownload(model)
        }
    }

    private fun startDownload(model: com.watabou.pixeldungeon.llm.LlmModelInfo) {
        val token = PixelDungeon.llmHfToken()
        if (token.isBlank()) {
            WndHfToken.show { newToken ->
                LlmDownloadManager.download(model, newToken)
                actionButtons[model.id]?.text("Cancel")
                progressBars[model.id]?.visible = true
            }
        } else {
            LlmDownloadManager.download(model, token)
            actionButtons[model.id]?.text("Cancel")
            progressBars[model.id]?.visible = true
        }
    }

    private fun updateModelUI(modelId: String) {
        val model = LlmConfig.AVAILABLE_MODELS.find { it.id == modelId } ?: return
        val isDownloaded = LlmDownloadManager.isDownloaded(model)
        actionButtons[modelId]?.text(if (isDownloaded) "Delete" else "Download")
        progressBars[modelId]?.visible = false
    }

    override fun update() {
        super.update()
        if (LlmDownloadManager.state == LlmDownloadManager.State.DOWNLOADING) {
            val modelId = LlmDownloadManager.currentModelId
            if (modelId != null) {
                val progress = LlmDownloadManager.progress()
                progressBars[modelId]?.progress(progress)
                progressBars[modelId]?.visible = true
            }
        } else if (LlmDownloadManager.state == LlmDownloadManager.State.COMPLETED ||
            LlmDownloadManager.state == LlmDownloadManager.State.FAILED) {
            val modelId = LlmDownloadManager.currentModelId
            if (modelId != null) {
                updateModelUI(modelId)
                if (LlmDownloadManager.state == LlmDownloadManager.State.COMPLETED) {
                    LlmDownloadManager.resetState()
                } else {
                    val error = LlmDownloadManager.lastError
                    if (error != null && error.contains("Auth")) {
                        // Clear saved token so user is re-prompted
                        PixelDungeon.llmHfToken("")
                    }
                    actionButtons[modelId]?.text("Retry")
                    progressBars[modelId]?.visible = false
                    LlmDownloadManager.resetState()
                }
            }
        }
    }

    companion object {
        private const val WIDTH = 120
        private const val MARGIN = 4
        private const val BTN_HEIGHT = 18
        private const val PROGRESS_HEIGHT = 6
    }
}
