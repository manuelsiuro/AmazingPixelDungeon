package com.watabou.pixeldungeon.actors.mobs.npcs

import com.watabou.pixeldungeon.Dungeon
import com.watabou.pixeldungeon.actors.Actor
import com.watabou.pixeldungeon.actors.Char
import com.watabou.pixeldungeon.actors.buffs.Buff
import com.watabou.pixeldungeon.items.Generator
import com.watabou.pixeldungeon.items.Gold
import com.watabou.pixeldungeon.items.Item
import com.watabou.pixeldungeon.llm.LlmTextEnhancer
import com.watabou.pixeldungeon.quests.AiQuest
import com.watabou.pixeldungeon.quests.AiQuestBook
import com.watabou.pixeldungeon.quests.AiQuestGenerator
import com.watabou.pixeldungeon.scenes.GameScene
import com.watabou.pixeldungeon.sprites.AiNpcSprite
import com.watabou.pixeldungeon.utils.GLog
import com.watabou.pixeldungeon.windows.WndAiQuest
import com.watabou.pixeldungeon.windows.WndAiQuestComplete
import com.watabou.utils.Bundle
import java.util.HashSet

class AiNpc : NPC() {

    var questId: Int = 0
    var variant: Int = 0
    var aiName: String = "Stranger"
    var personality: String = ""
    var questGiven: Boolean = false

    init {
        spriteClass = AiNpcSprite::class.java
        state = PASSIVE
    }

    override fun defenseSkill(enemy: Char?): Int = 1000

    override fun speed(): Float = 0.5f

    override fun chooseEnemy(): Char? = null

    override fun damage(dmg: Int, src: Any?) {}

    override fun add(buff: Buff) {}

    override fun reset(): Boolean = true

    override fun interact() {
        val hero = Dungeon.hero ?: return
        sprite?.turnTo(pos, hero.pos)

        val quests = AiQuestBook.findQuestsForNpc(variant, Dungeon.depth)
        val quest = quests.firstOrNull()

        if (quest == null) {
            // Generate a new quest for this NPC
            val newQuest = AiQuestGenerator.generateQuest(Dungeon.depth, variant)
            AiQuestBook.addQuest(newQuest)
            questId = newQuest.questId
            showQuestOffer(newQuest)
        } else {
            when (quest.status) {
                AiQuest.Status.OFFERED -> showQuestOffer(quest)
                AiQuest.Status.ACTIVE -> {
                    updateLazyProgress(quest)
                    if (quest.currentCount >= quest.targetCount) {
                        showQuestComplete(quest)
                    } else {
                        showQuestProgress(quest)
                    }
                }
                AiQuest.Status.COMPLETED -> {
                    val heroClass = hero.className()
                    val text = LlmTextEnhancer.enhanceNpcDialog(
                        aiName, "thanks", heroClass, Dungeon.depth,
                        "Thank you again, adventurer. You have my eternal gratitude."
                    )
                    GameScene.show(com.watabou.pixeldungeon.windows.WndQuest(this, text))
                }
                AiQuest.Status.FAILED -> {
                    val heroClass = hero.className()
                    val text = LlmTextEnhancer.enhanceNpcDialog(
                        aiName, "failed", heroClass, Dungeon.depth,
                        "It seems you were unable to complete my request. Perhaps another time."
                    )
                    GameScene.show(com.watabou.pixeldungeon.windows.WndQuest(this, text))
                }
            }
        }
    }

    private fun showQuestOffer(quest: AiQuest) {
        val hero = Dungeon.hero ?: return
        val heroClass = hero.className()
        val fallback = "Greetings, adventurer! I am ${quest.npcName}. ${quest.questDescription} " +
            "I will reward you handsomely - ${quest.goldReward} gold and a useful item."
        val text = LlmTextEnhancer.generateAiQuestText(
            quest.npcName, quest.npcPersonality, quest.type.name,
            quest.questDescription, heroClass, Dungeon.depth, fallback
        )
        GameScene.show(WndAiQuest(this, quest, text))
    }

