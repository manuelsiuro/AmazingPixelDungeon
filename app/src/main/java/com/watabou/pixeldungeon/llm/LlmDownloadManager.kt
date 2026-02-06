package com.watabou.pixeldungeon.llm

import com.watabou.noosa.Game
import com.watabou.pixeldungeon.PixelDungeon
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.atomic.AtomicLong

object LlmDownloadManager {

    enum class State { IDLE, DOWNLOADING, COMPLETED, FAILED }

    @Volatile
    var state: State = State.IDLE
        private set

    var currentModelId: String? = null
        private set

    private val bytesDownloaded = AtomicLong(0)
    private var totalBytes: Long = 0
    private var downloadThread: Thread? = null

    fun isDownloaded(model: LlmModelInfo): Boolean {
        val dir = modelDir() ?: return false
        val file = File(dir, model.fileName)
        return file.exists() && file.length() > 0
    }

    fun progress(): Float {
        if (totalBytes <= 0) return 0f
        return (bytesDownloaded.get().toFloat() / totalBytes).coerceIn(0f, 1f)
    }

    @Volatile
    var lastError: String? = null
        private set

    fun download(model: LlmModelInfo, token: String? = null) {
        if (state == State.DOWNLOADING) return

        state = State.DOWNLOADING
        currentModelId = model.id
        bytesDownloaded.set(0)
        totalBytes = model.fileSizeMb.toLong() * 1024 * 1024
        lastError = null

        downloadThread = Thread {
            try {
                val dir = modelDir() ?: throw IllegalStateException("Cannot access files directory")
                dir.mkdirs()
                val file = File(dir, model.fileName)
                val tempFile = File(dir, model.fileName + ".tmp")

                val url = URL(model.downloadUrl)
                val conn = url.openConnection() as HttpURLConnection
                conn.connectTimeout = 15000
                conn.readTimeout = 15000
                conn.instanceFollowRedirects = true
                if (!token.isNullOrBlank()) {
                    conn.setRequestProperty("Authorization", "Bearer $token")
                }
                conn.connect()

                val code = conn.responseCode
                if (code == HttpURLConnection.HTTP_UNAUTHORIZED || code == HttpURLConnection.HTTP_FORBIDDEN) {
                    lastError = "Auth required. Check your HuggingFace token and accept the Gemma license at huggingface.co."
                    throw Exception("HTTP $code - authorization failed")
                }
                if (code != HttpURLConnection.HTTP_OK) {
                    lastError = "Download failed: HTTP $code"
                    throw Exception("HTTP $code")
                }

                val contentLength = conn.contentLength.toLong()
                if (contentLength > 0) totalBytes = contentLength

                conn.inputStream.use { input ->
                    FileOutputStream(tempFile).use { output ->
                        val buffer = ByteArray(8192)
                        var read: Int
                        while (input.read(buffer).also { read = it } != -1) {
                            if (state != State.DOWNLOADING) {
                                tempFile.delete()
                                return@Thread
                            }
                            output.write(buffer, 0, read)
                            bytesDownloaded.addAndGet(read.toLong())
                        }
                    }
                }

                tempFile.renameTo(file)
                state = State.COMPLETED
            } catch (e: Exception) {
                if (state == State.DOWNLOADING) {
                    state = State.FAILED
                    PixelDungeon.reportException(e)
                }
            }
        }
        downloadThread?.start()
    }

    fun cancel() {
        if (state == State.DOWNLOADING) {
            state = State.IDLE
            currentModelId = null
        }
    }

    fun deleteModel(model: LlmModelInfo) {
        val dir = modelDir() ?: return
        val file = File(dir, model.fileName)
        file.delete()
        val tempFile = File(dir, model.fileName + ".tmp")
        tempFile.delete()
    }

    fun resetState() {
        if (state != State.DOWNLOADING) {
            state = State.IDLE
            currentModelId = null
        }
    }

    fun storageSizeMb(): Float {
        val dir = modelDir() ?: return 0f
        if (!dir.exists()) return 0f
        var total = 0L
        dir.listFiles()?.forEach { total += it.length() }
        return total / (1024f * 1024f)
    }

    fun modelPath(model: LlmModelInfo): String? {
        val dir = modelDir() ?: return null
        val file = File(dir, model.fileName)
        return if (file.exists()) file.absolutePath else null
    }

    private fun modelDir(): File? {
        val context = Game.instance ?: return null
        return File(context.filesDir, LlmConfig.MODEL_DIR)
    }
}
