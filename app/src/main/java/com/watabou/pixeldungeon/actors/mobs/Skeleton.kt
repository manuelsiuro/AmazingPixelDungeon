package com.watabou.pixeldungeon.actors.mobs
import com.watabou.noosa.audio.Sample
import com.watabou.pixeldungeon.Assets
import com.watabou.pixeldungeon.Dungeon
import com.watabou.pixeldungeon.ResultDescriptions
import com.watabou.pixeldungeon.actors.Char
import com.watabou.pixeldungeon.items.Generator
import com.watabou.pixeldungeon.items.Item
import com.watabou.pixeldungeon.items.weapon.enchantments.Death
import com.watabou.pixeldungeon.levels.Level
import com.watabou.pixeldungeon.sprites.SkeletonSprite
import com.watabou.pixeldungeon.utils.GLog
import com.watabou.pixeldungeon.utils.Utils
import com.watabou.utils.Random
import java.util.*
import kotlin.math.max
class Skeleton : Mob() {
    init {
        name = "skeleton"
        spriteClass = SkeletonSprite::class.java
        HT = 25
        HP = HT
        defenseSkill = 9
        EXP = 5
        maxLvl = 10
    }
    override fun damageRoll(): Int {
        return Random.NormalIntRange(3, 8)
    }
    override fun die(src: Any?) {
        super.die(src)
        var heroKilled = false
        for (i in Level.NEIGHBOURS8.indices) {
            val ch = findChar(pos + Level.NEIGHBOURS8[i])
            if (ch != null && ch.isAlive) {
                val damage = max(0, damageRoll() - Random.IntRange(0, ch.dr() / 2))
                ch.damage(damage, this)
                if (ch === Dungeon.hero && !ch.isAlive) {
                    heroKilled = true
                }
            }
        }
        if (Dungeon.visible[pos]) {
            Sample.play(Assets.SND_BONES)
        }
        if (heroKilled) {
            Dungeon.fail(Utils.format(ResultDescriptions.MOB, Utils.indefinite(name), Dungeon.depth))
            GLog.n(TXT_HERO_KILLED)
        }
    }
    override fun dropLoot() {
        if (Random.Int(5) == 0) {
            var loot = Generator.random(Generator.Category.WEAPON) ?: return
            for (i in 0..1) {
                val l = Generator.random(Generator.Category.WEAPON) ?: continue
                if (l.level() < loot.level()) {
                    loot = l
                }
            }
            Dungeon.level?.drop(loot, pos)?.sprite?.drop()
        }
    }
    override fun attackSkill(target: Char?): Int {
        return 12
    }
    override fun dr(): Int {
        return 5
    }
    override fun defenseVerb(): String {
        return "blocked"
    }
    override fun description(): String {
        return "Skeletons are composed of corpses bones from unlucky adventurers and inhabitants of the dungeon, animated by emanations of evil magic from the depths below. After they have been damaged enough, they disintegrate in an explosion of bones."
    }
    override fun immunities(): HashSet<Class<*>> {
        return IMMUNITIES
    }
    companion object {
        private const val TXT_HERO_KILLED = "You were killed by the explosion of bones..."
        private val IMMUNITIES = hashSetOf<Class<*>>(Death::class.java)
    }
}
