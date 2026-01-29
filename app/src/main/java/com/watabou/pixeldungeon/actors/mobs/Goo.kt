package com.watabou.pixeldungeon.actors.mobs
import com.watabou.pixeldungeon.Badges
import com.watabou.pixeldungeon.Dungeon
import com.watabou.pixeldungeon.Statistics
import com.watabou.pixeldungeon.actors.Char
import com.watabou.pixeldungeon.actors.blobs.ToxicGas
import com.watabou.pixeldungeon.actors.buffs.Buffs
import com.watabou.pixeldungeon.actors.buffs.Ooze
import com.watabou.pixeldungeon.effects.Speck
import com.watabou.pixeldungeon.items.LloydsBeacon
import com.watabou.pixeldungeon.items.keys.SkeletonKey
import com.watabou.pixeldungeon.items.scrolls.ScrollOfPsionicBlast
import com.watabou.pixeldungeon.items.weapon.enchantments.Death
import com.watabou.pixeldungeon.levels.Level
import com.watabou.pixeldungeon.levels.SewerBossLevel
import com.watabou.pixeldungeon.mechanics.Ballistica
import com.watabou.pixeldungeon.scenes.GameScene
import com.watabou.pixeldungeon.sprites.CharSprite
import com.watabou.pixeldungeon.sprites.GooSprite
import com.watabou.pixeldungeon.utils.GLog
import com.watabou.utils.Callback
import com.watabou.utils.Random
import java.util.*
class Goo : Mob() {
    init {
        name = if (Dungeon.depth == Statistics.deepestFloor) "Goo" else "spawn of Goo"
        HP = 80
        HT = HP
        EXP = 10
        defenseSkill = 12
        spriteClass = GooSprite::class.java
        loot = LloydsBeacon()
        lootChance = 0.333f
    }
    private var pumpedUp = false
    private var jumped = false
    override fun damageRoll(): Int {
        return if (pumpedUp) {
            Random.NormalIntRange(5, 30)
        } else {
            Random.NormalIntRange(2, 12)
        }
    }
    override fun attackSkill(target: Char?): Int {
        return if (pumpedUp && !jumped) 30 else 15
    }
    override fun dr(): Int {
        return 2
    }
    override fun act(): Boolean {
        if (Level.water[pos] && HP < HT) {
            sprite?.emitter()?.burst(Speck.factory(Speck.HEALING), 1)
            HP++
        }
        return super.act()
    }
    override fun canAttack(enemy: Char): Boolean {
        return if (pumpedUp) distance(enemy) <= 2 else super.canAttack(enemy)
    }
    override fun attackProc(enemy: Char, damage: Int): Int {
        if (Random.Int(3) == 0) {
            Buffs.affect(enemy, Ooze::class.java)
            enemy.sprite?.burst(0x000000, 5)
        }
        return damage
    }
    override fun doAttack(enemy: Char): Boolean {
        if (pumpedUp) {
            if (Level.adjacent(pos, enemy.pos)) {
                jumped = false
                return super.doAttack(enemy)
            } else {
                jumped = true
                if (Ballistica.cast(pos, enemy.pos, false, true) == enemy.pos) {
                    val dest = Ballistica.trace[Ballistica.distance - 2]
                    val afterJump = object : Callback {
                        override fun call() {
                            move(dest)
                            Dungeon.level?.mobPress(this@Goo)
                            super@Goo.doAttack(enemy)
                        }
                    }
                    return if (Dungeon.visible[pos] || Dungeon.visible[dest]) {
                        (sprite as GooSprite?)?.jump(pos, dest, afterJump)
                        false
                    } else {
                        afterJump.call()
                        true
                    }
                } else {
                    sprite?.idle()
                    pumpedUp = false
                    return true
                }
            }
        } else if (Random.Int(3) > 0) {
            return super.doAttack(enemy)
        } else {
            pumpedUp = true
            spend(PUMP_UP_DELAY)
            (sprite as GooSprite?)?.pumpUp()
            if (Dungeon.visible[pos]) {
                sprite?.showStatus(CharSprite.NEGATIVE, "!!!")
                GLog.n("Goo is pumping itself up!")
            }
            return true
        }
    }
    override fun attack(enemy: Char): Boolean {
        val result = super.attack(enemy)
        pumpedUp = false
        return result
    }
    override fun getCloser(target: Int): Boolean {
        pumpedUp = false
        return super.getCloser(target)
    }
    override fun move(step: Int) {
        (Dungeon.level as SewerBossLevel?)?.seal()
        super.move(step)
    }
    override fun die(src: Any?) {
        super.die(src)
        (Dungeon.level as SewerBossLevel?)?.unseal()
        GameScene.bossSlain()
        Dungeon.level?.drop(SkeletonKey(), pos)?.sprite?.drop()
        Badges.validateBossSlain()
        yell("glurp... glurp...")
    }
    override fun notice() {
        super.notice()
        yell("GLURP-GLURP!")
    }
    override fun description(): String {
        return "Little known about The Goo. It's quite possible that it is not even a creature, but rather a conglomerate of substances from the sewers that gained rudiments of free will."
    }
    override fun resistances(): HashSet<Class<*>> {
        return RESISTANCES
    }
    companion object {
        private const val PUMP_UP_DELAY = 2f
        private val RESISTANCES = hashSetOf<Class<*>>(
            ToxicGas::class.java,
            Death::class.java,
            ScrollOfPsionicBlast::class.java
        )
    }
}
