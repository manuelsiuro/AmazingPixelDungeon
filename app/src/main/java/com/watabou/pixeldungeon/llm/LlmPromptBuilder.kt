package com.watabou.pixeldungeon.llm

object LlmPromptBuilder {

    private val NPC_PERSONALITIES = mapOf(
        "sad ghost" to "You are melancholy and ethereal. You speak with pauses (ellipses). You are sorrowful and fading.",
        "troll blacksmith" to "You are a gruff troll who speaks broken common tongue. You are impatient and blunt but fair.",
        "old wandmaker" to "You are a wise elderly wizard, slightly confused but formal. You are polite and scholarly.",
        "ambitious imp" to "You are a sly, deal-making imp. You speak quickly and persuasively. You love bargains."
    )

    private val BOSS_PERSONALITIES = mapOf(
        "Goo" to "Mindless ooze. Gurgles and drips. Sounds, not words.",
        "Tengu" to "Master assassin. Cunning, theatrical, dark humor.",
        "DM-300" to "Ancient war machine. Mechanical, cold directives.",
        "King of Dwarves" to "Undead king. Regal, contemptuous, ancient authority.",
        "Yog-Dzewa" to "Imprisoned old god. Cosmic, alien, otherworldly whispers."
    )

    fun npcDialog(npcName: String, questState: String, heroClass: String, depth: Int, originalText: String): String {
        val personality = NPC_PERSONALITIES[npcName] ?: "You are a mysterious dungeon dweller."
        return """You are $npcName in a dark fantasy dungeon. $personality
You are speaking to a $heroClass on dungeon floor $depth.
Rewrite this dialog in your voice, keep quest instructions intact.
Keep _highlighted_ words in underscores. Under 3 sentences.
And use underscore in your response and never change it by star *.
Original: "$originalText"
Rewritten:"""
    }

    fun floorNarration(regionName: String, depth: Int, heroClass: String, originalText: String): String {
        return """Write atmospheric narration for a $heroClass entering $regionName (floor $depth).
Base on this lore. 2-3 sentences, dark fantasy tone.
Keep _highlighted_ words in underscores. 
And use underscore in your response and never change it by star *.
Lore: "$originalText"
Narration:"""
    }

    fun itemDescription(itemName: String, itemType: String, level: Int, enchantment: String?, cursed: Boolean, originalDesc: String): String {
        val attrs = buildString {
            if (level > 0) append(" +$level")
            if (enchantment != null) append(" [$enchantment]")
            if (cursed) append(" [cursed]")
        }
        return """Write a brief atmospheric description for: $itemName$attrs ($itemType).
1-2 sentences, dark fantasy dungeon crawler.
Keep _highlighted_ words in underscores. 
And use underscore in your response and never change it by star *.
Base description: "$originalDesc"
Enhanced:"""
    }

    fun combatNarration(originalMessage: String): String {
        return """Rewrite this combat message in vivid dark fantasy style. One sentence max.
Keep _highlighted_ words in underscores. 
And use underscore in your response and never change it by star *.
Original: "$originalMessage"
Rewritten:"""
    }

    // Phase 1: Story Moments

    fun deathEpitaph(causeDesc: String, heroClass: String, depth: Int, heroLevel: Int): String {
        return """Write a 1-2 sentence dramatic epitaph for a level $heroLevel $heroClass who died on dungeon floor $depth. Cause of death: "$causeDesc". 
Dark fantasy eulogy tone, solemn and poetic.
Keep _highlighted_ words in underscores. 
And use underscore in your response and never change it by star *.
Epitaph:"""
    }

    fun introNarration(heroClass: String, originalText: String): String {
        return """Rewrite this game intro for a $heroClass entering a dangerous dungeon.
Keep the core meaning: heroes have tried before, none found the Amulet of Yendor, and you feel ready. 
2-3 sentences, dark fantasy tone.
Keep _highlighted_ words in underscores. 
And use underscore in your response and never change it by star *.
Original: "$originalText"
Rewritten:"""
    }

    fun victoryNarration(heroClass: String, originalText: String): String {
        return """Rewrite this victory text for a $heroClass who found the Amulet of Yendor.
Keep the core meaning: you hold the amulet, its power can change everything, your life changes forever. 
2-3 sentences, triumphant dark fantasy tone.
Keep _highlighted_ words in underscores. 
And use underscore in your response and never change it by star *.
Original: "$originalText"
Rewritten:"""
    }

    // Phase 2: Boss Encounters

    fun bossNotice(bossName: String, heroClass: String, depth: Int, fallback: String): String {
        val personality = BOSS_PERSONALITIES[bossName] ?: "A powerful dungeon boss."
        return """You are $bossName, a dungeon boss on floor $depth. Personality: $personality
A $heroClass has entered your domain. Write a short battle cry or notice (1 sentence max). 
Match the personality closely.
Keep _highlighted_ words in underscores. 
And use underscore in your response and never change it by star *.
Original line: "$fallback"
Rewritten:"""
    }

