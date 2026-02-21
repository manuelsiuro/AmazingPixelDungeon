package com.watabou.pixeldungeon.actors.mobs.npcs

import com.watabou.pixeldungeon.Dungeon
import com.watabou.pixeldungeon.actors.buffs.Buff
import com.watabou.pixeldungeon.items.crafting.ResourceCacheItem
import com.watabou.pixeldungeon.sprites.ResourceCacheSprite
import com.watabou.pixeldungeon.utils.GLog
import com.watabou.pixeldungeon.windows.WndResourceCache
import com.watabou.pixeldungeon.scenes.GameScene
import com.watabou.utils.Bundle

class ResourceCacheNpc : NPC() {

    init {
        name = "resource cache"
        spriteClass = ResourceCacheSprite::class.java
        state = PASSIVE
    }

    override fun act(): Boolean {
        spend(TICK)
        return true
    }

    override fun interact() {
        val level = Dungeon.level ?: return
        GameScene.show(WndResourceCache(this, level.resourceCacheInventory, MAX_ITEMS))
    }

    override fun damage(dmg: Int, src: Any?) {
        val level = Dungeon.level ?: return

        // Scatter all stored items
        for (item in level.resourceCacheInventory) {
            level.drop(item, pos).sprite?.drop(pos)
        }
        level.resourceCacheInventory.clear()

        // Drop the cache item itself
        level.drop(ResourceCacheItem(), pos).sprite?.drop(pos)

        GLog.w("The resource cache breaks apart!")
        destroy()
        sprite?.killAndErase()
    }

    override fun add(buff: Buff) {
        // Immune to all debuffs
    }

    override fun reset(): Boolean = true

    override fun description(): String =
        "A communal resource cache. All caches on this floor share the same inventory."

    override fun defenseSkill(enemy: com.watabou.pixeldungeon.actors.Char?): Int = 1000

    override fun storeInBundle(bundle: Bundle) {
        super.storeInBundle(bundle)
    }

    override fun restoreFromBundle(bundle: Bundle) {
        super.restoreFromBundle(bundle)
    }

    companion object {
        const val MAX_ITEMS = 20
    }
}
