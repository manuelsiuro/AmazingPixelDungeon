package com.watabou.pixeldungeon.quests

import com.watabou.pixeldungeon.actors.mobs.Bat
import com.watabou.pixeldungeon.actors.mobs.Brute
import com.watabou.pixeldungeon.actors.mobs.Crab
import com.watabou.pixeldungeon.actors.mobs.Elemental
import com.watabou.pixeldungeon.actors.mobs.Eye
import com.watabou.pixeldungeon.actors.mobs.Gnoll
import com.watabou.pixeldungeon.actors.mobs.Golem
import com.watabou.pixeldungeon.actors.mobs.Monk
import com.watabou.pixeldungeon.actors.mobs.Rat
import com.watabou.pixeldungeon.actors.mobs.Scorpio
import com.watabou.pixeldungeon.actors.mobs.Shaman
import com.watabou.pixeldungeon.actors.mobs.Skeleton
import com.watabou.pixeldungeon.actors.mobs.Spinner
import com.watabou.pixeldungeon.actors.mobs.Succubus
import com.watabou.pixeldungeon.actors.mobs.Swarm
import com.watabou.pixeldungeon.actors.mobs.Thief
import com.watabou.pixeldungeon.actors.mobs.Warlock
import com.watabou.pixeldungeon.items.Bomb
import com.watabou.pixeldungeon.items.food.Food
import com.watabou.pixeldungeon.items.food.Pasty
import com.watabou.pixeldungeon.items.potions.PotionOfExperience
import com.watabou.pixeldungeon.items.potions.PotionOfFrost
import com.watabou.pixeldungeon.items.potions.PotionOfHealing
import com.watabou.pixeldungeon.items.potions.PotionOfInvisibility
import com.watabou.pixeldungeon.items.potions.PotionOfLevitation
import com.watabou.pixeldungeon.items.potions.PotionOfLiquidFlame
import com.watabou.pixeldungeon.items.potions.PotionOfMight
import com.watabou.pixeldungeon.items.potions.PotionOfMindVision
import com.watabou.pixeldungeon.items.potions.PotionOfPurity
import com.watabou.pixeldungeon.items.potions.PotionOfStrength
import com.watabou.pixeldungeon.items.scrolls.ScrollOfEnchantment
import com.watabou.pixeldungeon.items.scrolls.ScrollOfIdentify
import com.watabou.pixeldungeon.items.scrolls.ScrollOfMagicMapping
import com.watabou.pixeldungeon.items.scrolls.ScrollOfMirrorImage
import com.watabou.pixeldungeon.items.scrolls.ScrollOfPsionicBlast
import com.watabou.pixeldungeon.items.scrolls.ScrollOfRemoveCurse
import com.watabou.pixeldungeon.items.scrolls.ScrollOfTeleportation
import com.watabou.pixeldungeon.items.scrolls.ScrollOfTerror
import com.watabou.pixeldungeon.items.scrolls.ScrollOfUpgrade
import com.watabou.utils.Random

object AiQuestGenerator {

    // --- Data classes for targets ---

    private data class MobTarget(val className: String, val displayName: String)
    private data class ItemTarget(val className: String, val displayName: String)

    // --- NPC identity pools ---

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

    // --- Mob targets by depth tier ---

    private fun mobTargetsForDepth(depth: Int): List<MobTarget> {
        return when {
            depth <= 5 -> listOf(
                MobTarget(Rat::class.java.name, "rats"),
                MobTarget(Gnoll::class.java.name, "gnolls"),
                MobTarget(Crab::class.java.name, "crabs")
            )
            depth <= 10 -> listOf(
                MobTarget(Skeleton::class.java.name, "skeletons"),
                MobTarget(Thief::class.java.name, "thieves"),
                MobTarget(Shaman::class.java.name, "shamans"),
                MobTarget(Swarm::class.java.name, "fly swarms")
            )
            depth <= 15 -> listOf(
                MobTarget(Bat::class.java.name, "bats"),
                MobTarget(Brute::class.java.name, "brutes"),
                MobTarget(Spinner::class.java.name, "spinners"),
                MobTarget(Elemental::class.java.name, "elementals")
            )
            depth <= 20 -> listOf(
                MobTarget(Elemental::class.java.name, "elementals"),
                MobTarget(Monk::class.java.name, "monks"),
                MobTarget(Warlock::class.java.name, "warlocks"),
                MobTarget(Golem::class.java.name, "golems")
            )
            else -> listOf(
                MobTarget(Succubus::class.java.name, "succubi"),
                MobTarget(Eye::class.java.name, "evil eyes"),
                MobTarget(Scorpio::class.java.name, "scorpions")
            )
        }
    }

