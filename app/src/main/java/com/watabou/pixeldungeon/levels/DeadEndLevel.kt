package com.watabou.pixeldungeon.levels
import com.watabou.pixeldungeon.Assets
import com.watabou.utils.Random
import java.util.Arrays
class DeadEndLevel : Level() {
    init {
        color1 = 0x534f3e
        color2 = 0xb9d661
    }
    override fun tilesTex(): String {
        return Assets.TILES_CAVES
    }
    override fun waterTex(): String {
        return Assets.WATER_HALLS
    }
    override fun build(): Boolean {
        Arrays.fill(map, Terrain.WALL)
        for (i in 2 until SIZE) {
            for (j in 2 until SIZE) {
                map[i * WIDTH + j] = Terrain.EMPTY
            }
        }
        for (i in 1..SIZE) {
            map[WIDTH * i + SIZE] = Terrain.WATER
            map[WIDTH * i + 1] = Terrain.WATER
            map[WIDTH * SIZE + i] = Terrain.WATER
            map[WIDTH + i] = Terrain.WATER
        }
        entrance = SIZE * WIDTH + SIZE / 2 + 1
        map[entrance] = Terrain.ENTRANCE
        exit = -1
        map[(SIZE / 2 + 1) * (WIDTH + 1)] = Terrain.SIGN
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
    }
    override fun createMobs() {
    }
    override fun createItems() {
    }
    override fun randomRespawnCell(): Int {
        return -1
    }
    companion object {
        private const val SIZE = 5
    }
}
