package com.watabou.pixeldungeon.items.armor
import com.watabou.noosa.audio.Sample
import com.watabou.pixeldungeon.Assets
import com.watabou.pixeldungeon.Dungeon
import com.watabou.pixeldungeon.actors.Actor
import com.watabou.pixeldungeon.actors.buffs.Blindness
import com.watabou.pixeldungeon.actors.buffs.Buffs
import com.watabou.pixeldungeon.actors.hero.Hero
import com.watabou.pixeldungeon.actors.hero.HeroClass
import com.watabou.pixeldungeon.effects.CellEmitter
import com.watabou.pixeldungeon.effects.Speck
import com.watabou.pixeldungeon.items.wands.WandOfBlink
import com.watabou.pixeldungeon.levels.Level
import com.watabou.pixeldungeon.scenes.CellSelector
import com.watabou.pixeldungeon.scenes.GameScene
import com.watabou.pixeldungeon.sprites.ItemSpriteSheet
import com.watabou.pixeldungeon.utils.GLog
class RogueArmor : ClassArmor() {
    init {
        name = "rogue garb"
        image = ItemSpriteSheet.ARMOR_ROGUE
    }
    override fun special(): String {
        return AC_SPECIAL
    }
    override fun doSpecial() {
        GameScene.selectCell(teleporter)
    }
    override fun doEquip(hero: Hero): Boolean {
        if (hero.heroClass == HeroClass.ROGUE) {
            return super.doEquip(hero)
        } else {
            GLog.w(TXT_NOT_ROGUE)
            return false
        }
    }
    override fun desc(): String {
        return "Wearing this dark garb, a rogue can perform a trick, that is called \"smoke bomb\" " +
                "(though no real explosives are used): he blinds enemies who could see him and jumps aside."
    }
    companion object {
        private const val TXT_FOV = "You can only jump to an empty location in your field of view"
        private const val TXT_NOT_ROGUE = "Only rogues can use this armor!"
        private const val AC_SPECIAL = "SMOKE BOMB"
        protected var teleporter: CellSelector.Listener = object : CellSelector.Listener {
            override fun onSelect(cell: Int?) {
                if (cell != null) {
                    val user = curUser ?: return
                    if (!Level.fieldOfView[cell] ||
                        !(Level.passable[cell] || Level.avoid[cell]) ||
                        Actor.findChar(cell) != null
                    ) {
                        GLog.w(TXT_FOV)
                        return
                    }
                    user.HP -= (user.HP / 3)
                    for (mob in Dungeon.level?.mobs ?: emptyList()) {
                        if (Level.fieldOfView[mob.pos]) {
                            Buffs.prolong(mob, Blindness::class.java, 2f)
                            mob.state = mob.WANDERING
                            mob.sprite?.emitter()?.burst(Speck.factory(Speck.LIGHT), 4)
                        }
                    }
                    WandOfBlink.appear(user, cell)
                    CellEmitter.get(cell).burst(Speck.factory(Speck.WOOL), 10)
                    Sample.play(Assets.SND_PUFF)
                    Dungeon.level?.press(cell, user)
                    Dungeon.observe()
                    user.spendAndNext(Actor.TICK)
                }
            }
            override fun prompt(): String {
                return "Choose a location to jump to"
            }
        }
    }
}
