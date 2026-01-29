package com.watabou.pixeldungeon.scenes
import com.watabou.input.Touchscreen.Touch
import com.watabou.noosa.TouchArea
import com.watabou.pixeldungeon.DungeonTilemap
import com.watabou.pixeldungeon.PixelDungeon
import com.watabou.utils.GameMath
import com.watabou.utils.PointF
class CellSelector(map: DungeonTilemap) : TouchArea(map) {
    var listener: Listener? = null
    var enabled: Boolean = false
    private val dragThreshold: Float = PixelScene.defaultZoom * DungeonTilemap.SIZE / 2
    private var pinching = false
    private var another: Touch? = null
    private var startZoom: Float = 0.toFloat()
    private var startSpan: Float = 0.toFloat()
    private var dragging = false
    private val lastPos = PointF()
    init {
        camera = map.camera
    }
    override fun onClick(touch: Touch) {
        if (dragging) {
            dragging = false
        } else {
            select(
                (target as DungeonTilemap).screenToTile(
                    touch.current.x.toInt(),
                    touch.current.y.toInt()
                )
            )
        }
    }
    fun select(cell: Int) {
        if (enabled && listener != null && cell != -1) {
            listener!!.onSelect(cell)
            GameScene.ready()
        } else {
            GameScene.cancel()
        }
    }
    override fun onTouchDown(touch: Touch) {
        if (touch !== this.touch && another == null) {
            if (!this.touch!!.down) {
                this.touch = touch
                onTouchDown(touch)
                return
            }
            pinching = true
            another = touch
            startSpan = PointF.distance(this.touch!!.current, another!!.current)
            startZoom = camera!!.zoom
            dragging = false
        }
    }
    override fun onTouchUp(touch: Touch) {
        if (pinching && (touch === this.touch || touch === another)) {
            pinching = false
            val zoom = Math.round(camera()!!.zoom)
            camera()!!.zoom(zoom.toFloat())
            PixelDungeon.zoom((zoom - PixelScene.defaultZoom).toInt())
            dragging = true
            if (touch === this.touch) {
                this.touch = another
            }
            another = null
            lastPos.set(this.touch!!.current)
        }
    }
    override fun onDrag(touch: Touch) {
        camera()?.target = null
        if (pinching) {
            val curSpan = PointF.distance(this.touch!!.current, another!!.current)
            camera()?.zoom(
                GameMath.gate(
                    PixelScene.minZoom,
                    startZoom * curSpan / startSpan,
                    PixelScene.maxZoom
                )
            )
        } else {
            if (!dragging && PointF.distance(touch.current, touch.start) > dragThreshold) {
                dragging = true
                lastPos.set(touch.current)
            } else if (dragging) {
                camera!!.scroll.offset(PointF.diff(lastPos, touch.current).invScale(camera!!.zoom))
                lastPos.set(touch.current)
            }
        }
    }
    fun cancel() {
        if (listener != null) {
            listener!!.onSelect(null)
        }
        GameScene.ready()
    }
    interface Listener {
        fun onSelect(cell: Int?)
        fun prompt(): String
    }
}
