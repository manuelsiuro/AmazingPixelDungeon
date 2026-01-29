package com.watabou.pixeldungeon.actors.mobs
import com.watabou.noosa.tweeners.AlphaTweener
import com.watabou.pixeldungeon.Dungeon
import com.watabou.pixeldungeon.actors.Actor
import com.watabou.pixeldungeon.actors.Char
import com.watabou.pixeldungeon.actors.buffs.Terror
import com.watabou.pixeldungeon.effects.particles.ShadowParticle
import com.watabou.pixeldungeon.items.weapon.enchantments.Death
import com.watabou.pixeldungeon.levels.Level
import com.watabou.pixeldungeon.scenes.GameScene
import com.watabou.pixeldungeon.sprites.WraithSprite
import com.watabou.utils.Bundle
import com.watabou.utils.Random
import java.util.*
class Wraith : Mob() {
    private var level: Int = 0
    init {
        name = "wraith"
        spriteClass = WraithSprite::class.java
        HT = 1
        HP = HT
        EXP = 0
        flying = true
    }
    override fun storeInBundle(bundle: Bundle) {
        super.storeInBundle(bundle)
        bundle.put(LEVEL, level)
    }
    override fun restoreFromBundle(bundle: Bundle) {
        super.restoreFromBundle(bundle)
        level = bundle.getInt(LEVEL)
        adjustStats(level)
    }
    override fun damageRoll(): Int {
        return Random.NormalIntRange(1, 3 + level)
    }
    override fun attackSkill(target: Char?): Int {
        return 10 + level
    }
    fun adjustStats(level: Int) {
        this.level = level
        defenseSkill = attackSkill(null) * 5
        enemySeen = true
    }
    override fun defenseVerb(): String {
        return "evaded"
    }
    override fun reset(): Boolean {
        state = WANDERING
        return true
    }
    override fun description(): String {
        return "A wraith is a vengeful spirit of a sinner, whose grave or tomb was disturbed. Being an ethereal entity, it is very hard to hit with a regular weapon."
    }
    override fun immunities(): HashSet<Class<*>> {
        return IMMUNITIES
    }
    companion object {
        private const val SPAWN_DELAY = 2f
        private const val LEVEL = "level"
        private val IMMUNITIES = hashSetOf<Class<*>>(Death::class.java, Terror::class.java)
        fun spawnAround(pos: Int) {
            for (n in Level.NEIGHBOURS4) {
                val cell = pos + n
                if (Level.passable[cell] && Actor.findChar(cell) == null) {
                    spawnAt(cell)
                }
            }
        }
        fun spawnAt(pos: Int): Wraith? {
            if (Level.passable[pos] && Actor.findChar(pos) == null) {
                val w = Wraith()
                w.adjustStats(Dungeon.depth)
                w.pos = pos
                w.state = w.HUNTING
                GameScene.add(w, SPAWN_DELAY)
                val s = w.sprite
                if (s != null) {
                    s.alpha(0f)
                    s.parent?.add(AlphaTweener(s, 1f, 0.5f))
                    s.emitter().burst(ShadowParticle.CURSE, 5)
                }
                return w
            } else {
                return null
            }
        }
    }
}
