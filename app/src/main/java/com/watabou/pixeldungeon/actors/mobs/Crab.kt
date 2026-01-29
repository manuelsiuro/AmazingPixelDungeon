package com.watabou.pixeldungeon.actors.mobs
import com.watabou.pixeldungeon.actors.Char
import com.watabou.pixeldungeon.actors.mobs.npcs.Ghost
import com.watabou.pixeldungeon.items.food.MysteryMeat
import com.watabou.pixeldungeon.sprites.CrabSprite
import com.watabou.utils.Random
class Crab : Mob() {
    init {
        name = "sewer crab"
        spriteClass = CrabSprite::class.java
        HT = 15
        HP = HT
        defenseSkill = 5
        baseSpeed = 2f
        EXP = 3
        maxLvl = 9
        loot = MysteryMeat()
        lootChance = 0.167f
    }
    override fun damageRoll(): Int {
        return Random.NormalIntRange(3, 6)
    }
    override fun attackSkill(target: Char?): Int {
        return 12
    }
    override fun dr(): Int {
        return 4
    }
    override fun defenseVerb(): String {
        return "parried"
    }
    override fun die(src: Any?) {
        Ghost.Quest.processSewersKill(pos)
        super.die(src)
    }
    override fun description(): String {
        return "These huge crabs are at the top of the food chain in the sewers. They are extremely fast and their thick exoskeleton can withstand heavy blows."
    }
}
