package com.watabou.pixeldungeon.actors.mobs.npcs

import com.watabou.pixeldungeon.Dungeon
import com.watabou.pixeldungeon.actors.buffs.Buff
import com.watabou.pixeldungeon.items.crafting.DimensionalChestItem
import com.watabou.pixeldungeon.sprites.DimensionalChestSprite
import com.watabou.pixeldungeon.utils.GLog
import com.watabou.pixeldungeon.windows.WndStorageChest
import com.watabou.pixeldungeon.scenes.GameScene
import com.watabou.utils.Bundle

class DimensionalChestNpc : NPC() {

    init {
        name = "dimensional chest"
        spriteClass = DimensionalChestSprite::class.java
        state = PASSIVE
    }

    override fun act(): Boolean {
        spend(TICK)
        return true
    }

    override fun interact() {
        GameScene.show(WndStorageChest(this, DimensionalStorage.items, DimensionalStorage.MAX_ITEMS))
    }

    override fun damage(dmg: Int, src: Any?) {
        val level = Dungeon.level ?: return

        // Contents are safe in global storage — just drop the chest item
        level.drop(DimensionalChestItem(), pos).sprite?.drop(pos)

        GLog.w("The dimensional chest shatters! Its contents remain safe in the void.")
        destroy()
        sprite?.killAndErase()
    }

    override fun add(buff: Buff) {
        // Immune to all debuffs
    }

    override fun reset(): Boolean = true

    override fun description(): String =
        "A mysterious chest linked to a pocket dimension. Items stored here can be accessed from any dimensional chest."

    override fun defenseSkill(enemy: com.watabou.pixeldungeon.actors.Char?): Int = 1000

    override fun storeInBundle(bundle: Bundle) {
        super.storeInBundle(bundle)
    }

    override fun restoreFromBundle(bundle: Bundle) {
        super.restoreFromBundle(bundle)
    }
}
