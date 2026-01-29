package com.watabou.pixeldungeon.scenes
import com.watabou.input.Touchscreen
import com.watabou.noosa.BitmapText
import com.watabou.noosa.BitmapText.Font
import com.watabou.noosa.BitmapTextMultiline
import com.watabou.noosa.Camera
import com.watabou.noosa.ColorBlock
import com.watabou.noosa.Game
import com.watabou.noosa.Scene
import com.watabou.noosa.Visual
import com.watabou.pixeldungeon.Assets
import com.watabou.pixeldungeon.Badges
import com.watabou.pixeldungeon.PixelDungeon
import com.watabou.pixeldungeon.effects.BadgeBanner
import com.watabou.utils.BitmapCache
import javax.microedition.khronos.opengles.GL10
import android.opengl.GLES20
open class PixelScene : Scene() {
    override fun create() {
        super.create()
        GameScene.scene = null
        val minWidth: Float
        val minHeight: Float
        if (PixelDungeon.landscape()) {
            minWidth = MIN_WIDTH_L
            minHeight = MIN_HEIGHT_L
        } else {
            minWidth = MIN_WIDTH_P
            minHeight = MIN_HEIGHT_P
        }
        defaultZoom = Math.ceil(Game.density * 2.5).toFloat()
        while ((Game.width / defaultZoom < minWidth || Game.height / defaultZoom < minHeight) && defaultZoom > 1) {
            defaultZoom--
        }
        if (PixelDungeon.scaleUp()) {
            while (Game.width / (defaultZoom + 1) >= minWidth && Game.height / (defaultZoom + 1) >= minHeight) {
                defaultZoom++
            }
        }
        minZoom = 1f
        maxZoom = defaultZoom * 2
        Camera.reset(PixelCamera(defaultZoom))
        val uiZoom = defaultZoom
        uiCamera = Camera.createFullscreen(uiZoom)
        Camera.add(uiCamera)
        if (font1x == null) {
            // 3x5 (6)
            val fonts1x = BitmapCache.get(Assets.FONTS1X) ?: return
            font1x = Font.colorMarked(fonts1x, 0x00000000, Font.LATIN_FULL)
            font1x?.baseLine = 6f
            font1x?.tracking = -1f
            // 5x8 (10)
            val fonts15x = BitmapCache.get(Assets.FONTS15X) ?: return
            font15x = Font.colorMarked(fonts15x, 12, 0x00000000, Font.LATIN_FULL)
            font15x?.baseLine = 9f
            font15x?.tracking = -1f
            // 6x10 (12)
            val fonts2x = BitmapCache.get(Assets.FONTS2X) ?: return
            font2x = Font.colorMarked(fonts2x, 14, 0x00000000, Font.LATIN_FULL)
            font2x?.baseLine = 11f
            font2x?.tracking = -1f
            // 7x12 (15)
            val fonts25x = BitmapCache.get(Assets.FONTS25X) ?: return
            font25x = Font.colorMarked(fonts25x, 17, 0x00000000, Font.LATIN_FULL)
            font25x?.baseLine = 13f
            font25x?.tracking = -1f
            // 9x15 (18)
            val fonts3x = BitmapCache.get(Assets.FONTS3X) ?: return
            font3x = Font.colorMarked(fonts3x, 22, 0x00000000, Font.LATIN_FULL)
            font3x?.baseLine = 17f
            font3x?.tracking = -2f
        }
    }
    override fun destroy() {
        super.destroy()
        Touchscreen.event.removeAll()
    }
    protected fun fadeIn() {
        if (noFade) {
            noFade = false
        } else {
            fadeIn(0xFF000000.toInt(), false)
        }
    }
    protected fun fadeIn(color: Int, light: Boolean) {
        add(Fader(color, light))
    }
    protected class Fader(color: Int, private val light: Boolean) :
            ColorBlock(uiCamera.width.toFloat(), uiCamera.height.toFloat(), color) {
        private var time: Float = 0.toFloat()
        init {
            camera = uiCamera
            alpha(1f)
            time = FADE_TIME
        }
        override fun update() {
            super.update()
            time -= Game.elapsed
            if (time <= 0) {
                alpha(0f)
                parent?.remove(this)
            } else {
                alpha(time / FADE_TIME)
            }
        }
        override fun draw() {
            if (light) {
                GLES20.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE)
                super.draw()
                GLES20.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA)
            } else {
                super.draw()
            }
        }
        companion object {
            private val FADE_TIME = 1f
        }
    }
    private class PixelCamera(zoom: Float) : Camera(
            (Game.width - Math.ceil((Game.width / zoom).toDouble()) * zoom).toInt() / 2,
            (Game.height - Math.ceil((Game.height / zoom).toDouble()) * zoom).toInt() / 2,
            Math.ceil((Game.width / zoom).toDouble()).toInt(),
            Math.ceil((Game.height / zoom).toDouble()).toInt(), zoom) {
        override fun updateMatrix() {
            val sx = align(this, scroll.x + shakeX)
            val sy = align(this, scroll.y + shakeY)
            matrix[0] = +zoom * invW2
            matrix[5] = -zoom * invH2
            matrix[12] = -1 + x * invW2 - sx * matrix[0]
            matrix[13] = +1 - y * invH2 - sy * matrix[5]
        }
    }
    companion object {
        // Minimum virtual display size for portrait orientation
        const val MIN_WIDTH_P = 128f
        const val MIN_HEIGHT_P = 224f
        // Minimum virtual display size for landscape orientation
        const val MIN_WIDTH_L = 224f
        const val MIN_HEIGHT_L = 160f
        var defaultZoom: Float = 0.toFloat()
        var minZoom: Float = 0.toFloat()
        var maxZoom: Float = 0.toFloat()
        lateinit var uiCamera: Camera
        var font1x: Font? = null
        var font15x: Font? = null
        var font2x: Font? = null
        var font25x: Font? = null
        var font3x: Font? = null
        var font: Font? = null
        var scale: Float = 0.toFloat()
        fun chooseFont(size: Float) {
            chooseFont(size, defaultZoom)
        }
        fun chooseFont(size: Float, zoom: Float) {
            val pt = size * zoom
            if (pt >= 19) {
                scale = pt / 19
                if (1.5f <= scale && scale < 2) {
                    font = font25x
                    scale = (pt / 14).toInt().toFloat()
                } else {
                    font = font3x
                    scale = scale.toInt().toFloat()
                }
            } else if (pt >= 14) {
                scale = pt / 14
                if (1.8f <= scale && scale < 2) {
                    font = font2x
                    scale = (pt / 12).toInt().toFloat()
                } else {
                    font = font25x
                    scale = scale.toInt().toFloat()
                }
            } else if (pt >= 12) {
                scale = pt / 12
                if (1.7f <= scale && scale < 2) {
                    font = font15x
                    scale = (pt / 10).toInt().toFloat()
                } else {
                    font = font2x
                    scale = scale.toInt().toFloat()
                }
            } else if (pt >= 10) {
                scale = pt / 10
                if (1.4f <= scale && scale < 2) {
                    font = font1x
                    scale = (pt / 7).toInt().toFloat()
                } else {
                    font = font15x
                    scale = scale.toInt().toFloat()
                }
            } else {
                font = font1x
                scale = Math.max(1, (pt / 7).toInt()).toFloat()
            }
            scale /= zoom
        }
        fun createText(size: Float): BitmapText {
            return createText(null, size)
        }
        fun createText(text: String?, size: Float): BitmapText {
            chooseFont(size)
            val f = font ?: throw IllegalStateException("Font not initialized")
            val result = BitmapText(text ?: "", f)
            result.scale.set(scale)
            return result
        }
        fun createMultiline(size: Float): BitmapTextMultiline {
            return createMultiline(null, size)
        }
        fun createMultiline(text: String?, size: Float): BitmapTextMultiline {
            chooseFont(size)
            val f = font ?: throw IllegalStateException("Font not initialized")
            val result = BitmapTextMultiline(text ?: "", f)
            result.scale.set(scale)
            return result
        }
        fun align(camera: Camera, pos: Float): Float {
            return (pos * camera.zoom).toInt() / camera.zoom
        }
        // This one should be used for UI elements
        fun align(pos: Float): Float {
            return (pos * defaultZoom).toInt() / defaultZoom
        }
        fun align(v: Visual) {
            val c = v.camera()
            if (c != null) {
                v.x = align(c, v.x)
                v.y = align(c, v.y)
            }
        }
        var noFade = false
        fun showBadge(badge: Badges.Badge) {
            val banner = BadgeBanner.show(badge.image)
            banner.camera = uiCamera
            val cam = banner.camera ?: return
            banner.x = align(cam, (cam.width - banner.width) / 2)
            banner.y = align(cam, (cam.height - banner.height) / 3)
            Game.scene()?.add(banner)
        }
    }
}
