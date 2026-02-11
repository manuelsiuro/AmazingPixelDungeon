package com.watabou.pixeldungeon.llm

object LlmPromptBuilder {

    private val BOSS_PERSONALITIES = mapOf(
        "Goo" to "Mindless ooze. Gurgles and drips. Sounds, not words.",
        "Tengu" to "Master assassin. Cunning, theatrical, dark humor.",
        "DM-300" to "Ancient war machine. Mechanical, cold directives.",
        "King of Dwarves" to "Undead king. Regal, contemptuous, ancient authority.",
        "Yog-Dzewa" to "Imprisoned old god. Cosmic, alien, otherworldly whispers."
    )

    // --- NPC Dialog (with variety system) ---

    fun npcDialog(
        personality: NpcPersonality,
        npcName: String,
        heroClass: String,
        depth: Int,
        originalText: String,
        encounterCount: Int,
        situational: String,
        classFlavor: String,
        toneSuffix: String
    ): String {
        val regionTone = DialogContext.regionTone(depth)
        val encounter = DialogContext.encounterContext(encounterCount)

        // Build context lines — only include non-empty ones
        val contextParts = listOfNotNull(
            regionTone,
            encounter.ifEmpty { null },
            situational.ifEmpty { null },
            classFlavor.ifEmpty { null }
        ).joinToString(" ")

        val toneDirective = if (toneSuffix.isNotEmpty()) " $toneSuffix" else ""

        return """${personality.toPromptLine(npcName)}
Speaking to a $heroClass on floor $depth. $contextParts
Rewrite in your voice, keep quest details. Under 3 sentences. Use _underscores_ for emphasis, never *.$toneDirective
Original: "$originalText"
Rewritten:"""
    }

    // --- Floor Narration ---

    fun floorNarration(
        regionName: String,
        depth: Int,
        heroClass: String,
        originalText: String,
        seed: Int
    ): String {
        val tone = DialogContext.regionTone(depth)
        val style = DialogContext.narrationStyle(seed)
        val atmo = DialogContext.atmosphereWords(seed, depth)
        val urgency = DialogContext.urgencyModifier(depth)
        val urgencyLine = if (urgency.isNotEmpty()) " $urgency" else ""

        return """Write atmospheric narration for a $heroClass entering the $atmo depths of $regionName (floor $depth).
$tone.$urgencyLine $style
2-3 sentences, dark fantasy. Use _underscores_ for emphasis, never *.
Lore: "$originalText"
Narration:"""
    }

    // --- Item Description ---

    fun itemDescription(
        itemName: String,
        itemType: String,
        level: Int,
        enchantment: String?,
        cursed: Boolean,
        originalDesc: String,
        seed: Int
    ): String {
        val attrs = buildString {
            if (level > 0) append(" +$level")
            if (enchantment != null) append(" [$enchantment]")
            if (cursed) append(" [cursed]")
        }
        val cursedLine = if (cursed) " A dark aura clings to it." else ""
        val toneSuffix = DialogContext.toneSuffix(seed)
        val toneDirective = if (toneSuffix.isNotEmpty()) " $toneSuffix" else ""

        return """Write a brief atmospheric description for: $itemName$attrs ($itemType).$cursedLine
1-2 sentences, dark fantasy. Use _underscores_ for emphasis, never *.$toneDirective
Base: "$originalDesc"
Enhanced:"""
    }

    // --- Combat Narration ---

    fun combatNarration(originalMessage: String, seed: Int): String {
        val style = DialogContext.combatStyle(seed)
        return """Rewrite this combat message in dark fantasy style. $style One sentence max.
Use _underscores_ for emphasis, never *.
Original: "$originalMessage"
Rewritten:"""
    }

    // --- Death Epitaph ---

    fun deathEpitaph(causeDesc: String, heroClass: String, depth: Int, heroLevel: Int): String {
        return """Level-$heroLevel $heroClass died on floor $depth. Cause: "$causeDesc".
Dark fantasy epitaph, 1-2 sentences, solemn. Use _underscores_ for emphasis, never *.
Only output the epitaph text:"""
    }

    // --- Story Moments ---

