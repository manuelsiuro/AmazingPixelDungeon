package com.watabou.pixeldungeon.items

import com.watabou.noosa.audio.Sample
import com.watabou.pixeldungeon.Assets
import com.watabou.pixeldungeon.Dungeon
import com.watabou.pixeldungeon.actors.Actor
import com.watabou.pixeldungeon.actors.buffs.Blindness
import com.watabou.pixeldungeon.actors.buffs.Buffs
import com.watabou.pixeldungeon.actors.buffs.Invisibility
import com.watabou.pixeldungeon.effects.CellEmitter
import com.watabou.pixeldungeon.effects.particles.SmokeParticle
import com.watabou.pixeldungeon.levels.Level
import com.watabou.pixeldungeon.sprites.ItemSpriteSheet
import com.watabou.utils.Random

class SmokeBomb : Item() {

    init {
        name = "smoke bomb"
        image = ItemSpriteSheet.SMOKE_BOMB
        defaultAction = AC_THROW
        stackable = true
    }

    override fun onThrow(cell: Int) {
        if (Level.pit[cell]) {
            super.onThrow(cell)
        } else {
            Sample.play(Assets.SND_BLAST, 1f)

            for (n in Level.NEIGHBOURS9) {
                val c = cell + n
                if (c >= 0 && c < Level.LENGTH) {
                    if (Dungeon.visible[c]) {
                        CellEmitter.get(c).burst(SmokeParticle.FACTORY, 6)
                    }
                    val ch = Actor.findChar(c)
                    if (ch != null && ch !== Dungeon.hero) {
                        Buffs.prolong(ch, Blindness::class.java, 3f)
                    }
                }
            }

            // Self-targeted: also grant invisibility
            val hero = Dungeon.hero
            if (hero != null && Level.adjacent(hero.pos, cell) || hero?.pos == cell) {
                Buffs.prolong(hero, Invisibility::class.java, 3f)
            }
        }
    }

    override val isUpgradable: Boolean
        get() = false

    override val isIdentified: Boolean
        get() = true

    override fun random(): Item {
        quantity = Random.IntRange(1, 2)
        return this
    }

    override fun price(): Int {
        return 15 * quantity
    }

    override fun info(): String {
        return "A small pellet that erupts in a thick cloud of smoke on impact, blinding nearby enemies. " +
                "If thrown at your own feet, the cover also grants a brief moment of invisibility."
    }
}
