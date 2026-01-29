package com.watabou.noosa.audio
import android.media.AudioManager
import android.media.SoundPool
import com.watabou.noosa.Game
import java.io.IOException
import java.util.HashMap
import java.util.LinkedList
@Suppress("DEPRECATION")
object Sample : SoundPool.OnLoadCompleteListener {
    const val MAX_STREAMS = 8
    private var pool: SoundPool = SoundPool(MAX_STREAMS, AudioManager.STREAM_MUSIC, 0)
    private val ids = HashMap<Any, Int>()
    private var enabled: Boolean = true
    private val loadingQueue = LinkedList<String>()
    fun reset() {
        pool.release()
        pool = SoundPool(MAX_STREAMS, AudioManager.STREAM_MUSIC, 0)
        pool.setOnLoadCompleteListener(this)
        ids.clear()
    }
    fun pause() {
        pool.autoPause()
    }
    fun resume() {
        pool.autoResume()
    }
    fun load(vararg assets: String) {
        for (asset in assets) {
            loadingQueue.add(asset)
        }
        loadNext()
    }
    private fun loadNext() {
        val asset = loadingQueue.poll()
        if (asset != null) {
            if (!ids.containsKey(asset)) {
                try {
                    pool.setOnLoadCompleteListener { _, _, _ -> loadNext() }
                    val manager = Game.instance!!.assets
                    val fd = manager.openFd(asset)
                    val streamID = pool.load(fd, 1)
                    ids[asset] = streamID
                    fd.close()
                } catch (e: IOException) {
                    loadNext()
                } catch (e: NullPointerException) {
                    // Do nothing (stop loading sounds)
                }
            } else {
                loadNext()
            }
        }
    }
    fun unload(src: Any) {
        if (ids.containsKey(src)) {
            pool.unload(ids[src]!!)
            ids.remove(src)
        }
    }
    fun play(id: Any): Int {
        return play(id, 1f, 1f, 1f)
    }
    fun play(id: Any, volume: Float): Int {
        return play(id, volume, volume, 1f)
    }
    fun play(id: Any, leftVolume: Float, rightVolume: Float, rate: Float): Int {
        return if (enabled && ids.containsKey(id)) {
            pool.play(ids[id]!!, leftVolume, rightVolume, 0, 0, rate)
        } else {
            -1
        }
    }
    fun enable(value: Boolean) {
        enabled = value
    }
    fun isEnabled(): Boolean {
        return enabled
    }
    override fun onLoadComplete(soundPool: SoundPool, sampleId: Int, status: Int) {
    }
}
