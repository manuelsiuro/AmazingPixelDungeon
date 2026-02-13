package com.watabou.pixeldungeon.items.food.farming

import com.watabou.pixeldungeon.actors.buffs.Buffs
import com.watabou.pixeldungeon.actors.buffs.WellFed
import com.watabou.pixeldungeon.actors.hero.Hero
import com.watabou.pixeldungeon.items.food.Food
import com.watabou.pixeldungeon.sprites.ItemSpriteSheet

class RabbitStew : Food() {
    init {
        name = "rabbit stew"
        image = ItemSpriteSheet.RABBIT_STEW
        energy = 360f
        stackable = false
        message = "The hearty stew warms you from within!"
    }

    override fun execute(hero: Hero, action: String) {
        super.execute(hero, action)
        if (action == AC_EAT) {
            Buffs.affect(hero, WellFed::class.java)?.set(100f)
        }
    }

    override fun info(): String =
        "A rich stew made with meat, carrot, baked potato and served in a wooden bowl. Grants regeneration."

    override fun desc(): String = info()

    override fun price(): Int = 30 * quantity
}
