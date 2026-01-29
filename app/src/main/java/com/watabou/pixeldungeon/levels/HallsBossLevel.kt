package com.watabou.pixeldungeon.levels
import com.watabou.noosa.Scene
import com.watabou.pixeldungeon.Assets
import com.watabou.pixeldungeon.Bones
import com.watabou.pixeldungeon.Dungeon
import com.watabou.pixeldungeon.actors.Actor
import com.watabou.pixeldungeon.actors.Char
import com.watabou.pixeldungeon.actors.mobs.Yog
import com.watabou.pixeldungeon.effects.CellEmitter
import com.watabou.pixeldungeon.effects.particles.FlameParticle
import com.watabou.pixeldungeon.items.Heap
import com.watabou.pixeldungeon.items.Item
import com.watabou.pixeldungeon.items.keys.SkeletonKey
import com.watabou.pixeldungeon.levels.painters.Painter
import com.watabou.pixeldungeon.scenes.GameScene
import com.watabou.utils.Bundle
import com.watabou.utils.Random
class HallsBossLevel : Level() {
    init {
        color1 = 0x801500
        color2 = 0xa68521
        viewDistance = 3
    }
    private var stairs = -1
    private var enteredArena = false
    private var keyDropped = false
    override fun tilesTex(): String {
        return Assets.TILES_HALLS
    }
    override fun waterTex(): String {
        return Assets.WATER_HALLS
    }
    override fun storeInBundle(bundle: Bundle) {
        super.storeInBundle(bundle)
        bundle.put(STAIRS, stairs)
        bundle.put(ENTERED, enteredArena)
        bundle.put(DROPPED, keyDropped)
    }
    override fun restoreFromBundle(bundle: Bundle) {
        super.restoreFromBundle(bundle)
        stairs = bundle.getInt(STAIRS)
        enteredArena = bundle.getBoolean(ENTERED)
        keyDropped = bundle.getBoolean(DROPPED)
    }
    override fun build(): Boolean {
        for (i in 0 until 5) {
            val top = Random.IntRange(2, ROOM_TOP - 1)
            val bottom = Random.IntRange(ROOM_BOTTOM + 1, 22)
            Painter.fill(this, 2 + i * 4, top, 4, bottom - top + 1, Terrain.EMPTY)
            if (i == 2) {
                exit = (i * 4 + 3) + (top - 1) * WIDTH
            }
            for (j in 0 until 4) {
                if (Random.Int(2) == 0) {
                    val y = Random.IntRange(top + 1, bottom - 1)
                    map[i * 4 + j + y * WIDTH] = Terrain.WALL_DECO
                }
            }
        }
        map[exit] = Terrain.LOCKED_EXIT
        Painter.fill(this, ROOM_LEFT - 1, ROOM_TOP - 1,
            ROOM_RIGHT - ROOM_LEFT + 3, ROOM_BOTTOM - ROOM_TOP + 3, Terrain.WALL)
        Painter.fill(this, ROOM_LEFT, ROOM_TOP,
            ROOM_RIGHT - ROOM_LEFT + 1, ROOM_BOTTOM - ROOM_TOP + 1, Terrain.EMPTY)
        entrance = Random.Int(ROOM_LEFT + 1, ROOM_RIGHT - 1) +
                Random.Int(ROOM_TOP + 1, ROOM_BOTTOM - 1) * WIDTH
        map[entrance] = Terrain.ENTRANCE
        val patch = Patch.generate(0.45f, 6)
        for (i in 0 until LENGTH) {
            if (map[i] == Terrain.EMPTY && patch[i]) {
                map[i] = Terrain.WATER
            }
        }
        return true
    }
    override fun decorate() {
        for (i in 0 until LENGTH) {
            if (map[i] == Terrain.EMPTY && Random.Int(10) == 0) {
                map[i] = Terrain.EMPTY_DECO
            }
        }
    }
    override fun createMobs() {
    }
    override fun respawner(): Actor? {
        return null
    }
    override fun createItems() {
        val item = Bones.get()
        if (item != null) {
            var pos: Int
            do {
                pos = Random.IntRange(ROOM_LEFT, ROOM_RIGHT) + Random.IntRange(ROOM_TOP + 1, ROOM_BOTTOM) * WIDTH
            } while (pos == entrance || map[pos] == Terrain.SIGN)
            drop(item, pos).type = Heap.Type.SKELETON
        }
    }
    override fun randomRespawnCell(): Int {
        return -1
    }
    override fun press(cell: Int, ch: Char?) {
        val localHero = ch ?: return
        super.press(cell, localHero)
        if (!enteredArena && localHero === Dungeon.hero && cell != entrance) {
            enteredArena = true
            for (i in ROOM_LEFT - 1..ROOM_RIGHT + 1) {
                doMagic((ROOM_TOP - 1) * WIDTH + i)
                doMagic((ROOM_BOTTOM + 1) * WIDTH + i)
            }
            for (i in ROOM_TOP until ROOM_BOTTOM + 1) {
                doMagic(i * WIDTH + ROOM_LEFT - 1)
                doMagic(i * WIDTH + ROOM_RIGHT + 1)
            }
            doMagic(entrance)
            GameScene.updateMap()
            Dungeon.observe()
            val boss = Yog()
            do {
                boss.pos = Random.Int(LENGTH)
            } while (
                !passable[boss.pos] ||
                Dungeon.visible[boss.pos])
            GameScene.add(boss)
            boss.spawnFists()
            stairs = entrance
            entrance = -1
        }
    }
    private fun doMagic(cell: Int) {
        set(cell, Terrain.EMPTY_SP)
        CellEmitter.get(cell).start(FlameParticle.FACTORY, 0.1f, 3)
    }
    override fun drop(item: Item, cell: Int): Heap {
        if (!keyDropped && item is SkeletonKey) {
            keyDropped = true
            entrance = stairs
            set(entrance, Terrain.ENTRANCE)
            GameScene.updateMap(entrance)
        }
        return super.drop(item, cell)
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
            else -> super.tileDesc(tile)
        }
    }
    override fun addVisuals(scene: Scene) {
        HallsLevel.addVisuals(this, scene)
    }
    companion object {
        private const val ROOM_LEFT = WIDTH / 2 - 1
        private const val ROOM_RIGHT = WIDTH / 2 + 1
        private const val ROOM_TOP = HEIGHT / 2 - 1
        private const val ROOM_BOTTOM = HEIGHT / 2 + 1
        private const val STAIRS = "stairs"
        private const val ENTERED = "entered"
        private const val DROPPED = "droppped"
    }
}