    // --- Item targets by depth tier ---

    private fun itemTargetsForDepth(depth: Int): List<ItemTarget> {
        return when {
            depth <= 5 -> listOf(
                ItemTarget(PotionOfHealing::class.java.name, "Potion of Healing"),
                ItemTarget(ScrollOfIdentify::class.java.name, "Scroll of Identify"),
                ItemTarget(ScrollOfMagicMapping::class.java.name, "Scroll of Magic Mapping"),
                ItemTarget(Food::class.java.name, "ration of food"),
                ItemTarget(PotionOfMindVision::class.java.name, "Potion of Mind Vision")
            )
            depth <= 10 -> listOf(
                ItemTarget(PotionOfHealing::class.java.name, "Potion of Healing"),
                ItemTarget(ScrollOfRemoveCurse::class.java.name, "Scroll of Remove Curse"),
                ItemTarget(PotionOfInvisibility::class.java.name, "Potion of Invisibility"),
                ItemTarget(PotionOfLevitation::class.java.name, "Potion of Levitation")
            )
            depth <= 15 -> listOf(
                ItemTarget(PotionOfHealing::class.java.name, "Potion of Healing"),
                ItemTarget(ScrollOfTeleportation::class.java.name, "Scroll of Teleportation"),
                ItemTarget(PotionOfFrost::class.java.name, "Potion of Frost"),
                ItemTarget(Bomb::class.java.name, "bomb"),
                ItemTarget(PotionOfLiquidFlame::class.java.name, "Potion of Liquid Flame")
            )
            depth <= 20 -> listOf(
                ItemTarget(ScrollOfMirrorImage::class.java.name, "Scroll of Mirror Image"),
                ItemTarget(PotionOfPurity::class.java.name, "Potion of Purity"),
                ItemTarget(ScrollOfTerror::class.java.name, "Scroll of Terror"),
                ItemTarget(PotionOfMindVision::class.java.name, "Potion of Mind Vision")
            )
            else -> listOf(
                ItemTarget(ScrollOfPsionicBlast::class.java.name, "Scroll of Psionic Blast"),
                ItemTarget(PotionOfInvisibility::class.java.name, "Potion of Invisibility"),
                ItemTarget(Bomb::class.java.name, "bomb")
            )
        }
    }

    // --- Tiered reward pool by depth ---

    private fun rewardItemForDepth(depth: Int): String {
        val pool = when {
            depth <= 5 -> arrayOf(
                PotionOfHealing::class.java.name,
                ScrollOfIdentify::class.java.name,
                Food::class.java.name,
                Pasty::class.java.name,
                ScrollOfMagicMapping::class.java.name
            )
            depth <= 10 -> arrayOf(
                PotionOfHealing::class.java.name,
                ScrollOfIdentify::class.java.name,
                ScrollOfRemoveCurse::class.java.name,
                PotionOfInvisibility::class.java.name,
                Bomb::class.java.name,
                Pasty::class.java.name
            )
            depth <= 15 -> arrayOf(
                PotionOfHealing::class.java.name,
                ScrollOfUpgrade::class.java.name,
                PotionOfStrength::class.java.name,
                ScrollOfRemoveCurse::class.java.name,
                PotionOfInvisibility::class.java.name,
                Bomb::class.java.name
            )
            depth <= 20 -> arrayOf(
                ScrollOfUpgrade::class.java.name,
                PotionOfStrength::class.java.name,
                ScrollOfEnchantment::class.java.name,
                PotionOfExperience::class.java.name,
                PotionOfHealing::class.java.name
            )
            else -> arrayOf(
                ScrollOfUpgrade::class.java.name,
                PotionOfMight::class.java.name,
                ScrollOfEnchantment::class.java.name,
                PotionOfExperience::class.java.name,
                PotionOfStrength::class.java.name
            )
        }
        return pool[Random.Int(pool.size)]
    }

    // --- Description templates (4 per type) with {count}, {mob}, {item} placeholders ---

    private val KILL_MOBS_DESCS = arrayOf(
        "Slay {count} monsters lurking on this floor.",
        "These halls are infested. Kill {count} creatures for me.",
        "I need {count} monsters cleared out before I can move on.",
        "Thin out the horde — slay at least {count} beasts."
    )
    private val KILL_MOBS_COMPLETIONS = arrayOf(
        "You've done it! The monsters are thinned out. Here's your reward.",
        "Much safer now. Take this for your trouble.",
        "That's the last of them. You've earned this."
    )

