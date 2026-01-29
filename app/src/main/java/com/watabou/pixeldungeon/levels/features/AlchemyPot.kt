package com.watabou.pixeldungeon.levels.features
import com.watabou.pixeldungeon.actors.hero.Hero
import com.watabou.pixeldungeon.items.Item
import com.watabou.pixeldungeon.scenes.GameScene
import com.watabou.pixeldungeon.windows.WndBag
object AlchemyPot {
    private const val TXT_SELECT_SEED = "Select a seed to throw"
    private var hero: Hero? = null
    private var pos: Int = 0
    fun operate(hero: Hero, pos: Int) {
        AlchemyPot.hero = hero
        AlchemyPot.pos = pos
        GameScene.selectItem(itemSelector, WndBag.Mode.SEED, TXT_SELECT_SEED)
    }
    private val itemSelector = object : WndBag.Listener {
        override fun onSelect(item: Item?) {
            val currentHero = hero ?: return
            if (item != null) {
                item.cast(currentHero, pos)
            }
        }
    }
}
