package com.watabou.pixeldungeon.actors.mobs
import com.watabou.pixeldungeon.Dungeon
import com.watabou.pixeldungeon.ResultDescriptions
import com.watabou.pixeldungeon.actors.Actor
import com.watabou.pixeldungeon.actors.Char
import com.watabou.pixeldungeon.actors.buffs.Light
import com.watabou.pixeldungeon.actors.buffs.Terror
import com.watabou.pixeldungeon.effects.CellEmitter
import com.watabou.pixeldungeon.effects.particles.PurpleParticle
import com.watabou.pixeldungeon.items.Dewdrop
import com.watabou.pixeldungeon.items.wands.WandOfDisintegration
import com.watabou.pixeldungeon.items.weapon.enchantments.Death
import com.watabou.pixeldungeon.items.weapon.enchantments.Leech
import com.watabou.pixeldungeon.mechanics.Ballistica
import com.watabou.pixeldungeon.sprites.CharSprite
import com.watabou.pixeldungeon.sprites.EyeSprite
import com.watabou.pixeldungeon.utils.GLog
import com.watabou.pixeldungeon.utils.Utils
import com.watabou.utils.Random
import java.util.*
class Eye : Mob() {
    private var hitCell = 0
    init {
        name = "evil eye"
        spriteClass = EyeSprite::class.java
        HT = 100
        HP = HT
        defenseSkill = 20
        viewDistance = Light.DISTANCE
        EXP = 13
        maxLvl = 25
        flying = true
        loot = Dewdrop()
        lootChance = 0.5f
    }
    override fun dr(): Int {
        return 10
    }
    override fun canAttack(enemy: Char): Boolean {
        hitCell = Ballistica.cast(pos, enemy.pos, true, false)
        for (i in 1 until Ballistica.distance) {
            if (Ballistica.trace[i] == enemy.pos) {
                return true
            }
        }
        return false
    }
    override fun attackSkill(target: Char?): Int {
        return 30
    }
    override fun attackDelay(): Float {
        return 1.6f
    }
    override fun doAttack(enemy: Char): Boolean {
        spend(attackDelay())
        var rayVisible = false
        val visible = Dungeon.visible
        for (i in 0 until Ballistica.distance) {
            if (visible[Ballistica.trace[i]]) {
                rayVisible = true
            }
        }
        return if (rayVisible) {
            sprite?.attack(hitCell)
            false
        } else {
            attack(enemy)
            true
        }
    }
    override fun attack(enemy: Char): Boolean {
        for (i in 1 until Ballistica.distance) {
            val pos = Ballistica.trace[i]
            val ch = Actor.findChar(pos)
            if (ch == null) {
                continue
            }
            if (hit(this, ch, true)) {
                ch.damage(Random.NormalIntRange(14, 20), this)
                if (Dungeon.visible[pos]) {
                    ch.sprite?.flash()
                    CellEmitter.center(pos).burst(PurpleParticle.BURST, Random.IntRange(1, 2))
                }
                if (!ch.isAlive && ch === Dungeon.hero) {
                    Dungeon.fail(Utils.format(ResultDescriptions.MOB, Utils.indefinite(name), Dungeon.depth))
                    GLog.n(TXT_DEATHGAZE_KILLED, name)
                }
            } else {
                ch.sprite?.showStatus(CharSprite.NEUTRAL, ch.defenseVerb())
            }
        }
        return true
    }
    override fun description(): String {
        return "One of this demon's other names is \"orb of hatred\", because when it sees an enemy, it uses its deathgaze recklessly, often ignoring its allies and wounding them."
    }
    override fun resistances(): HashSet<Class<*>> {
        return RESISTANCES
    }
    override fun immunities(): HashSet<Class<*>> {
        return IMMUNITIES
    }
    companion object {
        private const val TXT_DEATHGAZE_KILLED = "%s's deathgaze killed you..."
        private val RESISTANCES = hashSetOf<Class<*>>(
            WandOfDisintegration::class.java,
            Death::class.java,
            Leech::class.java
        )
        private val IMMUNITIES = hashSetOf<Class<*>>(Terror::class.java)
    }
}
