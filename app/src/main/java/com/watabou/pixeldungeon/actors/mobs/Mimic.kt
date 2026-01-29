package com.watabou.pixeldungeon.actors.mobs
import com.watabou.noosa.audio.Sample
import com.watabou.pixeldungeon.Assets
import com.watabou.pixeldungeon.Dungeon
import com.watabou.pixeldungeon.actors.Actor
import com.watabou.pixeldungeon.actors.Char
import com.watabou.pixeldungeon.effects.CellEmitter
import com.watabou.pixeldungeon.effects.Pushing
import com.watabou.pixeldungeon.effects.Speck
import com.watabou.pixeldungeon.items.Gold
import com.watabou.pixeldungeon.items.Item
import com.watabou.pixeldungeon.items.scrolls.ScrollOfPsionicBlast
import com.watabou.pixeldungeon.levels.Level
import com.watabou.pixeldungeon.scenes.GameScene
import com.watabou.pixeldungeon.sprites.MimicSprite
import com.watabou.utils.Bundle
import com.watabou.utils.Random
import java.util.*
class Mimic : Mob() {
    var level: Int = 0
        private set
    init {
        name = "mimic"
        spriteClass = MimicSprite::class.java
    }
    var items: ArrayList<Item>? = null
    override fun storeInBundle(bundle: Bundle) {
        super.storeInBundle(bundle)
        items?.let { bundle.put(ITEMS, it) }
        bundle.put(LEVEL, level)
    }
    @Suppress("UNCHECKED_CAST")
    override fun restoreFromBundle(bundle: Bundle) {
        super.restoreFromBundle(bundle)
        items = ArrayList((bundle.getCollection(ITEMS) as Collection<Item>?) ?: emptyList())
        adjustStats(bundle.getInt(LEVEL))
    }
    override fun damageRoll(): Int {
        return Random.NormalIntRange(HT / 10, HT / 4)
    }
    override fun attackSkill(target: Char?): Int {
        return 9 + level
    }
    override fun attackProc(enemy: Char, damage: Int): Int {
        val hero = Dungeon.hero
        if (enemy === hero && Random.Int(3) == 0) {
            val gold = Gold(Random.Int(Dungeon.gold / 10, Dungeon.gold / 2))
            if (gold.quantity() > 0) {
                Dungeon.gold -= gold.quantity()
                Dungeon.level?.drop(gold, hero.pos)?.sprite?.drop()
            }
        }
        return super.attackProc(enemy, damage)
    }
    fun adjustStats(level: Int) {
        this.level = level
        HT = (3 + level) * 4
        HP = HT
        EXP = 2 + 2 * (level - 1) / 5
        defenseSkill = attackSkill(null) / 2
        enemySeen = true
    }
    override fun die(src: Any?) {
        super.die(src)
        items?.forEach { item ->
            Dungeon.level?.drop(item, pos)?.sprite?.drop()
        }
    }
    override fun reset(): Boolean {
        state = WANDERING
        return true
    }
    override fun description(): String {
        return "Mimics are magical creatures which can take any shape they wish. In dungeons they almost always choose a shape of a treasure chest, because they know how to beckon an adventurer."
    }
    override fun immunities(): HashSet<Class<*>> {
        return IMMUNITIES
    }
    companion object {
        private const val LEVEL = "level"
        private const val ITEMS = "items"
        private val IMMUNITIES = hashSetOf<Class<*>>(ScrollOfPsionicBlast::class.java)
        fun spawnAt(pos: Int, items: List<Item>): Mimic? {
            val level = Dungeon.level ?: return null
            val ch = Actor.findChar(pos)
            if (ch != null) {
                val candidates = ArrayList<Int>()
                for (n in Level.NEIGHBOURS8) {
                    val cell = pos + n
                    if ((Level.passable[cell] || Level.avoid[cell]) && Actor.findChar(cell) == null) {
                        candidates.add(cell)
                    }
                }
                if (candidates.isNotEmpty()) {
                    val newPos = Random.element(candidates) ?: return null
                    Actor.addDelayed(Pushing(ch, ch.pos, newPos), -1f)
                    ch.pos = newPos
                    if (ch is Mob) {
                        level.mobPress(ch)
                    } else {
                        level.press(newPos, ch)
                    }
                } else {
                    return null
                }
            }
            val m = Mimic()
            m.items = ArrayList(items)
            m.adjustStats(Dungeon.depth)
            m.HT = m.HT
            m.HP = m.HT
            m.pos = pos
            m.state = m.HUNTING
            GameScene.add(m, 1f)
            Dungeon.hero?.let { m.sprite?.turnTo(pos, it.pos) }
            if (Dungeon.visible[m.pos]) {
                CellEmitter.get(pos).burst(Speck.factory(Speck.STAR), 10)
                Sample.play(Assets.SND_MIMIC)
            }
            return m
        }
    }
}
