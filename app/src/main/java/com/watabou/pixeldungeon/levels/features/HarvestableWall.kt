package com.watabou.pixeldungeon.levels.features

import com.watabou.noosa.audio.Sample
import com.watabou.pixeldungeon.Assets
import com.watabou.pixeldungeon.Dungeon
import com.watabou.pixeldungeon.actors.hero.Hero
import com.watabou.pixeldungeon.effects.CellEmitter
import com.watabou.pixeldungeon.effects.Speck
import com.watabou.pixeldungeon.items.crafting.Cobblestone
import com.watabou.pixeldungeon.items.crafting.DiamondShard
import com.watabou.pixeldungeon.items.crafting.GoldOre
import com.watabou.pixeldungeon.items.crafting.IronOre
import com.watabou.pixeldungeon.levels.Level
import com.watabou.pixeldungeon.levels.Terrain
import com.watabou.pixeldungeon.scenes.GameScene
import com.watabou.utils.Random

object HarvestableWall {
    fun mine(level: Level, pos: Int, hero: Hero) {
        Level.set(pos, Terrain.EMPTY)
        GameScene.updateMap(pos)

        // Drop cobblestone
        val n = Random.IntRange(1, 3)
        for (i in 0 until n) {
            level.drop(Cobblestone(), pos).sprite?.drop()
        }

        // Small chance for ore at deeper levels
        if (Dungeon.depth >= 6 && Random.Int(5) == 0) {
            level.drop(IronOre(), pos).sprite?.drop()
        }
        if (Dungeon.depth >= 11 && Random.Int(8) == 0) {
            level.drop(GoldOre(), pos).sprite?.drop()
        }
        if (Dungeon.depth >= 16 && Random.Int(15) == 0) {
            level.drop(DiamondShard(), pos).sprite?.drop()
        }

        CellEmitter.center(pos).burst(Speck.factory(Speck.STAR), 5)
        Sample.play(Assets.SND_ROCKS)
        Dungeon.observe()
    }
}
