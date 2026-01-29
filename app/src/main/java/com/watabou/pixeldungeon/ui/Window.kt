package com.watabou.pixeldungeon.ui
import com.watabou.input.Keys
import com.watabou.input.Touchscreen.Touch
import com.watabou.noosa.Camera
import com.watabou.noosa.Game
import com.watabou.noosa.Group
import com.watabou.noosa.NinePatch
import com.watabou.noosa.TouchArea
import com.watabou.pixeldungeon.Chrome
import com.watabou.pixeldungeon.effects.ShadowBox
import com.watabou.pixeldungeon.scenes.PixelScene
import com.watabou.utils.Signal
open class Window(
    width: Int,
    height: Int,
    chrome: NinePatch
) : Group(), Signal.Listener<Keys.Key> {
    protected var width: Int = width
    protected var height: Int = height
    protected var chrome: NinePatch = chrome
    protected val blocker: TouchArea
    protected val shadow: ShadowBox
    constructor() : this(0, 0, Chrome.get(Chrome.Type.WINDOW) ?: throw IllegalStateException("Chrome window not available"))
    constructor(width: Int, height: Int) : this(width, height, Chrome.get(Chrome.Type.WINDOW) ?: throw IllegalStateException("Chrome window not available"))
    init {
        blocker = object : TouchArea(0f, 0f, PixelScene.uiCamera.width.toFloat(), PixelScene.uiCamera.height.toFloat()) {
            override fun onClick(touch: Touch) {
                if (!this@Window.chrome.overlapsScreenPoint(
                        touch.current.x.toInt(),
                        touch.current.y.toInt()
                    )
                ) {
                    onBackPressed()
                }
            }
        }
        blocker.camera = PixelScene.uiCamera
        add(blocker)
        shadow = ShadowBox()
        shadow.am = 0.5f
        // isVisible property access (assuming Gizmo defines it as property)
        shadow.camera = if (PixelScene.uiCamera.visible) PixelScene.uiCamera else Camera.main
        add(shadow)
        this.chrome.x = -this.chrome.marginLeft().toFloat()
        this.chrome.y = -this.chrome.marginTop().toFloat()
        this.chrome.size(
            (this.width - this.chrome.x + this.chrome.marginRight()).toFloat(),
            (this.height - this.chrome.y + this.chrome.marginBottom()).toFloat()
        )
        add(this.chrome)
        camera = Camera(
            0, 0,
            this.chrome.width().toInt(),
            this.chrome.height().toInt(),
            PixelScene.defaultZoom
        )
        val c = camera
        if (c != null) {
            c.x = (Game.width - c.width * c.zoom).toInt() / 2
            c.y = (Game.height - c.height * c.zoom).toInt() / 2
            c.scroll.set(this.chrome.x, this.chrome.y)
            Camera.add(c)
            shadow.boxRect(
                (c.x / c.zoom),
                (c.y / c.zoom),
                this.chrome.width(), this.chrome.height()
            )
        }
        Keys.event.add(this)
    }
    open fun resize(w: Int, h: Int) {
        this.width = w
        this.height = h
        chrome.size(
            (width + chrome.marginHor()).toFloat(),
            (height + chrome.marginVer()).toFloat()
        )
        val c = camera ?: return
        c.resize(chrome.width().toInt(), chrome.height().toInt())
        c.x = (Game.width - c.screenWidth()).toInt() / 2
        c.y = (Game.height - c.screenHeight()).toInt() / 2
        shadow.boxRect(c.x / c.zoom, c.y / c.zoom, chrome.width(), chrome.height())
    }
    open fun hide() {
        parent?.erase(this)
        destroy()
    }
    override fun destroy() {
        super.destroy()
        camera?.let { Camera.remove(it) }
        Keys.event.remove(this)
    }
    override fun onSignal(t: Keys.Key?) {
        val k = t ?: return
        if (k.pressed) {
            when (k.code) {
                Keys.BACK -> onBackPressed()
                Keys.MENU -> onMenuPressed()
            }
        }
        Keys.event.cancel()
    }
    open fun onBackPressed() {
        hide()
    }
    open fun onMenuPressed() {
    }
    companion object {
        const val TITLE_COLOR = 0xFFFF44
    }
}
