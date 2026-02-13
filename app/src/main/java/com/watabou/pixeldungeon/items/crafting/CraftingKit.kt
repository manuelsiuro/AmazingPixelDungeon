package com.watabou.pixeldungeon.items.crafting

import com.watabou.pixeldungeon.actors.hero.Hero
import com.watabou.pixeldungeon.crafting.StationType
import com.watabou.pixeldungeon.items.Item
import com.watabou.pixeldungeon.scenes.GameScene
import com.watabou.pixeldungeon.sprites.ItemSpriteSheet
import com.watabou.pixeldungeon.windows.WndCrafting

class CraftingKit : Item() {
    init {
        name = "crafting kit"
        image = ItemSpriteSheet.CRAFTING_KIT
        defaultAction = AC_CRAFT
        unique = true
    }

    override fun actions(hero: Hero): ArrayList<String> {
        val actions = super.actions(hero)
        actions.add(AC_CRAFT)
        return actions
    }

    override fun execute(hero: Hero, action: String) {
        if (action == AC_CRAFT) {
            curUser = hero
            GameScene.show(WndCrafting(hero, StationType.NONE))
        } else {
            super.execute(hero, action)
        }
    }

    override val isUpgradable: Boolean
        get() = false

    override val isIdentified: Boolean
        get() = true

    override fun price(): Int = 200

    override fun info(): String =
        "A portable toolkit with basic crafting tools. Allows you to craft simple items on the go."

    override fun desc(): String = info()

    companion object {
        const val AC_CRAFT = "CRAFT"
    }
}