    fun introNarration(heroClass: String, originalText: String, seed: Int): String {
        val style = DialogContext.narrationStyle(seed)
        return """Rewrite this game intro for a $heroClass entering a dangerous dungeon.
Keep core meaning: heroes tried before, none found the Amulet of Yendor, you feel ready.
2-3 sentences, dark fantasy. $style Use _underscores_ for emphasis, never *.
Original: "$originalText"
Rewritten:"""
    }

    fun victoryNarration(heroClass: String, originalText: String, seed: Int): String {
        val style = DialogContext.narrationStyle(seed)
        return """Rewrite this victory text for a $heroClass who found the Amulet of Yendor.
Keep core meaning: you hold the amulet, its power can change everything.
2-3 sentences, triumphant dark fantasy. $style Use _underscores_ for emphasis, never *.
Original: "$originalText"
Rewritten:"""
    }

    // --- Boss Encounters ---

    fun bossNotice(bossName: String, heroClass: String, depth: Int, fallback: String, seed: Int): String {
        val personality = BOSS_PERSONALITIES[bossName] ?: "A powerful dungeon boss."
        val toneSuffix = DialogContext.toneSuffix(seed)
        val toneDirective = if (toneSuffix.isNotEmpty()) " $toneSuffix" else ""
        return """You are $bossName, boss on floor $depth. $personality
A $heroClass enters your domain. Battle cry or threat (1 sentence). Match personality.
Use _underscores_ for emphasis, never *.$toneDirective
Original: "$fallback"
Rewritten:"""
    }

    fun bossDeath(bossName: String, heroClass: String, fallback: String, seed: Int): String {
        val personality = BOSS_PERSONALITIES[bossName] ?: "A powerful dungeon boss."
        val toneSuffix = DialogContext.toneSuffix(seed)
        val toneDirective = if (toneSuffix.isNotEmpty()) " $toneSuffix" else ""
        return """You are $bossName, dying boss. $personality
Defeated by a $heroClass. Dying words (1 sentence). Match personality.
Use _underscores_ for emphasis, never *.$toneDirective
Original: "$fallback"
Rewritten:"""
    }

    fun bossSummon(bossName: String, fallback: String, seed: Int): String {
        val personality = BOSS_PERSONALITIES[bossName] ?: "A powerful dungeon boss."
        val toneSuffix = DialogContext.toneSuffix(seed)
        val toneDirective = if (toneSuffix.isNotEmpty()) " $toneSuffix" else ""
        return """You are $bossName. $personality
Summoning undead minions. Summoning command (1 sentence).
Use _underscores_ for emphasis, never *.$toneDirective
Original: "$fallback"
Rewritten:"""
    }

    // --- Atmosphere ---

    fun levelFeeling(
        feelingType: String,
        regionName: String,
        depth: Int,
        heroClass: String,
        fallback: String,
        seed: Int
    ): String {
        val tone = DialogContext.regionTone(depth)
        val atmo = DialogContext.atmosphereWords(seed, depth)
        return """Write an atmospheric one-sentence observation for a $heroClass on floor $depth of $regionName.
Feeling: "$feelingType". $tone. The air is $atmo. Dark fantasy, evocative, brief.
Use _underscores_ for emphasis, never *.
Original: "$fallback"
Rewritten:"""
    }

    fun signTip(depth: Int, heroClass: String, fallbackTip: String, seed: Int): String {
        val tone = DialogContext.regionTone(depth)
        val toneSuffix = DialogContext.toneSuffix(seed)
        val toneDirective = if (toneSuffix.isNotEmpty()) " $toneSuffix" else ""
        return """Rewrite as advice scratched into a weathered dungeon sign. $tone.
Keep gameplay advice intact, add dark fantasy flavor. One sentence.
Use _underscores_ for emphasis, never *.$toneDirective
Original: "$fallbackTip"
Rewritten:"""
    }

    fun mobDescription(
        mobName: String,
        mobState: String,
        depth: Int,
        fallbackDesc: String,
        seed: Int
    ): String {
        val tone = DialogContext.regionTone(depth)
        val toneSuffix = DialogContext.toneSuffix(seed)
        val toneDirective = if (toneSuffix.isNotEmpty()) " $toneSuffix" else ""
        return """Write a bestiary entry for: $mobName (currently $mobState, floor $depth).
$tone. 1-2 sentences, dark fantasy. Use _underscores_ for emphasis, never *.$toneDirective
Base: "$fallbackDesc"
Enhanced:"""
    }

