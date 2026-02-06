package com.watabou.pixeldungeon.llm

object LlmPromptBuilder {

    private val NPC_PERSONALITIES = mapOf(
        "sad ghost" to "You are melancholy and ethereal. You speak with pauses (ellipses). You are sorrowful and fading.",
        "troll blacksmith" to "You are a gruff troll who speaks broken common tongue. You are impatient and blunt but fair.",
        "old wandmaker" to "You are a wise elderly wizard, slightly confused but formal. You are polite and scholarly.",
        "ambitious imp" to "You are a sly, deal-making imp. You speak quickly and persuasively. You love bargains."
    )

    fun npcDialog(npcName: String, questState: String, heroClass: String, depth: Int, originalText: String): String {
        val personality = NPC_PERSONALITIES[npcName] ?: "You are a mysterious dungeon dweller."
        return """You are $npcName in a dark fantasy dungeon. $personality
You are speaking to a $heroClass on dungeon floor $depth.
Rewrite this dialog in your voice, keep quest instructions intact.
Keep _highlighted_ words in underscores. Under 3 sentences.
Original: "$originalText"
Rewritten:"""
    }

    fun floorNarration(regionName: String, depth: Int, heroClass: String, originalText: String): String {
        return """Write atmospheric narration for a $heroClass entering $regionName (floor $depth).
Base on this lore. 2-3 sentences, dark fantasy tone.
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
Base description: "$originalDesc"
Enhanced:"""
    }

    fun combatNarration(originalMessage: String): String {
        return """Rewrite this combat message in vivid dark fantasy style. One sentence max.
Original: "$originalMessage"
Rewritten:"""
    }
}
