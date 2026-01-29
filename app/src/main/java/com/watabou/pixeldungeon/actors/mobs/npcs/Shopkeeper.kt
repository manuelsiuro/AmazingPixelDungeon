package com.watabou.pixeldungeon.actors.mobs.npcs
import com.watabou.pixeldungeon.Dungeon
import com.watabou.pixeldungeon.actors.buffs.Buff
import com.watabou.pixeldungeon.effects.CellEmitter
import com.watabou.pixeldungeon.effects.particles.ElmoParticle
import com.watabou.pixeldungeon.items.Heap
import com.watabou.pixeldungeon.items.Item
import com.watabou.pixeldungeon.scenes.GameScene
import com.watabou.pixeldungeon.sprites.ShopkeeperSprite
import com.watabou.pixeldungeon.windows.WndBag
import com.watabou.pixeldungeon.windows.WndTradeItem
open class Shopkeeper : NPC() {
    init {
        name = "shopkeeper"
        spriteClass = ShopkeeperSprite::class.java
    }
    override fun act(): Boolean {
        throwItem()
        Dungeon.hero?.let { sprite?.turnTo(pos, it.pos) }
        spend(TICK)
        return true
    }
    override fun damage(dmg: Int, src: Any?) {
        flee()
    }
    override fun add(buff: Buff) {
        flee()
    }
    protected open fun flee() {
        Dungeon.level?.heaps?.values()?.forEach { heap ->
            if (heap.type == Heap.Type.FOR_SALE) {
                CellEmitter.get(heap.pos).burst(ElmoParticle.FACTORY, 4)
                heap.destroy()
            }
        }
        destroy()
        sprite?.killAndErase()
        CellEmitter.get(pos).burst(ElmoParticle.FACTORY, 6)
    }
    override fun reset(): Boolean {
        return true
    }
    override fun description(): String {
        return "This stout guy looks more appropriate for a trade district in some large city than for a dungeon. His prices explain why he prefers to do business here."
    }
    override fun interact() {
        sell()
    }
    companion object {
        fun sell(): WndBag {
            return GameScene.selectItem(itemSelector, WndBag.Mode.FOR_SALE, "Select an item to sell")
        }
        private val itemSelector = object : WndBag.Listener {
            override fun onSelect(item: Item?) {
                if (item != null) {
                    val parentWnd = sell()
                    GameScene.show(WndTradeItem(item, parentWnd))
                }
            }
        }
    }
}
