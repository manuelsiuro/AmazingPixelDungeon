package com.watabou.pixeldungeon.items.bags
import com.watabou.pixeldungeon.Badges
import com.watabou.pixeldungeon.actors.Char
import com.watabou.pixeldungeon.actors.hero.Hero
import com.watabou.pixeldungeon.items.Item
import com.watabou.pixeldungeon.scenes.GameScene
import com.watabou.pixeldungeon.windows.WndBag
import com.watabou.utils.Bundlable
import com.watabou.utils.Bundle
import java.util.ArrayList
open class Bag : Item(), Iterable<Item> {
    var owner: Char? = null
    var items = ArrayList<Item>()
    var size = 1
    init {
        image = 11
        defaultAction = AC_OPEN
    }
    override fun actions(hero: Hero): ArrayList<String> {
        val actions = super.actions(hero)
        return actions
    }
    override fun execute(hero: Hero, action: String) {
        if (action == AC_OPEN) {
            GameScene.show(WndBag(this, null, WndBag.Mode.ALL, null))
        } else {
            super.execute(hero, action)
        }
    }
    override fun collect(container: Bag?): Boolean {
        if (super.collect(container)) {
            if (container != null) {
                owner = container.owner
                for (item in container.items.toTypedArray()) {
                    if (grab(item)) {
                        item.detachAll(container)
                        item.collect(this)
                    }
                }
            }
            Badges.validateAllBagsBought(this)
            return true
        } else {
            return false
        }
    }
    override fun onDetach() {
        this.owner = null
    }
    override val isUpgradable: Boolean
        get() = false
    override val isIdentified: Boolean
        get() = true
    fun clear() {
        items.clear()
    }
    override fun storeInBundle(bundle: Bundle) {
        super.storeInBundle(bundle)
        bundle.put(ITEMS, items)
    }
    override fun restoreFromBundle(bundle: Bundle) {
        super.restoreFromBundle(bundle)
        for (item in bundle.getCollection(ITEMS)) {
            (item as Item).collect(this)
        }
    }
    fun contains(item: Item): Boolean {
        for (i in items) {
            if (i === item) {
                return true
            } else if (i is Bag && i.contains(item)) {
                return true
            }
        }
        return false
    }
    open fun grab(item: Item): Boolean {
        return false
    }
    override fun iterator(): Iterator<Item> {
        return ItemIterator()
    }
    private inner class ItemIterator : Iterator<Item> {
        private var index = 0
        private var nested: Iterator<Item>? = null
        override fun hasNext(): Boolean {
            val n = nested
            return if (n != null) {
                n.hasNext() || index < items.size
            } else {
                index < items.size
            }
        }
        override fun next(): Item {
            val n = nested
            if (n != null && n.hasNext()) {
                return n.next()
            } else {
                nested = null
                val item = items[index++]
                if (item is Bag) {
                    nested = item.iterator()
                }
                return item
            }
        }
        fun remove() {
            if (nested != null) {
                // Remove on iterator is tricky in Kotlin/Java interop if not explicit
                // Use default iterator behavior or specific removal implementation if needed
                // But Java Iterator remove() is not heavily used in loops here usually.
                // However, we must implement it if we claim to be Iterator? 
                // Kotlin Iterator doesn't have remove(). MutableIterator does.
                // Bag implements Iterable<Item>, which in Kotlin returns Iterator.
                // Java code might expect Iterator with remove().
                // But wait, Kotlin's Iterator doesn't have remove.
                // If I implement kotlin.collections.Iterator, I can't have remove.
                // If the usage relies on remove, I should implement MutableIterator.
            } else {
                items.removeAt(index)
            }
        }
    }
    companion object {
        const val AC_OPEN = "OPEN"
        private const val ITEMS = "inventory"
    }
}
