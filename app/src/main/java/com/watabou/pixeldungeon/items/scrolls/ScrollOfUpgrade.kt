package com.watabou.pixeldungeon.items.scrolls
import com.watabou.pixeldungeon.Badges
import com.watabou.pixeldungeon.Dungeon
import com.watabou.pixeldungeon.actors.hero.Hero
import com.watabou.pixeldungeon.effects.Speck
import com.watabou.pixeldungeon.items.Item
import com.watabou.pixeldungeon.utils.GLog
import com.watabou.pixeldungeon.windows.WndBag
class ScrollOfUpgrade : InventoryScroll() {
    init {
        name = "Scroll of Upgrade"
        inventoryTitle = "Select an item to upgrade"
        mode = WndBag.Mode.UPGRADEABLE
    }
    override fun onItemSelected(item: Item) {
        val hero = Dungeon.hero ?: return
        ScrollOfRemoveCurse.uncurse(hero, item)
        if (item.isBroken) {
            item.fix()
        } else {
            item.upgrade()
        }
        val user = curUser ?: return
        upgrade(user)
        GLog.p(TXT_LOOKS_BETTER, item.name())
        Badges.validateItemLevelAquired(item)
    }
    override fun desc(): String {
        return "This scroll will upgrade a single item, improving its quality. A wand will " +
                "increase in power and in number of charges; a weapon will inflict more damage " +
                "or find its mark more frequently; a suit of armor will deflect additional blows; " +
                "the effect of a ring on its wearer will intensify. Weapons and armor will also " +
                "require less strength to use, and any curses on the item will be lifted."
    }
    companion object {
        private const val TXT_LOOKS_BETTER = "your %s certainly looks better now"
        fun upgrade(hero: Hero) {
            hero.sprite?.emitter()?.start(Speck.factory(Speck.UP), 0.2f, 3)
        }
    }
}
