package com.watabou.pixeldungeon.items.quest
import com.watabou.noosa.audio.Sample
import com.watabou.pixeldungeon.Assets
import com.watabou.pixeldungeon.actors.buffs.Buffs
import com.watabou.pixeldungeon.actors.buffs.Invisibility
import com.watabou.pixeldungeon.actors.hero.Hero
import com.watabou.pixeldungeon.items.Item
import com.watabou.pixeldungeon.sprites.ItemSpriteSheet
import com.watabou.pixeldungeon.utils.GLog
import java.util.ArrayList
class PhantomFish : Item() {
    init {
        name = "phantom fish"
        image = ItemSpriteSheet.PHANTOM
        unique = true
    }
    override fun actions(hero: Hero): ArrayList<String> {
        val actions = super.actions(hero)
        actions.add(AC_EAT)
        return actions
    }
    override fun execute(hero: Hero, action: String) {
        if (action == AC_EAT) {
            detach(hero.belongings.backpack)
            hero.sprite?.operate(hero.pos)
            hero.busy()
            Sample.play(Assets.SND_EAT)
            Sample.play(Assets.SND_MELD)
            GLog.i("You see your hands turn invisible!")
            Buffs.affect(hero, Invisibility::class.java, Invisibility.DURATION)
            hero.spend(TIME_TO_EAT)
        } else {
            super.execute(hero, action)
        }
    }
    override val isUpgradable: Boolean
        get() = false
    override val isIdentified: Boolean
        get() = true
    override fun info(): String {
        return "You can barely see this tiny translucent fish in the air. " +
                "In the water it becomes effectively invisible."
    }
    companion object {
        private const val AC_EAT = "EAT"
        private const val TIME_TO_EAT = 2f
    }
}
