package com.watabou.pixeldungeon.llm

import com.watabou.noosa.Game
import com.watabou.pixeldungeon.PixelDungeon
import java.io.File
import java.security.MessageDigest
import java.util.concurrent.ConcurrentHashMap

object LlmResponseCache {

    private data class CachedResponse(val text: String, val timestamp: Long)

    private val memory = ConcurrentHashMap<String, CachedResponse>()

    fun get(key: String): String? {
        val now = System.currentTimeMillis()

        // Check memory cache first
        memory[key]?.let {
            if (now - it.timestamp < LlmConfig.CACHE_TTL_MS) return it.text
            else memory.remove(key)
        }

        // Check disk cache
        val file = cacheFile(key) ?: return null
        if (file.exists()) {
            val lastModified = file.lastModified()
            if (now - lastModified < LlmConfig.CACHE_TTL_MS) {
                val text = file.readText()
                memory[key] = CachedResponse(text, lastModified)
                return text
            } else {
                file.delete()
            }
        }
        return null
    }

    fun put(key: String, text: String) {
        try {
            val now = System.currentTimeMillis()
            memory[key] = CachedResponse(text, now)

            // Evict if over limit
            if (memory.size > LlmConfig.MAX_CACHE_ENTRIES) {
                val oldest = memory.entries.minByOrNull { it.value.timestamp }
                oldest?.let { memory.remove(it.key) }
            }

            // Write to disk
            val file = cacheFile(key) ?: return
            file.parentFile?.mkdirs()
            file.writeText(text)
        } catch (e: Exception) {
            PixelDungeon.reportException(e)
        }
    }

    fun key(vararg parts: String): String {
        val input = parts.joinToString(":")
        val digest = MessageDigest.getInstance("SHA-256")
        val hash = digest.digest(input.toByteArray())
        return hash.joinToString("") { "%02x".format(it) }.substring(0, 32)
    }

    fun clearAll() {
        memory.clear()
        val dir = cacheDir() ?: return
        if (dir.exists()) {
            dir.listFiles()?.forEach { it.delete() }
        }
    }

    fun diskSizeMb(): Float {
        val dir = cacheDir() ?: return 0f
        if (!dir.exists()) return 0f
        var total = 0L
        dir.listFiles()?.forEach { total += it.length() }
        return total / (1024f * 1024f)
    }

    private fun cacheFile(key: String): File? {
        val dir = cacheDir() ?: return null
        return File(dir, key)
    }

    private fun cacheDir(): File? {
        val context = Game.instance ?: return null
        return File(context.filesDir, LlmConfig.CACHE_DIR)
    }
}
