package com.watabou.pixeldungeon.actors.mobs
import com.watabou.pixeldungeon.actors.Char
import com.watabou.pixeldungeon.actors.mobs.npcs.Ghost
import com.watabou.pixeldungeon.items.Generator
import com.watabou.pixeldungeon.sprites.RatSprite
import com.watabou.utils.Random
open class Rat : Mob() {
    init {
        name = "marsupial rat"
        spriteClass = RatSprite::class.java
        HT = 8
        HP = HT
        defenseSkill = 3
        maxLvl = 5

        loot = Generator.Category.FOOD
        lootChance = 0.1f
    }
    override fun damageRoll(): Int {
        return Random.NormalIntRange(1, 5)
    }
    override fun attackSkill(target: Char?): Int {
        return 8
    }
    override fun dr(): Int {
        return 1
    }
    override fun die(src: Any?) {
        Ghost.Quest.processSewersKill(pos)
        super.die(src)
    }
    override fun description(): String {
        return "Marsupial rats are aggressive, but rather weak denizens of the sewers. They can be dangerous only in big numbers."
    }
}
