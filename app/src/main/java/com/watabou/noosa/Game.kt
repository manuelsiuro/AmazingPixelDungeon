package com.watabou.noosa
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.media.AudioManager
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.os.Bundle
import android.os.Vibrator
import android.util.DisplayMetrics
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.View
import com.watabou.glscripts.Script
import com.watabou.gltextures.TextureCache
import com.watabou.input.Keys
import com.watabou.input.Touchscreen
import com.watabou.noosa.audio.Music
import com.watabou.noosa.audio.Sample
import com.watabou.utils.BitmapCache
import com.watabou.utils.SystemTime
import java.util.ArrayList
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10
open class Game(c: Class<out Scene>) : Activity(), GLSurfaceView.Renderer, View.OnTouchListener {
    // Current scene
    var scene: Scene? = null
    // New scene we are going to switch to
    protected var requestedScene: Scene? = null
    // true if scene switch is requested
    protected var requestedReset: Boolean = true
    // New scene class
    var sceneClass: Class<out Scene>? = null
    // Current time in milliseconds
    protected var now: Long = 0
    // Milliseconds passed since previous update
    protected var step: Long = 0
    protected var view: GLSurfaceView? = null
    protected var holder: SurfaceHolder? = null
    // Accumulated touch events
    protected var motionEvents: ArrayList<MotionEvent> = ArrayList()
    // Accumulated key events
    protected var keysEvents: ArrayList<KeyEvent> = ArrayList()
    init {
        sceneClass = c
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        instance = this
        TextureCache.context = this
        BitmapCache.context = this
        val m = DisplayMetrics()
        @Suppress("DEPRECATION")
        windowManager.defaultDisplay.getMetrics(m)
        density = m.density
        try {
            version = packageManager.getPackageInfo(packageName, 0).versionName
        } catch (e: PackageManager.NameNotFoundException) {
            version = "???"
        }
        volumeControlStream = AudioManager.STREAM_MUSIC
        view = GLSurfaceView(this)
        view!!.setEGLContextClientVersion(2)
        view!!.setEGLConfigChooser(false)
        view!!.setRenderer(this)
        view!!.setOnTouchListener(this)
        setContentView(view)
    }
    override fun onResume() {
        super.onResume()
        now = 0
        view!!.onResume()
        Music.resume()
        Sample.resume()
    }
    override fun onPause() {
        super.onPause()
        if (scene != null) {
            scene!!.pause()
        }
        view!!.onPause()
        Script.reset()
        Music.pause()
        Sample.pause()
    }
    override fun onDestroy() {
        super.onDestroy()
        destroyGame()
        Music.mute()
        Sample.reset()
    }
    @SuppressLint("Recycle", "ClickableViewAccessibility")
    override fun onTouch(view: View, event: MotionEvent): Boolean {
        synchronized(motionEvents) {
            motionEvents.add(MotionEvent.obtain(event))
        }
        return true
    }
    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == Keys.VOLUME_DOWN || keyCode == Keys.VOLUME_UP) {
            return false
        }
        synchronized(motionEvents) {
            keysEvents.add(event)
        }
        return true
    }
    override fun onKeyUp(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == Keys.VOLUME_DOWN || keyCode == Keys.VOLUME_UP) {
            return false
        }
        synchronized(motionEvents) {
            keysEvents.add(event)
        }
        return true
    }
    override fun onDrawFrame(gl: GL10?) {
        if (width == 0 || height == 0) {
            return
        }
        SystemTime.tick()
        val rightNow = SystemTime.now
        step = if (now == 0L) 0 else rightNow - now
        now = rightNow
        step()
        NoosaScript.get().resetCamera()
        GLES20.glScissor(0, 0, width, height)
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)
        draw()
    }
    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)
        Game.width = width
        Game.height = height
    }
    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        GLES20.glEnable(GL10.GL_BLEND)
        // For premultiplied alpha:
        // GLES20.glBlendFunc( GL10.GL_ONE, GL10.GL_ONE_MINUS_SRC_ALPHA );
        GLES20.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA)
        GLES20.glEnable(GL10.GL_SCISSOR_TEST)
        TextureCache.reload()
    }
    protected fun destroyGame() {
        if (scene != null) {
            scene!!.destroy()
            scene = null
        }
        instance = null
    }
    protected fun step() {
        if (requestedReset) {
            requestedReset = false
            try {
                requestedScene = sceneClass!!.getDeclaredConstructor().newInstance()
                switchScene()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        update()
    }
    protected fun draw() {
        scene!!.draw()
    }
    protected fun switchScene() {
        Camera.reset()
        if (scene != null) {
            scene!!.destroy()
        }
        scene = requestedScene
        scene!!.create()
        elapsed = 0f
        timeScale = 1f
    }
    protected fun update() {
        elapsed = timeScale * step * 0.001f
        synchronized(motionEvents) {
            Touchscreen.processTouchEvents(motionEvents)
            motionEvents.clear()
        }
        synchronized(keysEvents) {
            Keys.processTouchEvents(keysEvents)
            keysEvents.clear()
        }
        scene!!.update()
        Camera.updateAll()
    }
    companion object {
        var instance: Game? = null
        // Actual size of the screen
        var width: Int = 0
        var height: Int = 0
        // Density: mdpi=1, hdpi=1.5, xhdpi=2...
        var density: Float = 1f
        var version: String? = null
        var timeScale: Float = 1f
        var elapsed: Float = 0f
        fun resetScene() {
            switchScene(instance!!.sceneClass)
        }
        fun switchScene(c: Class<out Scene>?) {
            instance!!.sceneClass = c
            instance!!.requestedReset = true
        }
        fun scene(): Scene? {
            return instance!!.scene
        }
        fun vibrate(milliseconds: Int) {
            @Suppress("DEPRECATION")
            (instance!!.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator).vibrate(milliseconds.toLong())
        }
    }
}
