package com.watabou.pixeldungeon.actors.mobs
import com.watabou.noosa.audio.Sample
import com.watabou.pixeldungeon.Assets
import com.watabou.pixeldungeon.actors.Actor
import com.watabou.pixeldungeon.actors.Char
import com.watabou.pixeldungeon.actors.buffs.Buffs
import com.watabou.pixeldungeon.actors.buffs.Charm
import com.watabou.pixeldungeon.actors.buffs.Light
import com.watabou.pixeldungeon.actors.buffs.Sleep
import com.watabou.pixeldungeon.effects.Speck
import com.watabou.pixeldungeon.items.scrolls.ScrollOfLullaby
import com.watabou.pixeldungeon.items.wands.WandOfBlink
import com.watabou.pixeldungeon.items.weapon.enchantments.Leech
import com.watabou.pixeldungeon.levels.Level
import com.watabou.pixeldungeon.mechanics.Ballistica
import com.watabou.pixeldungeon.sprites.SuccubusSprite
import com.watabou.utils.Random
import java.util.*
class Succubus : Mob() {
    init {
        name = "succubus"
        spriteClass = SuccubusSprite::class.java
        HT = 80
        HP = HT
        defenseSkill = 25
        viewDistance = Light.DISTANCE
        EXP = 12
        maxLvl = 25
        loot = ScrollOfLullaby()
        lootChance = 0.05f
    }
    private var delay = 0
    override fun damageRoll(): Int {
        return Random.NormalIntRange(15, 25)
    }
    override fun attackProc(enemy: Char, damage: Int): Int {
        if (Random.Int(3) == 0) {
            val charm = Buffs.affect(enemy, Charm::class.java, Charm.durationFactor(enemy) * Random.IntRange(3, 7))
            charm?.`object` = id()
            enemy.sprite?.centerEmitter()?.start(Speck.factory(Speck.HEART), 0.2f, 5)
            Sample.play(Assets.SND_CHARMS)
        }
        return damage
    }
    override fun getCloser(target: Int): Boolean {
        if (Level.fieldOfView[target] && Level.distance(pos, target) > 2 && delay <= 0) {
            blink(target)
            spend(-1 / speed())
            return true
        } else {
            delay--
            return super.getCloser(target)
        }
    }
    private fun blink(target: Int) {
        var cell = Ballistica.cast(pos, target, true, true)
        if (Actor.findChar(cell) != null && Ballistica.distance > 1) {
            cell = Ballistica.trace[Ballistica.distance - 2]
        }
        WandOfBlink.appear(this, cell)
        delay = BLINK_DELAY
    }
    override fun attackSkill(target: Char?): Int {
        return 40
    }
    override fun dr(): Int {
        return 10
    }
    override fun description(): String {
        return "The succubi are demons that look like seductive (in a slightly gothic way) girls. Using its magic, the succubus can charm a hero, who will become unable to attack anything until the charm wears off."
    }
    override fun resistances(): HashSet<Class<*>> {
        return RESISTANCES
    }
    override fun immunities(): HashSet<Class<*>> {
        return IMMUNITIES
    }
    companion object {
        private const val BLINK_DELAY = 5
        private val RESISTANCES = hashSetOf<Class<*>>(Leech::class.java)
        private val IMMUNITIES = hashSetOf<Class<*>>(Sleep::class.java)
    }
}
