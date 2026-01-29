package com.watabou.pixeldungeon.levels
import com.watabou.noosa.Scene
import com.watabou.pixeldungeon.Assets
import com.watabou.pixeldungeon.items.Amulet
import com.watabou.pixeldungeon.levels.painters.Painter
import com.watabou.utils.Random
import java.util.Arrays
class LastLevel : Level() {
    private var pedestal: Int = 0
    init {
        color1 = 0x801500
        color2 = 0xa68521
    }
    override fun tilesTex(): String {
        return Assets.TILES_HALLS
    }
    override fun waterTex(): String {
        return Assets.WATER_HALLS
    }
    override fun build(): Boolean {
        Arrays.fill(map, Terrain.WALL)
        Painter.fill(this, 1, 1, SIZE, SIZE, Terrain.WATER)
        Painter.fill(this, 2, 2, SIZE - 2, SIZE - 2, Terrain.EMPTY)
        Painter.fill(this, SIZE / 2, SIZE / 2, 3, 3, Terrain.EMPTY_SP)
        entrance = SIZE * WIDTH + SIZE / 2 + 1
        map[entrance] = Terrain.ENTRANCE
        exit = entrance - WIDTH * SIZE
        map[exit] = Terrain.LOCKED_EXIT
        pedestal = (SIZE / 2 + 1) * (WIDTH + 1)
        map[pedestal] = Terrain.PEDESTAL
        map[pedestal + 1] = Terrain.STATUE_SP
        map[pedestal - 1] = Terrain.STATUE_SP
        feeling = Feeling.NONE
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
    override fun createItems() {
        drop(Amulet(), pedestal)
    }
    override fun randomRespawnCell(): Int {
        return -1
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
        private const val SIZE = 7
    }
}
