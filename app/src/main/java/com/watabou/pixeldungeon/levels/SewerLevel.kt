package com.watabou.pixeldungeon.levels
import com.watabou.noosa.Game
import com.watabou.noosa.Scene
import com.watabou.noosa.particles.Emitter
import com.watabou.noosa.particles.PixelParticle
import com.watabou.pixeldungeon.Assets
import com.watabou.pixeldungeon.Dungeon
import com.watabou.pixeldungeon.DungeonTilemap
import com.watabou.pixeldungeon.actors.mobs.npcs.Ghost
import com.watabou.pixeldungeon.items.DewVial
import com.watabou.pixeldungeon.scenes.GameScene
import com.watabou.utils.ColorMath
import com.watabou.utils.Random
open class SewerLevel : RegularLevel() {
    init {
        color1 = 0x48763c
        color2 = 0x59994a
    }
    override fun tilesTex(): String {
        return Assets.TILES_SEWERS
    }
    override fun waterTex(): String {
        return Assets.WATER_SEWERS
    }
    override fun water(): BooleanArray {
        return Patch.generate(if (feeling === Level.Feeling.WATER) 0.60f else 0.45f, 5)
    }
    override fun grass(): BooleanArray {
        return Patch.generate(if (feeling === Level.Feeling.GRASS) 0.60f else 0.40f, 4)
    }
    override fun decorate() {
        for (i in 0 until WIDTH) {
            if (map[i] == Terrain.WALL &&
                map[i + WIDTH] == Terrain.WATER &&
                Random.Int(4) == 0
            ) {
                map[i] = Terrain.WALL_DECO
            }
        }
        for (i in WIDTH until LENGTH - WIDTH) {
            if (map[i] == Terrain.WALL &&
                map[i - WIDTH] == Terrain.WALL &&
                map[i + WIDTH] == Terrain.WATER &&
                Random.Int(2) == 0
            ) {
                map[i] = Terrain.WALL_DECO
            }
        }
        for (i in WIDTH + 1 until LENGTH - WIDTH - 1) {
            if (map[i] == Terrain.EMPTY) {
                val count =
                    (if (map[i + 1] == Terrain.WALL) 1 else 0) +
                            (if (map[i - 1] == Terrain.WALL) 1 else 0) +
                            (if (map[i + WIDTH] == Terrain.WALL) 1 else 0) +
                            (if (map[i - WIDTH] == Terrain.WALL) 1 else 0)
                if (Random.Int(16) < count * count) {
                    map[i] = Terrain.EMPTY_DECO
                }
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
    override fun createMobs() {
        super.createMobs()
        Ghost.Quest.spawn(this)
    }
    override fun createItems() {
        if (Dungeon.dewVial && Random.Int(4 - Dungeon.depth) == 0) {
            addItemToSpawn(DewVial())
            Dungeon.dewVial = false
        }
        super.createItems()
    }
    override fun addVisuals(scene: Scene) {
        super.addVisuals(scene)
        addVisuals(this, scene)
    }
    override fun tileName(tile: Int): String {
        return when (tile) {
            Terrain.WATER -> "Murky water"
            else -> super.tileName(tile)
        }
    }
    override fun tileDesc(tile: Int): String {
        return when (tile) {
            Terrain.EMPTY_DECO -> "Wet yellowish moss covers the floor."
            Terrain.BOOKSHELF -> "The bookshelf is packed with cheap useless books. Might it burn?"
            else -> super.tileDesc(tile)
        }
    }
    private class Sink(private val pos: Int) : Emitter() {
        private var rippleDelay = 0f
        init {
            val p = DungeonTilemap.tileCenterToWorld(pos)
            pos(p.x - 2, p.y + 1, 4f, 0f)
            pour(particleFactory, 0.05f)
        }
        override fun update() {
            visible = Dungeon.visible[pos]
            if (visible) {
                super.update()
                rippleDelay -= Game.elapsed
                if (rippleDelay <= 0) {
                    GameScene.ripple(pos + WIDTH).y -= DungeonTilemap.SIZE / 2
                    rippleDelay = Random.Float(0.2f, 0.3f)
                }
            }
        }
        companion object {
            private val particleFactory: Emitter.Factory = object : Emitter.Factory() {
                override fun emit(emitter: Emitter, index: Int, x: Float, y: Float) {
                    val p = emitter.recycle(WaterParticle::class.java) as WaterParticle
                    p.reset(x, y)
                }
            }
        }
    }
    class WaterParticle : PixelParticle() {
        init {
            acc.y = 50f
            am = 0.5f
            color(ColorMath.random(0xb6ccc2, 0x3b6653))
            size(2f)
        }
        fun reset(x: Float, y: Float) {
            revive()
            this.x = x
            this.y = y
            speed.set(Random.Float(-2f, +2f), 0f)
            lifespan = 0.5f
            left = lifespan
        }
    }
    companion object {
        fun addVisuals(level: Level, scene: Scene) {
            for (i in 0 until LENGTH) {
                if (level.map[i] == Terrain.WALL_DECO) {
                    scene.add(Sink(i))
                }
            }
        }
    }
}