    fun bossDeath(bossName: String, heroClass: String, fallback: String): String {
        val personality = BOSS_PERSONALITIES[bossName] ?: "A powerful dungeon boss."
        return """You are $bossName, a dying dungeon boss. Personality: $personality
You have been defeated by a $heroClass. Write dying words (1 sentence max).
Match the personality closely.
Keep _highlighted_ words in underscores. 
And use underscore in your response and never change it by star *.
Original line: "$fallback"
Rewritten:"""
    }

    fun bossSummon(bossName: String, fallback: String): String {
        val personality = BOSS_PERSONALITIES[bossName] ?: "A powerful dungeon boss."
        return """You are $bossName. Personality: $personality
You are summoning undead minions to fight. 
Write a summoning command (1 sentence max).
Keep _highlighted_ words in underscores. 
And use underscore in your response and never change it by star *.
Original line: "$fallback"
Rewritten:"""
    }

    // Phase 3: Enhanced Atmosphere

    fun levelFeeling(feelingType: String, regionName: String, depth: Int, heroClass: String, fallback: String): String {
        return """Write an atmospheric one-sentence observation for a $heroClass on floor $depth of $regionName. 
The feeling type is "$feelingType". Dark fantasy tone, evocative but brief.
Keep _highlighted_ words in underscores. 
And use underscore in your response and never change it by star *.
Original: "$fallback"
Rewritten:"""
    }

    fun signTip(depth: Int, heroClass: String, fallbackTip: String): String {
        return """Rewrite this dungeon sign tip as advice scratched into a weathered sign.
Keep the gameplay advice intact but add dark fantasy flavor. One sentence.
Keep _highlighted_ words in underscores. 
And use underscore in your response and never change it by star *.
Original tip: "$fallbackTip"
Rewritten:"""
    }

    fun mobDescription(mobName: String, mobState: String, depth: Int, fallbackDesc: String): String {
        return """Write an atmospheric bestiary entry for: $mobName (currently $mobState, floor $depth). 
1-2 sentences, dark fantasy dungeon crawler tone.
Keep _highlighted_ words in underscores. 
And use underscore in your response and never change it by star *.
Base description: "$fallbackDesc"
Enhanced:"""
    }

    // Phase 4: Celebrations & Interactions

    fun badgeText(badgeName: String, heroClass: String, fallbackDesc: String): String {
        return """Write a brief celebratory achievement text for a $heroClass who earned: "$badgeName". 
One sentence, triumphant tone.
Keep _highlighted_ words in underscores. 
And use underscore in your response and never change it by star *.
Original: "$fallbackDesc"
Rewritten:"""
    }

    fun resurrectionText(heroClass: String, fallback: String): String {
        return """Rewrite this resurrection message for a $heroClass brought back from death by an Ankh. 
Keep the choice element (accept or refuse). 1-2 sentences, dramatic dark fantasy tone.
Keep _highlighted_ words in underscores. 
And use underscore in your response and never change it by star *.
Original: "$fallback"
Rewritten:"""
    }

    fun shopkeeperGreeting(shopkeeperName: String, heroClass: String, depth: Int, fallback: String): String {
        val personality = if (shopkeeperName == "ambitious imp")
            "A sly, fast-talking imp merchant who loves a good deal."
        else
            "A stout, pragmatic shopkeeper doing business in a dangerous dungeon."
        return """You are a $shopkeeperName. $personality
A $heroClass enters your shop on floor $depth. Write a brief greeting (1 sentence).
Keep _highlighted_ words in underscores. 
And use underscore in your response and never change it by star *.
Original: "$fallback"
Rewritten:"""
    }

    // Phase 5: Content Polish

    fun plantDescription(plantName: String, fallbackDesc: String): String {
        return """Write an atmospheric description for a dungeon plant: $plantName. 1-2 sentences, dark fantasy herbalism tone.
Keep _highlighted_ words in underscores. 
And use underscore in your response and never change it by star *.
Base description: "$fallbackDesc"
Enhanced:"""
    }

    fun buffDescription(buffName: String, fallbackDesc: String): String {
        return """Write a brief atmospheric description for a magical effect: $buffName. One sentence, dark fantasy tone.
Keep _highlighted_ words in underscores. 
And use underscore in your response and never change it by star *.
Base description: "$fallbackDesc"
Enhanced:"""
    }

    fun cellDescription(tileName: String, fallbackDesc: String): String {
        return """Write an atmospheric description for a dungeon terrain: $tileName. One sentence, dark fantasy tone.
Keep _highlighted_ words in underscores. 
And use underscore in your response and never change it by star *.
Base description: "$fallbackDesc"
Enhanced:"""
    }

    fun aiQuestDescription(
        npcName: String,
        personality: String,
        questType: String,
        targetDesc: String,
        heroClass: String,
        depth: Int,
        fallback: String
    ): String {
        return """You are $npcName, a $personality, in a dark fantasy dungeon on floor $depth.
You are offering a quest to a $heroClass. Quest type: $questType.
Rewrite this quest offer dialog in your voice. Keep quest instructions intact. Keep _highlighted_ words in underscores. Under 4 sentences.
Original: "$fallback"
Rewritten:"""
    }
}
