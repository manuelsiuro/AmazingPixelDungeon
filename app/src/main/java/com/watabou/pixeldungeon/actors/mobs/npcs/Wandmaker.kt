package com.watabou.pixeldungeon.actors.mobs.npcs
import com.watabou.pixeldungeon.Dungeon
import com.watabou.pixeldungeon.Journal
import com.watabou.pixeldungeon.actors.Actor
import com.watabou.pixeldungeon.actors.Char
import com.watabou.pixeldungeon.actors.buffs.Buff
import com.watabou.pixeldungeon.items.Heap
import com.watabou.pixeldungeon.items.Item
import com.watabou.pixeldungeon.items.quest.CorpseDust
import com.watabou.pixeldungeon.items.quest.PhantomFish
import com.watabou.pixeldungeon.items.wands.*
import com.watabou.pixeldungeon.levels.Level
import com.watabou.pixeldungeon.levels.PrisonLevel
import com.watabou.pixeldungeon.levels.Room
import com.watabou.pixeldungeon.levels.Terrain
import com.watabou.pixeldungeon.plants.Rotberry
import com.watabou.pixeldungeon.scenes.GameScene
import com.watabou.pixeldungeon.sprites.WandmakerSprite
import com.watabou.pixeldungeon.utils.Utils
import com.watabou.pixeldungeon.windows.WndQuest
import com.watabou.pixeldungeon.windows.WndWandmaker
import com.watabou.utils.Bundle
import com.watabou.utils.Random
import java.util.*
class Wandmaker : NPC() {
    init {
        name = "old wandmaker"
        spriteClass = WandmakerSprite::class.java
    }
    override fun act(): Boolean {
        throwItem()
        return super.act()
    }
    override fun defenseSkill(enemy: Char?): Int {
        return 1000
    }
    override fun defenseVerb(): String {
        return "absorbed"
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
        Quest.type.handler?.interact(this)
    }
    fun tell(format: String, vararg args: Any) {
        GameScene.show(WndQuest(this, Utils.format(format, *args)))
    }
    override fun description(): String {
        return "This old but hale gentleman wears a slightly confused expression. He is protected by a magic shield."
    }
    object Quest {
        enum class Type(val handler: QuestHandler?) {
            ILLEGAL(null), BERRY(berryQuest), DUST(dustQuest), FISH(fishQuest)
        }
        var type: Type = Type.ILLEGAL
        var spawned: Boolean = false
        var given: Boolean = false
        var wand1: Wand? = null
        var wand2: Wand? = null
        fun reset() {
            spawned = false
            wand1 = null
            wand2 = null
        }
        private const val NODE = "wandmaker"
        private const val SPAWNED = "spawned"
        private const val TYPE_KEY = "type"
        private const val ALTERNATIVE = "alternative"
        private const val GIVEN = "given"
        private const val WAND1 = "wand1"
        private const val WAND2 = "wand2"
        fun storeInBundle(bundle: Bundle) {
            val node = Bundle()
            node.put(SPAWNED, spawned)
            if (spawned) {
                node.put(TYPE_KEY, type.toString())
                node.put(GIVEN, given)
                node.put(WAND1, wand1)
                node.put(WAND2, wand2)
            }
            bundle.put(NODE, node)
        }
        fun restoreFromBundle(bundle: Bundle) {
            val node = bundle.getBundle(NODE)
            if (!node.isNull() && node.getBoolean(SPAWNED).also { spawned = it }) {
                type = node.getEnum(TYPE_KEY, Type::class.java)
                if (type == Type.ILLEGAL) {
                    type = if (node.getBoolean(ALTERNATIVE)) Type.DUST else Type.BERRY
                }
                given = node.getBoolean(GIVEN)
                wand1 = node.get(WAND1) as? Wand
                wand2 = node.get(WAND2) as? Wand
            } else {
                reset()
            }
        }
        fun spawn(level: PrisonLevel, room: Room) {
            if (!spawned && Dungeon.depth > 6 && Random.Int(10 - Dungeon.depth) == 0) {
                val npc = Wandmaker()
                do {
                    npc.pos = room.random()
                } while (level.map[npc.pos] == Terrain.ENTRANCE || level.map[npc.pos] == Terrain.SIGN)
                level.mobs.add(npc)
                Actor.occupyCell(npc)
                spawned = true
                when (Random.Int(3)) {
                    0 -> type = Type.BERRY
                    1 -> type = Type.DUST
                    2 -> {
                        type = Type.FISH
                        var water = 0
                        for (i in 0 until Level.LENGTH) {
                            if (Level.water[i]) {
                                if (++water > Level.LENGTH / 16) {
                                    type = if (Random.Int(2) == 0) Type.BERRY else Type.DUST
                                    break
                                }
                            }
                        }
                    }
                }
                given = false
                wand1 = when (Random.Int(5)) {
                    0 -> WandOfAvalanche()
                    1 -> WandOfDisintegration()
                    2 -> WandOfFirebolt()
                    3 -> WandOfLightning()
                    4 -> WandOfPoison()
                    else -> WandOfAvalanche()
                }
                wand1?.random()?.upgrade()
                wand2 = when (Random.Int(5)) {
                    0 -> WandOfAmok()
                    1 -> WandOfBlink()
                    2 -> WandOfRegrowth()
                    3 -> WandOfSlowness()
                    4 -> WandOfReach()
                    else -> WandOfAmok()
                }
                wand2?.random()?.upgrade()
            }
        }
        fun complete() {
            wand1 = null
            wand2 = null
            Journal.remove(Journal.Feature.WANDMAKER)
        }
    }
    abstract class QuestHandler {
        protected var txtQuest1: String? = null
        protected var txtQuest2: String? = null
        fun interact(wandmaker: Wandmaker) {
            val hero = Dungeon.hero ?: return
            if (Quest.given) {
                val item = checkItem()
                if (item != null) {
                    GameScene.show(WndWandmaker(wandmaker, item))
                } else {
                    txtQuest2?.let { wandmaker.tell(it, hero.className()) }
                }
            } else {
                txtQuest1?.let { wandmaker.tell(it) }
                Quest.given = true
                placeItem()
                Journal.add(Journal.Feature.WANDMAKER)
            }
        }
        abstract fun checkItem(): Item?
        abstract fun placeItem()
    }
}
private val berryQuest = object : Wandmaker.QuestHandler() {
    init {
        txtQuest1 = "Oh, what a pleasant surprise to meet a decent person in such place! I came here for a rare ingredient - a _Rotberry seed_. Being a magic user, I'm quite able to defend myself against local monsters, but I'm getting lost in no time, it's very embarrassing. Probably you could help me? I would be happy to pay for your service with one of my best wands."
        txtQuest2 = "Any luck with a _Rotberry seed_, %s? No? Don't worry, I'm not in a hurry."
    }
    override fun checkItem(): Item? {
        return Dungeon.hero?.belongings?.getItem(Rotberry.Seed::class.java)
    }
    override fun placeItem() {
        val level = Dungeon.level ?: return
        var shrubPos = level.randomRespawnCell()
        while (level.heaps[shrubPos] != null) {
            shrubPos = level.randomRespawnCell()
        }
        level.plant(Rotberry.Seed(), shrubPos)
    }
}
private val dustQuest = object : Wandmaker.QuestHandler() {
    init {
        txtQuest1 = "Oh, what a pleasant surprise to meet a decent person in such place! I came here for a rare ingredient - _corpse dust_. It can be gathered from skeletal remains and there is an ample number of them in the dungeon. Being a magic user, I'm quite able to defend myself against local monsters, but I'm getting lost in no time, it's very embarrassing. Probably you could help me? I would be happy to pay for your service with one of my best wands."
        txtQuest2 = "Any luck with _corpse dust_, %s? Bone piles are the most obvious places to look."
    }
    override fun checkItem(): Item? {
        return Dungeon.hero?.belongings?.getItem(CorpseDust::class.java)
    }
    override fun placeItem() {
        val level = Dungeon.level ?: return
        val candidates = ArrayList<Heap>()
        for (heap in level.heaps.values()) {
            if (heap.type == Heap.Type.SKELETON && !Dungeon.visible[heap.pos]) {
                candidates.add(heap)
            }
        }
        if (candidates.isNotEmpty()) {
            Random.element(candidates)?.drop(CorpseDust())
        } else {
            var pos = level.randomRespawnCell()
            while (level.heaps[pos] != null) {
                pos = level.randomRespawnCell()
            }
            val heap = level.drop(CorpseDust(), pos)
            heap.type = Heap.Type.SKELETON
            heap.sprite?.link()
        }
    }
}
private val fishQuest = object : Wandmaker.QuestHandler() {
    init {
        txtQuest1 = "Oh, what a pleasant surprise to meet a decent person in such place! I came here for a rare ingredient: a _phantom fish_. You can catch it with your bare hands, but it's very hard to notice in the water. Being a magic user, I'm quite able to defend myself against local monsters, but I'm getting lost in no time, it's very embarrassing. Probably you could help me? I would be happy to pay for your service with one of my best wands."
        txtQuest2 = "Any luck with a _phantom fish_, %s? You may want to try searching for it in one of the local pools."
    }
    override fun checkItem(): Item? {
        return Dungeon.hero?.belongings?.getItem(PhantomFish::class.java)
    }
    override fun placeItem() {
        val level = Dungeon.level ?: return
        var heap: Heap? = null
        for (i in 0 until 100) {
            val pos = Random.Int(Level.LENGTH)
            if (Level.water[pos]) {
                heap = level.drop(PhantomFish(), pos)
                heap.type = Heap.Type.HIDDEN
                heap.sprite?.link()
                return
            }
        }
        if (heap == null) {
            var pos = level.randomRespawnCell()
            while (level.heaps[pos] != null) {
                pos = level.randomRespawnCell()
            }
            level.drop(PhantomFish(), pos)
        }
    }
}
