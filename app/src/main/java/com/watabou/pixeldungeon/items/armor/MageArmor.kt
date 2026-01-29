package com.watabou.pixeldungeon.items.armor
import com.watabou.noosa.audio.Sample
import com.watabou.pixeldungeon.Assets
import com.watabou.pixeldungeon.Dungeon
import com.watabou.pixeldungeon.actors.Actor
import com.watabou.pixeldungeon.actors.buffs.Buffs
import com.watabou.pixeldungeon.actors.buffs.Burning
import com.watabou.pixeldungeon.actors.buffs.Roots
import com.watabou.pixeldungeon.actors.hero.Hero
import com.watabou.pixeldungeon.actors.hero.HeroClass
import com.watabou.pixeldungeon.effects.particles.ElmoParticle
import com.watabou.pixeldungeon.levels.Level
import com.watabou.pixeldungeon.sprites.ItemSpriteSheet
import com.watabou.pixeldungeon.utils.GLog
class MageArmor : ClassArmor() {
    init {
        name = "mage robe"
        image = ItemSpriteSheet.ARMOR_MAGE
    }
    override fun special(): String {
        return AC_SPECIAL
    }
    override fun desc(): String {
        return "Wearing this gorgeous robe, a mage can cast a spell of molten earth: all the enemies " +
                "in his field of view will be set on fire and unable to move at the same time."
    }
    override fun doSpecial() {
        val user = curUser ?: return
        for (mob in Dungeon.level?.mobs ?: emptyList()) {
            if (Level.fieldOfView[mob.pos]) {
                Buffs.affect(mob, Burning::class.java)?.reignite(mob)
                Buffs.prolong(mob, Roots::class.java, 3f)
            }
        }
        user.HP -= (user.HP / 3)
        user.spend(Actor.TICK)
        user.sprite?.operate(user.pos)
        user.busy()
        user.sprite?.centerEmitter()?.start(ElmoParticle.FACTORY, 0.15f, 4)
        Sample.play(Assets.SND_READ)
    }
    override fun doEquip(hero: Hero): Boolean {
        if (hero.heroClass == HeroClass.MAGE) {
            return super.doEquip(hero)
        } else {
            GLog.w(TXT_NOT_MAGE)
            return false
        }
    }
    companion object {
        private const val AC_SPECIAL = "MOLTEN EARTH"
        private const val TXT_NOT_MAGE = "Only mages can use this armor!"
    }
}
