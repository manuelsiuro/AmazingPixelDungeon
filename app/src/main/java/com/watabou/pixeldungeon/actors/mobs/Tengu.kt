package com.watabou.pixeldungeon.actors.mobs
import com.watabou.noosa.audio.Sample
import com.watabou.pixeldungeon.Assets
import com.watabou.pixeldungeon.Badges
import com.watabou.pixeldungeon.Statistics
import com.watabou.pixeldungeon.Dungeon
import com.watabou.pixeldungeon.actors.Actor
import com.watabou.pixeldungeon.actors.Char
import com.watabou.pixeldungeon.actors.blobs.ToxicGas
import com.watabou.pixeldungeon.actors.buffs.Poison
import com.watabou.pixeldungeon.actors.hero.HeroSubClass
import com.watabou.pixeldungeon.effects.CellEmitter
import com.watabou.pixeldungeon.effects.Speck
import com.watabou.pixeldungeon.items.TomeOfMastery
import com.watabou.pixeldungeon.items.keys.SkeletonKey
import com.watabou.pixeldungeon.items.scrolls.ScrollOfMagicMapping
import com.watabou.pixeldungeon.items.scrolls.ScrollOfPsionicBlast
import com.watabou.pixeldungeon.items.weapon.enchantments.Death
import com.watabou.pixeldungeon.levels.Level
import com.watabou.pixeldungeon.levels.Terrain
import com.watabou.pixeldungeon.mechanics.Ballistica
import com.watabou.pixeldungeon.scenes.GameScene
import com.watabou.pixeldungeon.sprites.TenguSprite
import com.watabou.utils.Random
import java.util.*
class Tengu : Mob() {
    init {
        name = if (Dungeon.depth == Statistics.deepestFloor) "Tengu" else "memory of Tengu"
        spriteClass = TenguSprite::class.java
        HT = 120
        HP = HT
        EXP = 20
        defenseSkill = 20
    }
    private var timeToJump = JUMP_DELAY
    override fun damageRoll(): Int {
        return Random.NormalIntRange(8, 15)
    }
    override fun attackSkill(target: Char?): Int {
        return 20
    }
    override fun dr(): Int {
        return 5
    }
    override fun die(src: Any?) {
        val hero = Dungeon.hero
        val level = Dungeon.level
        val badgeToCheck = when (hero?.heroClass) {
            com.watabou.pixeldungeon.actors.hero.HeroClass.WARRIOR -> Badges.Badge.MASTERY_WARRIOR
            com.watabou.pixeldungeon.actors.hero.HeroClass.MAGE -> Badges.Badge.MASTERY_MAGE
            com.watabou.pixeldungeon.actors.hero.HeroClass.ROGUE -> Badges.Badge.MASTERY_ROGUE
            com.watabou.pixeldungeon.actors.hero.HeroClass.HUNTRESS -> Badges.Badge.MASTERY_HUNTRESS
            else -> null
        }
        if (badgeToCheck != null && hero != null && (!Badges.isUnlocked(badgeToCheck) || hero.subClass == HeroSubClass.NONE)) {
            level?.drop(TomeOfMastery(), pos)?.sprite?.drop()
        }
        GameScene.bossSlain()
        level?.drop(SkeletonKey(), pos)?.sprite?.drop()
        super.die(src)
        Badges.validateBossSlain()
        yell("Free at last...")
    }
    override fun getCloser(target: Int): Boolean {
        return if (Level.fieldOfView[target]) {
            jump()
            true
        } else {
            super.getCloser(target)
        }
    }
    override fun canAttack(enemy: Char): Boolean {
        return Ballistica.cast(pos, enemy.pos, false, true) == enemy.pos
    }
    override fun doAttack(enemy: Char): Boolean {
        timeToJump--
        return if (timeToJump <= 0 && Level.adjacent(pos, enemy.pos)) {
            jump()
            true
        } else {
            super.doAttack(enemy)
        }
    }
    private fun jump(): Boolean {
        timeToJump = JUMP_DELAY
        for (i in 0 until 4) {
            var trapPos: Int
            do {
                trapPos = Random.Int(Level.LENGTH)
            } while (!Level.fieldOfView[trapPos] || !Level.passable[trapPos])
            if (Dungeon.level?.map?.get(trapPos) == Terrain.INACTIVE_TRAP) {
                Level.set(trapPos, Terrain.POISON_TRAP)
                GameScene.updateMap(trapPos)
                ScrollOfMagicMapping.discover(trapPos)
            }
        }
        var newPos: Int
        val currentEnemy = enemy
        do {
            newPos = Random.Int(Level.LENGTH)
        } while (!Level.fieldOfView[newPos] ||
                 !Level.passable[newPos] ||
                 (currentEnemy != null && Level.adjacent(newPos, currentEnemy.pos)) ||
                 Actor.findChar(newPos) != null)
        sprite?.move(pos, newPos)
        move(newPos)
        if (Dungeon.visible[newPos]) {
            CellEmitter.get(newPos).burst(Speck.factory(Speck.WOOL), 6)
            Sample.play(Assets.SND_PUFF)
        }
        spend(1 / speed())
        return true
    }
    override fun notice() {
        super.notice()
        Dungeon.hero?.let { yell("Gotcha, " + it.heroClass.title() + "!") }
    }
    override fun description(): String {
        return "Tengu are members of the ancient assassins clan, which is also called Tengu. These assassins are noted for extensive use of shuriken and traps."
    }
    override fun resistances(): HashSet<Class<*>> {
        return RESISTANCES
    }
    companion object {
        private const val JUMP_DELAY = 5
        private val RESISTANCES = hashSetOf<Class<*>>(
            ToxicGas::class.java,
            Poison::class.java,
            Death::class.java,
            ScrollOfPsionicBlast::class.java
        )
    }
}
