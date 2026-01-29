package com.watabou.pixeldungeon
object Challenges {
    const val NO_FOOD = 1
    const val NO_ARMOR = 2
    const val NO_HEALING = 4
    const val NO_HERBALISM = 8
    const val SWARM_INTELLIGENCE = 16
    const val DARKNESS = 32
    const val NO_SCROLLS = 64
    val NAMES = arrayOf(
        "On diet",
        "Faith is my armor",
        "Pharmacophobia",
        "Barren land",
        "Swarm intelligence",
        "Into darkness",
        "Forbidden runes"
    )
    val MASKS = intArrayOf(
        NO_FOOD, NO_ARMOR, NO_HEALING, NO_HERBALISM, SWARM_INTELLIGENCE, DARKNESS, NO_SCROLLS
    )
}
