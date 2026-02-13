package com.watabou.pixeldungeon.items.crafting

import com.watabou.pixeldungeon.actors.hero.Hero
import com.watabou.pixeldungeon.sprites.ItemSpriteSheet
import com.watabou.pixeldungeon.items.Item
import com.watabou.pixeldungeon.utils.GLog
import com.watabou.utils.Random
import java.util.ArrayList

class Bandage : Item() {
    init {
        name = "bandage"
        image = ItemSpriteSheet.BANDAGE
        stackable = true
        defaultAction = AC_USE
    }

    override fun actions(hero: Hero): ArrayList<String> {
        val actions = super.actions(hero)
        actions.add(AC_USE)
        return actions
    }

    override fun execute(hero: Hero, action: String) {
        if (action == AC_USE) {
            val heal = Random.IntRange(10, 15)
            hero.HP = Math.min(hero.HP + heal, hero.HT)
            hero.spend(TIME_TO_USE)
            hero.busy()
            hero.sprite?.operate(hero.pos)
            detach(hero.belongings.backpack)
            GLog.p("You apply the bandage and recover %d health.", heal)
        } else {
            super.execute(hero, action)
        }
    }

    override val isUpgradable: Boolean
        get() = false

    override val isIdentified: Boolean
        get() = true

    override fun price(): Int = 15 * quantity

    override fun info(): String =
        "A strip of woven plant fiber that can be used to bind wounds. Not as effective as a healing potion, but easy to craft."

    override fun desc(): String = info()

    companion object {
        const val AC_USE = "USE"
        const val TIME_TO_USE = 2f
    }
}
