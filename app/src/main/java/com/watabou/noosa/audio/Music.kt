package com.watabou.noosa.audio
import android.media.AudioManager
import android.media.MediaPlayer
import com.watabou.noosa.Game
import java.io.IOException
object Music : MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener {
    private var player: MediaPlayer? = null
    private var lastPlayed: String? = null
    private var lastLooping: Boolean = false
    private var enabled: Boolean = true
    fun play(assetName: String?, looping: Boolean) {
        if (isPlaying() && lastPlayed == assetName) {
            return
        }
        stop()
        lastPlayed = assetName
        lastLooping = looping
        if (!enabled || assetName == null) {
            return
        }
        try {
            val afd = Game.instance!!.assets.openFd(assetName)
            player = MediaPlayer()
            @Suppress("DEPRECATION")
            player!!.setAudioStreamType(AudioManager.STREAM_MUSIC)
            player!!.setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
            player!!.setOnPreparedListener(this)
            player!!.setOnErrorListener(this)
            player!!.isLooping = looping
            player!!.prepareAsync()
        } catch (e: IOException) {
            player!!.release()
            player = null
        }
    }
    fun mute() {
        lastPlayed = null
        stop()
    }
    override fun onPrepared(player: MediaPlayer) {
        player.start()
    }
    override fun onError(mp: MediaPlayer, what: Int, extra: Int): Boolean {
        if (player != null) {
            player!!.release()
            player = null
        }
        return true
    }
    fun pause() {
        if (player != null) {
            player!!.pause()
        }
    }
    fun resume() {
        if (player != null) {
            player!!.start()
        }
    }
    fun stop() {
        if (player != null) {
            player!!.stop()
            player!!.release()
            player = null
        }
    }
    fun volume(value: Float) {
        if (player != null) {
            player!!.setVolume(value, value)
        }
    }
    fun isPlaying(): Boolean {
        return player != null && player!!.isPlaying
    }
    fun enable(value: Boolean) {
        enabled = value
        if (isPlaying() && !value) {
            stop()
        } else if (!isPlaying() && value) {
            play(lastPlayed, lastLooping)
        }
    }
    fun isEnabled(): Boolean {
        return enabled
    }
}
