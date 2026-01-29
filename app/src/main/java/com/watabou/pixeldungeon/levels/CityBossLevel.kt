package com.watabou.pixeldungeon.levels
import com.watabou.noosa.Scene
import com.watabou.noosa.tweeners.AlphaTweener
import com.watabou.pixeldungeon.Assets
import com.watabou.pixeldungeon.Bones
import com.watabou.pixeldungeon.Dungeon
import com.watabou.pixeldungeon.actors.Actor
import com.watabou.pixeldungeon.actors.Char
import com.watabou.pixeldungeon.actors.mobs.Bestiary
import com.watabou.pixeldungeon.actors.mobs.Mob
import com.watabou.pixeldungeon.items.Heap
import com.watabou.pixeldungeon.items.Item
import com.watabou.pixeldungeon.items.keys.SkeletonKey
import com.watabou.pixeldungeon.levels.painters.Painter
import com.watabou.pixeldungeon.scenes.GameScene
import com.watabou.utils.Bundle
import com.watabou.utils.Random
class CityBossLevel : Level() {
    init {
        color1 = 0x4b6636
        color2 = 0xf2f2f2
    }
    private var arenaDoor = 0
    private var enteredArena = false
    private var keyDropped = false
    override fun tilesTex(): String {
        return Assets.TILES_CITY
    }
    override fun waterTex(): String {
        return Assets.WATER_CITY
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
        Painter.fill(this, LEFT, TOP, HALL_WIDTH, HALL_HEIGHT, Terrain.EMPTY)
        Painter.fill(this, CENTER, TOP, 1, HALL_HEIGHT, Terrain.EMPTY_SP)
        var y = TOP + 1
        while (y < TOP + HALL_HEIGHT) {
            map[y * WIDTH + CENTER - 2] = Terrain.STATUE_SP
            map[y * WIDTH + CENTER + 2] = Terrain.STATUE_SP
            y += 2
        }
        val left = pedestal(true)
        val right = pedestal(false)
        map[right] = Terrain.PEDESTAL
        map[left] = Terrain.PEDESTAL
        for (i in left + 1 until right) {
            map[i] = Terrain.EMPTY_SP
        }
        exit = (TOP - 1) * WIDTH + CENTER
        map[exit] = Terrain.LOCKED_EXIT
        arenaDoor = (TOP + HALL_HEIGHT) * WIDTH + CENTER
        map[arenaDoor] = Terrain.DOOR
        Painter.fill(this, LEFT, TOP + HALL_HEIGHT + 1, HALL_WIDTH, CHAMBER_HEIGHT, Terrain.EMPTY)
        Painter.fill(this, LEFT, TOP + HALL_HEIGHT + 1, 1, CHAMBER_HEIGHT, Terrain.BOOKSHELF)
        Painter.fill(this, LEFT + HALL_WIDTH - 1, TOP + HALL_HEIGHT + 1, 1, CHAMBER_HEIGHT, Terrain.BOOKSHELF)
        entrance = (TOP + HALL_HEIGHT + 2 + Random.Int(CHAMBER_HEIGHT - 1)) * WIDTH + LEFT + (/*1 +*/ Random.Int(HALL_WIDTH - 2))
        map[entrance] = Terrain.ENTRANCE
        return true
    }
    override fun decorate() {
        for (i in 0 until LENGTH) {
            if (map[i] == Terrain.EMPTY && Random.Int(10) == 0) {
                map[i] = Terrain.EMPTY_DECO
            } else if (map[i] == Terrain.WALL && Random.Int(8) == 0) {
                map[i] = Terrain.WALL_DECO
            }
        }
        val sign = arenaDoor + WIDTH + 1
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
                pos = Random.IntRange(LEFT + 1, LEFT + HALL_WIDTH - 2) +
                        Random.IntRange(TOP + HALL_HEIGHT + 1, TOP + HALL_HEIGHT + CHAMBER_HEIGHT) * WIDTH
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
            var count = 0
            do {
                boss.pos = Random.Int(LENGTH)
            } while (
                !passable[boss.pos] ||
                !outsideEntraceRoom(boss.pos) ||
                (Dungeon.visible[boss.pos] && count++ < 20))
            GameScene.add(boss)
            if (Dungeon.visible[boss.pos]) {
                boss.notice()
                boss.sprite?.let { sprite ->
                    sprite.alpha(0f)
                    sprite.parent?.add(AlphaTweener(sprite, 1f, 0.1f))
                }
            }
            set(arenaDoor, Terrain.LOCKED_DOOR)
            GameScene.updateMap(arenaDoor)
            Dungeon.observe()
        }
    }
    override fun drop(item: Item, cell: Int): Heap {
        if (!keyDropped && item is SkeletonKey) {
            keyDropped = true
            set(arenaDoor, Terrain.DOOR)
            GameScene.updateMap(arenaDoor)
            Dungeon.observe()
        }
        return super.drop(item, cell)
    }
    private fun outsideEntraceRoom(cell: Int): Boolean {
        return cell / WIDTH < arenaDoor / WIDTH
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
        CityLevel.addVisuals(this, scene)
    }
    companion object {
        private const val TOP = 2
        private const val HALL_WIDTH = 7
        private const val HALL_HEIGHT = 15
        private const val CHAMBER_HEIGHT = 3
        private const val LEFT = (WIDTH - HALL_WIDTH) / 2
        private const val CENTER = LEFT + HALL_WIDTH / 2
        private const val DOOR = "door"
        private const val ENTERED = "entered"
        private const val DROPPED = "droppped"
        fun pedestal(left: Boolean): Int {
            return if (left) {
                (TOP + HALL_HEIGHT / 2) * WIDTH + CENTER - 2
            } else {
                (TOP + HALL_HEIGHT / 2) * WIDTH + CENTER + 2
            }
        }
    }
}
