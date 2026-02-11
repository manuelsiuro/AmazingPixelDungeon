package com.watabou.pixeldungeon.actors.mobs
import com.watabou.pixeldungeon.Badges
import com.watabou.pixeldungeon.items.Generator
import com.watabou.pixeldungeon.sprites.ShieldedSprite
class Shielded : Brute() {
    init {
        name = "shielded brute"
        spriteClass = ShieldedSprite::class.java
        defenseSkill = 20

        loot = Generator.Category.ARMOR
        lootChance = 0.15f
    }
    override fun dr(): Int {
        return 10
    }
    override fun defenseVerb(): String {
        return "blocked"
    }
    override fun die(src: Any?) {
        super.die(src)
        Badges.validateRare(this)
    }
}
