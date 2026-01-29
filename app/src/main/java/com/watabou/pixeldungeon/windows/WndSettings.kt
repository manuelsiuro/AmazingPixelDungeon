package com.watabou.pixeldungeon.windows
import com.watabou.noosa.Camera
import com.watabou.noosa.audio.Sample
import com.watabou.pixeldungeon.Assets
import com.watabou.pixeldungeon.PixelDungeon
import com.watabou.pixeldungeon.scenes.PixelScene
import com.watabou.pixeldungeon.ui.CheckBox
import com.watabou.pixeldungeon.ui.RedButton
import com.watabou.pixeldungeon.ui.Toolbar
import com.watabou.pixeldungeon.ui.Window
class WndSettings(inGame: Boolean) : Window() {
    private var btnZoomOut: RedButton? = null
    private var btnZoomIn: RedButton? = null
    init {
        var btnImmersive: CheckBox? = null
        if (inGame) {
            val w = BTN_HEIGHT
            btnZoomOut = object : RedButton(TXT_ZOOM_OUT) {
                override fun onClick() {
                    Camera.main?.let { zoom(it.zoom - 1) }
                }
            }
            btnZoomOut?.let { add(it.setRect(0f, 0f, w.toFloat(), BTN_HEIGHT.toFloat())) }
            btnZoomIn = object : RedButton(TXT_ZOOM_IN) {
                override fun onClick() {
                    Camera.main?.let { zoom(it.zoom + 1) }
                }
            }
            btnZoomIn?.let { add(it.setRect((WIDTH - w).toFloat(), 0f, w.toFloat(), BTN_HEIGHT.toFloat())) }
            add(object : RedButton(TXT_ZOOM_DEFAULT) {
                override fun onClick() {
                    zoom(PixelScene.defaultZoom)
                }
            }.setRect(
                btnZoomOut?.right() ?: 0f,
                0f,
                (WIDTH - (btnZoomIn?.width() ?: 0f) - (btnZoomOut?.width() ?: 0f)),
                BTN_HEIGHT.toFloat()
            ))
            updateEnabled()
        } else {
            val btnScaleUp = object : CheckBox(TXT_SCALE_UP) {
                override fun onClick() {
                    super.onClick()
                    PixelDungeon.scaleUp(checked())
                }
            }
            btnScaleUp.setRect(0f, 0f, WIDTH.toFloat(), BTN_HEIGHT.toFloat())
            btnScaleUp.checked(PixelDungeon.scaleUp())
            add(btnScaleUp)
            btnImmersive = object : CheckBox(TXT_IMMERSIVE) {
                override fun onClick() {
                    super.onClick()
                    PixelDungeon.immerse(checked())
                }
            }
            btnImmersive.setRect(0f, btnScaleUp.bottom() + GAP, WIDTH.toFloat(), BTN_HEIGHT.toFloat())
            btnImmersive.checked(PixelDungeon.immersed())
            btnImmersive.enable(android.os.Build.VERSION.SDK_INT >= 19)
            add(btnImmersive)
        }
        val btnMusic = object : CheckBox(TXT_MUSIC) {
            override fun onClick() {
                super.onClick()
                PixelDungeon.music(checked())
            }
        }
        btnMusic.setRect(
            0f,
            (if (btnImmersive != null) btnImmersive.bottom() else BTN_HEIGHT.toFloat()) + GAP,
            WIDTH.toFloat(),
            BTN_HEIGHT.toFloat()
        )
        btnMusic.checked(PixelDungeon.music())
        add(btnMusic)
        val btnSound = object : CheckBox(TXT_SOUND) {
            override fun onClick() {
                super.onClick()
                PixelDungeon.soundFx(checked())
                Sample.play(Assets.SND_CLICK)
            }
        }
        btnSound.setRect(0f, btnMusic.bottom() + GAP, WIDTH.toFloat(), BTN_HEIGHT.toFloat())
        btnSound.checked(PixelDungeon.soundFx())
        add(btnSound)
        if (inGame) {
            val btnBrightness = object : CheckBox(TXT_BRIGHTNESS) {
                override fun onClick() {
                    super.onClick()
                    PixelDungeon.brightness(checked())
                }
            }
            btnBrightness.setRect(0f, btnSound.bottom() + GAP, WIDTH.toFloat(), BTN_HEIGHT.toFloat())
            btnBrightness.checked(PixelDungeon.brightness())
            add(btnBrightness)
            val btnQuickslot = object : CheckBox(TXT_QUICKSLOT) {
                override fun onClick() {
                    super.onClick()
                    Toolbar.secondQuickslot(checked())
                }
            }
            btnQuickslot.setRect(0f, btnBrightness.bottom() + GAP, WIDTH.toFloat(), BTN_HEIGHT.toFloat())
            btnQuickslot.checked(Toolbar.secondQuickslot())
            add(btnQuickslot)
            resize(WIDTH, btnQuickslot.bottom().toInt())
        } else {
            val btnOrientation = object : RedButton(orientationText()) {
                override fun onClick() {
                    PixelDungeon.landscape(!PixelDungeon.landscape())
                }
            }
            btnOrientation.setRect(0f, btnSound.bottom() + GAP, WIDTH.toFloat(), BTN_HEIGHT.toFloat())
            add(btnOrientation)
            resize(WIDTH, btnOrientation.bottom().toInt())
        }
    }
    private fun zoom(value: Float) {
        Camera.main?.zoom(value)
        PixelDungeon.zoom((value - PixelScene.defaultZoom).toInt())
        updateEnabled()
    }
    private fun updateEnabled() {
        val zoom = Camera.main?.zoom ?: return
        btnZoomIn?.enable(zoom < PixelScene.maxZoom)
        btnZoomOut?.enable(zoom > PixelScene.minZoom)
    }
    private fun orientationText(): String {
        return if (PixelDungeon.landscape()) TXT_SWITCH_PORT else TXT_SWITCH_LAND
    }
    companion object {
        private const val TXT_ZOOM_IN = "+"
        private const val TXT_ZOOM_OUT = "-"
        private const val TXT_ZOOM_DEFAULT = "Default Zoom"
        private const val TXT_SCALE_UP = "Scale up UI"
        private const val TXT_IMMERSIVE = "Immersive mode"
        private const val TXT_MUSIC = "Music"
        private const val TXT_SOUND = "Sound FX"
        private const val TXT_BRIGHTNESS = "Brightness"
        private const val TXT_QUICKSLOT = "Second quickslot"
        private const val TXT_SWITCH_PORT = "Switch to portrait"
        private const val TXT_SWITCH_LAND = "Switch to landscape"
        private const val WIDTH = 112
        private const val BTN_HEIGHT = 20
        private const val GAP = 2
    }
}
