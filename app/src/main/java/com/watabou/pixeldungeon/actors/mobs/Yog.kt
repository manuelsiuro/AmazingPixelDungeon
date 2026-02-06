package com.watabou.pixeldungeon.actors.mobs
import com.watabou.pixeldungeon.Dungeon
import com.watabou.pixeldungeon.ResultDescriptions
import com.watabou.pixeldungeon.Statistics
import com.watabou.pixeldungeon.actors.Actor
import com.watabou.pixeldungeon.actors.Char
import com.watabou.pixeldungeon.actors.blobs.Blob
import com.watabou.pixeldungeon.actors.blobs.Fire
import com.watabou.pixeldungeon.actors.blobs.ToxicGas
import com.watabou.pixeldungeon.actors.buffs.*
import com.watabou.pixeldungeon.effects.Pushing
import com.watabou.pixeldungeon.effects.particles.ShadowParticle
import com.watabou.pixeldungeon.llm.LlmTextEnhancer
import com.watabou.pixeldungeon.items.keys.SkeletonKey
import com.watabou.pixeldungeon.items.scrolls.ScrollOfPsionicBlast
import com.watabou.pixeldungeon.items.weapon.enchantments.Death
import com.watabou.pixeldungeon.levels.Level
import com.watabou.pixeldungeon.mechanics.Ballistica
import com.watabou.pixeldungeon.scenes.GameScene
import com.watabou.pixeldungeon.sprites.BurningFistSprite
import com.watabou.pixeldungeon.sprites.LarvaSprite
import com.watabou.pixeldungeon.sprites.RottingFistSprite
import com.watabou.pixeldungeon.sprites.YogSprite
import com.watabou.pixeldungeon.sprites.CharSprite
import com.watabou.pixeldungeon.utils.GLog
import com.watabou.pixeldungeon.utils.Utils
import com.watabou.utils.Random
import java.util.*
class Yog : Mob() {
    init {
        name = if (Dungeon.depth == Statistics.deepestFloor) "Yog-Dzewa" else "echo of Yog-Dzewa"
        spriteClass = YogSprite::class.java
        HT = 300
        HP = HT
        EXP = 50
        state = PASSIVE
    }
    fun spawnFists() {
        val fist1 = RottingFist()
        val fist2 = BurningFist()
        do {
            fist1.pos = pos + Level.NEIGHBOURS8[Random.Int(8)]
            fist2.pos = pos + Level.NEIGHBOURS8[Random.Int(8)]
        } while (!Level.passable[fist1.pos] || !Level.passable[fist2.pos] || fist1.pos == fist2.pos)
        GameScene.add(fist1)
        GameScene.add(fist2)
    }
    override fun damage(dmg: Int, src: Any?) {
        var damageMod = dmg
        if (fistsCount > 0) {
            Dungeon.level?.mobs?.forEach { mob ->
                if (mob is BurningFist || mob is RottingFist) {
                    mob.beckon(pos)
                }
            }
            damageMod = damageMod shr fistsCount
        }
        super.damage(damageMod, src)
    }
    override fun defenseProc(enemy: Char, damage: Int): Int {
        val spawnPoints = ArrayList<Int>()
        for (i in Level.NEIGHBOURS8.indices) {
            val p = pos + Level.NEIGHBOURS8[i]
            if (Actor.findChar(p) == null && (Level.passable[p] || Level.avoid[p])) {
                spawnPoints.add(p)
            }
        }
        if (spawnPoints.isNotEmpty()) {
            val larva = Larva()
            larva.pos = Random.element(spawnPoints) ?: return super.defenseProc(enemy, damage)
            GameScene.add(larva)
            Actor.addDelayed(Pushing(larva, pos, larva.pos), -1f)
        }
        return super.defenseProc(enemy, damage)
    }
    override fun beckon(cell: Int) {}
    @Suppress("UNCHECKED_CAST")
    override fun die(src: Any?) {
        val level = Dungeon.level
        if (level != null) {
            for (mob in level.mobs.clone() as ArrayList<Mob>) {
                if (mob is BurningFist || mob is RottingFist) {
                    mob.die(src)
                }
            }
        }
        GameScene.bossSlain()
        level?.drop(SkeletonKey(), pos)?.sprite?.drop()
        super.die(src)
        val heroClass = Dungeon.hero?.heroClass?.title() ?: "adventurer"
        yell(LlmTextEnhancer.enhanceBossDialog("Yog-Dzewa", "death", heroClass, Dungeon.depth, "..."))
    }
    override fun notice() {
        super.notice()
        val heroClass = Dungeon.hero?.heroClass?.title() ?: "adventurer"
        yell(LlmTextEnhancer.enhanceBossDialog("Yog-Dzewa", "notice", heroClass, Dungeon.depth, "Hope is an illusion..."))
    }
    override fun description(): String {
        return TXT_DESC
    }
    override fun immunities(): HashSet<Class<*>> {
        return IMMUNITIES
    }
    class RottingFist : Mob() {
        init {
            name = "rotting fist"
            spriteClass = RottingFistSprite::class.java
            HT = 300
            HP = HT
            defenseSkill = 25
            EXP = 0
            state = WANDERING
            fistsCount++
        }
        override fun die(src: Any?) {
            super.die(src)
            fistsCount--
        }
        override fun attackSkill(target: Char?): Int {
            return 36
        }
        override fun damageRoll(): Int {
            return Random.NormalIntRange(24, 36)
        }
        override fun dr(): Int {
            return 15
        }
        override fun attackProc(enemy: Char, damage: Int): Int {
            if (Random.Int(3) == 0) {
                Buffs.affect(enemy, Ooze::class.java)
                enemy.sprite?.burst(0xFF000000.toInt(), 5)
            }
            return damage
        }
        override fun act(): Boolean {
            if (Level.water[pos] && HP < HT) {
                sprite?.emitter()?.burst(ShadowParticle.UP, 2)
                HP += REGENERATION
            }
            return super.act()
        }
        override fun description(): String {
            return TXT_DESC
        }
        override fun resistances(): HashSet<Class<*>> {
            return RESISTANCES
        }
        override fun immunities(): HashSet<Class<*>> {
            return IMMUNITIES
        }
        companion object {
            private const val REGENERATION = 4
            private val RESISTANCES = hashSetOf<Class<*>>(
                ToxicGas::class.java,
                Death::class.java,
                ScrollOfPsionicBlast::class.java
            )
            private val IMMUNITIES = hashSetOf<Class<*>>(
                Amok::class.java,
                Sleep::class.java,
                Terror::class.java,
                Poison::class.java,
                Vertigo::class.java
            )
        }
    }
    class BurningFist : Mob() {
        init {
            name = "burning fist"
            spriteClass = BurningFistSprite::class.java
            HT = 200
            HP = HT
            defenseSkill = 25
            EXP = 0
            state = WANDERING
            fistsCount++
        }
        override fun die(src: Any?) {
            super.die(src)
            fistsCount--
        }
        override fun attackSkill(target: Char?): Int {
            return 36
        }
        override fun damageRoll(): Int {
            return Random.NormalIntRange(20, 32)
        }
        override fun dr(): Int {
            return 15
        }
        override fun canAttack(enemy: Char): Boolean {
            return Ballistica.cast(pos, enemy.pos, false, true) == enemy.pos
        }
        override fun attack(enemy: Char): Boolean {
            return if (!Level.adjacent(pos, enemy.pos)) {
                spend(attackDelay())
                if (hit(this, enemy, true)) {
                    val dmg = damageRoll()
                    enemy.damage(dmg, this)
                    enemy.sprite?.bloodBurstA(sprite?.center(), dmg)
                    enemy.sprite?.flash()
                    if (!enemy.isAlive && enemy === Dungeon.hero) {
                        Dungeon.fail(Utils.format(ResultDescriptions.BOSS, name, Dungeon.depth))
                        GLog.n(Mob.TXT_KILL, name)
                    }
                    true
                } else {
                    enemy.sprite?.showStatus(CharSprite.NEUTRAL, enemy.defenseVerb())
                    false
                }
            } else {
                super.attack(enemy)
            }
        }
        override fun act(): Boolean {
            for (i in Level.NEIGHBOURS9.indices) {
                GameScene.add(Blob.seed(pos + Level.NEIGHBOURS9[i], 2, Fire::class.java) as Blob)
            }
            return super.act()
        }
        override fun description(): String {
            return TXT_DESC
        }
        override fun resistances(): HashSet<Class<*>> {
            return RESISTANCES
        }
        override fun immunities(): HashSet<Class<*>> {
            return IMMUNITIES
        }
        companion object {
            private val RESISTANCES = hashSetOf<Class<*>>(
                ToxicGas::class.java,
                Death::class.java,
                ScrollOfPsionicBlast::class.java
            )
            private val IMMUNITIES = hashSetOf<Class<*>>(
                Amok::class.java,
                Sleep::class.java,
                Terror::class.java,
                Burning::class.java
            )
        }
    }
    class Larva : Mob() {
        init {
            name = "god's larva"
            spriteClass = LarvaSprite::class.java
            HT = 25
            HP = HT
            defenseSkill = 20
            EXP = 0
            state = HUNTING
        }
        override fun attackSkill(target: Char?): Int {
            return 30
        }
        override fun damageRoll(): Int {
            return Random.NormalIntRange(15, 20)
        }
        override fun dr(): Int {
            return 8
        }
        override fun description(): String {
            return TXT_DESC
        }
    }
    companion object {
        private const val TXT_DESC =
            "Yog-Dzewa is an Old God, a powerful entity from the realms of chaos. A century ago, the ancient dwarves barely won the war against its army of demons, but were unable to kill the god itself. Instead, they then imprisoned it in the halls below their city, believing it to be too weak to rise ever again."
        var fistsCount = 0
        private val IMMUNITIES = hashSetOf<Class<*>>(
            Death::class.java,
            Terror::class.java,
            Amok::class.java,
            Charm::class.java,
            Sleep::class.java,
            Burning::class.java,
            ToxicGas::class.java,
            ScrollOfPsionicBlast::class.java
        )
    }
}
