package com.watabou.pixeldungeon.ui
import com.watabou.input.Touchscreen.Touch
import com.watabou.noosa.Image
import com.watabou.noosa.ui.Button
import com.watabou.pixeldungeon.Dungeon
import com.watabou.pixeldungeon.DungeonTilemap
import com.watabou.pixeldungeon.actors.Actor
import com.watabou.pixeldungeon.actors.Char
import com.watabou.pixeldungeon.actors.mobs.Mob
import com.watabou.pixeldungeon.items.Item
import com.watabou.pixeldungeon.mechanics.Ballistica
import com.watabou.pixeldungeon.scenes.GameScene
import com.watabou.pixeldungeon.scenes.PixelScene
import com.watabou.pixeldungeon.windows.WndBag
import com.watabou.utils.Bundle
class QuickSlot : Button(), WndBag.Listener {
    private var itemInSlot: Item? = null
    private lateinit var slot: ItemSlot
    // Internal exposure for StatusPane or Toolbar might access crossB/crossM?
    // Java code: primary.crossB.visible = false. So they check primary (QuickSlot) static field, then instance field crossB.
    // So crossB needs to be accessible (internal or public or package-local).
    internal lateinit var crossB: Image
    internal lateinit var crossM: Image
    private var targeting = false
    fun primary() {
        primary = this
        item(select())
    }
    fun secondary() {
        secondary = this
        item(select())
    }
    override fun destroy() {
        super.destroy()
        if (this === primary) {
            primary = null
        } else {
            secondary = null
        }
        lastTarget = null
    }
    override fun createChildren() {
        super.createChildren()
        slot = object : ItemSlot() {
            override fun onClick() {
                val target = lastTarget
                if (targeting && target != null) {
                    GameScene.handleCell(target.pos)
                } else {
                    useTargeting()
                    val hero = Dungeon.hero ?: return
                    select()?.execute(hero)
                }
            }
            override fun onLongClick(): Boolean {
                return this@QuickSlot.onLongClick()
            }
            override fun onTouchDown() {
                icon.lightness(0.7f)
            }
            override fun onTouchUp() {
                icon.resetColor()
            }
        }
        add(slot)
        crossB = Icons.TARGET.get()
        crossB.visible = false
        add(crossB)
        crossM = Image()
        crossM.copy(crossB)
    }
    override fun layout() {
        super.layout()
        slot.fill(this)
        crossB.x = PixelScene.align(x + (width - crossB.width) / 2)
        crossB.y = PixelScene.align(y + (height - crossB.height) / 2)
    }
    override fun onClick() {
        GameScene.selectItem(this, WndBag.Mode.QUICKSLOT, TXT_SELECT_ITEM)
    }
    override fun onLongClick(): Boolean {
        GameScene.selectItem(this, WndBag.Mode.QUICKSLOT, TXT_SELECT_ITEM)
        return true
    }
    @Suppress("UNCHECKED_CAST")
    private fun select(): Item? {
        val content = if (this === primary) primaryValue else secondaryValue
        return if (content is Item) {
            content
        } else if (content != null) {
            val hero = Dungeon.hero ?: return null
            val item = hero.belongings.getItem(content as Class<out Item>)
            item ?: Item.virtual(content)
        } else {
            null
        }
    }
    override fun onSelect(item: Item?) {
        if (item != null) {
            if (this === primary) {
                primaryValue = if (item.stackable) item.javaClass else item
            } else {
                secondaryValue = if (item.stackable) item.javaClass else item
            }
            refresh()
        }
    }
    fun item(item: Item?) {
        slot.item(item)
        itemInSlot = item
        enableSlot()
    }
    fun enable(value: Boolean) {
        active = value
        if (value) {
            enableSlot()
        } else {
            slot.enable(false)
        }
    }
    private fun enableSlot() {
        val i = itemInSlot
        val hero = Dungeon.hero
        slot.enable(
            i != null &&
                    i.quantity() > 0 &&
                    hero != null &&
                    (hero.belongings.backpack.contains(i) || i.isEquipped(hero))
        )
    }
    private fun useTargeting() {
        var target = lastTarget
        targeting = target != null && target.isAlive && Dungeon.visible[target.pos]
        val hero = Dungeon.hero ?: return
        if (targeting && target != null) {
            val pos = Ballistica.cast(hero.pos, target.pos, false, true)
            if (pos != target.pos) {
                lastTarget = null
                targeting = false
            }
        }
        if (!targeting) {
            val n = hero.visibleEnemies()
            for (i in 0 until n) {
                val enemy = hero.visibleEnemy(i)
                val pos = Ballistica.cast(hero.pos, enemy.pos, false, true)
                if (pos == enemy.pos) {
                    lastTarget = enemy
                    targeting = true
                    break
                }
            }
        }
        target = lastTarget
        if (targeting && target != null) {
            if (Actor.all().contains(target)) {
                target.sprite?.parent?.add(crossM)
                crossM.point(DungeonTilemap.tileToWorld(target.pos))
                crossB.visible = true
            } else {
                lastTarget = null
            }
        }
    }
    companion object {
        private const val TXT_SELECT_ITEM = "Select an item for the quickslot"
        private var primary: QuickSlot? = null
        private var secondary: QuickSlot? = null
        private var lastTarget: Char? = null
        var primaryValue: Any? = null
        var secondaryValue: Any? = null
        fun refresh() {
            primary?.let { it.item(it.select()) }
            secondary?.let { it.item(it.select()) }
        }
        @Suppress("UNUSED_PARAMETER")
        fun target(item: Item, target: Char) {
            if (target !== Dungeon.hero) {
                lastTarget = target
                HealthIndicator.instance?.target(target)
            }
        }
        fun cancel() {
            primary?.let {
                if (it.targeting) {
                    it.crossB.visible = false
                    it.crossM.remove()
                    it.targeting = false
                }
            }
            secondary?.let {
                if (it.targeting) {
                    it.crossB.visible = false
                    it.crossM.remove()
                    it.targeting = false
                }
            }
        }
        private const val QUICKSLOT1 = "quickslot"
        private const val QUICKSLOT2 = "quickslot2"
        @Suppress("UNCHECKED_CAST")
        fun save(bundle: Bundle) {
            val stuff = Dungeon.hero?.belongings ?: return
            if (primaryValue is Class<*> &&
                stuff.getItem(primaryValue as Class<out Item>) != null
            ) {
                bundle.put(QUICKSLOT1, (primaryValue as Class<*>).name)
            }
            if (secondaryValue is Class<*> &&
                stuff.getItem(secondaryValue as Class<out Item>) != null &&
                Toolbar.secondQuickslot()
            ) {
                bundle.put(QUICKSLOT2, (secondaryValue as Class<*>).name)
            }
        }
        fun save(bundle: Bundle, item: Item?) {
            if (item === primaryValue) {
                bundle.put(QUICKSLOT1, true)
            }
            if (item === secondaryValue && Toolbar.secondQuickslot()) {
                bundle.put(QUICKSLOT2, true)
            }
        }
        fun restore(bundle: Bundle) {
            primaryValue = null
            secondaryValue = null
            var qsClass = bundle.getString(QUICKSLOT1)
            try {
                primaryValue = Class.forName(qsClass)
            } catch (e: ClassNotFoundException) {
            }
            qsClass = bundle.getString(QUICKSLOT2)
            try {
                secondaryValue = Class.forName(qsClass)
            } catch (e: ClassNotFoundException) {
            }
        }
        fun restore(bundle: Bundle, item: Item) {
            if (bundle.getBoolean(QUICKSLOT1)) {
                primaryValue = item
            }
            if (bundle.getBoolean(QUICKSLOT2)) {
                secondaryValue = item
            }
        }
        fun compress() {
            if ((primaryValue == null && secondaryValue != null) ||
                (primaryValue === secondaryValue)
            ) {
                primaryValue = secondaryValue
                secondaryValue = null
            }
        }
    }
}
