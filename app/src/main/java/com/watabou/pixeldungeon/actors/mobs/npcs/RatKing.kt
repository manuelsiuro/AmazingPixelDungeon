package com.watabou.pixeldungeon.actors.mobs.npcs
import com.watabou.pixeldungeon.Dungeon
import com.watabou.pixeldungeon.actors.Char
import com.watabou.pixeldungeon.actors.buffs.Buff
import com.watabou.pixeldungeon.sprites.RatKingSprite
class RatKing : NPC() {
    init {
        name = "rat king"
        spriteClass = RatKingSprite::class.java
        state = SLEEPING
    }
    override fun defenseSkill(enemy: Char?): Int {
        return 1000
    }
    override fun speed(): Float {
        return 2f
    }
    override fun chooseEnemy(): Char? {
        return null
    }
    override fun damage(dmg: Int, src: Any?) {
    }
    override fun add(buff: Buff) {
    }
    override fun reset(): Boolean {
        return true
    }
    override fun interact() {
        Dungeon.hero?.let { sprite?.turnTo(pos, it.pos) }
        if (state == SLEEPING) {
            notice()
            yell("I'm not sleeping!")
            state = WANDERING
        } else {
            yell("What is it? I have no time for this nonsense. My kingdom won't rule itself!")
        }
    }
    override fun description(): String {
        return "This rat is a little bigger than a regular marsupial rat and it's wearing a tiny crown on its head."
    }
}
