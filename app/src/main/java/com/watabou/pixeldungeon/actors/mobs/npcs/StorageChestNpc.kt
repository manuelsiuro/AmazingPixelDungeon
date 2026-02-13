package com.watabou.pixeldungeon.actors.mobs.npcs

import com.watabou.pixeldungeon.Dungeon
import com.watabou.pixeldungeon.actors.buffs.Buff
import com.watabou.pixeldungeon.items.Item
import com.watabou.pixeldungeon.items.crafting.StorageChestItem
import com.watabou.pixeldungeon.sprites.StorageChestSprite
import com.watabou.pixeldungeon.utils.GLog
import com.watabou.pixeldungeon.windows.WndStorageChest
import com.watabou.pixeldungeon.scenes.GameScene
import com.watabou.utils.Bundle
import java.util.ArrayList

class StorageChestNpc : NPC() {

    val items = ArrayList<Item>()

    init {
        name = "storage chest"
        spriteClass = StorageChestSprite::class.java
        state = PASSIVE
    }

    override fun act(): Boolean {
        spend(TICK)
        return true
    }

    override fun interact() {
        GameScene.show(WndStorageChest(this, items, MAX_ITEMS))
    }

    override fun damage(dmg: Int, src: Any?) {
        val level = Dungeon.level ?: return

        // Scatter all stored items
        for (item in items) {
            level.drop(item, pos).sprite?.drop(pos)
        }
        items.clear()

        // Drop the chest item itself
        level.drop(StorageChestItem(), pos).sprite?.drop(pos)

        GLog.w("The storage chest breaks apart!")
        destroy()
        sprite?.killAndErase()
    }

    override fun add(buff: Buff) {
        // Immune to all debuffs
    }

    override fun reset(): Boolean = true

    override fun description(): String =
        "A sturdy wooden chest for storing your belongings. It will persist on this level."

    override fun defenseSkill(enemy: com.watabou.pixeldungeon.actors.Char?): Int = 1000

    override fun storeInBundle(bundle: Bundle) {
        super.storeInBundle(bundle)
        bundle.put(ITEMS_KEY, items)
    }

    override fun restoreFromBundle(bundle: Bundle) {
        super.restoreFromBundle(bundle)
        items.clear()
        for (item in bundle.getCollection(ITEMS_KEY)) {
            items.add(item as Item)
        }
    }

    companion object {
        const val MAX_ITEMS = 16
        private const val ITEMS_KEY = "chestItems"
    }
}
