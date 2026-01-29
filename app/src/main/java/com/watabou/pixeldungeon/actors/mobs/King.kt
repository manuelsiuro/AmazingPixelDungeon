package com.watabou.pixeldungeon.actors.mobs
import com.watabou.noosa.audio.Sample
import com.watabou.pixeldungeon.Assets
import com.watabou.pixeldungeon.Badges
import com.watabou.pixeldungeon.Dungeon
import com.watabou.pixeldungeon.Statistics
import com.watabou.pixeldungeon.actors.Actor
import com.watabou.pixeldungeon.actors.Char
import com.watabou.pixeldungeon.actors.blobs.ToxicGas
import com.watabou.pixeldungeon.actors.buffs.Buffs
import com.watabou.pixeldungeon.actors.buffs.Paralysis
import com.watabou.pixeldungeon.actors.buffs.Vertigo
import com.watabou.pixeldungeon.effects.Flare
import com.watabou.pixeldungeon.effects.Speck
import com.watabou.pixeldungeon.items.ArmorKit
import com.watabou.pixeldungeon.items.keys.SkeletonKey
import com.watabou.pixeldungeon.items.scrolls.ScrollOfPsionicBlast
import com.watabou.pixeldungeon.items.wands.WandOfBlink
import com.watabou.pixeldungeon.items.wands.WandOfDisintegration
import com.watabou.pixeldungeon.items.weapon.enchantments.Death
import com.watabou.pixeldungeon.levels.CityBossLevel
import com.watabou.pixeldungeon.levels.Level
import com.watabou.pixeldungeon.scenes.GameScene
import com.watabou.pixeldungeon.sprites.KingSprite
import com.watabou.pixeldungeon.sprites.UndeadSprite
import com.watabou.utils.Bundle
import com.watabou.utils.PathFinder
import com.watabou.utils.Random
import java.util.*
class King : Mob() {
    init {
        name = if (Dungeon.depth == Statistics.deepestFloor) "King of Dwarves" else "undead King of Dwarves"
        spriteClass = KingSprite::class.java
        HT = 300
        HP = HT
        EXP = 40
        defenseSkill = 25
        Undead.count = 0
    }
    private var nextPedestal = true
    override fun storeInBundle(bundle: Bundle) {
        super.storeInBundle(bundle)
        bundle.put(PEDESTAL, nextPedestal)
    }
    override fun restoreFromBundle(bundle: Bundle) {
        super.restoreFromBundle(bundle)
        nextPedestal = bundle.getBoolean(PEDESTAL)
    }
    override fun damageRoll(): Int {
        return Random.NormalIntRange(20, 38)
    }
    override fun attackSkill(target: Char?): Int {
        return 32
    }
    override fun dr(): Int {
        return 14
    }
    override fun defenseVerb(): String {
        return "parried"
    }
    override fun getCloser(target: Int): Boolean {
        return if (canTryToSummon()) {
            super.getCloser(CityBossLevel.pedestal(nextPedestal))
        } else {
            super.getCloser(target)
        }
    }
    override fun canAttack(enemy: Char): Boolean {
        return if (canTryToSummon()) {
            pos == CityBossLevel.pedestal(nextPedestal)
        } else {
            Level.adjacent(pos, enemy.pos)
        }
    }
    private fun canTryToSummon(): Boolean {
        if (Undead.count < maxArmySize()) {
            val ch = Actor.findChar(CityBossLevel.pedestal(nextPedestal))
            return ch === this || ch == null
        } else {
            return false
        }
    }
    override fun attack(enemy: Char): Boolean {
        return if (canTryToSummon() && pos == CityBossLevel.pedestal(nextPedestal)) {
            summon()
            true
        } else {
            if (Actor.findChar(CityBossLevel.pedestal(nextPedestal)) === enemy) {
                nextPedestal = !nextPedestal
            }
            super.attack(enemy)
        }
    }
    override fun die(src: Any?) {
        GameScene.bossSlain()
        val level = Dungeon.level
        level?.drop(ArmorKit(), pos)?.sprite?.drop()
        level?.drop(SkeletonKey(), pos)?.sprite?.drop()
        super.die(src)
        Badges.validateBossSlain()
        Dungeon.hero?.let { yell("You cannot kill me, " + it.heroClass.title() + "... I am... immortal...") }
    }
    private fun maxArmySize(): Int {
        return 1 + MAX_ARMY_SIZE * (HT - HP) / HT
    }
    private fun summon() {
        nextPedestal = !nextPedestal
        sprite?.centerEmitter()?.start(Speck.factory(Speck.SCREAM), 0.4f, 2)
        Sample.play(Assets.SND_CHALLENGE)
        val passable = Level.passable.clone()
        for (actor in Actor.all()) {
            if (actor is Char) {
                passable[actor.pos] = false
            }
        }
        val undeadsToSummon = maxArmySize() - Undead.count
        PathFinder.buildDistanceMap(pos, passable, undeadsToSummon)
        val distance = PathFinder.distance ?: return
        distance[pos] = Int.MAX_VALUE
        var dist = 1
        outer@ for (i in 0 until undeadsToSummon) {
            do {
                for (j in 0 until Level.LENGTH) {
                    if (distance[j] == dist) {
                        val undead = Undead()
                        undead.pos = j
                        GameScene.add(undead)
                        WandOfBlink.appear(undead, j)
                        undead.sprite?.let { newFlare(3, 32).color(0x000000, false).show(it, 2f) }
                        distance[j] = Int.MAX_VALUE
                        continue@outer
                    }
                }
                dist++
            } while (dist < Level.LENGTH) // Added a safety bound here
        }
        yell("Arise, slaves!")
    }
    private fun newFlare(r: Int, n: Int): Flare {
        return Flare(r, n.toFloat())
    }
    override fun notice() {
        super.notice()
        yell("How dare you!")
    }
    override fun description(): String {
        return "The last king of dwarves was known for his deep understanding of processes of life and death. He has persuaded members of his court to participate in a ritual, that should have granted them eternal youthfulness. In the end he was the only one, who got it - and an army of undead as a bonus."
    }
    override fun resistances(): HashSet<Class<*>> {
        return RESISTANCES
    }
    override fun immunities(): HashSet<Class<*>> {
        return IMMUNITIES
    }
    class Undead : Mob() {
        init {
            name = "undead dwarf"
            spriteClass = UndeadSprite::class.java
            HT = 28
            HP = HT
            defenseSkill = 15
            EXP = 0
            state = WANDERING
        }
        override fun onAdd() {
            count++
            super.onAdd()
        }
        override fun onRemove() {
            count--
            super.onRemove()
        }
        override fun damageRoll(): Int {
            return Random.NormalIntRange(12, 16)
        }
        override fun attackSkill(target: Char?): Int {
            return 16
        }
        override fun attackProc(enemy: Char, damage: Int): Int {
            if (Random.Int(MAX_ARMY_SIZE) == 0) {
                Buffs.prolong(enemy, Paralysis::class.java, 1f)
            }
            return damage
        }
        override fun damage(dmg: Int, src: Any?) {
            super.damage(dmg, src)
            if (src is ToxicGas) {
                src.clear(pos)
            }
        }
        override fun die(src: Any?) {
            super.die(src)
            if (Dungeon.visible[pos]) {
                Sample.play(Assets.SND_BONES)
            }
        }
        override fun dr(): Int {
            return 5
        }
        override fun defenseVerb(): String {
            return "blocked"
        }
        override fun description(): String {
            return "These undead dwarves, risen by the will of the King of Dwarves, were members of his court. They appear as skeletons with a stunning amount of facial hair."
        }
        override fun immunities(): HashSet<Class<*>> {
            return IMMUNITIES
        }
        companion object {
            var count = 0
            private val IMMUNITIES = hashSetOf<Class<*>>(Death::class.java, Paralysis::class.java)
        }
    }
    companion object {
        private const val MAX_ARMY_SIZE = 5
        private const val PEDESTAL = "pedestal"
        private val RESISTANCES = hashSetOf<Class<*>>(
            ToxicGas::class.java,
            Death::class.java,
            ScrollOfPsionicBlast::class.java,
            WandOfDisintegration::class.java
        )
        private val IMMUNITIES = hashSetOf<Class<*>>(Paralysis::class.java, Vertigo::class.java)
    }
}
