package com.watabou.pixeldungeon.quests

import com.watabou.utils.Bundlable
import com.watabou.utils.Bundle

class AiQuest : Bundlable {

    enum class Type {
        KILL_MOBS, COLLECT_GOLD, SURVIVE_TURNS, FIND_ITEM, EXPLORE_ROOMS, KILL_TYPE,
        COLLECT_SEEDS, DISARM_TRAPS
    }

    enum class Status {
        OFFERED, ACTIVE, COMPLETED, FAILED
    }

    var questId: Int = 0
    var type: Type = Type.KILL_MOBS
    var status: Status = Status.OFFERED
    var targetCount: Int = 0
    var currentCount: Int = 0
    var depth: Int = 0
    var npcName: String = ""
    var npcPersonality: String = ""
    var npcVariant: Int = 0
    var questDescription: String = ""
    var completionText: String = ""
    var goldReward: Int = 0
    var rewardItemClass: String? = null
    var startTurn: Float = 0f
    var targetMobClass: String? = null

    override fun storeInBundle(bundle: Bundle) {
        bundle.put(QUEST_ID, questId)
        bundle.put(TYPE, type.toString())
        bundle.put(STATUS, status.toString())
        bundle.put(TARGET_COUNT, targetCount)
        bundle.put(CURRENT_COUNT, currentCount)
        bundle.put(DEPTH, depth)
        bundle.put(NPC_NAME, npcName)
        bundle.put(NPC_PERSONALITY, npcPersonality)
        bundle.put(NPC_VARIANT, npcVariant)
        bundle.put(QUEST_DESC, questDescription)
        bundle.put(COMPLETION_TEXT, completionText)
        bundle.put(GOLD_REWARD, goldReward)
        bundle.put(REWARD_ITEM, rewardItemClass ?: "")
        bundle.put(START_TURN, startTurn)
        bundle.put(TARGET_MOB, targetMobClass ?: "")
    }

    override fun restoreFromBundle(bundle: Bundle) {
        questId = bundle.getInt(QUEST_ID)
        type = try {
            Type.valueOf(bundle.getString(TYPE))
        } catch (e: Exception) {
            Type.KILL_MOBS
        }
        status = try {
            Status.valueOf(bundle.getString(STATUS))
        } catch (e: Exception) {
            Status.OFFERED
        }
        targetCount = bundle.getInt(TARGET_COUNT)
        currentCount = bundle.getInt(CURRENT_COUNT)
        depth = bundle.getInt(DEPTH)
        npcName = bundle.getString(NPC_NAME)
        npcPersonality = bundle.getString(NPC_PERSONALITY)
        npcVariant = bundle.getInt(NPC_VARIANT)
        questDescription = bundle.getString(QUEST_DESC)
        completionText = bundle.getString(COMPLETION_TEXT)
        goldReward = bundle.getInt(GOLD_REWARD)
        val rewardStr = bundle.getString(REWARD_ITEM)
        rewardItemClass = if (rewardStr.isNotEmpty()) rewardStr else null
        startTurn = bundle.getFloat(START_TURN)
        val mobStr = bundle.getString(TARGET_MOB)
        targetMobClass = if (mobStr.isNotEmpty()) mobStr else null
    }

    fun progressText(): String {
        return when (type) {
            Type.KILL_MOBS, Type.KILL_TYPE -> "$currentCount/$targetCount kills"
            Type.COLLECT_GOLD -> "$currentCount/$targetCount gold"
            Type.SURVIVE_TURNS -> "$currentCount/$targetCount turns"
            Type.FIND_ITEM -> if (currentCount >= targetCount) "Found!" else "Searching..."
            Type.EXPLORE_ROOMS -> "$currentCount/$targetCount rooms"
            Type.COLLECT_SEEDS -> "$currentCount/$targetCount seeds"
            Type.DISARM_TRAPS -> "$currentCount/$targetCount traps"
        }
    }

    fun typeDesc(): String {
        return when (type) {
            Type.KILL_MOBS -> "Slay Monsters"
            Type.COLLECT_GOLD -> "Collect Gold"
            Type.SURVIVE_TURNS -> "Survive"
            Type.FIND_ITEM -> "Find Item"
            Type.EXPLORE_ROOMS -> "Explore"
            Type.KILL_TYPE -> "Hunt"
            Type.COLLECT_SEEDS -> "Gather Seeds"
            Type.DISARM_TRAPS -> "Disarm Traps"
        }
    }

    companion object {
        private const val QUEST_ID = "questId"
        private const val TYPE = "type"
        private const val STATUS = "status"
        private const val TARGET_COUNT = "targetCount"
        private const val CURRENT_COUNT = "currentCount"
        private const val DEPTH = "depth"
        private const val NPC_NAME = "npcName"
        private const val NPC_PERSONALITY = "npcPersonality"
        private const val NPC_VARIANT = "npcVariant"
        private const val QUEST_DESC = "questDesc"
        private const val COMPLETION_TEXT = "completionText"
        private const val GOLD_REWARD = "goldReward"
        private const val REWARD_ITEM = "rewardItem"
        private const val START_TURN = "startTurn"
        private const val TARGET_MOB = "targetMob"
    }
}
