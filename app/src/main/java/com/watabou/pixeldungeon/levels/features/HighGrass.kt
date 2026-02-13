package com.watabou.pixeldungeon.levels.features
import com.watabou.pixeldungeon.Challenges
import com.watabou.pixeldungeon.Dungeon
import com.watabou.pixeldungeon.actors.Char
import com.watabou.pixeldungeon.actors.buffs.Barkskin
import com.watabou.pixeldungeon.actors.buffs.Buffs
import com.watabou.pixeldungeon.actors.hero.Hero
import com.watabou.pixeldungeon.actors.hero.HeroSubClass
import com.watabou.pixeldungeon.effects.CellEmitter
import com.watabou.pixeldungeon.effects.particles.LeafParticle
import com.watabou.pixeldungeon.items.Dewdrop
import com.watabou.pixeldungeon.items.Generator
import com.watabou.pixeldungeon.items.crafting.Fiber
import com.watabou.pixeldungeon.items.crafting.Stick
import com.watabou.pixeldungeon.items.rings.RingOfHerbalism
import com.watabou.pixeldungeon.levels.Level
import com.watabou.pixeldungeon.levels.Terrain
import com.watabou.pixeldungeon.scenes.GameScene
import com.watabou.utils.Random
object HighGrass {
    fun trample(level: Level, pos: Int, ch: Char?) {
        Level.set(pos, Terrain.GRASS)
        GameScene.updateMap(pos)
        if (!Dungeon.isChallenged(Challenges.NO_HERBALISM)) {
            var herbalismLevel = 0
            if (ch != null) {
                val herbalism = ch.buff(RingOfHerbalism.Herbalism::class.java)
                if (herbalism != null) {
                    herbalismLevel = herbalism.level
                }
            }
            // Seed
            if (herbalismLevel >= 0 && Random.Int(18) <= Random.Int(herbalismLevel + 1)) {
                Generator.random(Generator.Category.SEED)?.let { level.drop(it, pos).sprite?.drop() }
            }
            // Dew
            if (herbalismLevel >= 0 && Random.Int(6) <= Random.Int(herbalismLevel + 1)) {
                level.drop(Dewdrop(), pos).sprite?.drop()
            }
        }
        // Crafting material drops
        if (Random.Int(10) < 3) { // 30% chance
            level.drop(Fiber(), pos).sprite?.drop()
        }
        if (Random.Int(20) < 3) { // 15% chance
            level.drop(Stick(), pos).sprite?.drop()
        }

        var leaves = 4
        // Warlock's barkskin
        if (ch is Hero && ch.subClass == HeroSubClass.WARDEN) {
            Buffs.affect(ch, Barkskin::class.java)?.level(ch.HT / 3)
            leaves = 8
        }
        CellEmitter.get(pos).burst(LeafParticle.LEVEL_SPECIFIC, leaves)
        Dungeon.observe()
    }
}
