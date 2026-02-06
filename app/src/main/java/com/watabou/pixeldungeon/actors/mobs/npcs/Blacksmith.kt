package com.watabou.pixeldungeon.actors.mobs.npcs
import com.watabou.noosa.audio.Sample
import com.watabou.pixeldungeon.Assets
import com.watabou.pixeldungeon.Badges
import com.watabou.pixeldungeon.Dungeon
import com.watabou.pixeldungeon.Journal
import com.watabou.pixeldungeon.actors.Char
import com.watabou.pixeldungeon.actors.buffs.Buff
import com.watabou.pixeldungeon.actors.hero.Hero
import com.watabou.pixeldungeon.items.EquipableItem
import com.watabou.pixeldungeon.items.Item
import com.watabou.pixeldungeon.items.quest.DarkGold
import com.watabou.pixeldungeon.items.quest.Pickaxe
import com.watabou.pixeldungeon.items.scrolls.ScrollOfUpgrade
import com.watabou.pixeldungeon.levels.Room
import com.watabou.pixeldungeon.scenes.GameScene
import com.watabou.pixeldungeon.sprites.BlacksmithSprite
import com.watabou.pixeldungeon.utils.GLog
import com.watabou.pixeldungeon.llm.LlmTextEnhancer
import com.watabou.pixeldungeon.windows.WndBlacksmith
import com.watabou.pixeldungeon.windows.WndQuest
import com.watabou.utils.Bundle
import com.watabou.utils.Random
class Blacksmith : NPC() {
    init {
        name = "troll blacksmith"
        spriteClass = BlacksmithSprite::class.java
    }
    override fun act(): Boolean {
        throwItem()
        return super.act()
    }
    override fun interact() {
        val hero = Dungeon.hero ?: return
        sprite?.turnTo(pos, hero.pos)
        val heroClass = hero.className()
        val depth = Dungeon.depth
        if (!Quest.given) {
            val baseText = if (Quest.alternative) TXT_BLOOD_1 else TXT_GOLD_1
            val questState = if (Quest.alternative) "blood_initial" else "gold_initial"
            val text = LlmTextEnhancer.enhanceNpcDialog("troll blacksmith", questState, heroClass, depth, baseText)
            GameScene.show(object : WndQuest(this@Blacksmith, text) {
                override fun onBackPressed() {
                    super.onBackPressed()
                    Quest.given = true
                    Quest.completed = false
                    val pick = Pickaxe()
                    if (pick.doPickUp(hero)) {
                        GLog.i(Hero.TXT_YOU_NOW_HAVE, pick.name())
                    } else {
                        Dungeon.level?.drop(pick, hero.pos)?.sprite?.drop()
                    }
                }
            })
            Journal.add(Journal.Feature.TROLL)
        } else if (!Quest.completed) {
            if (Quest.alternative) {
                val pick = hero.belongings.getItem(Pickaxe::class.java)
                if (pick == null) {
                    tell(TXT2)
                } else if (!pick.bloodStained) {
                    tell(TXT4)
                } else {
                    if (pick.isEquipped(hero)) {
                        pick.doUnequip(hero, false)
                    }
                    pick.detach(hero.belongings.backpack)
                    tell(TXT_COMPLETED)
                    Quest.completed = true
                    Quest.reforged = false
                }
            } else {
                val pick = hero.belongings.getItem(Pickaxe::class.java)
                val gold = hero.belongings.getItem(DarkGold::class.java)
                if (pick == null) {
                    tell(TXT2)
                } else if (gold == null || gold.quantity() < 15) {
                    tell(TXT3)
                } else {
                    if (pick.isEquipped(hero)) {
                        pick.doUnequip(hero, false)
                    }
                    pick.detach(hero.belongings.backpack)
                    gold.detachAll(hero.belongings.backpack)
                    tell(TXT_COMPLETED)
                    Quest.completed = true
                    Quest.reforged = false
                }
            }
        } else if (!Quest.reforged) {
            GameScene.show(WndBlacksmith(this, hero))
        } else {
            tell(TXT_GET_LOST)
        }
    }
    private fun tell(text: String) {
        val heroClass = Dungeon.hero?.className() ?: "adventurer"
        val enhanced = LlmTextEnhancer.enhanceNpcDialog("troll blacksmith", "tell", heroClass, Dungeon.depth, text)
        GameScene.show(WndQuest(this, enhanced))
    }
    override fun defenseSkill(enemy: Char?): Int {
        return 1000
    }
    override fun damage(dmg: Int, src: Any?) {
    }
    override fun add(buff: Buff) {
    }
    override fun reset(): Boolean {
        return true
    }
    override fun description(): String {
        return "This troll blacksmith looks like all trolls look: he is tall and lean, and his skin resembles stone in both color and texture. The troll blacksmith is tinkering with unproportionally small tools."
    }
    companion object {
         const val TXT_GOLD_1 = "Hey human! Wanna be useful, eh? Take dis pickaxe and mine me some _dark gold ore_, _15 pieces_ should be enough. " +
                "What do you mean, how am I gonna pay? You greedy...\n" +
                "Ok, ok, I don't have money to pay, but I can do some smithin' for you. Consider yourself lucky, " +
                "I'm the only blacksmith around."
         const val TXT_BLOOD_1 = "Hey human! Wanna be useful, eh? Take dis pickaxe and _kill a bat_ wit' it, I need its blood on the head. " +
                "What do you mean, how am I gonna pay? You greedy...\n" +
                "Ok, ok, I don't have money to pay, but I can do some smithin' for you. Consider yourself lucky, " +
                "I'm the only blacksmith around."
         const val TXT2 = "Are you kiddin' me? Where is my pickaxe?!"
         const val TXT3 = "Dark gold ore. 15 pieces. Seriously, is it dat hard?"
         const val TXT4 = "I said I need bat blood on the pickaxe. Chop chop!"
         const val TXT_COMPLETED = "Oh, you have returned... Better late dan never."
         const val TXT_GET_LOST = "I'm busy. Get lost!"
        private const val TXT_LOOKS_BETTER = "your %s certainly looks better now"
        fun verify(item1: Item, item2: Item): String? {
            if (item1 === item2) {
                return "Select 2 different items, not the same item twice!"
            }
            if (item1::class.java != item2::class.java) {
                return "Select 2 items of the same type!"
            }
            if (!item1.isIdentified || !item2.isIdentified) {
                return "I need to know what I'm working with, identify them first!"
            }
            if (item1.cursed || item2.cursed) {
                return "I don't work with cursed items!"
            }
            if (item1.level() < 0 || item2.level() < 0) {
                return "It's a junk, the quality is too poor!"
            }
            if (!item1.isUpgradable || !item2.isUpgradable) {
                return "I can't reforge these items!"
            }
            return null
        }
        fun upgrade(item1: Item, item2: Item) {
            val hero = Dungeon.hero ?: return
            val first: Item
            val second: Item
            if (item2.level() > item1.level()) {
                first = item2
                second = item1
            } else {
                first = item1
                second = item2
            }
            Sample.play(Assets.SND_EVOKE)
            ScrollOfUpgrade.upgrade(hero)
            Item.evoke(hero)
            if (first.isEquipped(hero)) {
                (first as EquipableItem).doUnequip(hero, true)
            }
            first.upgrade()
            GLog.p(TXT_LOOKS_BETTER, first.name())
            hero.spendAndNext(2f)
            Badges.validateItemLevelAquired(first)
            if (second.isEquipped(hero)) {
                (second as EquipableItem).doUnequip(hero, false)
            }
            second.detachAll(hero.belongings.backpack)
            Quest.reforged = true
            Journal.remove(Journal.Feature.TROLL)
        }
    }
    object Quest {
        var spawned: Boolean = false
        var alternative: Boolean = false
        var given: Boolean = false
        var completed: Boolean = false
        var reforged: Boolean = false
        fun reset() {
            spawned = false
            given = false
            completed = false
            reforged = false
        }
        private const val NODE = "blacksmith"
        private const val SPAWNED = "spawned"
        private const val ALTERNATIVE = "alternative"
        private const val GIVEN = "given"
        private const val COMPLETED = "completed"
        private const val REFORGED = "reforged"
        fun storeInBundle(bundle: Bundle) {
            val node = Bundle()
            node.put(SPAWNED, spawned)
            if (spawned) {
                node.put(ALTERNATIVE, alternative)
                node.put(GIVEN, given)
                node.put(COMPLETED, completed)
                node.put(REFORGED, reforged)
            }
            bundle.put(NODE, node)
        }
        fun restoreFromBundle(bundle: Bundle) {
            val node = bundle.getBundle(NODE)
            if (!node.isNull() && node.getBoolean(SPAWNED).also { spawned = it }) {
                alternative = node.getBoolean(ALTERNATIVE)
                given = node.getBoolean(GIVEN)
                completed = node.getBoolean(COMPLETED)
                reforged = node.getBoolean(REFORGED)
            } else {
                reset()
            }
        }
        fun spawn(rooms: Collection<Room>) {
            if (!spawned && Dungeon.depth > 11 && Random.Int(15 - Dungeon.depth) == 0) {
                var blacksmith: Room?
                for (r in rooms) {
                    if (r.type == Room.Type.STANDARD && r.width() > 4 && r.height() > 4) {
                        blacksmith = r
                        blacksmith.type = Room.Type.BLACKSMITH
                        spawned = true
                        alternative = Random.Int(2) == 0
                        given = false
                        break
                    }
                }
            }
        }
    }
}
