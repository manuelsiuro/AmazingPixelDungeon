package com.watabou.pixeldungeon.items
import com.watabou.noosa.audio.Sample
import com.watabou.pixeldungeon.Assets
import com.watabou.pixeldungeon.Badges
import com.watabou.pixeldungeon.Dungeon
import com.watabou.pixeldungeon.actors.buffs.Blindness
import com.watabou.pixeldungeon.actors.buffs.Buffs
import com.watabou.pixeldungeon.actors.buffs.Fury
import com.watabou.pixeldungeon.actors.hero.Hero
import com.watabou.pixeldungeon.actors.hero.HeroClass
import com.watabou.pixeldungeon.actors.hero.HeroSubClass
import com.watabou.pixeldungeon.effects.Speck
import com.watabou.pixeldungeon.effects.SpellSprite
import com.watabou.pixeldungeon.scenes.GameScene
import com.watabou.pixeldungeon.sprites.ItemSpriteSheet
import com.watabou.pixeldungeon.utils.GLog
import com.watabou.pixeldungeon.utils.Utils
import com.watabou.pixeldungeon.windows.WndChooseWay
import java.util.ArrayList
class TomeOfMastery : Item() {
    init {
        stackable = false
        name = if (Dungeon.hero?.subClass != null && Dungeon.hero?.subClass != HeroSubClass.NONE)
            "Tome of Remastery"
        else
            "Tome of Mastery"
        image = ItemSpriteSheet.MASTERY
        unique = true
    }
    override fun actions(hero: Hero): ArrayList<String> {
        val actions = super.actions(hero)
        actions.add(AC_READ)
        return actions
    }
    override fun execute(hero: Hero, action: String) {
        if (action == AC_READ) {
            if (hero.buff(Blindness::class.java) != null) {
                GLog.w(TXT_BLINDED)
                return
            }
            curUser = hero
            when (hero.heroClass) {
                HeroClass.WARRIOR -> read(hero, HeroSubClass.GLADIATOR, HeroSubClass.BERSERKER)
                HeroClass.MAGE -> read(hero, HeroSubClass.BATTLEMAGE, HeroSubClass.WARLOCK)
                HeroClass.ROGUE -> read(hero, HeroSubClass.ASSASSIN, HeroSubClass.FREERUNNER)
                HeroClass.HUNTRESS -> read(hero, HeroSubClass.SNIPER, HeroSubClass.WARDEN)
            }
        } else {
            super.execute(hero, action)
        }
    }
    override fun doPickUp(hero: Hero): Boolean {
        Badges.validateMastery()
        return super.doPickUp(hero)
    }
    override val isUpgradable: Boolean
        get() = false
    override val isIdentified: Boolean
        get() = true
    override fun info(): String {
        return "This worn leather book is not that thick, but you feel somehow, " +
                "that you can gather a lot from it. Remember though that reading " +
                "this tome may require some time."
    }
    private fun read(hero: Hero, sc1: HeroSubClass, sc2: HeroSubClass) {
        if (hero.subClass == sc1) {
            GameScene.show(WndChooseWay(this, sc2))
        } else if (hero.subClass == sc2) {
            GameScene.show(WndChooseWay(this, sc1))
        } else {
            GameScene.show(WndChooseWay(this, sc1, sc2))
        }
    }
    fun choose(way: HeroSubClass) {
        val user = curUser ?: return
        detach(user.belongings.backpack)
        user.spend(TIME_TO_READ)
        user.busy()
        user.subClass = way
        user.sprite?.operate(user.pos)
        Sample.play(Assets.SND_MASTERY)
        SpellSprite.show(user, SpellSprite.MASTERY)
        user.sprite?.emitter()?.burst(Speck.factory(Speck.MASTERY), 12)
        val title = way.title() ?: ""
        GLog.w("You have chosen the way of the %s!", Utils.capitalize(title))
        if (way == HeroSubClass.BERSERKER && user.HP <= user.HT * Fury.LEVEL) {
            Buffs.affect(user, Fury::class.java)
        }
    }
    companion object {
        private const val TXT_BLINDED = "You can't read while blinded"
        const val TIME_TO_READ = 10f
        const val AC_READ = "READ"
    }
}
