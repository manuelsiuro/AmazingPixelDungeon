package com.watabou.pixeldungeon.items.wands
import com.watabou.noosa.audio.Sample
import com.watabou.pixeldungeon.Assets
import com.watabou.pixeldungeon.Badges
import com.watabou.pixeldungeon.Dungeon
import com.watabou.pixeldungeon.ResultDescriptions
import com.watabou.pixeldungeon.actors.Actor
import com.watabou.pixeldungeon.actors.hero.Hero
import com.watabou.pixeldungeon.items.Item
import com.watabou.pixeldungeon.items.scrolls.ScrollOfUpgrade
import com.watabou.pixeldungeon.scenes.GameScene
import com.watabou.pixeldungeon.sprites.ItemSpriteSheet
import com.watabou.pixeldungeon.utils.GLog
import com.watabou.pixeldungeon.utils.Utils
import com.watabou.pixeldungeon.windows.WndBag
import com.watabou.utils.Random
import java.util.ArrayList
class WandOfMagicMissile : Wand() {
    private var disenchantEquipped: Boolean = false
    init {
        name = "Wand of Magic Missile"
        image = ItemSpriteSheet.WAND_MAGIC_MISSILE
    }
    override fun actions(hero: Hero): ArrayList<String> {
        val actions = super.actions(hero)
        if (level() > 0) {
            actions.add(AC_DISENCHANT)
        }
        return actions
    }
    override fun onZap(cell: Int) {
        val ch = Actor.findChar(cell)
        if (ch != null) {
            val level = power()
            ch.damage(Random.Int(1, 6 + level * 2), this)
            ch.sprite?.burst(0xFF99CCFF.toInt(), level / 2 + 2)
            if (ch == Item.curUser && !ch.isAlive) {
                Dungeon.fail(Utils.format(ResultDescriptions.WAND, name, Dungeon.depth))
                GLog.n("You killed yourself with your own Wand of Magic Missile...")
            }
        }
    }
    override fun execute(hero: Hero, action: String) {
        if (action == AC_DISENCHANT) {
            if (hero.belongings.weapon === this) {
                disenchantEquipped = true
                hero.belongings.weapon = null
                updateQuickslot()
            } else {
                disenchantEquipped = false
                detach(hero.belongings.backpack)
            }
            Item.curUser = hero
            GameScene.selectItem(itemSelector, WndBag.Mode.WAND, TXT_SELECT_WAND)
        } else {
            super.execute(hero, action)
        }
    }
    override fun isKnown(): Boolean {
        return true
    }
    override fun setKnown() {
    }
    override fun initialCharges(): Int {
        return 3
    }
    override fun desc(): String {
        return "This wand launches missiles of pure magical energy, dealing moderate damage to a target creature."
    }
    private val itemSelector = object : WndBag.Listener {
        override fun onSelect(item: Item?) {
            val user = Item.curUser ?: return
            if (item != null) {
                Sample.play(Assets.SND_EVOKE)
                ScrollOfUpgrade.upgrade(user)
                Item.evoke(user)
                GLog.w(TXT_DISENCHANTED, item.name())
                item.upgrade()
                user.spendAndNext(TIME_TO_DISENCHANT)
                Badges.validateItemLevelAquired(item)
            } else {
                if (disenchantEquipped) {
                    user.belongings.weapon = this@WandOfMagicMissile
                    this@WandOfMagicMissile.updateQuickslot()
                } else {
                    collect(user.belongings.backpack)
                }
            }
        }
    }
    companion object {
        const val AC_DISENCHANT = "DISENCHANT"
        private const val TXT_SELECT_WAND = "Select a wand to upgrade"
        private const val TXT_DISENCHANTED = "you disenchanted the Wand of Magic Missile and used its essence to upgrade your %s"
        private const val TIME_TO_DISENCHANT = 2f
    }
}
