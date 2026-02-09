package com.watabou.pixeldungeon.quests

import com.watabou.pixeldungeon.Badges
import com.watabou.pixeldungeon.actors.mobs.Mob
import com.watabou.pixeldungeon.items.Item
import com.watabou.pixeldungeon.plants.Plant
import com.watabou.utils.Bundle

object AiQuestBook {

    var activeQuests = ArrayList<AiQuest>()
    var completedCount: Int = 0
    var nextQuestId: Int = 1

    fun reset() {
        activeQuests.clear()
        completedCount = 0
        nextQuestId = 1
    }

    fun addQuest(quest: AiQuest) {
        quest.questId = nextQuestId++
        activeQuests.add(quest)
    }

    fun findQuestById(questId: Int): AiQuest? {
        return activeQuests.find { it.questId == questId }
    }

    fun findQuestsForNpc(npcVariant: Int, depth: Int): List<AiQuest> {
        return activeQuests.filter { it.npcVariant == npcVariant && it.depth == depth }
    }

    fun onMobKilled(mob: Mob) {
        val mobClassName = mob.javaClass.name
        for (quest in activeQuests) {
            if (quest.status != AiQuest.Status.ACTIVE) continue
            when (quest.type) {
                AiQuest.Type.KILL_MOBS -> quest.currentCount++
                AiQuest.Type.KILL_TYPE -> {
                    if (quest.targetMobClass == mobClassName) {
                        quest.currentCount++
                    }
                }
                else -> {}
            }
        }
    }

    fun onItemCollected(item: Item) {
        for (quest in activeQuests) {
            if (quest.status != AiQuest.Status.ACTIVE) continue
            when (quest.type) {
                AiQuest.Type.FIND_ITEM -> {
                    val targetClass = quest.targetMobClass // reusing field for item class
                    if (targetClass != null && item.javaClass.name == targetClass) {
                        quest.currentCount++
                    }
                }
                AiQuest.Type.COLLECT_SEEDS -> {
                    if (item is Plant.Seed) quest.currentCount++
                }
                else -> {}
            }
        }
    }

    fun onTrapTriggered() {
        for (quest in activeQuests) {
            if (quest.status != AiQuest.Status.ACTIVE) continue
            if (quest.type == AiQuest.Type.DISARM_TRAPS) quest.currentCount++
        }
    }

    fun completeQuest(quest: AiQuest) {
        quest.status = AiQuest.Status.COMPLETED
        completedCount++
        Badges.validateAiQuestsCompleted()
    }

    fun removeCompletedQuests() {
        activeQuests.removeAll { it.status == AiQuest.Status.COMPLETED }
    }

    fun storeInBundle(bundle: Bundle) {
        val node = Bundle()
        node.put(COMPLETED_COUNT, completedCount)
        node.put(NEXT_ID, nextQuestId)
        node.put(QUESTS, activeQuests)
        bundle.put(NODE, node)
    }

    fun restoreFromBundle(bundle: Bundle) {
        val node = bundle.getBundle(NODE)
        if (!node.isNull()) {
            completedCount = node.getInt(COMPLETED_COUNT)
            nextQuestId = node.getInt(NEXT_ID)
            if (nextQuestId == 0) nextQuestId = 1
            activeQuests.clear()
            val collection = node.getCollection(QUESTS)
            for (b in collection) {
                if (b is AiQuest) {
                    activeQuests.add(b)
                }
            }
        } else {
            reset()
        }
    }

    private const val NODE = "aiQuestBook"
    private const val COMPLETED_COUNT = "completedCount"
    private const val NEXT_ID = "nextId"
    private const val QUESTS = "quests"
}
