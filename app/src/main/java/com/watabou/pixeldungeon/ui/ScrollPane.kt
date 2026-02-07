package com.watabou.pixeldungeon.ui
import com.watabou.input.Touchscreen.Touch
import com.watabou.noosa.Camera
import com.watabou.noosa.ColorBlock
import com.watabou.noosa.TouchArea
import com.watabou.noosa.ui.Component
import com.watabou.pixeldungeon.scenes.PixelScene
import com.watabou.utils.PointF
// Converted ScrollPane.kt
open class ScrollPane(val content: Component) : Component() {
    protected lateinit var controller: TouchController
    protected lateinit var thumb: ColorBlock
    // Protected properties from Java (implicit/default visibility), in Kotiin 'protected' works.
    // minX/Y/maxX/Y unused in Java snippet?
    // They appeared in Java code:
    // private float minX; ...
    // But implementation didn't use them visibly in snippet? 
    // Ah, line 38-41.
    // Check Java code... 1394 step. 
    // They are defined but NOT used in snippet?
    // Wait, snippet shows 1 to 167 lines.
    // I only see them declared. Not used layout or update?
    // Maybe subclass uses them?
    // I will include them as protected vars.
    protected var minX: Float = 0f
    protected var minY: Float = 0f
    protected var maxX: Float = 0f
    protected var maxY: Float = 0f
    init {
        addToBack(content)
        // Using width() and height() and setting width/height via properties if accessible or setSize
        // Component properties width/height are protected.
        // ScrollPane extends Component, so it can access them.
        width = content.width()
        height = content.height()
        content.camera = Camera(0, 0, 1, 1, PixelScene.defaultZoom)
        content.camera?.let { Camera.add(it) }
    }
    override fun destroy() {
        super.destroy()
        content.camera?.let { Camera.remove(it) }
    }
    fun scrollTo(x: Float, y: Float) {
        content.camera?.scroll?.set(x, y)
    }
    override fun createChildren() {
        controller = TouchController()
        add(controller)
        thumb = ColorBlock(1f, 1f, THUMB_COLOR)
        thumb.am = THUMB_ALPHA
        add(thumb)
    }
    override fun layout() {
        content.setPos(0f, 0f)
        controller.x = x
        controller.y = y
        controller.width = width
        controller.height = height
        // content.camera is nullable? In Java `content.camera = new Camera(...)`.
        // In Component.java `public Camera camera`.
        val cam = camera() ?: return
        val p = cam.cameraToScreen(x, y)
        val cs = content.camera ?: return
        cs.x = p.x
        cs.y = p.y
        cs.resize(width.toInt(), height.toInt())
        thumb.visible = height < content.height()
        if (thumb.visible) {
            thumb.scale.set(2f, height * height / content.height())
            thumb.x = right() - thumb.width()
            thumb.y = y
        }
    }
    fun content(): Component {
        return content
    }
    open fun onClick(x: Float, y: Float) {}
    inner class TouchController : TouchArea(0f, 0f, 0f, 0f) {
        private var dragThreshold: Float = 0f
        private var dragging = false
        private val lastPos = PointF()
        init {
            dragThreshold = PixelScene.defaultZoom * 8
        }
        override fun onClick(touch: Touch) {
            if (dragging) {
                dragging = false
                thumb.am = THUMB_ALPHA
            } else {
                val contentCam = content.camera ?: return
                val p = contentCam.screenToCamera(touch.current.x.toInt(), touch.current.y.toInt())
                this@ScrollPane.onClick(p.x, p.y)
            }
        }
        override fun onDrag(touch: Touch) {
            if (dragging) {
                val c = content.camera ?: return
                c.scroll.offset(PointF.diff(lastPos, touch.current).invScale(c.zoom))
                if (c.scroll.x + width > content.width()) {
                    c.scroll.x = content.width() - width
                }
                if (c.scroll.x < 0f) {
                    c.scroll.x = 0f
                }
                if (c.scroll.y + height > content.height()) {
                    c.scroll.y = content.height() - height
                }
                if (c.scroll.y < 0f) {
                    c.scroll.y = 0f
                }
                thumb.y = y + height * c.scroll.y / content.height()
                lastPos.set(touch.current)
            } else if (PointF.distance(touch.current, touch.start) > dragThreshold) {
                dragging = true
                lastPos.set(touch.current)
                thumb.am = 1f
            }
        }
    }
    companion object {
        protected const val THUMB_COLOR = 0xFF7b8073.toInt()
        protected const val THUMB_ALPHA = 0.5f
    }
}
