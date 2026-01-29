package com.watabou.pixeldungeon.levels
import com.watabou.noosa.Scene
import com.watabou.noosa.particles.Emitter
import com.watabou.noosa.particles.PixelParticle
import com.watabou.pixeldungeon.Assets
import com.watabou.pixeldungeon.Dungeon
import com.watabou.pixeldungeon.DungeonTilemap
import com.watabou.pixeldungeon.actors.mobs.npcs.Imp
import com.watabou.pixeldungeon.levels.Room.Type
import com.watabou.utils.Random
open class CityLevel : RegularLevel() {
    init {
        color1 = 0x4b6636
        color2 = 0xf2f2f2
    }
    override fun tilesTex(): String {
        return Assets.TILES_CITY
    }
    override fun waterTex(): String {
        return Assets.WATER_CITY
    }
    override fun water(): BooleanArray {
        return Patch.generate(if (feeling === Level.Feeling.WATER) 0.65f else 0.45f, 4)
    }
    override fun grass(): BooleanArray {
        return Patch.generate(if (feeling === Level.Feeling.GRASS) 0.60f else 0.40f, 3)
    }
    override fun assignRoomType() {
        super.assignRoomType()
        for (r in rooms.orEmpty()) {
            if (r.type == Type.TUNNEL) {
                r.type = Type.PASSAGE
            }
        }
    }
    override fun decorate() {
        for (i in 0 until LENGTH) {
            if (map[i] == Terrain.EMPTY && Random.Int(10) == 0) {
                map[i] = Terrain.EMPTY_DECO
            } else if (map[i] == Terrain.WALL && Random.Int(8) == 0) {
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
    override fun createItems() {
        super.createItems()
        roomEntrance?.let { Imp.Quest.spawn(this, it) }
    }
    override fun tileName(tile: Int): String {
        return when (tile) {
            Terrain.WATER -> "Suspiciously colored water"
            Terrain.HIGH_GRASS -> "High blooming flowers"
            else -> super.tileName(tile)
        }
    }
    override fun tileDesc(tile: Int): String {
        return when (tile) {
            Terrain.ENTRANCE -> "A ramp leads up to the upper depth."
            Terrain.EXIT -> "A ramp leads down to the lower depth."
            Terrain.WALL_DECO, Terrain.EMPTY_DECO -> "Several tiles are missing here."
            Terrain.EMPTY_SP -> "Thick carpet covers the floor."
            Terrain.STATUE, Terrain.STATUE_SP -> "The statue depicts some dwarf standing in a heroic stance."
            Terrain.BOOKSHELF -> "The rows of books on different disciplines fill the bookshelf."
            else -> super.tileDesc(tile)
        }
    }
    override fun addVisuals(scene: Scene) {
        super.addVisuals(scene)
        addVisuals(this, scene)
    }
    private class Smoke(private val pos: Int) : Emitter() {
        init {
            val p = DungeonTilemap.tileCenterToWorld(pos)
            pos(p.x - 4, p.y - 2, 4f, 0f)
            pour(particleFactory, 0.2f)
        }
        override fun update() {
            visible = Dungeon.visible[pos]
            if (visible) {
                super.update()
            }
        }
        companion object {
            private val particleFactory: Emitter.Factory = object : Emitter.Factory() {
                override fun emit(emitter: Emitter, index: Int, x: Float, y: Float) {
                    val p = emitter.recycle(SmokeParticle::class.java) as SmokeParticle
                    p.reset(x, y)
                }
            }
        }
    }
    class SmokeParticle : PixelParticle() {
        init {
            color(0x000000)
            speed.set(Random.Float(8f), -Random.Float(8f))
        }
        fun reset(x: Float, y: Float) {
            revive()
            this.x = x
            this.y = y
            lifespan = 2f
            left = lifespan
        }
        override fun update() {
            super.update()
            val p = left / lifespan
            am = if (p > 0.8f) 1 - p else p * 0.25f
            size(8 - p * 4)
        }
    }
    companion object {
        fun addVisuals(level: Level, scene: Scene) {
            for (i in 0 until LENGTH) {
                if (level.map[i] == Terrain.WALL_DECO) {
                    scene.add(Smoke(i))
                }
            }
        }
    }
}
