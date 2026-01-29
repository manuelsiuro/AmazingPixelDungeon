package com.watabou.pixeldungeon
import com.watabou.utils.Bundle
object Statistics {
    var goldCollected: Int = 0
    var deepestFloor: Int = 0
    var enemiesSlain: Int = 0
    var foodEaten: Int = 0
    var potionsCooked: Int = 0
    var piranhasKilled: Int = 0
    var nightHunt: Int = 0
    var ankhsUsed: Int = 0
    var duration: Float = 0f
    var qualifiedForNoKilling: Boolean = false
    var completedWithNoKilling: Boolean = false
    var amuletObtained: Boolean = false
    fun reset() {
        goldCollected = 0
        deepestFloor = 0
        enemiesSlain = 0
        foodEaten = 0
        potionsCooked = 0
        piranhasKilled = 0
        nightHunt = 0
        ankhsUsed = 0
        duration = 0f
        qualifiedForNoKilling = false
        amuletObtained = false
    }
    private const val GOLD = "score"
    private const val DEEPEST = "maxDepth"
    private const val SLAIN = "enemiesSlain"
    private const val FOOD = "foodEaten"
    private const val ALCHEMY = "potionsCooked"
    private const val PIRANHAS = "priranhas"
    private const val NIGHT = "nightHunt"
    private const val ANKHS = "ankhsUsed"
    private const val DURATION = "duration"
    private const val AMULET = "amuletObtained"
    fun storeInBundle(bundle: Bundle) {
        bundle.put(GOLD, goldCollected)
        bundle.put(DEEPEST, deepestFloor)
        bundle.put(SLAIN, enemiesSlain)
        bundle.put(FOOD, foodEaten)
        bundle.put(ALCHEMY, potionsCooked)
        bundle.put(PIRANHAS, piranhasKilled)
        bundle.put(NIGHT, nightHunt)
        bundle.put(ANKHS, ankhsUsed)
        bundle.put(DURATION, duration)
        bundle.put(AMULET, amuletObtained)
    }
    fun restoreFromBundle(bundle: Bundle) {
        goldCollected = bundle.getInt(GOLD)
        deepestFloor = bundle.getInt(DEEPEST)
        enemiesSlain = bundle.getInt(SLAIN)
        foodEaten = bundle.getInt(FOOD)
        potionsCooked = bundle.getInt(ALCHEMY)
        piranhasKilled = bundle.getInt(PIRANHAS)
        nightHunt = bundle.getInt(NIGHT)
        ankhsUsed = bundle.getInt(ANKHS)
        duration = bundle.getFloat(DURATION)
        amuletObtained = bundle.getBoolean(AMULET)
    }
}
