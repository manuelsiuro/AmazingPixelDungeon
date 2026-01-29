package com.watabou.noosa
import com.watabou.glwrap.Matrix
import com.watabou.utils.GameMath
import com.watabou.utils.Point
import com.watabou.utils.PointF
open class Visual(
    var x: Float,
    var y: Float,
    var width: Float,
    var height: Float
) : Gizmo() {
    var scale: PointF = PointF(1f, 1f)
    var origin: PointF = PointF()
    protected var matrix: FloatArray = FloatArray(16)
    var rm: Float = 0f
    var gm: Float = 0f
    var bm: Float = 0f
    var am: Float = 0f
    var ra: Float = 0f
    var ga: Float = 0f
    var ba: Float = 0f
    var aa: Float = 0f
    var speed: PointF = PointF()
    var acc: PointF = PointF()
    var angle: Float = 0f
    var angularSpeed: Float = 0f
    init {
        resetColor()
    }
    override fun update() {
        updateMotion()
    }
    override fun draw() {
        updateMatrix()
    }
    protected open fun updateMatrix() {
        Matrix.setIdentity(matrix)
        Matrix.translate(matrix, x, y)
        Matrix.translate(matrix, origin.x, origin.y)
        if (angle != 0f) {
            Matrix.rotate(matrix, angle)
        }
        if (scale.x != 1f || scale.y != 1f) {
            Matrix.scale(matrix, scale.x, scale.y)
        }
        Matrix.translate(matrix, -origin.x, -origin.y)
    }
    fun point(): PointF {
        return PointF(x, y)
    }
    fun point(p: PointF): PointF {
        x = p.x
        y = p.y
        return p
    }
    fun point(p: Point): Point {
        x = p.x.toFloat()
        y = p.y.toFloat()
        return p
    }
    fun center(): PointF {
        return PointF(x + width / 2, y + height / 2)
    }
    fun center(p: PointF): PointF {
        x = p.x - width / 2
        y = p.y - height / 2
        return p
    }
    open fun width(): Float {
        return width * scale.x
    }
    open fun height(): Float {
        return height * scale.y
    }
    protected fun updateMotion() {
        val elapsed = Game.elapsed
        var d = (GameMath.speed(speed.x, acc.x) - speed.x) / 2
        speed.x += d
        x += speed.x * elapsed
        speed.x += d
        d = (GameMath.speed(speed.y, acc.y) - speed.y) / 2
        speed.y += d
        y += speed.y * elapsed
        speed.y += d
        angle += angularSpeed * elapsed
    }
    fun alpha(value: Float) {
        am = value
        aa = 0f
    }
    fun alpha(): Float {
        return am + aa
    }
    fun invert() {
        bm = -1f
        gm = bm
        rm = gm
        ba = 1f
        ga = ba
        ra = ga
    }
    fun lightness(value: Float) {
        if (value < 0.5f) {
            bm = value * 2f
            gm = bm
            rm = gm
            ba = 0f
            ga = ba
            ra = ga
        } else {
            bm = 2f - value * 2f
            gm = bm
            rm = gm
            ba = value * 2f - 1f
            ga = ba
            ra = ga
        }
    }
    fun brightness(value: Float) {
        bm = value
        gm = bm
        rm = gm
    }
    fun tint(r: Float, g: Float, b: Float, strength: Float) {
        bm = 1f - strength
        gm = bm
        rm = gm
        ra = r * strength
        ga = g * strength
        ba = b * strength
    }
    fun tint(color: Int, strength: Float) {
        bm = 1f - strength
        gm = bm
        rm = gm
        ra = ((color shr 16) and 0xFF) / 255f * strength
        ga = ((color shr 8) and 0xFF) / 255f * strength
        ba = (color and 0xFF) / 255f * strength
    }
    fun color(r: Float, g: Float, b: Float) {
        bm = 0f
        gm = bm
        rm = gm
        ra = r
        ga = g
        ba = b
    }
    fun color(color: Int) {
        color(((color shr 16) and 0xFF) / 255f, ((color shr 8) and 0xFF) / 255f, (color and 0xFF) / 255f)
    }
    fun hardlight(r: Float, g: Float, b: Float) {
        ba = 0f
        ga = ba
        ra = ga
        rm = r
        gm = g
        bm = b
    }
    fun hardlight(color: Int) {
        hardlight((color shr 16) / 255f, ((color shr 8) and 0xFF) / 255f, (color and 0xFF) / 255f)
    }
    fun resetColor() {
        am = 1f
        bm = am
        gm = bm
        rm = gm
        aa = 0f
        ba = aa
        ga = ba
        ra = ga
    }
    open fun overlapsPoint(x: Float, y: Float): Boolean {
        return x >= this.x && x < this.x + width * scale.x && y >= this.y && y < this.y + height * scale.y
    }
    open fun overlapsScreenPoint(x: Int, y: Int): Boolean {
        val c = camera()
        if (c != null) {
            val p = c.screenToCamera(x, y)
            return overlapsPoint(p.x, p.y)
        } else {
            return false
        }
    }
    // true if its bounding box intersects its camera's bounds
    override fun isVisible(): Boolean {
        val c = camera() ?: return false
        val cx = c.scroll.x
        val cy = c.scroll.y
        val w = width()
        val h = height()
        return x + w >= cx && y + h >= cy && x < cx + c.width && y < cy + c.height
    }
}
