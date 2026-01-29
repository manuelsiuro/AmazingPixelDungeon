package com.watabou.pixeldungeon.actors.mobs
import com.watabou.pixeldungeon.actors.Char
import com.watabou.pixeldungeon.actors.mobs.npcs.Ghost
import com.watabou.pixeldungeon.items.Gold
import com.watabou.pixeldungeon.sprites.GnollSprite
import com.watabou.utils.Random
class Gnoll : Mob() {
    init {
        name = "gnoll scout"
        spriteClass = GnollSprite::class.java
        HT = 12
        HP = HT
        defenseSkill = 4
        EXP = 2
        maxLvl = 8
        loot = Gold::class.java
        lootChance = 0.5f
    }
    override fun damageRoll(): Int {
        return Random.NormalIntRange(2, 5)
    }
    override fun attackSkill(target: Char?): Int {
        return 11
    }
    override fun dr(): Int {
        return 2
    }
    override fun die(src: Any?) {
        Ghost.Quest.processSewersKill(pos)
        super.die(src)
    }
    override fun description(): String {
        return "Gnolls are hyena-like humanoids. They dwell in sewers and dungeons, venturing up to raid the surface from time to time. Gnoll scouts are regular members of their pack, they are not as strong as brutes and not as intelligent as shamans."
    }
}
