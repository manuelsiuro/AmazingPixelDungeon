package com.watabou.pixeldungeon.items.armor
import com.watabou.noosa.Camera
import com.watabou.pixeldungeon.Dungeon
import com.watabou.pixeldungeon.actors.Actor
import com.watabou.pixeldungeon.actors.Char
import com.watabou.pixeldungeon.actors.buffs.Buffs
import com.watabou.pixeldungeon.actors.buffs.Fury
import com.watabou.pixeldungeon.actors.buffs.Invisibility
import com.watabou.pixeldungeon.actors.buffs.Paralysis
import com.watabou.pixeldungeon.actors.hero.Hero
import com.watabou.pixeldungeon.actors.hero.HeroClass
import com.watabou.pixeldungeon.actors.hero.HeroSubClass
import com.watabou.pixeldungeon.effects.CellEmitter
import com.watabou.pixeldungeon.effects.Speck
import com.watabou.pixeldungeon.levels.Level
import com.watabou.pixeldungeon.mechanics.Ballistica
import com.watabou.pixeldungeon.scenes.CellSelector
import com.watabou.pixeldungeon.scenes.GameScene
import com.watabou.pixeldungeon.sprites.ItemSpriteSheet
import com.watabou.pixeldungeon.utils.GLog
import com.watabou.utils.Callback
class WarriorArmor : ClassArmor() {
    init {
        name = "warrior suit of armor"
        image = ItemSpriteSheet.ARMOR_WARRIOR
    }
    override fun special(): String {
        return AC_SPECIAL
    }
    override fun doSpecial() {
        GameScene.selectCell(leaper)
    }
    override fun doEquip(hero: Hero): Boolean {
        if (hero.heroClass == HeroClass.WARRIOR) {
            return super.doEquip(hero)
        } else {
            GLog.w(TXT_NOT_WARRIOR)
            return false
        }
    }
    override fun desc(): String {
        return "While this armor looks heavy, it allows a warrior to perform heroic leap towards " +
                "a targeted location, slamming down to stun all neighbouring enemies."
    }
    companion object {
        private const val LEAP_TIME = 1f
        private const val SHOCK_TIME = 3f
        private const val AC_SPECIAL = "HEROIC LEAP"
        private const val TXT_NOT_WARRIOR = "Only warriors can use this armor!"
        protected var leaper: CellSelector.Listener = object : CellSelector.Listener {
            override fun onSelect(cell: Int?) {
                if (cell != null && cell != curUser?.pos) {
                    val user = curUser ?: return
                    var targetCell = Ballistica.cast(user.pos, cell, false, true)
                    if (Actor.findChar(targetCell) != null && targetCell != user.pos) {
                        targetCell = Ballistica.trace[Ballistica.distance - 2]
                    }
                    user.HP -= (user.HP / 3)
                    if (user.subClass == HeroSubClass.BERSERKER && user.HP <= user.HT * Fury.LEVEL) {
                        Buffs.affect(user, Fury::class.java)
                    }
                    Invisibility.dispel()
                    val dest = targetCell
                    user.busy()
                    user.sprite?.jump(user.pos, targetCell, object : Callback {
                        override fun call() {
                            user.move(dest)
                            Dungeon.level?.press(dest, user)
                            Dungeon.observe()
                            for (i in Level.NEIGHBOURS8.indices) {
                                val mob = Actor.findChar(user.pos + Level.NEIGHBOURS8[i])
                                if (mob != null && mob !== user) {
                                    Buffs.prolong(mob, Paralysis::class.java, SHOCK_TIME)
                                }
                            }
                            CellEmitter.center(dest).burst(Speck.factory(Speck.DUST), 10)
                            Camera.main?.shake(2f, 0.5f)
                            user.spendAndNext(LEAP_TIME)
                        }
                    })
                }
            }
            override fun prompt(): String {
                return "Choose direction to leap"
            }
        }
    }
}
