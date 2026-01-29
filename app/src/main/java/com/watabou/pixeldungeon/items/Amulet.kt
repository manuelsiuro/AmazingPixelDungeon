package com.watabou.pixeldungeon.items
import com.watabou.noosa.Game
import com.watabou.pixeldungeon.Badges
import com.watabou.pixeldungeon.Dungeon
import com.watabou.pixeldungeon.Statistics
import com.watabou.pixeldungeon.actors.hero.Hero
import com.watabou.pixeldungeon.scenes.AmuletScene
import com.watabou.pixeldungeon.sprites.ItemSpriteSheet
import java.io.IOException
import java.util.ArrayList
class Amulet : Item() {
    init {
        name = "Amulet of Yendor"
        image = ItemSpriteSheet.AMULET
        unique = true
    }
    override fun actions(hero: Hero): ArrayList<String> {
        val actions = super.actions(hero)
        actions.add(AC_END)
        return actions
    }
    override fun execute(hero: Hero, action: String) {
        if (action == AC_END) {
            showAmuletScene(false)
        } else {
            super.execute(hero, action)
        }
    }
    override fun doPickUp(hero: Hero): Boolean {
        if (super.doPickUp(hero)) {
            if (!Statistics.amuletObtained) {
                Statistics.amuletObtained = true
                Badges.validateVictory()
                showAmuletScene(true)
            }
            return true
        } else {
            return false
        }
    }
    private fun showAmuletScene(showText: Boolean) {
        try {
            Dungeon.saveAll()
            AmuletScene.noText = !showText
            Game.switchScene(AmuletScene::class.java)
        } catch (e: IOException) {
        }
    }
    override val isIdentified: Boolean
        get() = true
    override val isUpgradable: Boolean
        get() = false
    override fun info(): String {
        return "The Amulet of Yendor is the most powerful known artifact of unknown origin. It is said that the amulet " +
                "is able to fulfil any wish if its owner's will-power is strong enough to \"persuade\" it to do it."
    }
    companion object {
        private const val AC_END = "END THE GAME"
    }
}
