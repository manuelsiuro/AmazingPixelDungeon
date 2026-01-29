package com.watabou.pixeldungeon.actors.mobs
import com.watabou.noosa.Camera
import com.watabou.pixeldungeon.Dungeon
import com.watabou.pixeldungeon.ResultDescriptions
import com.watabou.pixeldungeon.actors.Char
import com.watabou.pixeldungeon.effects.particles.SparkParticle
import com.watabou.pixeldungeon.items.Generator
import com.watabou.pixeldungeon.levels.Level
import com.watabou.pixeldungeon.levels.traps.LightningTrap
import com.watabou.pixeldungeon.mechanics.Ballistica
import com.watabou.pixeldungeon.sprites.CharSprite
import com.watabou.pixeldungeon.sprites.ShamanSprite
import com.watabou.pixeldungeon.utils.GLog
import com.watabou.pixeldungeon.utils.Utils
import com.watabou.utils.Callback
import com.watabou.utils.Random
import java.util.*
class Shaman : Mob(), Callback {
    init {
        name = "gnoll shaman"
        spriteClass = ShamanSprite::class.java
        HT = 18
        HP = HT
        defenseSkill = 8
        EXP = 6
        maxLvl = 14
        loot = Generator.Category.SCROLL
        lootChance = 0.33f
    }
    override fun damageRoll(): Int {
        return Random.NormalIntRange(2, 6)
    }
    override fun attackSkill(target: Char?): Int {
        return 11
    }
    override fun dr(): Int {
        return 4
    }
    override fun canAttack(enemy: Char): Boolean {
        return Ballistica.cast(pos, enemy.pos, false, true) == enemy.pos
    }
    override fun doAttack(enemy: Char): Boolean {
        if (Level.distance(pos, enemy.pos) <= 1) {
            return super.doAttack(enemy)
        } else {
            val visible = Level.fieldOfView[pos] || Level.fieldOfView[enemy.pos]
            if (visible) {
                (sprite as ShamanSprite?)?.zap(enemy.pos)
            }
            spend(TIME_TO_ZAP)
            if (hit(this, enemy, true)) {
                var dmg = Random.Int(2, 12).toFloat()
                if (Level.water[enemy.pos] && !enemy.flying) {
                    dmg *= 1.5f
                }
                enemy.damage(dmg.toInt(), LightningTrap.LIGHTNING)
                enemy.sprite?.centerEmitter()?.burst(SparkParticle.FACTORY, 3)
                enemy.sprite?.flash()
                if (enemy === Dungeon.hero) {
                    Camera.main?.shake(2f, 0.3f)
                    if (!enemy.isAlive) {
                        Dungeon.fail(
                            Utils.format(
                                ResultDescriptions.MOB,
                                Utils.indefinite(name), Dungeon.depth
                            )
                        )
                        GLog.n(TXT_LIGHTNING_KILLED, name)
                    }
                }
            } else {
                enemy.sprite?.showStatus(CharSprite.NEUTRAL, enemy.defenseVerb())
            }
            return !visible
        }
    }
    override fun call() {
        next()
    }
    override fun description(): String {
        return "The most intelligent gnolls can master shamanistic magic. Gnoll shamans prefer battle spells to compensate for lack of might, not hesitating to use them on those who question their status in a tribe."
    }
    override fun resistances(): HashSet<Class<*>> {
        return RESISTANCES
    }
    companion object {
        private const val TIME_TO_ZAP = 2f
        private const val TXT_LIGHTNING_KILLED = "%s's lightning bolt killed you..."
        private val RESISTANCES = hashSetOf<Class<*>>(LightningTrap.Electricity::class.java)
    }
}