    private val COLLECT_GOLD_DESCS = arrayOf(
        "Accumulate at least {count} gold for me.",
        "I need {count} gold to fund my expedition. Help me gather it.",
        "Bring me {count} gold and I'll make it worth your while.",
        "Scrape together {count} gold from this dungeon."
    )
    private val COLLECT_GOLD_COMPLETIONS = arrayOf(
        "Impressive wealth! You've earned this reward.",
        "That's enough gold. Here, take your payment.",
        "Well done, prospector. This is yours."
    )

    private val SURVIVE_TURNS_DESCS = arrayOf(
        "Survive for {count} turns on this floor.",
        "Stay alive for {count} turns. Prove your endurance.",
        "I need {count} turns to finish my work. Just stay alive that long.",
        "Hold out for {count} turns and the reward is yours."
    )
    private val SURVIVE_TURNS_COMPLETIONS = arrayOf(
        "Your endurance is remarkable. Take this.",
        "You survived! Not many can say that. Here.",
        "Still standing after all that. Impressive."
    )

    private val FIND_ITEM_DESCS = arrayOf(
        "Find and bring me a {item}.",
        "I desperately need a {item}. Find one for me.",
        "Somewhere on this floor is a {item}. Retrieve it.",
        "Locate a {item} and I'll reward you handsomely."
    )
    private val FIND_ITEM_COMPLETIONS = arrayOf(
        "You found what I needed! Here, take this in return.",
        "That's exactly what I was looking for. Your reward.",
        "Perfect! I knew you could find it. This is yours."
    )

    private val EXPLORE_ROOMS_DESCS = arrayOf(
        "Explore at least {count} rooms on this floor.",
        "Map out {count} rooms for me. I need to know the layout.",
        "Scout ahead and explore {count} rooms.",
        "Chart {count} rooms so others can follow safely."
    )
    private val EXPLORE_ROOMS_COMPLETIONS = arrayOf(
        "You've mapped this area well. This is for you.",
        "Excellent cartography! Here's your payment.",
        "Now I know the lay of the land. Take this."
    )

    private val KILL_TYPE_DESCS = arrayOf(
        "Hunt down {count} {mob} for me.",
        "I have a bounty on {mob}. Kill {count} of them.",
        "The {mob} here are a menace. Slay {count}.",
        "Bring me proof you've slain {count} {mob}."
    )
    private val KILL_TYPE_COMPLETIONS = arrayOf(
        "The hunt is over. Well done, adventurer.",
        "Those {mob} won't bother anyone again. Your reward.",
        "Fine hunting! Take this bounty."
    )

    private val COLLECT_SEEDS_DESCS = arrayOf(
        "I'm studying the flora of this dungeon. Gather {count} seeds for me.",
        "I need {count} plant seeds for my alchemical research.",
        "Collect {count} seeds from the undergrowth. Any type will do.",
        "Trample through the grass and bring me {count} seeds."
    )
    private val COLLECT_SEEDS_COMPLETIONS = arrayOf(
        "Wonderful specimens! Here's your reward.",
        "These seeds are perfect for my research. Take this.",
        "Exactly what I needed. You have a botanist's eye."
    )

    private val DISARM_TRAPS_DESCS = arrayOf(
        "I need someone brave enough to trigger {count} traps and clear the path.",
        "Step on {count} traps so others can pass safely. I'll make it worth the pain.",
        "Disarm {count} traps by setting them off. Risky, but rewarding.",
        "There are traps ahead. Trigger {count} of them to make the way safe."
    )
    private val DISARM_TRAPS_COMPLETIONS = arrayOf(
        "Brave soul! The path is safer now. Here's your reward.",
        "You took quite a beating for that. Well earned.",
        "That took courage. Take this for your sacrifice."
    )

    // --- Public API ---

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
        quest.type = types[Random.Int(types.size)]

