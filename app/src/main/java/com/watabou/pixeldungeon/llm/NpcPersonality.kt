package com.watabou.pixeldungeon.llm

/**
 * Multi-axis NPC personality system. Each axis is a short phrase (2-6 tokens)
 * selected based on NPC identity and current game state. Combined into a single
 * compact personality line in the prompt.
 */
data class NpcPersonality(
    val voice: String,
    val mood: String,
    val address: String,
    val quirk: String
) {
    /** Assembles personality axes into a single prompt-ready line. */
    fun toPromptLine(npcName: String): String =
        "You are $npcName. You $voice. Mood: $mood. Call the hero \"$address\". $quirk."

    companion object {

        /**
         * Resolves the personality axes for a given NPC based on game state.
         * Pure function — no LLM calls, just data lookups and simple conditionals.
         */
        fun resolve(
            npcName: String,
            heroClass: String,
            hpPercent: Int,
            gold: Int,
            heroLevel: Int
        ): NpcPersonality {
            return when (npcName) {
                "sad ghost" -> resolveGhost(heroClass, hpPercent)
                "troll blacksmith" -> resolveBlacksmith(heroClass, hpPercent)
                "old wandmaker" -> resolveWandmaker(heroClass, heroLevel)
                "ambitious imp" -> resolveImp(heroClass, gold)
                else -> NpcPersonality(
                    voice = "speaks cautiously",
                    mood = "wary",
                    address = addressForClass(heroClass, DEFAULT_ADDRESSES),
                    quirk = "glances around nervously"
                )
            }
        }

        // --- Ghost (Sewers) ---

        private fun resolveGhost(heroClass: String, hpPercent: Int): NpcPersonality {
            val addresses = mapOf(
                "Warrior" to "brave one",
                "Mage" to "wise one",
                "Rogue" to "shadow walker",
                "Huntress" to "keen one"
            )
            return when {
                hpPercent < 30 -> NpcPersonality(
                    voice = "moans painfully",
                    mood = "pitying",
                    address = "poor wounded thing",
                    quirk = "sighs between words"
                )
                hpPercent < 60 -> NpcPersonality(
                    voice = "whispers brokenly",
                    mood = "worried",
                    address = addressForClass(heroClass, addresses),
                    quirk = "trails off with '...'"
                )
                else -> NpcPersonality(
                    voice = "whispers brokenly",
                    mood = "sorrowful",
                    address = addressForClass(heroClass, addresses),
                    quirk = "trails off with '...'"
                )
            }
        }

        // --- Blacksmith (Caves) ---

        private fun resolveBlacksmith(heroClass: String, hpPercent: Int): NpcPersonality {
            val addresses = mapOf(
                "Warrior" to "fellow fighter",
                "Mage" to "spellslinger",
                "Rogue" to "sneakthief",
                "Huntress" to "sharp-eye"
            )
            val (voice, mood) = when (heroClass) {
                "Warrior" -> "grunts approvingly" to "respectful"
                "Mage" -> "snorts dismissively" to "suspicious"
                else -> "barks gruffly" to "impatient"
            }
            return if (hpPercent < 30) {
                NpcPersonality(
                    voice = "grumbles",
                    mood = "concerned",
                    address = "battered fool",
                    quirk = "shakes head"
                )
            } else {
                NpcPersonality(
                    voice = voice,
                    mood = mood,
                    address = addressForClass(heroClass, addresses),
                    quirk = "bangs hammer for emphasis"
                )
            }
        }

        // --- Wandmaker (Prison) ---

        private fun resolveWandmaker(heroClass: String, heroLevel: Int): NpcPersonality {
            val addresses = mapOf(
                "Warrior" to "strong one",
                "Mage" to "kindred spirit",
                "Rogue" to "clever one",
                "Huntress" to "nature's child"
            )
            return when {
                heroLevel >= 15 -> NpcPersonality(
                    voice = "whispers urgently",
                    mood = "worried",
                    address = "brave one",
                    quirk = "grips staff tightly"
                )
                heroLevel >= 8 -> NpcPersonality(
                    voice = "speaks slowly, precisely",
                    mood = "scholarly",
                    address = addressForClass(heroClass, addresses),
                    quirk = "strokes beard thoughtfully"
                )
                else -> NpcPersonality(
                    voice = "lectures gently",
                    mood = "patient",
                    address = "my pupil",
                    quirk = "chuckles wisely"
                )
            }
        }

        // --- Imp (Metropolis) ---

        private fun resolveImp(heroClass: String, gold: Int): NpcPersonality {
            val addresses = mapOf(
                "Warrior" to "muscle",
                "Mage" to "magic-user",
                "Rogue" to "fellow shadow",
                "Huntress" to "hunter"
            )
            return when {
                gold > 500 -> NpcPersonality(
                    voice = "purrs smoothly",
                    mood = "delighted",
                    address = "esteemed customer",
                    quirk = "counts coins in head"
                )
                gold < 50 -> NpcPersonality(
                    voice = "wheedles desperately",
                    mood = "eager",
                    address = "pal, buddy",
                    quirk = "glances at your purse"
                )
                else -> NpcPersonality(
                    voice = "chatters quickly",
                    mood = "scheming",
                    address = addressForClass(heroClass, addresses),
                    quirk = "rubs hands together"
                )
            }
        }

        // --- Helpers ---

        private val DEFAULT_ADDRESSES = mapOf(
            "Warrior" to "brave warrior",
            "Mage" to "learned scholar",
            "Rogue" to "silent blade",
            "Huntress" to "keen-eyed hunter"
        )

        private fun addressForClass(heroClass: String, table: Map<String, String>): String =
            table[heroClass] ?: "stranger"
    }
}
