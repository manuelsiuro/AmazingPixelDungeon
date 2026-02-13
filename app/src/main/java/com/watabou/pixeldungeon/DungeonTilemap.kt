package com.watabou.pixeldungeon
import com.watabou.noosa.Image
import com.watabou.noosa.TextureFilm
import com.watabou.noosa.Tilemap
import com.watabou.noosa.tweeners.AlphaTweener
import com.watabou.pixeldungeon.levels.Level
import com.watabou.utils.Point
import com.watabou.utils.PointF
class DungeonTilemap : Tilemap {
    constructor() : super(
        Dungeon.level!!.tilesTex()!!,
        TextureFilm(Dungeon.level!!.tilesTex()!!, SIZE, SIZE)
    ) {
        map(Dungeon.level!!.map, Level.WIDTH)
        instance = this
    }
    fun screenToTile(x: Int, y: Int): Int {
        val p = camera()!!.screenToCamera(x, y)
            .offset(this.point().negate())
            .invScale(SIZE.toFloat())
            .floor()
        return if (p.x >= 0 && p.x < Level.WIDTH && p.y >= 0 && p.y < Level.HEIGHT) p.x + p.y * Level.WIDTH else -1
    }
    override fun overlapsPoint(x: Float, y: Float): Boolean {
        return true
    }
    fun discover(pos: Int, oldValue: Int) {
        val tile = tile(oldValue)
        tile.point(tileToWorld(pos))
        // For bright mode
        tile.bm = rm
        tile.gm = bm
        tile.rm = gm
        tile.ba = ra
        tile.ga = ba
        tile.ra = ga
        parent?.add(tile)
        parent?.add(object : AlphaTweener(tile, 0f, 0.6f) {
            override fun onComplete() {
                tile.killAndErase()
                killAndErase()
            }
        })
    }
    override fun overlapsScreenPoint(x: Int, y: Int): Boolean {
        return true
    }
    companion object {
        const val SIZE = 16
        private var instance: DungeonTilemap? = null

        fun tileToWorld(pos: Int): PointF {
            return PointF((pos % Level.WIDTH).toFloat(), (pos / Level.WIDTH).toFloat()).scale(SIZE.toFloat())
        }
        fun tileCenterToWorld(pos: Int): PointF {
            return PointF(
                (pos % Level.WIDTH + 0.5f) * SIZE,
                (pos / Level.WIDTH + 0.5f) * SIZE
            )
        }
        fun tile(index: Int): Image {
            val img = Image(instance!!.texture!!)
            img.frame(instance!!.tileset!!.get(index)!!)
            return img
        }
    }
}
