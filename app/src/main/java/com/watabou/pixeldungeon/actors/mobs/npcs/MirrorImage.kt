package com.watabou.pixeldungeon.actors.mobs.npcs
import com.watabou.pixeldungeon.Dungeon
import com.watabou.pixeldungeon.actors.Char
import com.watabou.pixeldungeon.actors.blobs.ToxicGas
import com.watabou.pixeldungeon.actors.buffs.Burning
import com.watabou.pixeldungeon.actors.hero.Hero
import com.watabou.pixeldungeon.actors.mobs.Mob
import com.watabou.pixeldungeon.levels.Level
import com.watabou.pixeldungeon.sprites.CharSprite
import com.watabou.pixeldungeon.sprites.MirrorSprite
import com.watabou.utils.Bundle
import com.watabou.utils.Random
import java.util.*
class MirrorImage : NPC() {
    init {
        name = "mirror image"
        spriteClass = MirrorSprite::class.java
        state = HUNTING
    }
    var tier = 0
    private var attack = 0
    private var damage = 0
    override fun storeInBundle(bundle: Bundle) {
        super.storeInBundle(bundle)
        bundle.put(TIER, tier)
        bundle.put(ATTACK, attack)
        bundle.put(DAMAGE, damage)
    }
    override fun restoreFromBundle(bundle: Bundle) {
        super.restoreFromBundle(bundle)
        tier = bundle.getInt(TIER)
        attack = bundle.getInt(ATTACK)
        damage = bundle.getInt(DAMAGE)
    }
    fun duplicate(hero: Hero) {
        tier = hero.tier()
        attack = hero.attackSkill(hero)
        damage = hero.damageRoll()
    }
    override fun attackSkill(target: Char?): Int {
        return attack
    }
    override fun damageRoll(): Int {
        return damage
    }
    override fun attackProc(enemy: Char, damage: Int): Int {
        val dmg = super.attackProc(enemy, damage)
        destroy()
        sprite?.die()
        return dmg
    }
    override fun chooseEnemy(): Char? {
        val currentEnemy = enemy
        if (currentEnemy == null || !currentEnemy.isAlive) {
            val level = Dungeon.level ?: return null
            val enemies = HashSet<Mob>()
            for (mob in level.mobs) {
                if (mob.hostile && Level.fieldOfView[mob.pos]) {
                    enemies.add(mob)
                }
            }
            return if (enemies.isNotEmpty()) Random.element(enemies) else null
        }
        return currentEnemy
    }
    override fun description(): String {
        return "This illusion bears a close resemblance to you, but it's paler and twitches a little."
    }
    override fun sprite(): CharSprite? {
        val s = super.sprite()
        (s as? MirrorSprite)?.updateArmor(tier)
        return s
    }
    override fun interact() {
        val hero = Dungeon.hero ?: return
        val curPos = pos
        moveSprite(pos, hero.pos)
        move(hero.pos)
        hero.sprite?.move(hero.pos, curPos)
        hero.move(curPos)
        hero.spend(1 / hero.speed())
        hero.busy()
    }
    override fun immunities(): HashSet<Class<*>> {
        return IMMUNITIES
    }
    companion object {
        private const val TIER = "tier"
        private const val ATTACK = "attack"
        private const val DAMAGE = "damage"
        private val IMMUNITIES = hashSetOf<Class<*>>(ToxicGas::class.java, Burning::class.java)
    }
}