        when (quest.type) {
            AiQuest.Type.KILL_MOBS -> {
                quest.targetCount = 3 + depth / 3
                val desc = KILL_MOBS_DESCS[Random.Int(KILL_MOBS_DESCS.size)]
                quest.questDescription = desc.replace("{count}", quest.targetCount.toString())
                quest.completionText = KILL_MOBS_COMPLETIONS[Random.Int(KILL_MOBS_COMPLETIONS.size)]
            }
            AiQuest.Type.COLLECT_GOLD -> {
                quest.targetCount = 50 + depth * 20
                val desc = COLLECT_GOLD_DESCS[Random.Int(COLLECT_GOLD_DESCS.size)]
                quest.questDescription = desc.replace("{count}", quest.targetCount.toString())
                quest.completionText = COLLECT_GOLD_COMPLETIONS[Random.Int(COLLECT_GOLD_COMPLETIONS.size)]
            }
            AiQuest.Type.SURVIVE_TURNS -> {
                quest.targetCount = 30 + depth * 2
                val desc = SURVIVE_TURNS_DESCS[Random.Int(SURVIVE_TURNS_DESCS.size)]
                quest.questDescription = desc.replace("{count}", quest.targetCount.toString())
                quest.completionText = SURVIVE_TURNS_COMPLETIONS[Random.Int(SURVIVE_TURNS_COMPLETIONS.size)]
            }
            AiQuest.Type.FIND_ITEM -> {
                quest.targetCount = 1
                val targets = itemTargetsForDepth(depth)
                val target = targets[Random.Int(targets.size)]
                quest.targetMobClass = target.className
                val desc = FIND_ITEM_DESCS[Random.Int(FIND_ITEM_DESCS.size)]
                quest.questDescription = desc.replace("{item}", target.displayName)
                quest.completionText = FIND_ITEM_COMPLETIONS[Random.Int(FIND_ITEM_COMPLETIONS.size)]
            }
            AiQuest.Type.EXPLORE_ROOMS -> {
                quest.targetCount = 5 + depth / 4
                val desc = EXPLORE_ROOMS_DESCS[Random.Int(EXPLORE_ROOMS_DESCS.size)]
                quest.questDescription = desc.replace("{count}", quest.targetCount.toString())
                quest.completionText = EXPLORE_ROOMS_COMPLETIONS[Random.Int(EXPLORE_ROOMS_COMPLETIONS.size)]
            }
            AiQuest.Type.KILL_TYPE -> {
                quest.targetCount = 2 + depth / 5
                val targets = mobTargetsForDepth(depth)
                val target = targets[Random.Int(targets.size)]
                quest.targetMobClass = target.className
                val desc = KILL_TYPE_DESCS[Random.Int(KILL_TYPE_DESCS.size)]
                quest.questDescription = desc
                    .replace("{count}", quest.targetCount.toString())
                    .replace("{mob}", target.displayName)
                quest.completionText = KILL_TYPE_COMPLETIONS[Random.Int(KILL_TYPE_COMPLETIONS.size)]
                    .replace("{mob}", target.displayName)
            }
            AiQuest.Type.COLLECT_SEEDS -> {
                quest.targetCount = 2 + depth / 5
                val desc = COLLECT_SEEDS_DESCS[Random.Int(COLLECT_SEEDS_DESCS.size)]
                quest.questDescription = desc.replace("{count}", quest.targetCount.toString())
                quest.completionText = COLLECT_SEEDS_COMPLETIONS[Random.Int(COLLECT_SEEDS_COMPLETIONS.size)]
            }
            AiQuest.Type.DISARM_TRAPS -> {
                quest.targetCount = 2 + depth / 6
                val desc = DISARM_TRAPS_DESCS[Random.Int(DISARM_TRAPS_DESCS.size)]
                quest.questDescription = desc.replace("{count}", quest.targetCount.toString())
                quest.completionText = DISARM_TRAPS_COMPLETIONS[Random.Int(DISARM_TRAPS_COMPLETIONS.size)]
            }
        }

        // Tiered rewards — gold scales steeper at later depths
        quest.goldReward = when {
            depth <= 5 -> 20 + depth * 10 + Random.Int(depth * 5 + 1)
            depth <= 10 -> 30 + depth * 15 + Random.Int(depth * 5 + 1)
            depth <= 15 -> 40 + depth * 20 + Random.Int(depth * 8 + 1)
            depth <= 20 -> 50 + depth * 25 + Random.Int(depth * 10 + 1)
            else -> 80 + depth * 30 + Random.Int(depth * 12 + 1)
        }
        quest.rewardItemClass = rewardItemForDepth(depth)

        return quest
    }

    fun npcNameForVariant(variant: Int): String = NPC_NAMES[variant % NPC_NAMES.size]
    fun npcPersonalityForVariant(variant: Int): String = NPC_PERSONALITIES[variant % NPC_PERSONALITIES.size]
}
