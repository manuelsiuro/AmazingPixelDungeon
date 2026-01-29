package com.watabou.pixeldungeon.actors.hero
import com.watabou.pixeldungeon.Badges
import com.watabou.pixeldungeon.Dungeon
import com.watabou.pixeldungeon.items.Item
import com.watabou.pixeldungeon.items.KindOfWeapon
import com.watabou.pixeldungeon.items.armor.Armor
import com.watabou.pixeldungeon.items.bags.Bag
import com.watabou.pixeldungeon.items.keys.IronKey
import com.watabou.pixeldungeon.items.keys.Key
import com.watabou.pixeldungeon.items.rings.Ring
import com.watabou.pixeldungeon.items.scrolls.ScrollOfRemoveCurse
import com.watabou.pixeldungeon.items.wands.Wand
import com.watabou.utils.Bundle
import com.watabou.utils.Random
class Belongings(private val owner: Hero) : Iterable<Item> {
    var backpack: Bag
    var weapon: KindOfWeapon? = null
    var armor: Armor? = null
    var ring1: Ring? = null
    var ring2: Ring? = null
    init {
        backpack = object : Bag() {
            init {
                name = "backpack"
                size = BACKPACK_SIZE
            }
        }
        backpack.owner = owner
    }
    fun storeInBundle(bundle: Bundle) {
        backpack.storeInBundle(bundle)
        bundle.put(WEAPON, weapon)
        bundle.put(ARMOR, armor)
        bundle.put(RING1, ring1)
        bundle.put(RING2, ring2)
    }
    fun restoreFromBundle(bundle: Bundle) {
        backpack.clear()
        backpack.restoreFromBundle(bundle)
        weapon = bundle[WEAPON] as KindOfWeapon?
        weapon?.activate(owner)
        armor = bundle[ARMOR] as Armor?
        ring1 = bundle[RING1] as Ring?
        ring1?.activate(owner)
        ring2 = bundle[RING2] as Ring?
        ring2?.activate(owner)
    }
    fun <T : Item> getItem(itemClass: Class<T>): T? {
        for (item in this) {
            if (itemClass.isInstance(item)) {
                @Suppress("UNCHECKED_CAST")
                return item as T
            }
        }
        return null
    }
    fun <T : Key> getKey(kind: Class<T>, depth: Int): T? {
        for (item in backpack) {
            if (item.javaClass == kind && (item as Key).depth == depth) {
                @Suppress("UNCHECKED_CAST")
                return item as T
            }
        }
        return null
    }
    fun countIronKeys() {
        IronKey.curDepthQuantity = 0
        for (item in backpack) {
            if (item is IronKey && item.depth == Dungeon.depth) {
                IronKey.curDepthQuantity++
            }
        }
    }
    fun identify() {
        for (item in this) {
            item.identify()
        }
    }
    fun observe() {
        weapon?.let {
            it.identify()
            Badges.validateItemLevelAquired(it)
        }
        armor?.let {
            it.identify()
            Badges.validateItemLevelAquired(it)
        }
        ring1?.let {
            it.identify()
            Badges.validateItemLevelAquired(it)
        }
        ring2?.let {
            it.identify()
            Badges.validateItemLevelAquired(it)
        }
        for (item in backpack) {
            item.cursedKnown = true
        }
    }
    fun uncurseEquipped() {
        ScrollOfRemoveCurse.uncurse(owner, armor, weapon, ring1, ring2)
    }
    fun randomUnequipped(): Item? {
        return Random.element(backpack.items)
    }
    fun resurrect(depth: Int) {
        for (item in backpack.items.toTypedArray()) {
            if (item is Key) {
                if (item.depth == depth) {
                    item.detachAll(backpack)
                }
            } else if (item.unique) {
                // Keep unique items
            } else if (!item.isEquipped(owner)) {
                item.detachAll(backpack)
            }
        }
        weapon?.let {
            it.cursed = false
            it.activate(owner)
        }
        armor?.let {
            it.cursed = false
        }
        ring1?.let {
            it.cursed = false
            it.activate(owner)
        }
        ring2?.let {
            it.cursed = false
            it.activate(owner)
        }
    }
    fun charge(full: Boolean): Int {
        var count = 0
        for (item in this) {
            if (item is Wand) {
                if (item.curCharges < item.maxCharges) {
                    item.curCharges = if (full) item.maxCharges else item.curCharges + 1
                    count++
                    item.updateQuickslot()
                }
            }
        }
        return count
    }
    fun discharge(): Int {
        var count = 0
        for (item in this) {
            if (item is Wand) {
                if (item.curCharges > 0) {
                    item.curCharges--
                    count++
                    item.updateQuickslot()
                }
            }
        }
        return count
    }
    override fun iterator(): Iterator<Item> {
        return ItemIterator()
    }
    private inner class ItemIterator : Iterator<Item> {
        private var index = 0
        private val backpackIterator = backpack.iterator()
        private val equipped: Array<Item?>
            get() = arrayOf(weapon, armor, ring1, ring2)
        private val backpackIndex = 4
        override fun hasNext(): Boolean {
            val eq = equipped
            for (i in index until backpackIndex) {
                if (eq[i] != null) {
                    return true
                }
            }
            return backpackIterator.hasNext()
        }
        override fun next(): Item {
            val eq = equipped
            while (index < backpackIndex) {
                val item = eq[index++]
                if (item != null) {
                    return item
                }
            }
            return backpackIterator.next()
        }
        // remove() is not part of Kotlin's Iterator. 
        // If it's needed, we might need MutableIterator or custom logic.
        // However, standard Iterator.remove in Java is rarely used this way in Pixel Dungeon.
        // Let's check if it's used.
    }
    companion object {
        const val BACKPACK_SIZE = 19
        private const val WEAPON = "weapon"
        private const val ARMOR = "armor"
        private const val RING1 = "ring1"
        private const val RING2 = "ring2"
    }
}
