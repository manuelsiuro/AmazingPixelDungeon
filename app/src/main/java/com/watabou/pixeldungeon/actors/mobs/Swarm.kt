package com.watabou.pixeldungeon.actors.mobs
import com.watabou.pixeldungeon.Dungeon
import com.watabou.pixeldungeon.actors.Actor
import com.watabou.pixeldungeon.actors.Char
import com.watabou.pixeldungeon.actors.buffs.Buffs
import com.watabou.pixeldungeon.actors.buffs.Burning
import com.watabou.pixeldungeon.actors.buffs.Poison
import com.watabou.pixeldungeon.effects.Pushing
import com.watabou.pixeldungeon.items.potions.PotionOfHealing
import com.watabou.pixeldungeon.levels.Level
import com.watabou.pixeldungeon.levels.Terrain
import com.watabou.pixeldungeon.levels.features.Door
import com.watabou.pixeldungeon.scenes.GameScene
import com.watabou.pixeldungeon.sprites.SwarmSprite
import com.watabou.utils.Bundle
import com.watabou.utils.Random
import java.util.*
class Swarm : Mob() {
    init {
        name = "swarm of flies"
        spriteClass = SwarmSprite::class.java
        HT = 80
        HP = HT
        defenseSkill = 5
        maxLvl = 10
        flying = true
    }
    var generation = 0
    override fun storeInBundle(bundle: Bundle) {
        super.storeInBundle(bundle)
        bundle.put(GENERATION, generation)
    }
    override fun restoreFromBundle(bundle: Bundle) {
        super.restoreFromBundle(bundle)
        generation = bundle.getInt(GENERATION)
    }
    override fun damageRoll(): Int {
        return Random.NormalIntRange(1, 4)
    }
    override fun defenseProc(enemy: Char, damage: Int): Int {
        if (HP >= damage + 2) {
            val candidates = ArrayList<Int>()
            val passable = Level.passable
            val neighbours = intArrayOf(pos + 1, pos - 1, pos + Level.WIDTH, pos - Level.WIDTH)
            for (n in neighbours) {
                if (passable[n] && Actor.findChar(n) == null) {
                    candidates.add(n)
                }
            }
            if (candidates.isNotEmpty()) {
                val clone = split()
                clone.HT = clone.HP // split() creates a new Swarm with default stats? No, Java Swarm() ctor doesn't set HP/HT. 
                // Wait, Java init block sets HP = HT = 80.
                clone.HP = (HP - damage) / 2
                clone.HT = clone.HP
                clone.pos = Random.element(candidates) ?: return damage
                clone.state = clone.HUNTING
                if (Dungeon.level?.map?.get(clone.pos) == Terrain.DOOR) {
                    Door.enter(clone.pos)
                }
                GameScene.add(clone, SPLIT_DELAY)
                Actor.addDelayed(Pushing(clone, pos, clone.pos), -1f)
                HP -= clone.HP
            }
        }
        return damage
    }
    override fun attackSkill(target: Char?): Int {
        return 12
    }
    override fun defenseVerb(): String {
        return "evaded"
    }
    private fun split(): Swarm {
        val clone = Swarm()
        clone.generation = generation + 1
        if (buff(Burning::class.java) != null) {
            Buffs.affect(clone, Burning::class.java)?.reignite(clone)
        }
        if (buff(Poison::class.java) != null) {
            Buffs.affect(clone, Poison::class.java)?.set(2f)
        }
        return clone
    }
    override fun dropLoot() {
        if (Random.Int(5 * (generation + 1)) == 0) {
            Dungeon.level?.drop(PotionOfHealing(), pos)?.sprite?.drop()
        }
    }
    override fun description(): String {
        return "The deadly swarm of flies buzzes angrily. Every non-magical attack will split it into two smaller but equally dangerous swarms."
    }
    companion object {
        private const val SPLIT_DELAY = 1f
        private const val GENERATION = "generation"
    }
}
