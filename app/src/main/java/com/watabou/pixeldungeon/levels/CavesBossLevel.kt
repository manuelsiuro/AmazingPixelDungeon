package com.watabou.pixeldungeon.levels
import com.watabou.noosa.Camera
import com.watabou.noosa.Scene
import com.watabou.noosa.audio.Sample
import com.watabou.pixeldungeon.Assets
import com.watabou.pixeldungeon.Bones
import com.watabou.pixeldungeon.Dungeon
import com.watabou.pixeldungeon.actors.Actor
import com.watabou.pixeldungeon.actors.Char
import com.watabou.pixeldungeon.actors.mobs.Bestiary
import com.watabou.pixeldungeon.actors.mobs.Mob
import com.watabou.pixeldungeon.effects.CellEmitter
import com.watabou.pixeldungeon.effects.Speck
import com.watabou.pixeldungeon.items.Heap
import com.watabou.pixeldungeon.items.Item
import com.watabou.pixeldungeon.items.keys.SkeletonKey
import com.watabou.pixeldungeon.levels.painters.Painter
import com.watabou.pixeldungeon.scenes.GameScene
import com.watabou.utils.Bundle
import com.watabou.utils.Random
class CavesBossLevel : Level() {
    init {
        color1 = 0x534f3e
        color2 = 0xb9d661
        viewDistance = 6
    }
    private var arenaDoor = 0
    private var enteredArena = false
    private var keyDropped = false
    override fun tilesTex(): String {
        return Assets.TILES_CAVES
    }
    override fun waterTex(): String {
        return Assets.WATER_CAVES
    }
    override fun storeInBundle(bundle: Bundle) {
        super.storeInBundle(bundle)
        bundle.put(DOOR, arenaDoor)
        bundle.put(ENTERED, enteredArena)
        bundle.put(DROPPED, keyDropped)
    }
    override fun restoreFromBundle(bundle: Bundle) {
        super.restoreFromBundle(bundle)
        arenaDoor = bundle.getInt(DOOR)
        enteredArena = bundle.getBoolean(ENTERED)
        keyDropped = bundle.getBoolean(DROPPED)
    }
    override fun build(): Boolean {
        var topMost = Int.MAX_VALUE
        for (i in 0 until 8) {
            val left: Int
            val right: Int
            val top: Int
            val bottom: Int
            if (Random.Int(2) == 0) {
                left = Random.Int(1, ROOM_LEFT - 3)
                right = ROOM_RIGHT + 3
            } else {
                left = ROOM_LEFT - 3
                right = Random.Int(ROOM_RIGHT + 3, WIDTH - 1)
            }
            if (Random.Int(2) == 0) {
                top = Random.Int(2, ROOM_TOP - 3)
                bottom = ROOM_BOTTOM + 3
            } else {
                top = ROOM_LEFT - 3
                bottom = Random.Int(ROOM_TOP + 3, HEIGHT - 1)
            }
            Painter.fill(this, left, top, right - left + 1, bottom - top + 1, Terrain.EMPTY)
            if (top < topMost) {
                topMost = top
                exit = Random.Int(left, right) + (top - 1) * WIDTH
            }
        }
        map[exit] = Terrain.LOCKED_EXIT
        for (i in 0 until LENGTH) {
            if (map[i] == Terrain.EMPTY && Random.Int(6) == 0) {
                map[i] = Terrain.INACTIVE_TRAP
            }
        }
        Painter.fill(this, ROOM_LEFT - 1, ROOM_TOP - 1,
            ROOM_RIGHT - ROOM_LEFT + 3, ROOM_BOTTOM - ROOM_TOP + 3, Terrain.WALL)
        Painter.fill(this, ROOM_LEFT, ROOM_TOP + 1,
            ROOM_RIGHT - ROOM_LEFT + 1, ROOM_BOTTOM - ROOM_TOP, Terrain.EMPTY)
        Painter.fill(this, ROOM_LEFT, ROOM_TOP,
            ROOM_RIGHT - ROOM_LEFT + 1, 1, Terrain.TOXIC_TRAP)
        arenaDoor = Random.Int(ROOM_LEFT, ROOM_RIGHT) + (ROOM_BOTTOM + 1) * WIDTH
        map[arenaDoor] = Terrain.DOOR
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
        for (i in WIDTH + 1 until LENGTH - WIDTH) {
            if (map[i] == Terrain.EMPTY) {
                var n = 0
                if (map[i + 1] == Terrain.WALL) {
                    n++
                }
                if (map[i - 1] == Terrain.WALL) {
                    n++
                }
                if (map[i + WIDTH] == Terrain.WALL) {
                    n++
                }
                if (map[i - WIDTH] == Terrain.WALL) {
                    n++
                }
                if (Random.Int(8) <= n) {
                    map[i] = Terrain.EMPTY_DECO
                }
            }
        }
        for (i in 0 until LENGTH) {
            if (map[i] == Terrain.WALL && Random.Int(8) == 0) {
                map[i] = Terrain.WALL_DECO
            }
        }
        var sign: Int
        do {
            sign = Random.Int(ROOM_LEFT, ROOM_RIGHT) + Random.Int(ROOM_TOP, ROOM_BOTTOM) * WIDTH
        } while (sign == entrance)
        map[sign] = Terrain.SIGN
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
        if (!enteredArena && outsideEntraceRoom(cell) && localHero === Dungeon.hero) {
            enteredArena = true
            val boss = Bestiary.mob(Dungeon.depth) ?: return
            boss.state = boss.HUNTING
            do {
                boss.pos = Random.Int(LENGTH)
            } while (
                !passable[boss.pos] ||
                !outsideEntraceRoom(boss.pos) ||
                Dungeon.visible[boss.pos])
            GameScene.add(boss)
            set(arenaDoor, Terrain.WALL)
            GameScene.updateMap(arenaDoor)
            Dungeon.observe()
            CellEmitter.get(arenaDoor).start(Speck.factory(Speck.ROCK), 0.07f, 10)
            Camera.main?.shake(3f, 0.7f)
            Sample.play(Assets.SND_ROCKS)
        }
    }
    override fun drop(item: Item, cell: Int): Heap {
        if (!keyDropped && item is SkeletonKey) {
            keyDropped = true
            CellEmitter.get(arenaDoor).start(Speck.factory(Speck.ROCK), 0.07f, 10)
            set(arenaDoor, Terrain.EMPTY_DECO)
            GameScene.updateMap(arenaDoor)
            Dungeon.observe()
        }
        return super.drop(item, cell)
    }
    private fun outsideEntraceRoom(cell: Int): Boolean {
        val cx = cell % WIDTH
        val cy = cell / WIDTH
        return cx < ROOM_LEFT - 1 || cx > ROOM_RIGHT + 1 || cy < ROOM_TOP - 1 || cy > ROOM_BOTTOM + 1
    }
    override fun tileName(tile: Int): String {
        return when (tile) {
            Terrain.GRASS -> "Fluorescent moss"
            Terrain.HIGH_GRASS -> "Fluorescent mushrooms"
            Terrain.WATER -> "Freezing cold water."
            else -> super.tileName(tile)
        }
    }
    override fun tileDesc(tile: Int): String {
        return when (tile) {
            Terrain.ENTRANCE -> "The ladder leads up to the upper depth."
            Terrain.EXIT -> "The ladder leads down to the lower depth."
            Terrain.HIGH_GRASS -> "Huge mushrooms block the view."
            Terrain.WALL_DECO -> "A vein of some ore is visible on the wall. Gold?"
            else -> super.tileDesc(tile)
        }
    }
    override fun addVisuals(scene: Scene) {
        CavesLevel.addVisuals(this, scene)
    }
    companion object {
        private const val ROOM_LEFT = WIDTH / 2 - 2
        private const val ROOM_RIGHT = WIDTH / 2 + 2
        private const val ROOM_TOP = HEIGHT / 2 - 2
        private const val ROOM_BOTTOM = HEIGHT / 2 + 2
        private const val DOOR = "door"
        private const val ENTERED = "entered"
        private const val DROPPED = "droppped"
    }
}