    private fun showQuestProgress(quest: AiQuest) {
        val hero = Dungeon.hero ?: return
        val heroClass = hero.className()
        val fallback = "You're making progress. ${quest.progressText()}. Keep going!"
        val text = LlmTextEnhancer.enhanceNpcDialog(
            aiName, "progress", heroClass, Dungeon.depth, fallback
        )
        GameScene.show(com.watabou.pixeldungeon.windows.WndQuest(this, text))
    }

    private fun showQuestComplete(quest: AiQuest) {
        val hero = Dungeon.hero ?: return
        val heroClass = hero.className()
        val fallback = quest.completionText
        val text = LlmTextEnhancer.enhanceNpcDialog(
            aiName, "complete", heroClass, Dungeon.depth, fallback
        )
        GameScene.show(WndAiQuestComplete(this, quest, text))
    }

    private fun updateLazyProgress(quest: AiQuest) {
        when (quest.type) {
            AiQuest.Type.COLLECT_GOLD -> {
                quest.currentCount = Dungeon.gold
            }
            AiQuest.Type.SURVIVE_TURNS -> {
                quest.currentCount = (Actor.now - quest.startTurn).toInt()
            }
            AiQuest.Type.EXPLORE_ROOMS -> {
                val level = Dungeon.level ?: return
                var visitedCount = 0
                for (v in level.visited) {
                    if (v) visitedCount++
                }
                quest.currentCount = visitedCount / 20 // rough room approximation
            }
            else -> {}
        }
    }

    fun giveRewards(quest: AiQuest) {
        val hero = Dungeon.hero ?: return

        // Gold reward
        if (quest.goldReward > 0) {
            Dungeon.gold += quest.goldReward
            GLog.p("You received %d gold.", quest.goldReward)
        }

        // Item reward
        val itemClassName = quest.rewardItemClass
        if (itemClassName != null) {
            try {
                val itemClass = Class.forName(itemClassName)
                val item = itemClass.getDeclaredConstructor().newInstance() as Item
                if (item.collect()) {
                    GLog.p("You received %s.", item.name())
                } else {
                    Dungeon.level?.drop(item, hero.pos)?.sprite?.drop()
                }
            } catch (e: Exception) {
                // Fallback: give a random item
                val item = Generator.random() ?: return
                if (item.collect()) {
                    GLog.p("You received %s.", item.name())
                } else {
                    Dungeon.level?.drop(item, hero.pos)?.sprite?.drop()
                }
            }
        }

        AiQuestBook.completeQuest(quest)
    }

    override fun description(): String {
        return "$aiName, a $personality. They seem to have a task for willing adventurers."
    }

    override fun storeInBundle(bundle: Bundle) {
        super.storeInBundle(bundle)
        bundle.put(QUEST_ID, questId)
        bundle.put(VARIANT, variant)
        bundle.put(AI_NAME, aiName)
        bundle.put(PERSONALITY, personality)
        bundle.put(QUEST_GIVEN, questGiven)
    }

    override fun restoreFromBundle(bundle: Bundle) {
        super.restoreFromBundle(bundle)
        questId = bundle.getInt(QUEST_ID)
        variant = bundle.getInt(VARIANT)
        aiName = bundle.getString(AI_NAME)
        personality = bundle.getString(PERSONALITY)
        questGiven = bundle.getBoolean(QUEST_GIVEN)
        name = aiName
    }

    override fun immunities(): HashSet<Class<*>> = IMMUNITIES

    companion object {
        private val IMMUNITIES = hashSetOf<Class<*>>()

        private const val QUEST_ID = "questId"
        private const val VARIANT = "variant"
        private const val AI_NAME = "aiName"
        private const val PERSONALITY = "personality"
        private const val QUEST_GIVEN = "questGiven"
    }
}
