package com.watabou.pixeldungeon.items.armor
import com.watabou.pixeldungeon.Dungeon
import com.watabou.pixeldungeon.actors.hero.Hero
import com.watabou.pixeldungeon.actors.hero.HeroClass
import com.watabou.pixeldungeon.actors.mobs.Mob
import com.watabou.pixeldungeon.items.Item
import com.watabou.pixeldungeon.items.weapon.missiles.Shuriken
import com.watabou.pixeldungeon.levels.Level
import com.watabou.pixeldungeon.sprites.ItemSpriteSheet
import com.watabou.pixeldungeon.sprites.MissileSprite
import com.watabou.pixeldungeon.utils.GLog
import com.watabou.utils.Callback
import java.util.HashMap
class HuntressArmor : ClassArmor() {
    init {
        name = "huntress cloak"
        image = ItemSpriteSheet.ARMOR_HUNTRESS
    }
    private val targets = HashMap<Callback, Mob>()
    override fun special(): String {
        return AC_SPECIAL
    }
    override fun doSpecial() {
        val user = curUser ?: return
        val proto = Shuriken()
        for (mob in Dungeon.level?.mobs ?: emptyList()) {
            if (Level.fieldOfView[mob.pos]) {
                val callback = object : Callback {
                    override fun call() {
                        val target = targets[this]
                        if (target != null) {
                            user.attack(target)
                        }
                        targets.remove(this)
                        if (targets.isEmpty()) {
                            user.spendAndNext(user.attackDelay())
                        }
                    }
                }
                (user.sprite?.parent?.recycle(MissileSprite::class.java) as? MissileSprite)?.reset(user.pos, mob.pos, proto, callback)
                targets[callback] = mob
            }
        }
        if (targets.isEmpty()) {
            GLog.w(TXT_NO_ENEMIES)
            return
        }
        user.HP -= (user.HP / 3)
        user.sprite?.zap(user.pos)
        user.busy()
    }
    override fun doEquip(hero: Hero): Boolean {
        if (hero.heroClass == HeroClass.HUNTRESS) {
            return super.doEquip(hero)
        } else {
            GLog.w(TXT_NOT_HUNTRESS)
            return false
        }
    }
    override fun desc(): String {
        return "A huntress in such cloak can create a fan of spectral blades. Each of these blades " +
                "will target a single enemy in the huntress's field of view, inflicting damage depending " +
                "on her currently equipped melee weapon."
    }
    companion object {
        private const val TXT_NO_ENEMIES = "No enemies in sight"
        private const val TXT_NOT_HUNTRESS = "Only huntresses can use this armor!"
        private const val AC_SPECIAL = "SPECTRAL BLADES"
    }
}
