package com.watabou.pixeldungeon.levels
import android.opengl.GLES20
import com.watabou.noosa.Game
import com.watabou.noosa.Group
import com.watabou.noosa.Scene
import com.watabou.noosa.particles.PixelParticle
import com.watabou.pixeldungeon.Assets
import com.watabou.pixeldungeon.Dungeon
import com.watabou.pixeldungeon.DungeonTilemap
import com.watabou.pixeldungeon.items.Torch
import com.watabou.utils.PointF
import com.watabou.utils.Random
import javax.microedition.khronos.opengles.GL10
import kotlin.math.max
open class HallsLevel : RegularLevel() {
    init {
        minRoomSize = 6
        viewDistance = max(25 - Dungeon.depth, 1)
        color1 = 0x801500
        color2 = 0xa68521
    }
    override fun create() {
        addItemToSpawn(Torch())
        super.create()
    }
    override fun tilesTex(): String {
        return Assets.TILES_HALLS
    }
    override fun waterTex(): String {
        return Assets.WATER_HALLS
    }
    override fun water(): BooleanArray {
        return Patch.generate(if (feeling === Level.Feeling.WATER) 0.55f else 0.40f, 6)
    }
    override fun grass(): BooleanArray {
        return Patch.generate(if (feeling === Level.Feeling.GRASS) 0.55f else 0.30f, 3)
    }
    override fun decorate() {
        for (i in WIDTH + 1 until LENGTH - WIDTH - 1) {
            if (map[i] == Terrain.EMPTY) {
                var count = 0
                for (j in Level.NEIGHBOURS8.indices) {
                    if ((Terrain.flags[map[i + Level.NEIGHBOURS8[j]]] and Terrain.PASSABLE) > 0) {
                        count++
                    }
                }
                if (Random.Int(80) < count) {
                    map[i] = Terrain.EMPTY_DECO
                }
            } else if (map[i] == Terrain.WALL &&
                map[i - 1] != Terrain.WALL_DECO && map[i - WIDTH] != Terrain.WALL_DECO &&
                Random.Int(20) == 0
            ) {
                map[i] = Terrain.WALL_DECO
            }
        }
        while (true) {
            val entrance = roomEntrance ?: break
            val pos = entrance.random()
            if (pos != this.entrance) {
                map[pos] = Terrain.SIGN
                break
            }
        }
    }
    override fun tileName(tile: Int): String {
        return when (tile) {
            Terrain.WATER -> "Cold lava"
            Terrain.GRASS -> "Embermoss"
            Terrain.HIGH_GRASS -> "Emberfungi"
            Terrain.STATUE, Terrain.STATUE_SP -> "Pillar"
            else -> super.tileName(tile)
        }
    }
    override fun tileDesc(tile: Int): String {
        return when (tile) {
            Terrain.WATER -> "It looks like lava, but it's cold and probably safe to touch."
            Terrain.STATUE, Terrain.STATUE_SP -> "The pillar is made of real humanoid skulls. Awesome."
            Terrain.BOOKSHELF -> "Books in ancient languages smoulder in the bookshelf."
            else -> super.tileDesc(tile)
        }
    }
    override fun addVisuals(scene: Scene) {
        super.addVisuals(scene)
        addVisuals(this, scene)
    }
    private class Stream(private val pos: Int) : Group() {
        private var delay: Float = Random.Float(2f)
        override fun update() {
            visible = Dungeon.visible[pos]
            if (visible) {
                super.update()
                delay -= Game.elapsed
                if (delay <= 0) {
                    delay = Random.Float(2f)
                    val p = DungeonTilemap.tileToWorld(pos)
                    (recycle(FireParticle::class.java) as FireParticle).reset(
                        p.x + Random.Float(DungeonTilemap.SIZE.toFloat()),
                        p.y + Random.Float(DungeonTilemap.SIZE.toFloat())
                    )
                }
            }
        }
        override fun draw() {
            GLES20.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE)
            super.draw()
            GLES20.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA)
        }
    }
    class FireParticle : PixelParticle.Shrinking() {
        init {
            color(0xEE7722)
            lifespan = 1f
            acc.set(0f, +80f)
        }
        fun reset(x: Float, y: Float) {
            revive()
            this.x = x
            this.y = y
            left = lifespan
            speed.set(0f, -40f)
            size = 4f
        }
        override fun update() {
            super.update()
            val p = left / lifespan
            am = if (p > 0.8f) (1 - p) * 5 else 1f
        }
    }
    companion object {
        fun addVisuals(level: Level, scene: Scene) {
            for (i in 0 until LENGTH) {
                if (level.map[i] == 63) {
                    scene.add(Stream(i))
                }
            }
        }
    }
}
