package com.watabou.pixeldungeon.actors.mobs.npcs
import com.watabou.pixeldungeon.Dungeon
import com.watabou.pixeldungeon.effects.CellEmitter
import com.watabou.pixeldungeon.effects.Speck
import com.watabou.pixeldungeon.effects.particles.ElmoParticle
import com.watabou.pixeldungeon.items.Heap
import com.watabou.pixeldungeon.sprites.ImpSprite
import com.watabou.pixeldungeon.utils.Utils
class ImpShopkeeper : Shopkeeper() {
    init {
        name = "ambitious imp"
        spriteClass = ImpSprite::class.java
    }
    private var seenBefore = false
    override fun act(): Boolean {
        if (!seenBefore && Dungeon.visible[pos]) {
            yell(Utils.format(TXT_GREETINGS))
            seenBefore = true
        }
        return super.act()
    }
    override fun flee() {
        Dungeon.level?.heaps?.values()?.forEach { heap ->
            if (heap.type == Heap.Type.FOR_SALE) {
                CellEmitter.get(heap.pos).burst(ElmoParticle.FACTORY, 4)
                heap.destroy()
            }
        }
        destroy()
        sprite?.emitter()?.burst(Speck.factory(Speck.WOOL), 15)
        sprite?.killAndErase()
    }
    override fun description(): String {
        return "Imps are lesser demons. They are notable for neither their strength nor their magic talent. But they are quite smart and sociable, and many of imps prefer to live and do business among non-demons."
    }
    companion object {
        private const val TXT_GREETINGS = "Hello, friend!"
    }
}
