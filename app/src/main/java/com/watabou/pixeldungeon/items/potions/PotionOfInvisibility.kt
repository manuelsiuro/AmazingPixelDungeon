package com.watabou.pixeldungeon.items.potions
import com.watabou.noosa.audio.Sample
import com.watabou.noosa.tweeners.AlphaTweener
import com.watabou.pixeldungeon.Assets
import com.watabou.pixeldungeon.actors.Char
import com.watabou.pixeldungeon.actors.buffs.Buffs
import com.watabou.pixeldungeon.actors.buffs.Invisibility
import com.watabou.pixeldungeon.actors.hero.Hero
import com.watabou.pixeldungeon.utils.GLog
class PotionOfInvisibility : Potion() {
    init {
        name = "Potion of Invisibility"
    }
    override fun apply(hero: Hero) {
        setKnown()
        Buffs.affect(hero, Invisibility::class.java, Invisibility.DURATION)
        GLog.i("You see your hands turn invisible!")
        Sample.play(Assets.SND_MELD)
    }
    override fun desc(): String {
        return "Drinking this potion will render you temporarily invisible. While invisible, " +
                "enemies will be unable to see you. Attacking an enemy, as well as using a wand or a scroll " +
                "before enemy's eyes, will dispel the effect."
    }
    override fun price(): Int {
        return if (isKnown) 40 * quantity else super.price()
    }
    companion object {
        private const val ALPHA = 0.4f
        fun melt(ch: Char) {
            val sprite = ch.sprite
            val parent = sprite?.parent
            if (parent != null) {
                parent.add(AlphaTweener(sprite, ALPHA, 0.4f))
            } else {
                sprite?.alpha(ALPHA)
            }
        }
    }
}
