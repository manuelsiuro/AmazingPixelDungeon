package com.watabou.pixeldungeon.items.food.farming

import com.watabou.pixeldungeon.actors.buffs.Buffs
import com.watabou.pixeldungeon.actors.buffs.Poison
import com.watabou.pixeldungeon.actors.hero.Hero
import com.watabou.pixeldungeon.items.food.Food
import com.watabou.pixeldungeon.sprites.ItemSpriteSheet

class BakedPotato : Food() {
    init {
        name = "baked potato"
        image = ItemSpriteSheet.BAKED_POTATO
        energy = 250f
        message = "The baked potato is hearty and delicious."
    }

    override fun execute(hero: Hero, action: String) {
        super.execute(hero, action)
        if (action == AC_EAT) {
            Buffs.detach(hero, Poison::class.java)
        }
    }

    override fun info(): String =
        "A potato baked in a furnace. Safe to eat and cures poison."

    override fun desc(): String = info()

    override fun price(): Int = 12 * quantity
}
