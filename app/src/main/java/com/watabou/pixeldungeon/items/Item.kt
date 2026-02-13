package com.watabou.pixeldungeon.items
import com.watabou.noosa.audio.Sample
import com.watabou.pixeldungeon.Assets
import com.watabou.pixeldungeon.Badges
import com.watabou.pixeldungeon.Dungeon
import com.watabou.pixeldungeon.actors.Actor
import com.watabou.pixeldungeon.actors.Char
import com.watabou.pixeldungeon.actors.buffs.SnipersMark
import com.watabou.pixeldungeon.actors.hero.Hero
import com.watabou.pixeldungeon.effects.Degradation
import com.watabou.pixeldungeon.effects.Speck
import com.watabou.pixeldungeon.items.armor.Armor
import com.watabou.pixeldungeon.items.bags.Bag
import com.watabou.pixeldungeon.llm.LlmTextEnhancer
import com.watabou.pixeldungeon.items.rings.Ring
import com.watabou.pixeldungeon.items.wands.Wand
import com.watabou.pixeldungeon.items.weapon.Weapon
import com.watabou.pixeldungeon.items.weapon.missiles.MissileWeapon
import com.watabou.pixeldungeon.mechanics.Ballistica
import com.watabou.pixeldungeon.scenes.CellSelector
import com.watabou.pixeldungeon.scenes.GameScene
import com.watabou.pixeldungeon.sprites.CharSprite
import com.watabou.pixeldungeon.sprites.ItemSprite
import com.watabou.pixeldungeon.sprites.MissileSprite
import com.watabou.pixeldungeon.ui.QuickSlot
import com.watabou.pixeldungeon.utils.GLog
import com.watabou.pixeldungeon.utils.Utils
import com.watabou.utils.Bundlable
import com.watabou.utils.Bundle
import com.watabou.utils.Callback
import java.util.Collections
open class Item : Bundlable {
    var defaultAction: String? = null
    // Open properties so they can be overridden or accessed
    var name: String = "smth"
    var image: Int = 0
    var stackable: Boolean = false
    var quantity: Int = 1
    private var level: Int = 0
    private var durability: Int = maxDurability()
    var levelKnown: Boolean = false
    var cursed: Boolean = false
    var cursedKnown: Boolean = false
    var unique: Boolean = false
    // To match Java protected visibility for subclasses
    // In Kotlin, valid public accessors are generated.
    open fun actions(hero: Hero): ArrayList<String> {
        val actions = ArrayList<String>()
        actions.add(AC_DROP)
        actions.add(AC_THROW)
        return actions
    }
    open fun doPickUp(hero: Hero): Boolean {
        if (collect(hero.belongings.backpack)) {
            GameScene.pickUp(this)
            Sample.play(Assets.SND_ITEM)
            hero.spendAndNext(TIME_TO_PICK_UP)
            return true
        } else {
            return false
        }
    }
    open fun doDrop(hero: Hero) {
        hero.spendAndNext(TIME_TO_DROP)
        val level = Dungeon.level ?: return
        level.drop(detachAll(hero.belongings.backpack), hero.pos).sprite?.drop(hero.pos)
    }
    open fun doThrow(hero: Hero) {
        GameScene.selectCell(thrower)
    }
    open fun execute(hero: Hero, action: String) {
        curUser = hero
        curItem = this
        if (action == AC_DROP) {
            doDrop(hero)
        } else if (action == AC_THROW) {
            doThrow(hero)
        }
    }
    open fun execute(hero: Hero) {
        defaultAction?.let { execute(hero, it) }
    }
    protected open fun onThrow(cell: Int) {
        val level = Dungeon.level ?: return
        val heap = level.drop(this, cell)
        if (!heap.isEmpty) {
            heap.sprite?.drop(cell)
        }
    }
    open fun collect(container: Bag?): Boolean {
        if (container == null) return false
        val items = container.items
        if (items.contains(this)) {
            return true
        }
        for (item in items) {
            if (item is Bag && item.grab(this)) {
                return collect(item)
            }
        }
        if (stackable) {
            val c = this::class.java
            for (item in items) {
                if (item.javaClass == c) {
                    item.quantity += quantity
                    item.updateQuickslot()
                    return true
                }
            }
        }
        if (items.size < container.size) {
            if (Dungeon.hero?.isAlive == true) {
                Badges.validateItemLevelAquired(this)
            }
            items.add(this)
            com.watabou.pixeldungeon.quests.AiQuestBook.onItemCollected(this)
            QuickSlot.refresh()
            Collections.sort(items, itemComparator)
            return true
        } else {
            GLog.n(TXT_PACK_FULL, name())
            return false
        }
    }
    fun collect(): Boolean {
        val hero = Dungeon.hero ?: return false
        return collect(hero.belongings.backpack)
    }
    fun detach(container: Bag): Item? {
        if (quantity <= 0) {
            return null
        } else if (quantity == 1) {
            return detachAll(container)
        } else {
            quantity--
            updateQuickslot()
            try {
                val detached = this.javaClass.getDeclaredConstructor().newInstance()
                detached.onDetach()
                return detached
            } catch (e: Exception) {
                return null
            }
        }
    }
    fun detachAll(container: Bag): Item {
        for (item in container.items) {
            if (item === this) {
                container.items.remove(this)
                item.onDetach()
                QuickSlot.refresh()
                return this
            } else if (item is Bag) {
                if (item.contains(this)) {
                    return detachAll(item)
                }
            }
        }
        return this
    }
    open fun onDetach() {}
    fun level(): Int {
        return level
    }
    open fun level(value: Int) {
        level = value
    }
    open fun effectiveLevel(): Int {
        return if (isBroken) 0 else level
    }
    open fun upgrade(): Item {
        cursed = false
        cursedKnown = true
        level++
        fix()
        return this
    }
    fun upgrade(n: Int): Item {
        for (i in 0 until n) {
            upgrade()
        }
        return this
    }
    open fun degrade(): Item {
        level--
        fix()
        return this
    }
    fun degrade(n: Int): Item {
        for (i in 0 until n) {
            degrade()
        }
        return this
    }
    open fun use() {
        if (level > 0 && !isBroken) {
            val threshold = (maxDurability() * DURABILITY_WARNING_LEVEL).toInt()
            if (durability-- in (threshold + 1)..threshold && levelKnown) { // Logic check: threshold > durability >= threshold? wait. Java: durability-- >= threshold && threshold > durability
                // Original: if (durability-- >= threshold && threshold > durability && levelKnown)
                // Post-decrement means we compare OLD value to threshold.
                // Kotlin needs care.
                 // Rewriting logic:
                 // In Java: if (durability-- >= threshold && threshold > durability ... )
                 // This effectively means: if (old_val >= threshold && threshold > new_val)
                 // i.e. we JUST crossed the threshold downwards.
             }
             // Let's implement directly
        }
        // Actually, let's redo the logic cleanly
        if (level > 0 && !isBroken) {
             val threshold = (maxDurability() * DURABILITY_WARNING_LEVEL).toInt()
             // Java: durability-- decrements and returns OLD value.
             val oldDurability = durability
             durability--
             val newDurability = durability
             if (oldDurability >= threshold && threshold > newDurability && levelKnown) {
                 GLog.w(TXT_GONNA_BREAK, name())
             }
             if (isBroken) {
                 getBroken()
                 if (levelKnown) {
                     GLog.n(TXT_BROKEN, name())
                     Dungeon.hero?.interrupt()
                     val sprite = Dungeon.hero?.sprite
                     if (sprite != null) {
                         val point = sprite.center().offset(0f, -16f)
                         if (this is Weapon) {
                             sprite.parent?.add(Degradation.weapon(point))
                         } else if (this is Armor) {
                             sprite.parent?.add(Degradation.armor(point))
                         } else if (this is Ring) {
                             sprite.parent?.add(Degradation.ring(point))
                         } else if (this is Wand) {
                             sprite.parent?.add(Degradation.wand(point))
                         }
                     }
                     Sample.play(Assets.SND_DEGRADE)
                 }
             }
        }
    }
    open val isBroken: Boolean
        get() = durability <= 0
    open fun getBroken() {}
    open fun fix() {
        durability = maxDurability()
    }
    fun polish() {
        if (durability < maxDurability()) {
            durability++
        }
    }
    fun durability(): Int {
        return durability
    }
    fun setDurability(value: Int) {
        durability = Math.min(value, maxDurability())
    }
    open fun maxDurability(lvl: Int): Int {
        return 1
    }
    fun maxDurability(): Int {
        return maxDurability(level)
    }
    fun visiblyUpgraded(): Int {
        return if (levelKnown) level else 0
    }
    fun visiblyCursed(): Boolean {
        return cursed && cursedKnown
    }
    fun visiblyBroken(): Boolean {
        return levelKnown && isBroken
    }
    open val isUpgradable: Boolean
        get() = true
    open val isIdentified: Boolean
        get() = levelKnown && cursedKnown
    open fun isEquipped(hero: Hero): Boolean {
        return false
    }
    open fun identify(): Item {
        levelKnown = true
        cursedKnown = true
        return this
    }
    override fun toString(): String {
        return if (levelKnown && level != 0) {
            if (quantity > 1) {
                Utils.format(TXT_TO_STRING_LVL_X, name(), level, quantity)
            } else {
                Utils.format(TXT_TO_STRING_LVL, name(), level)
            }
        } else {
            if (quantity > 1) {
                Utils.format(TXT_TO_STRING_X, name(), quantity)
            } else {
                Utils.format(TXT_TO_STRING, name())
            }
        }
    }
    open fun name(): String {
        return name
    }
    fun trueName(): String {
        return name
    }
    open fun image(): Int {
        return image
    }
    open fun glowing(): ItemSprite.Glowing? {
        return null
    }
    open fun info(): String {
        val baseDesc = desc()
        val enchName = (this as? Weapon)?.let { w ->
            w.enchantment?.name(w.name() ?: "")
        }
        return LlmTextEnhancer.enhanceItemInfo(
            name(), javaClass.simpleName, level(),
            enchName, cursed && cursedKnown, baseDesc
        )
    }
    open fun desc(): String {
        return ""
    }
    fun quantity(): Int {
        return quantity
    }
    fun quantity(value: Int) {
        quantity = value
    }
    open fun price(): Int {
        return 0
    }
    open fun considerState(price: Int): Int {
        var p = price
        if (cursed && cursedKnown) {
            p /= 2
        }
        if (levelKnown) {
            if (level > 0) {
                p *= (level + 1)
                if (isBroken) {
                    p /= 2
                }
            } else if (level < 0) {
                p /= (1 - level)
            }
        }
        if (p < 1) {
            p = 1
        }
        return p
    }
    open fun random(): Item {
        return this
    }
    open fun status(): String? {
        return if (quantity != 1) quantity.toString() else null
    }
    open fun updateQuickslot() {
        if (stackable) {
            val cl = this::class.java
            if (QuickSlot.primaryValue == cl || QuickSlot.secondaryValue == cl) {
                QuickSlot.refresh()
            }
        } else if (QuickSlot.primaryValue === this || QuickSlot.secondaryValue === this) {
            QuickSlot.refresh()
        }
    }
    override fun storeInBundle(bundle: Bundle) {
        bundle.put(QUANTITY, quantity)
        bundle.put(LEVEL, level)
        bundle.put(LEVEL_KNOWN, levelKnown)
        bundle.put(CURSED, cursed)
        bundle.put(CURSED_KNOWN, cursedKnown)
        if (isUpgradable) {
            bundle.put(DURABILITY, durability)
        }
        QuickSlot.save(bundle, this)
    }
    override fun restoreFromBundle(bundle: Bundle) {
        quantity = bundle.getInt(QUANTITY)
        levelKnown = bundle.getBoolean(LEVEL_KNOWN)
        cursedKnown = bundle.getBoolean(CURSED_KNOWN)
        val l = bundle.getInt(LEVEL)
        if (l > 0) {
            upgrade(l)
        } else if (l < 0) {
            degrade(-l)
        }
        cursed = bundle.getBoolean(CURSED)
        if (isUpgradable) {
            durability = bundle.getInt(DURABILITY)
        }
        QuickSlot.restore(bundle, this)
    }
    open fun cast(user: Hero, dst: Int) {
        val cell = Ballistica.cast(user.pos, dst, false, true)
        user.sprite?.zap(cell)
        user.busy()
        Sample.play(Assets.SND_MISS, 0.6f, 0.6f, 1.5f)
        val enemy = Actor.findChar(cell)
        if (enemy != null) {
            QuickSlot.target(this, enemy)
        }
        var delay = TIME_TO_THROW
        if (this is MissileWeapon) {
            delay *= speedFactor(user)
            if (enemy != null) {
                val mark = user.buff(SnipersMark::class.java)
                if (mark != null) {
                    if (mark.`object` == enemy.id()) {
                        delay *= 0.5f
                    }
                    user.remove(mark)
                }
            }
        }
        val finalDelay = delay
        val s = user.sprite
        if (s != null) {
            val p = s.parent
            if (p != null) {
                val missile = p.recycle(MissileSprite::class.java) as MissileSprite
                missile.reset(
                    user.pos,
                    cell,
                    this,
                    object : Callback {
                        override fun call() {
                            detach(user.belongings.backpack)?.onThrow(cell)
                            user.spendAndNext(finalDelay)
                        }
                    })
            }
        }
    }
    companion object {
        const val TXT_PACK_FULL = "Your pack is too full for the %s"
        const val TXT_BROKEN = "Because of frequent use, your %s has broken."
        const val TXT_GONNA_BREAK = "Because of frequent use, your %s is going to break soon."
        const val TXT_TO_STRING = "%s"
        const val TXT_TO_STRING_X = "%s x%d"
        const val TXT_TO_STRING_LVL = "%s%+d"
        const val TXT_TO_STRING_LVL_X = "%s%+d x%d"
        const val DURABILITY_WARNING_LEVEL = 1 / 6f
        const val TIME_TO_THROW = 1.0f
        const val TIME_TO_PICK_UP = 1.0f
        const val TIME_TO_DROP = 0.5f
        const val AC_DROP = "DROP"
        const val AC_THROW = "THROW"
        const val QUANTITY = "quantity"
        const val LEVEL = "level"
        const val LEVEL_KNOWN = "levelKnown"
        const val CURSED = "cursed"
        const val CURSED_KNOWN = "cursedKnown"
        const val DURABILITY = "durability"
        val itemComparator: Comparator<Item> = Comparator { lhs, rhs ->
            Generator.Category.order(lhs) - Generator.Category.order(rhs)
        }
        fun evoke(hero: Hero) {
            hero.sprite?.emitter()?.burst(Speck.factory(Speck.EVOKE), 5)
        }
        fun virtual(cl: Class<out Item>): Item? {
            return try {
                val item = cl.getDeclaredConstructor().newInstance()
                item.quantity = 0
                item
            } catch (e: Exception) {
                null
            }
        }
        var curUser: Hero? = null
        var curItem: Item? = null
        var thrower: CellSelector.Listener = object : CellSelector.Listener {
            override fun onSelect(cell: Int?) {
                if (cell != null) {
                    val user = curUser ?: return
                    curItem?.cast(user, cell)
                }
            }
            override fun prompt(): String {
                return "Choose direction of throw"
            }
        }
    }
}
