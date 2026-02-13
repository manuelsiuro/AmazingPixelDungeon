package com.watabou.pixeldungeon.items.food.farming

import com.watabou.pixeldungeon.actors.buffs.Buffs
import com.watabou.pixeldungeon.actors.buffs.Poison
import com.watabou.pixeldungeon.actors.hero.Hero
import com.watabou.pixeldungeon.items.food.Food
import com.watabou.pixeldungeon.sprites.ItemSpriteSheet
import com.watabou.pixeldungeon.utils.GLog
import com.watabou.utils.Random

class Potato : Food() {
    init {
        name = "potato"
        image = ItemSpriteSheet.POTATO
        energy = 60f
        message = "The raw potato is starchy and bland."
    }

    override fun execute(hero: Hero, action: String) {
        super.execute(hero, action)
        if (action == AC_EAT && Random.Float() < 0.15f) {
            GLog.w("The raw potato makes you feel sick!")
            Buffs.affect(hero, Poison::class.java)?.set(3f)
        }
    }

    override fun info(): String =
        "A raw potato. Edible but slightly toxic - cooking removes the poison risk."

    override fun desc(): String = info()

    override fun price(): Int = 5 * quantity
}
