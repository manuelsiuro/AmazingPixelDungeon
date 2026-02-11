package com.watabou.pixeldungeon.llm

import com.watabou.pixeldungeon.Dungeon
import com.watabou.pixeldungeon.Statistics

/**
 * Computes dynamic context for LLM prompts: situational lines, region tone,
 * encounter tracking, variation seeds, and class flavor. All functions are
 * pure data lookups or simple arithmetic — no LLM calls.
 */
object DialogContext {

    // --- Encounter Tracking (in-memory, resets on app restart) ---

    private val encounterCounts = mutableMapOf<String, Int>()

    /** Records an encounter and returns the updated count. */
    fun recordEncounter(npcName: String): Int {
        val count = (encounterCounts[npcName] ?: 0) + 1
        encounterCounts[npcName] = count
        return count
    }

    /** Resets all encounter counts (call on new game). */
    fun resetEncounters() {
        encounterCounts.clear()
    }

    /** Returns a prompt line that simulates NPC memory of past visits. */
    fun encounterContext(count: Int): String = when (count) {
        1 -> ""
        2 -> "You've met this hero before. Acknowledge the return briefly."
        3 -> "This hero keeps returning. Show familiarity."
        else -> "You know this hero well. Speak as to an old acquaintance."
    }

    // --- Situational Line (one per prompt, most dramatic wins) ---

    /**
     * Returns a single short situational line based on game state.
     * Priority-ordered: near-death > wounded > experienced > rich > veteran > deep > fresh.
     */
    fun situationalLine(hpPercent: Int, depth: Int, gold: Int, heroLevel: Int): String {
        return when {
            hpPercent < 20 -> "The hero is gravely wounded, near death."
            hpPercent < 50 -> "The hero looks battered and tired."
            heroLevel >= 20 -> "The hero radiates power and experience."
            gold > 500 -> "The hero's purse is heavy with gold."
            Statistics.enemiesSlain > 100 -> "The hero is a seasoned killer."
            depth > 20 -> "Few adventurers survive this deep."
            depth == 1 -> "The hero looks fresh and untested."
            else -> ""
        }
    }

    // --- Region Tone ---

    /** Returns atmospheric tone words that replace raw region names. 5-7 tokens. */
    fun regionTone(depth: Int): String = when {
        depth == 0 -> "Tone: hopeful, safe"
        depth <= 5 -> "Tone: uneasy, damp, echoing"
        depth <= 10 -> "Tone: grim, oppressive, cold"
        depth <= 15 -> "Tone: primal, echoing, vast"
        depth <= 20 -> "Tone: ancient, crumbling, grand"
        else -> "Tone: dread, apocalyptic, final"
    }

    /** Returns urgency modifier for atmosphere text (not NPC dialog). */
    fun urgencyModifier(depth: Int): String = when {
        depth <= 5 -> ""
        depth <= 10 -> "The dungeon grows more dangerous."
        depth <= 15 -> "Darkness presses in."
        depth <= 20 -> "Ancient evil stirs."
        else -> "The end is near."
    }

    // --- Class Flavor ---

    /** Returns a special line when a specific NPC+class combo has narrative affinity. */
    fun classFlavorLine(heroClass: String, npcName: String): String = when {
        heroClass == "Warrior" && npcName == "troll blacksmith" ->
            "You respect fellow craftsmen of war."
        heroClass == "Mage" && npcName == "old wandmaker" ->
            "You sense a kindred magical spirit."
        heroClass == "Rogue" && npcName == "ambitious imp" ->
            "You recognize a fellow dealer in shadows."
        heroClass == "Huntress" && npcName == "sad ghost" ->
            "The ghost seems drawn to your connection with nature."
        else -> ""
    }

    // --- Variation Seed ---

    /**
     * Computes a variation seed (0-3) from run-unique game state.
     * Different runs produce different seeds, giving 4 cache slots per interaction.
     */
    fun variationSeed(): Int {
        val runHash = (Statistics.duration.toInt() * 31 +
                Statistics.enemiesSlain * 17 +
                Dungeon.gold * 7 +
                Dungeon.depth * 3)
        return (runHash and 0x7FFFFFFF) % 4
    }

    /** Tone suffix variant keyed by seed — nudges model toward different phrasings. */
    private val TONE_VARIANTS = arrayOf(
        "",
        "Be slightly more terse.",
        "Add a hint of dark humor.",
        "Be slightly more poetic."
    )

    fun toneSuffix(seed: Int): String = TONE_VARIANTS[seed.coerceIn(0, 3)]

    // --- Style Rotation for Non-NPC Text ---

    private val NARRATION_STYLES = arrayOf(
        "Use sensory details (smell, sound, touch).",
        "Focus on what the hero feels emotionally.",
        "Use terse, punchy prose.",
        "Describe through metaphor."
    )

    private val COMBAT_STYLES = arrayOf(
        "Visceral and sharp.",
        "Poetic and flowing.",
        "Terse and brutal.",
        "Dark and ominous."
    )

    private val ATMOSPHERE_WORDS = arrayOf(
        "eerie", "foreboding", "ancient", "crumbling", "festering",
        "silent", "echoing", "dim", "musty", "damp", "frigid",
        "oppressive", "haunted", "forgotten", "cursed"
    )

    /** Selects a narration style based on seed. */
    fun narrationStyle(seed: Int): String = NARRATION_STYLES[seed.coerceIn(0, 3)]

    /** Selects a combat style based on seed. */
    fun combatStyle(seed: Int): String = COMBAT_STYLES[seed.coerceIn(0, 3)]

    /** Returns 2 atmosphere words selected by seed for injecting into prompts. */
    fun atmosphereWords(seed: Int, depth: Int): String {
        val idx1 = ((seed * 7 + depth * 3) and 0x7FFFFFFF) % ATMOSPHERE_WORDS.size
        val idx2 = ((seed * 13 + depth * 5 + 1) and 0x7FFFFFFF) % ATMOSPHERE_WORDS.size
        val w1 = ATMOSPHERE_WORDS[idx1]
        val w2 = ATMOSPHERE_WORDS[if (idx2 == idx1) (idx2 + 1) % ATMOSPHERE_WORDS.size else idx2]
        return "$w1, $w2"
    }

    // --- Helper: Compute HP percent safely ---

    fun heroHpPercent(): Int {
        val hero = Dungeon.hero ?: return 100
        return if (hero.HT > 0) (hero.HP * 100) / hero.HT else 100
    }
}
