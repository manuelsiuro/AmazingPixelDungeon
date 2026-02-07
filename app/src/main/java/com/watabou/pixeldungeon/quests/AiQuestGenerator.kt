package com.watabou.pixeldungeon.quests

import com.watabou.pixeldungeon.items.food.MysteryMeat
import com.watabou.pixeldungeon.items.food.Pasty
import com.watabou.pixeldungeon.items.potions.PotionOfHealing
import com.watabou.pixeldungeon.items.potions.PotionOfStrength
import com.watabou.pixeldungeon.items.scrolls.ScrollOfIdentify
import com.watabou.pixeldungeon.items.scrolls.ScrollOfMagicMapping
import com.watabou.pixeldungeon.items.scrolls.ScrollOfUpgrade
import com.watabou.utils.Random

object AiQuestGenerator {

    private val NPC_NAMES = arrayOf(
        "Brin the Wanderer",
        "Kelda Ashmore",
        "Theron Dusk",
        "Mira Silverthread",
        "Grix the Collector",
        "Selene Frostwhisper",
        "Dagoth the Bold",
        "Yara Embercrest",
        "Voss Ironhand",
        "Lira Moonshade"
    )

    private val NPC_PERSONALITIES = arrayOf(
        "weary but hopeful traveler",
        "gruff and no-nonsense veteran",
        "mysterious and enigmatic stranger",
        "cheerful and oddly optimistic soul",
        "paranoid and jittery survivor",
        "serene and wise mystic",
        "boastful and overconfident warrior",
        "quiet and observant scout",
        "stern and practical merchant",
        "whimsical and eccentric wanderer"
    )

    private val REWARD_ITEMS = arrayOf(
        PotionOfHealing::class.java.name,
        ScrollOfIdentify::class.java.name,
        ScrollOfMagicMapping::class.java.name,
        PotionOfStrength::class.java.name,
        ScrollOfUpgrade::class.java.name,
        Pasty::class.java.name,
        MysteryMeat::class.java.name
    )

    fun npcCountForDepth(depth: Int): Int {
        return when {
            depth <= 5 -> 1
            depth <= 15 -> 2
            else -> 3
        }
    }

    fun generateQuest(depth: Int, variant: Int): AiQuest {
        val quest = AiQuest()
        quest.depth = depth
        quest.npcVariant = variant
        quest.npcName = NPC_NAMES[variant % NPC_NAMES.size]
        quest.npcPersonality = NPC_PERSONALITIES[variant % NPC_PERSONALITIES.size]

        val types = AiQuest.Type.values()
        // Limit quest types based on depth to keep early game simple
        val availableTypes = if (depth <= 5) {
            arrayOf(AiQuest.Type.KILL_MOBS, AiQuest.Type.COLLECT_GOLD, AiQuest.Type.SURVIVE_TURNS)
        } else {
            types
        }
        quest.type = availableTypes[Random.Int(availableTypes.size)]

        when (quest.type) {
            AiQuest.Type.KILL_MOBS -> {
                quest.targetCount = 3 + depth / 3
                quest.questDescription = "Slay ${quest.targetCount} monsters on this floor."
                quest.completionText = "You've done it! The monsters are thinned out. Here's your reward."
            }
            AiQuest.Type.COLLECT_GOLD -> {
                quest.targetCount = 50 + depth * 20
                quest.questDescription = "Accumulate at least ${quest.targetCount} gold."
                quest.completionText = "Impressive wealth! You've earned this reward."
            }
            AiQuest.Type.SURVIVE_TURNS -> {
                quest.targetCount = 30 + depth * 2
                quest.questDescription = "Survive for ${quest.targetCount} turns."
                quest.completionText = "Your endurance is remarkable. Take this."
            }
            AiQuest.Type.FIND_ITEM -> {
                quest.targetCount = 1
                quest.targetMobClass = PotionOfHealing::class.java.name
                quest.questDescription = "Find and collect a Potion of Healing."
                quest.completionText = "You found what I needed! Here, take this in return."
            }
            AiQuest.Type.EXPLORE_ROOMS -> {
                quest.targetCount = 5 + depth / 4
                quest.questDescription = "Explore at least ${quest.targetCount} rooms."
                quest.completionText = "You've mapped this area well. This is for you."
            }
            AiQuest.Type.KILL_TYPE -> {
                quest.targetCount = 2 + depth / 5
                quest.questDescription = "Hunt ${quest.targetCount} specific creatures."
                quest.completionText = "The hunt is over. Well done, adventurer."
            }
        }

        // Rewards
        quest.goldReward = 20 + depth * 10 + Random.Int(depth * 5 + 1)
        quest.rewardItemClass = REWARD_ITEMS[Random.Int(REWARD_ITEMS.size)]

        return quest
    }

    fun npcNameForVariant(variant: Int): String = NPC_NAMES[variant % NPC_NAMES.size]
    fun npcPersonalityForVariant(variant: Int): String = NPC_PERSONALITIES[variant % NPC_PERSONALITIES.size]
}
