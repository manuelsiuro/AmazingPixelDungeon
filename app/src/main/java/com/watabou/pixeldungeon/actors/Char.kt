package com.watabou.pixeldungeon.actors
import com.watabou.noosa.Camera
import com.watabou.noosa.audio.Sample
import com.watabou.pixeldungeon.Assets
import com.watabou.pixeldungeon.Dungeon
import com.watabou.pixeldungeon.ResultDescriptions
import com.watabou.pixeldungeon.actors.buffs.*
import com.watabou.pixeldungeon.actors.hero.Hero
import com.watabou.pixeldungeon.actors.hero.HeroSubClass
import com.watabou.pixeldungeon.actors.mobs.Bestiary
import com.watabou.pixeldungeon.effects.CellEmitter
import com.watabou.pixeldungeon.effects.particles.PoisonParticle
import com.watabou.pixeldungeon.levels.Level
import com.watabou.pixeldungeon.levels.Terrain
import com.watabou.pixeldungeon.levels.features.Door
import com.watabou.pixeldungeon.sprites.CharSprite
import com.watabou.pixeldungeon.utils.GLog
import com.watabou.pixeldungeon.utils.Utils
import com.watabou.utils.Bundlable
import com.watabou.utils.Bundle
import com.watabou.utils.GameMath
import com.watabou.utils.Random
import java.util.*
abstract class Char : Actor() {
    var pos = 0
    var sprite: CharSprite? = null
    var name = "mob"
    var HT = 0
    var HP = 0
    protected var baseSpeed = 1f
    var paralysed = false
    var rooted = false
    var flying = false
    var invisible = 0
    var viewDistance = 8
    private val buffs = HashSet<Buff>()
    override fun act(): Boolean {
        Dungeon.level?.updateFieldOfView(this)
        return false
    }
    override fun storeInBundle(bundle: Bundle) {
        super.storeInBundle(bundle)
        bundle.put(POS, pos)
        bundle.put(TAG_HP, HP)
        bundle.put(TAG_HT, HT)
        bundle.put(BUFFS, buffs)
    }
    override fun restoreFromBundle(bundle: Bundle) {
        super.restoreFromBundle(bundle)
        pos = bundle.getInt(POS)
        HP = bundle.getInt(TAG_HP)
        HT = bundle.getInt(TAG_HT)
        for (b in bundle.getCollection(BUFFS)) {
            (b as Buff).attachTo(this)
        }
    }
    open fun attack(enemy: Char): Boolean {
        val visibleFight = Dungeon.visible[pos] || Dungeon.visible[enemy.pos]
        if (hit(this, enemy, false)) {
            if (visibleFight) {
                GLog.i(TXT_HIT, name, enemy.name)
            }
            // FIXME
            var dr = if (this is Hero && this.rangedWeapon != null && this.subClass === HeroSubClass.SNIPER) {
                0
            } else {
                Random.IntRange(0, enemy.dr())
            }
            val dmg = damageRoll()
            var effectiveDamage = Math.max(dmg - dr, 0)
            effectiveDamage = attackProc(enemy, effectiveDamage)
            effectiveDamage = enemy.defenseProc(this, effectiveDamage)
            enemy.damage(effectiveDamage, this)
            if (visibleFight) {
                Sample.play(Assets.SND_HIT, 1f, 1f, Random.Float(0.8f, 1.25f))
            }
            if (enemy === Dungeon.hero) {
                Dungeon.hero?.interrupt()
                if (effectiveDamage > enemy.HT / 4) {
                    Camera.main?.shake(GameMath.gate(1f, (effectiveDamage / (enemy.HT / 4)).toFloat(), 5f), 0.3f)
                }
            }
            val mySprite = sprite
            if (mySprite != null) {
                enemy.sprite?.bloodBurstA(mySprite.center(), effectiveDamage)
            }
            enemy.sprite?.flash()
            if (!enemy.isAlive && visibleFight) {
                if (enemy === Dungeon.hero) {
                    if (Dungeon.hero?.killerGlyph != null) {
                        // FIXME
                        //	Dungeon.fail( Utils.format( ResultDescriptions.GLYPH, Dungeon.hero.killerGlyph.name(), Dungeon.depth ) );
                        //	GLog.n( TXT_KILL, Dungeon.hero.killerGlyph.name() );
                    } else {
                        if (Bestiary.isBoss(this)) {
                            Dungeon.fail(Utils.format(ResultDescriptions.BOSS, name, Dungeon.depth))
                        } else {
                            Dungeon.fail(Utils.format(ResultDescriptions.MOB, Utils.indefinite(name), Dungeon.depth))
                        }
                        GLog.n(TXT_KILL, name)
                    }
                } else {
                    GLog.i(TXT_DEFEAT, name, enemy.name)
                }
            }
            return true
        } else {
            if (visibleFight) {
                val defense = enemy.defenseVerb()
                enemy.sprite?.showStatus(CharSprite.NEUTRAL, defense)
                if (this === Dungeon.hero) {
                    GLog.i(TXT_YOU_MISSED, enemy.name, defense)
                } else {
                    GLog.i(TXT_SMB_MISSED, enemy.name, defense, name)
                }
                Sample.play(Assets.SND_MISS)
            }
            return false
        }
    }
    open fun attackSkill(target: Char?): Int {
        return 0
    }
    open fun defenseSkill(enemy: Char?): Int {
        return 0
    }
    open fun defenseVerb(): String {
        return "dodged"
    }
    open fun dr(src: Any?): Int {
        return dr()
    }
    open fun dr(): Int {
        return 0
    }
    open fun damageRoll(): Int {
        return 1
    }
    open fun attackProc(enemy: Char, damage: Int): Int {
        return damage
    }
    open fun defenseProc(enemy: Char, damage: Int): Int {
        return damage
    }
    open fun speed(): Float {
        return if (buff(Cripple::class.java) == null) baseSpeed else baseSpeed * 0.5f
    }
    open fun damage(dmg: Int, src: Any?) {
        var effectiveDmg = dmg
        if (HP <= 0) {
            return
        }
        Buffs.detach(this, Frost::class.java)
        val srcClass = src?.javaClass
        if (srcClass != null && immunities().contains(srcClass)) {
            effectiveDmg = 0
        } else if (srcClass != null && resistances().contains(srcClass)) {
            effectiveDmg = Random.IntRange(0, effectiveDmg)
        }
        if (buff(Paralysis::class.java) != null) {
            if (Random.Int(effectiveDmg) >= Random.Int(HP)) {
                Buffs.detach(this, Paralysis::class.java)
                if (Dungeon.visible[pos]) {
                    GLog.i(TXT_OUT_OF_PARALYSIS, name)
                }
            }
        }
        HP -= effectiveDmg
        if (effectiveDmg > 0 || src is Char) {
            sprite?.showStatus(if (HP > HT / 2) CharSprite.WARNING else CharSprite.NEGATIVE, effectiveDmg.toString())
        }
        if (HP <= 0) {
            die(src)
        }
    }
    open fun destroy() {
        HP = 0
        Actor.remove(this)
        Actor.freeCell(pos)
    }
    open fun die(src: Any?) {
        destroy()
        sprite?.die()
    }
    val isAlive: Boolean
        get() = HP > 0
    override fun spend(time: Float) {
        var timeScale = 1f
        if (buff(Slow::class.java) != null) {
            timeScale *= 0.5f
        }
        if (buff(Speed::class.java) != null) {
            timeScale *= 2.0f
        }
        super.spend(time / timeScale)
    }
    fun buffs(): HashSet<Buff> {
        return buffs
    }
    fun <T : Buff> buffs(c: Class<T>): HashSet<T> {
        val filtered = HashSet<T>()
        for (b in buffs) {
            if (c.isInstance(b)) {
                @Suppress("UNCHECKED_CAST")
                filtered.add(b as T)
            }
        }
        return filtered
    }
    fun <T : Buff> buff(c: Class<T>): T? {
        for (b in buffs) {
            if (c.isInstance(b)) {
                @Suppress("UNCHECKED_CAST")
                return b as T
            }
        }
        return null
    }
    fun isCharmedBy(ch: Char): Boolean {
        val chID = ch.id()
        for (b in buffs) {
            if (b is Charm && b.`object` == chID) {
                return true
            }
        }
        return false
    }
    open fun add(buff: Buff) {
        buffs.add(buff)
        Actor.add(buff)
        val s = sprite ?: return
        when (buff) {
            is Poison -> {
                CellEmitter.center(pos).burst(PoisonParticle.SPLASH, 5)
                s.showStatus(CharSprite.NEGATIVE, "poisoned")
            }
            is Amok -> s.showStatus(CharSprite.NEGATIVE, "amok")
            is Slow -> s.showStatus(CharSprite.NEGATIVE, "slowed")
            is MindVision -> {
                s.showStatus(CharSprite.POSITIVE, "mind")
                s.showStatus(CharSprite.POSITIVE, "vision")
            }
            is Paralysis -> {
                s.add(CharSprite.State.PARALYSED)
                s.showStatus(CharSprite.NEGATIVE, "paralysed")
            }
            is Terror -> s.showStatus(CharSprite.NEGATIVE, "frightened")
            is Roots -> s.showStatus(CharSprite.NEGATIVE, "rooted")
            is Cripple -> s.showStatus(CharSprite.NEGATIVE, "crippled")
            is Bleeding -> s.showStatus(CharSprite.NEGATIVE, "bleeding")
            is Vertigo -> s.showStatus(CharSprite.NEGATIVE, "dizzy")
            is Sleep -> s.idle()
            is Burning -> s.add(CharSprite.State.BURNING)
            is Levitation -> s.add(CharSprite.State.LEVITATING)
            is Frost -> s.add(CharSprite.State.FROZEN)
            is Invisibility -> {
                if (buff !is Shadows) {
                    s.showStatus(CharSprite.POSITIVE, "invisible")
                }
                s.add(CharSprite.State.INVISIBLE)
            }
        }
    }
    open fun remove(buff: Buff) {
        buffs.remove(buff)
        Actor.remove(buff)
        val s = sprite ?: return
        when (buff) {
            is Burning -> s.remove(CharSprite.State.BURNING)
            is Levitation -> s.remove(CharSprite.State.LEVITATING)
            is Invisibility -> if (invisible <= 0) s.remove(CharSprite.State.INVISIBLE)
            is Paralysis -> s.remove(CharSprite.State.PARALYSED)
            is Frost -> s.remove(CharSprite.State.FROZEN)
        }
    }
    open fun remove(buffClass: Class<out Buff>) {
        for (buff in buffs(buffClass)) {
            remove(buff)
        }
    }
    override fun onRemove() {
        for (buff in buffs.toTypedArray()) {
            buff.detach()
        }
    }
    fun updateSpriteState() {
        val s = sprite ?: return
        for (buff in buffs) {
            when (buff) {
                is Burning -> s.add(CharSprite.State.BURNING)
                is Levitation -> s.add(CharSprite.State.LEVITATING)
                is Invisibility -> s.add(CharSprite.State.INVISIBLE)
                is Paralysis -> s.add(CharSprite.State.PARALYSED)
                is Frost -> s.add(CharSprite.State.FROZEN)
                is Light -> s.add(CharSprite.State.ILLUMINATED)
            }
        }
    }
    open fun stealth(): Int {
        return 0
    }
    open fun move(step: Int) {
        var nextStep = step
        if (Level.adjacent(nextStep, pos) && buff(Vertigo::class.java) != null) {
            nextStep = pos + Level.NEIGHBOURS8[Random.Int(8)]
            if (!(Level.passable[nextStep] || Level.avoid[nextStep]) || Actor.findChar(nextStep) != null) {
                return
            }
        }
        val currentLevel = Dungeon.level
        if (currentLevel != null && currentLevel.map[pos] == Terrain.OPEN_DOOR) {
            Door.leave(pos)
        }
        pos = nextStep
        if (flying && currentLevel != null && currentLevel.map[pos] == Terrain.DOOR) {
            Door.enter(pos)
        }
        if (this !== Dungeon.hero) {
            sprite?.visible = Dungeon.visible[pos]
        }
    }
    fun distance(other: Char): Int {
        return Level.distance(pos, other.pos)
    }
    open fun onMotionComplete() {
        next()
    }
    open fun onAttackComplete() {
        next()
    }
    open fun onOperateComplete() {
        next()
    }
    open fun resistances(): HashSet<Class<*>> {
        return EMPTY
    }
    open fun immunities(): HashSet<Class<*>> {
        return EMPTY
    }
    companion object {
        protected const val TXT_HIT = "%s hit %s"
        protected const val TXT_KILL = "%s killed you..."
        protected const val TXT_DEFEAT = "%s defeated %s"
        private const val TXT_YOU_MISSED = "%s %s your attack"
        private const val TXT_SMB_MISSED = "%s %s %s's attack"
        private const val TXT_OUT_OF_PARALYSIS = "The pain snapped %s out of paralysis"
        private const val POS = "pos"
        private const val TAG_HP = "HP"
        private const val TAG_HT = "HT"
        private const val BUFFS = "buffs"
        private val EMPTY = HashSet<Class<*>>()
        fun hit(attacker: Char, defender: Char, magic: Boolean): Boolean {
            val acuRoll = Random.Float(attacker.attackSkill(defender).toFloat())
            val defRoll = Random.Float(defender.defenseSkill(attacker).toFloat())
            return (if (magic) acuRoll * 2 else acuRoll) >= defRoll
        }
    }
}
