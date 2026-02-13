package com.watabou.pixeldungeon.levels.features

import com.watabou.pixeldungeon.actors.Char
import com.watabou.pixeldungeon.actors.hero.Hero
import com.watabou.pixeldungeon.actors.mobs.Mob
import com.watabou.pixeldungeon.farming.CropManager
import com.watabou.pixeldungeon.levels.Level
import com.watabou.pixeldungeon.levels.Terrain
import com.watabou.pixeldungeon.scenes.GameScene
import com.watabou.pixeldungeon.utils.GLog
import com.watabou.pixeldungeon.Dungeon
import com.watabou.utils.Random

object Farmland {

    fun harvest(level: Level, pos: Int, ch: Char?) {
        if (ch is Hero) {
            val crop = level.crops.get(pos) ?: return
            crop.updateStage(CropManager.currentTime())
            if (crop.isMature) {
                CropManager.harvest(level, pos)
            }
            // Immature crops: do nothing, hero walks through safely
        }
    }

    fun mobTrample(level: Level, pos: Int, mob: Mob) {
        if (mob.flying) return
        val crop = level.crops.get(pos) ?: return

        if (Random.Float() < 0.10f) {
            // Drop seed only
            val seed = when (crop.cropType) {
                com.watabou.pixeldungeon.farming.CropType.WHEAT -> com.watabou.pixeldungeon.farming.WheatSeed()
                com.watabou.pixeldungeon.farming.CropType.CARROT -> com.watabou.pixeldungeon.farming.CarrotSeed()
                com.watabou.pixeldungeon.farming.CropType.POTATO -> com.watabou.pixeldungeon.farming.PotatoSeed()
                com.watabou.pixeldungeon.farming.CropType.MELON -> com.watabou.pixeldungeon.farming.MelonSeed()
            }
            level.drop(seed, pos).sprite?.drop()
            level.crops.remove(pos)
            GameScene.removeCrop(pos)

            Level.set(pos, Terrain.EMPTY)
            GameScene.updateMap(pos)

            if (Dungeon.visible[pos]) {
                GLog.w("A %s tramples the crop!", mob.name)
            }
        }
    }
}
