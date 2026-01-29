package com.watabou.pixeldungeon.items
import com.watabou.noosa.audio.Sample
import com.watabou.pixeldungeon.Assets
import com.watabou.pixeldungeon.Dungeon
import com.watabou.pixeldungeon.ResultDescriptions
import com.watabou.pixeldungeon.actors.Actor
import com.watabou.pixeldungeon.actors.buffs.Buffs
import com.watabou.pixeldungeon.actors.buffs.Paralysis
import com.watabou.pixeldungeon.effects.CellEmitter
import com.watabou.pixeldungeon.effects.particles.BlastParticle
import com.watabou.pixeldungeon.effects.particles.SmokeParticle
import com.watabou.pixeldungeon.levels.Level
import com.watabou.pixeldungeon.scenes.GameScene
import com.watabou.pixeldungeon.sprites.ItemSpriteSheet
import com.watabou.pixeldungeon.utils.GLog
import com.watabou.pixeldungeon.utils.Utils
import com.watabou.utils.Random
class Bomb : Item() {
    init {
        name = "bomb"
        image = ItemSpriteSheet.BOMB
        defaultAction = AC_THROW
        stackable = true
    }
    override fun onThrow(cell: Int) {
        if (Level.pit[cell]) {
            super.onThrow(cell)
        } else {
            Sample.play(Assets.SND_BLAST, 2f)
            if (Dungeon.visible[cell]) {
                CellEmitter.center(cell).burst(BlastParticle.FACTORY, 30)
            }
            var terrainAffected = false
            for (n in Level.NEIGHBOURS9) {
                val c = cell + n
                if (c >= 0 && c < Level.LENGTH) {
                    if (Dungeon.visible[c]) {
                        CellEmitter.get(c).burst(SmokeParticle.FACTORY, 4)
                    }
                    if (Level.flamable[c]) {
                        Dungeon.level?.destroy(c)
                        GameScene.updateMap(c)
                        terrainAffected = true
                    }
                    val ch = Actor.findChar(c)
                    if (ch != null) {
                        val dmg = Random.Int(1 + Dungeon.depth, 10 + Dungeon.depth * 2) - Random.Int(ch.dr())
                        if (dmg > 0) {
                            ch.damage(dmg, this)
                            if (ch.isAlive) {
                                Buffs.prolong(ch, Paralysis::class.java, 2f)
                            } else if (ch === Dungeon.hero) {
                                Dungeon.fail(Utils.format(ResultDescriptions.BOMB, Dungeon.depth))
                                GLog.n("You killed yourself with a bomb...")
                            }
                        }
                    }
                }
            }
            if (terrainAffected) {
                Dungeon.observe()
            }
        }
    }
    override val isUpgradable: Boolean
        get() = false
    override val isIdentified: Boolean
        get() = true
    override fun random(): Item {
        quantity = Random.IntRange(1, 3)
        return this
    }
    override fun price(): Int {
        return 10 * quantity
    }
    override fun info(): String {
        return "This is a relatively small bomb, filled with black powder. Conveniently, its fuse is lit automatically when the bomb is thrown."
    }
}
