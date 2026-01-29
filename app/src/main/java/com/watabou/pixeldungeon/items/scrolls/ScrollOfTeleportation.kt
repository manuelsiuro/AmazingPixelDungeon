package com.watabou.pixeldungeon.items.scrolls
import com.watabou.noosa.audio.Sample
import com.watabou.pixeldungeon.Assets
import com.watabou.pixeldungeon.Dungeon
import com.watabou.pixeldungeon.actors.buffs.Invisibility
import com.watabou.pixeldungeon.actors.hero.Hero
import com.watabou.pixeldungeon.items.wands.WandOfBlink
import com.watabou.pixeldungeon.utils.GLog
class ScrollOfTeleportation : Scroll() {
    init {
        name = "Scroll of Teleportation"
    }
    override fun doRead() {
        Sample.play(Assets.SND_READ)
        Invisibility.dispel()
        val user = curUser ?: return
        teleportHero(user)
        setKnown()
        readAnimation()
    }
    override fun desc(): String {
        return "The spell on this parchment instantly transports the reader " +
                "to a random location on the dungeon level. It can be used " +
                "to escape a dangerous situation, but the unlucky reader might " +
                "find himself in an even more dangerous place."
    }
    override fun price(): Int {
        return if (isKnown) 40 * quantity else super.price()
    }
    companion object {
        const val TXT_TELEPORTED = "In a blink of an eye you were teleported to another location of the level."
        const val TXT_NO_TELEPORT = "Strong magic aura of this place prevents you from teleporting!"
        fun teleportHero(hero: Hero) {
            val level = Dungeon.level ?: return
            var count = 10
            var pos: Int
            do {
                pos = level.randomRespawnCell()
                if (count-- <= 0) {
                    break
                }
            } while (pos == -1)
            if (pos == -1) {
                GLog.w(TXT_NO_TELEPORT)
            } else {
                WandOfBlink.appear(hero, pos)
                level.press(pos, hero)
                Dungeon.observe()
                GLog.i(TXT_TELEPORTED)
            }
        }
    }
}
