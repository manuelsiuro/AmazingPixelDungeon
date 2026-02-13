package com.watabou.pixeldungeon.farming

import com.watabou.pixeldungeon.Dungeon
import com.watabou.pixeldungeon.Statistics
import com.watabou.pixeldungeon.actors.Actor
import com.watabou.pixeldungeon.items.food.farming.Carrot
import com.watabou.pixeldungeon.items.food.farming.MelonSlice
import com.watabou.pixeldungeon.items.food.farming.Potato
import com.watabou.pixeldungeon.items.food.farming.Wheat
import com.watabou.pixeldungeon.levels.Level
import com.watabou.pixeldungeon.levels.Terrain
import com.watabou.pixeldungeon.scenes.GameScene
import com.watabou.pixeldungeon.utils.GLog
import com.watabou.utils.Random
import kotlin.math.sqrt

object CropManager {

    private const val WATER_RADIUS = 4
    private const val FARMLAND_DECAY_TIME = 200

    fun currentTime(): Float = Statistics.duration + Actor.now

    fun plantCrop(level: Level, cell: Int, cropType: CropType) {
        val hydrated = isNearWater(level, cell, WATER_RADIUS)
        val crop = CropData(cell, cropType, currentTime(), hydrated)
        level.crops.put(cell, crop)

        if (hydrated && level.map[cell] == Terrain.FARMLAND) {
            Level.set(cell, Terrain.HYDRATED_FARMLAND)
            GameScene.updateMap(cell)
        }

        GameScene.addCrop(crop)
        level.farmlandTimers.delete(cell)
    }

    fun harvest(level: Level, cell: Int): Boolean {
        val crop = level.crops.get(cell) ?: return false
        crop.updateStage(currentTime())

        if (!crop.isMature) return false

        val count = Random.IntRange(crop.cropType.minYield, crop.cropType.maxYield)
        for (i in 0 until count) {
            val produce = createProduce(crop.cropType)
            level.drop(produce, cell).sprite?.drop()
        }
        // Drop 1-2 seeds back
        val seedCount = Random.IntRange(1, 2)
        for (i in 0 until seedCount) {
            val seed = createSeed(crop.cropType)
            level.drop(seed, cell).sprite?.drop()
        }
        level.crops.remove(cell)
        GameScene.removeCrop(cell)
        GLog.i("You harvest the %s.", crop.cropType.cropName.lowercase())
        return true
    }

    fun updateCrops(level: Level) {
        val time = currentTime()

        // Update crop stages and hydration
        for (i in 0 until level.crops.size()) {
            val crop = level.crops.valueAt(i)
            crop.hydrated = isNearWater(level, crop.pos, WATER_RADIUS)
            crop.updateStage(time)

            // Update terrain for hydration visual
            val terrain = level.map[crop.pos]
            if (crop.hydrated && terrain == Terrain.FARMLAND) {
                Level.set(crop.pos, Terrain.HYDRATED_FARMLAND)
                GameScene.updateMap(crop.pos)
            } else if (!crop.hydrated && terrain == Terrain.HYDRATED_FARMLAND) {
                Level.set(crop.pos, Terrain.FARMLAND)
                GameScene.updateMap(crop.pos)
            }
        }

        // Decay empty farmland
        val toRemove = mutableListOf<Int>()
        for (i in 0 until level.farmlandTimers.size()) {
            val cell = level.farmlandTimers.keyAt(i)
            val tilledAt = level.farmlandTimers.valueAt(i)
            if (level.crops.get(cell) == null &&
                (time - tilledAt) >= FARMLAND_DECAY_TIME
            ) {
                toRemove.add(cell)
            }
        }
        for (cell in toRemove) {
            level.farmlandTimers.delete(cell)
            if (level.map[cell] == Terrain.FARMLAND || level.map[cell] == Terrain.HYDRATED_FARMLAND) {
                Level.set(cell, Terrain.EMPTY)
                GameScene.updateMap(cell)
            }
        }
    }

    fun catchUpGrowth(level: Level) {
        updateCrops(level)
    }

    fun isNearWater(level: Level, cell: Int, radius: Int): Boolean {
        val cx = cell % Level.WIDTH
        val cy = cell / Level.WIDTH
        val r2 = radius * radius

        val minY = maxOf(0, cy - radius)
        val maxY = minOf(Level.HEIGHT - 1, cy + radius)
        val minX = maxOf(0, cx - radius)
        val maxX = minOf(Level.WIDTH - 1, cx + radius)

        for (y in minY..maxY) {
            for (x in minX..maxX) {
                val dx = x - cx
                val dy = y - cy
                if (dx * dx + dy * dy <= r2) {
                    val pos = x + y * Level.WIDTH
                    if ((Terrain.flags[level.map[pos]] and Terrain.LIQUID) != 0) {
                        return true
                    }
                }
            }
        }
        return false
    }

    private fun createProduce(cropType: CropType): com.watabou.pixeldungeon.items.Item {
        return when (cropType) {
            CropType.WHEAT -> Wheat()
            CropType.CARROT -> Carrot()
            CropType.POTATO -> Potato()
            CropType.MELON -> MelonSlice()
        }
    }

    private fun createSeed(cropType: CropType): com.watabou.pixeldungeon.items.Item {
        return when (cropType) {
            CropType.WHEAT -> WheatSeed()
            CropType.CARROT -> CarrotSeed()
            CropType.POTATO -> PotatoSeed()
            CropType.MELON -> MelonSeed()
        }
    }
}
