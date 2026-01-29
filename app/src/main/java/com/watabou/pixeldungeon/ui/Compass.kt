package com.watabou.pixeldungeon.ui
import com.watabou.noosa.Camera
import com.watabou.noosa.Image
import com.watabou.pixeldungeon.Dungeon
import com.watabou.pixeldungeon.DungeonTilemap
import com.watabou.utils.PointF
import kotlin.math.atan2
class Compass(private val cell: Int) : Image() {
    private val cellCenter: PointF
    private val lastScroll = PointF()
    init {
        copy(Icons.COMPASS.get())
        origin.set(width / 2, RADIUS)
        cellCenter = DungeonTilemap.tileCenterToWorld(cell)
        visible = false
    }
    override fun update() {
        super.update()
        if (!visible) {
            val level = Dungeon.level ?: return
            visible = level.visited[cell] || level.mapped[cell]
        }
        if (visible) {
            val mainCamera = Camera.main ?: return
            val scroll = mainCamera.scroll
            if (!scroll.equals(lastScroll)) {
                lastScroll.set(scroll)
                val center = mainCamera.center().offset(scroll)
                angle = (atan2((cellCenter.x - center.x).toDouble(), (center.y - cellCenter.y).toDouble()) * RAD_2_G).toFloat()
            }
        }
    }
    companion object {
        private const val RAD_2_G = 180f / 3.1415926f
        private const val RADIUS = 12f
    }
}
