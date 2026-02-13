package com.watabou.pixeldungeon.items.food.farming

import com.watabou.pixeldungeon.actors.hero.Hero
import com.watabou.pixeldungeon.items.food.Food
import com.watabou.pixeldungeon.sprites.ItemSpriteSheet
import kotlin.math.min

class MelonSlice : Food() {
    init {
        name = "melon slice"
        image = ItemSpriteSheet.MELON_SLICE
        energy = 80f
        message = "The melon is refreshing and juicy!"
    }

    override fun execute(hero: Hero, action: String) {
        super.execute(hero, action)
        if (action == AC_EAT) {
            hero.HP = min(hero.HP + 3, hero.HT)
        }
    }

    override fun info(): String =
        "A juicy slice of melon. Restores a small amount of health when eaten."

    override fun desc(): String = info()

    override fun price(): Int = 8 * quantity
}
