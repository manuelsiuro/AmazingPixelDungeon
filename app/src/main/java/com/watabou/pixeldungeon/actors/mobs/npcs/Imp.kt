package com.watabou.pixeldungeon.actors.mobs.npcs
import com.watabou.pixeldungeon.Dungeon
import com.watabou.pixeldungeon.Journal
import com.watabou.pixeldungeon.actors.Actor
import com.watabou.pixeldungeon.actors.Char
import com.watabou.pixeldungeon.actors.buffs.Buff
import com.watabou.pixeldungeon.actors.mobs.Golem
import com.watabou.pixeldungeon.actors.mobs.Mob
import com.watabou.pixeldungeon.actors.mobs.Monk
import com.watabou.pixeldungeon.items.quest.DwarfToken
import com.watabou.pixeldungeon.items.rings.Ring
import com.watabou.pixeldungeon.items.rings.RingOfPower
import com.watabou.pixeldungeon.items.rings.RingOfThorns
import com.watabou.pixeldungeon.levels.CityLevel
import com.watabou.pixeldungeon.levels.Room
import com.watabou.pixeldungeon.scenes.GameScene
import com.watabou.pixeldungeon.sprites.ImpSprite
import com.watabou.pixeldungeon.utils.Utils
import com.watabou.pixeldungeon.windows.WndImp
import com.watabou.pixeldungeon.windows.WndQuest
import com.watabou.utils.Bundle
import com.watabou.utils.Random
class Imp : NPC() {
    init {
        name = "ambitious imp"
        spriteClass = ImpSprite::class.java
    }
    private var seenBefore = false
    override fun act(): Boolean {
        if (!Quest.given && Dungeon.visible[pos]) {
            if (!seenBefore) {
                Dungeon.hero?.let { yell(Utils.format(TXT_HEY, it.className())) }
            }
            seenBefore = true
        } else {
            seenBefore = false
        }
        throwItem()
        return super.act()
    }
    override fun defenseSkill(enemy: Char?): Int {
        return 1000
    }
    override fun defenseVerb(): String {
        return "evaded"
    }
    override fun damage(dmg: Int, src: Any?) {
    }
    override fun add(buff: Buff) {
    }
    override fun reset(): Boolean {
        return true
    }
    override fun interact() {
        val hero = Dungeon.hero ?: return
        sprite?.turnTo(pos, hero.pos)
        if (Quest.given) {
            val tokens = hero.belongings.getItem(DwarfToken::class.java)
            if (tokens != null && (tokens.quantity() >= 8 || (!Quest.alternative && tokens.quantity() >= 6))) {
                GameScene.show(WndImp(this, tokens))
            } else {
                tell(if (Quest.alternative) TXT_MONKS2 else TXT_GOLEMS2, hero.className())
            }
        } else {
            tell(if (Quest.alternative) TXT_MONKS1 else TXT_GOLEMS1)
            Quest.given = true
            Quest.completed = false
            Journal.add(Journal.Feature.IMP)
        }
    }
    private fun tell(format: String, vararg args: Any) {
        GameScene.show(WndQuest(this, Utils.format(format, *args)))
    }
    fun flee() {
        Dungeon.hero?.let { yell(Utils.format(TXT_CYA, it.className())) }
        destroy()
        sprite?.die()
    }
    override fun description(): String {
        return "Imps are lesser demons. They are notable for neither their strength nor their magic talent, but they are quite smart and sociable. Many imps prefer to live among non-demons."
    }
    companion object {
        private const val TXT_GOLEMS1 = "Are you an adventurer? I love adventurers! You can always rely on them if something needs to be killed. Am I right? For a bounty, of course ;)\nIn my case this is _golems_ who need to be killed. You see, I'm going to start a little business here, but these stupid golems are bad for business! It's very hard to negotiate with wandering lumps of granite, damn them! So please, kill... let's say _6 of them_ and a reward is yours."
        private const val TXT_MONKS1 = "Are you an adventurer? I love adventurers! You can always rely on them if something needs to be killed. Am I right? For a bounty, of course ;)\nIn my case this is _monks_ who need to be killed. You see, I'm going to start a little business here, but these lunatics don't buy anything themselves and will scare away other customers. So please, kill... let's say _8 of them_ and a reward is yours."
        private const val TXT_GOLEMS2 = "How is your golem safari going?"
        private const val TXT_MONKS2 = "Oh, you are still alive! I knew that your kung-fu is stronger ;) Just don't forget to grab these monks' tokens."
        private const val TXT_CYA = "See you, %s!"
        private const val TXT_HEY = "Psst, %s!"
    }
    object Quest {
        var alternative: Boolean = false
        var spawned: Boolean = false
        var given: Boolean = false
        var completed: Boolean = false
        var reward: Ring? = null
        fun reset() {
            spawned = false
            reward = null
        }
        private const val NODE = "demon"
        private const val ALTERNATIVE = "alternative"
        private const val SPAWNED = "spawned"
        private const val GIVEN = "given"
        private const val COMPLETED = "completed"
        private const val REWARD = "reward"
        fun storeInBundle(bundle: Bundle) {
            val node = Bundle()
            node.put(SPAWNED, spawned)
            if (spawned) {
                node.put(ALTERNATIVE, alternative)
                node.put(GIVEN, given)
                node.put(COMPLETED, completed)
                node.put(REWARD, reward)
            }
            bundle.put(NODE, node)
        }
        fun restoreFromBundle(bundle: Bundle) {
            val node = bundle.getBundle(NODE)
            if (!node.isNull() && node.getBoolean(SPAWNED).also { spawned = it }) {
                alternative = node.getBoolean(ALTERNATIVE)
                given = node.getBoolean(GIVEN)
                completed = node.getBoolean(COMPLETED)
                if (completed) {
                    reward = null
                } else {
                    val r = if (alternative) RingOfPower() else RingOfThorns()
                    r.identify()
                    r.upgrade()
                    if (Random.Int(2) == 0) {
                        r.upgrade()
                    }
                    if (Random.Int(3) == 0) {
                        r.cursed = true
                    }
                    reward = r
                }
            } else {
                reset()
            }
        }
        @Suppress("UNUSED_PARAMETER")
        fun spawn(level: CityLevel, room: Room?) {
            if (!spawned && Dungeon.depth > 16 && Random.Int(20 - Dungeon.depth) == 0) {
                val imp = Imp()
                do {
                    imp.pos = level.randomRespawnCell()
                } while (imp.pos == -1 || level.heaps[imp.pos] != null)
                level.mobs.add(imp)
                Actor.occupyCell(imp)
                spawned = true
                alternative = Random.Int(2) == 0
                given = false
                completed = false
                val r = if (alternative) RingOfPower() else RingOfThorns()
                r.identify()
                r.upgrade(2)
                r.cursed = true
                reward = r
            }
        }
        fun process(mob: Mob) {
            if (spawned && given && !completed) {
                if (alternative && mob is Monk || !alternative && mob is Golem) {
                    Dungeon.level?.drop(DwarfToken(), mob.pos)?.sprite?.drop()
                }
            }
        }
        fun complete() {
            reward = null
            completed = true
            Journal.remove(Journal.Feature.IMP)
        }
        fun isCompleted(): Boolean {
            return completed
        }
    }
}
