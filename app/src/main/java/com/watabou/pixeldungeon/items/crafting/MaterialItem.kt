package com.watabou.pixeldungeon.items.crafting

import com.watabou.pixeldungeon.items.Item

open class MaterialItem : Item() {
    init {
        stackable = true
    }

    override val isUpgradable: Boolean
        get() = false

    override val isIdentified: Boolean
        get() = true
}