    // --- Celebrations & Interactions ---

    fun badgeText(badgeName: String, heroClass: String, fallbackDesc: String, seed: Int): String {
        val toneSuffix = DialogContext.toneSuffix(seed)
        val toneDirective = if (toneSuffix.isNotEmpty()) " $toneSuffix" else ""
        return """Brief celebratory achievement text for a $heroClass who earned: "$badgeName".
One sentence, triumphant. Use _underscores_ for emphasis, never *.$toneDirective
Original: "$fallbackDesc"
Rewritten:"""
    }

    fun resurrectionText(heroClass: String, fallback: String, seed: Int): String {
        val toneSuffix = DialogContext.toneSuffix(seed)
        val toneDirective = if (toneSuffix.isNotEmpty()) " $toneSuffix" else ""
        return """Rewrite this resurrection message for a $heroClass brought back by an Ankh.
Keep the choice element (accept or refuse). 1-2 sentences, dramatic dark fantasy.
Use _underscores_ for emphasis, never *.$toneDirective
Original: "$fallback"
Rewritten:"""
    }

    fun shopkeeperGreeting(
        shopkeeperName: String,
        heroClass: String,
        depth: Int,
        fallback: String,
        seed: Int
    ): String {
        val personality = if (shopkeeperName == "ambitious imp")
            "Sly, fast-talking imp merchant who loves deals."
        else
            "Stout, pragmatic shopkeeper in a dangerous dungeon."
        val tone = DialogContext.regionTone(depth)
        val toneSuffix = DialogContext.toneSuffix(seed)
        val toneDirective = if (toneSuffix.isNotEmpty()) " $toneSuffix" else ""
        return """You are $shopkeeperName. $personality $tone.
A $heroClass enters your shop. Brief greeting (1 sentence).
Use _underscores_ for emphasis, never *.$toneDirective
Original: "$fallback"
Rewritten:"""
    }

    // --- Content Polish ---

    fun plantDescription(plantName: String, fallbackDesc: String, seed: Int): String {
        val toneSuffix = DialogContext.toneSuffix(seed)
        val toneDirective = if (toneSuffix.isNotEmpty()) " $toneSuffix" else ""
        return """Atmospheric description for dungeon plant: $plantName. 1-2 sentences, dark fantasy herbalism.
Use _underscores_ for emphasis, never *.$toneDirective
Base: "$fallbackDesc"
Enhanced:"""
    }

    fun buffDescription(buffName: String, fallbackDesc: String, seed: Int): String {
        val toneSuffix = DialogContext.toneSuffix(seed)
        val toneDirective = if (toneSuffix.isNotEmpty()) " $toneSuffix" else ""
        return """Brief atmospheric description for magical effect: $buffName. One sentence, dark fantasy.
Use _underscores_ for emphasis, never *.$toneDirective
Base: "$fallbackDesc"
Enhanced:"""
    }

    fun cellDescription(tileName: String, fallbackDesc: String, seed: Int): String {
        val toneSuffix = DialogContext.toneSuffix(seed)
        val toneDirective = if (toneSuffix.isNotEmpty()) " $toneSuffix" else ""
        return """Atmospheric description for dungeon terrain: $tileName. One sentence, dark fantasy.
Use _underscores_ for emphasis, never *.$toneDirective
Base: "$fallbackDesc"
Enhanced:"""
    }

    fun aiQuestDescription(
        npcName: String,
        personality: String,
        questType: String,
        targetDesc: String,
        heroClass: String,
        depth: Int,
        fallback: String,
        seed: Int
    ): String {
        val tone = DialogContext.regionTone(depth)
        val toneSuffix = DialogContext.toneSuffix(seed)
        val toneDirective = if (toneSuffix.isNotEmpty()) " $toneSuffix" else ""
        return """You are $npcName, a $personality, on floor $depth. $tone.
Offering a quest to a $heroClass. Quest: $questType.
Rewrite in your voice. Keep quest details. Under 4 sentences. Use _underscores_ for emphasis, never *.$toneDirective
Original: "$fallback"
Rewritten:"""
    }
}
