package com.watabou.pixeldungeon.effects
import android.opengl.GLES20
import com.watabou.noosa.Game
import com.watabou.noosa.Group
import com.watabou.noosa.Image
import com.watabou.noosa.audio.Sample
import com.watabou.pixeldungeon.Assets
import com.watabou.pixeldungeon.DungeonTilemap
import com.watabou.pixeldungeon.levels.Level
import com.watabou.utils.Callback
import com.watabou.utils.Random
import javax.microedition.khronos.opengles.GL10
class Lightning(cells: IntArray, length: Int, private val callback: Callback?) : Group() {
    private var life: Float
    private val cellCount: Int
    private val cx: FloatArray
    private val cy: FloatArray
    private val arcsS: Array<Image>
    private val arcsE: Array<Image>
    init {
        val proto = Effects.get(Effects.Type.LIGHTNING)
        val ox = 0f
        val oy = proto.height / 2
        this.cellCount = length
        cx = FloatArray(length)
        cy = FloatArray(length)
        for (i in 0 until length) {
            val c = cells[i]
            cx[i] = (c % Level.WIDTH + 0.5f) * DungeonTilemap.SIZE
            cy[i] = (c / Level.WIDTH + 0.5f) * DungeonTilemap.SIZE
        }
        arcsS = Array(cellCount - 1) { i ->
            val arc = Image(proto)
            arc.x = cx[i] - arc.origin.x
            arc.y = cy[i] - arc.origin.y
            arc.origin.set(ox, oy)
            add(arc)
            arc
        }
        arcsE = Array(cellCount - 1) { _ ->
            val arc = Image(proto)
            arc.origin.set(ox, oy)
            add(arc)
            arc
        }
        life = DURATION
        Sample.play(Assets.SND_LIGHTNING)
    }
    override fun update() {
        super.update()
        life -= Game.elapsed
        if (life < 0) {
            killAndErase()
            callback?.call()
        } else {
            val alpha = life / DURATION
            for (i in 0 until cellCount - 1) {
                val sx = cx[i]
                val sy = cy[i]
                val ex = cx[i + 1]
                val ey = cy[i + 1]
                val x2 = (sx + ex) / 2 + Random.Float(-4f, +4f)
                val y2 = (sy + ey) / 2 + Random.Float(-4f, +4f)
                var dx = x2 - sx
                var dy = y2 - sy
                var arc = arcsS[i]
                arc.am = alpha
                arc.angle = (Math.atan2(dy.toDouble(), dx.toDouble()) * A).toFloat()
                arc.scale.x = Math.sqrt((dx * dx + dy * dy).toDouble()).toFloat() / arc.width
                dx = ex - x2
                dy = ey - y2
                arc = arcsE[i]
                arc.am = alpha
                arc.angle = (Math.atan2(dy.toDouble(), dx.toDouble()) * A).toFloat()
                arc.scale.x = Math.sqrt((dx * dx + dy * dy).toDouble()).toFloat() / arc.width
                arc.x = x2 - arc.origin.x
                arc.y = y2 - arc.origin.x
            }
        }
    }
    override fun draw() {
        GLES20.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE)
        super.draw()
        GLES20.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA)
    }
    companion object {
        private const val DURATION = 0.3f
        private const val A = 180 / Math.PI
    }
}
