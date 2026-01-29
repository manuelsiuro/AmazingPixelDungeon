package com.watabou.pixeldungeon.windows
import com.watabou.pixeldungeon.Dungeon
import com.watabou.pixeldungeon.actors.hero.Hero
import com.watabou.pixeldungeon.actors.mobs.npcs.Imp
import com.watabou.pixeldungeon.items.Item
import com.watabou.pixeldungeon.items.quest.DwarfToken
import com.watabou.pixeldungeon.scenes.PixelScene
import com.watabou.pixeldungeon.sprites.ItemSprite
import com.watabou.pixeldungeon.ui.RedButton
import com.watabou.pixeldungeon.ui.Window
import com.watabou.pixeldungeon.utils.GLog
import com.watabou.pixeldungeon.utils.Utils
class WndImp(imp: Imp, tokens: DwarfToken) : Window() {
    init {
        val titlebar = IconTitle()
        titlebar.icon(ItemSprite(tokens.image(), null))
        titlebar.label(Utils.capitalize(tokens.name()))
        titlebar.setRect(0f, 0f, WIDTH.toFloat(), 0f)
        add(titlebar)
        val message = PixelScene.createMultiline(TXT_MESSAGE, 6f)
        message.maxWidth = WIDTH
        message.measure()
        message.y = titlebar.bottom() + GAP
        add(message)
        val btnReward = object : RedButton(TXT_REWARD) {
            override fun onClick() {
                takeReward(imp, tokens, Imp.Quest.reward!!)
            }
        }
        btnReward.setRect(0f, message.y + message.height() + GAP, WIDTH.toFloat(), BTN_HEIGHT.toFloat())
        add(btnReward)
        resize(WIDTH, btnReward.bottom().toInt())
    }
    private fun takeReward(imp: Imp, tokens: DwarfToken, reward: Item) {
        hide()
        tokens.detachAll(Dungeon.hero!!.belongings.backpack)
        reward.identify()
        if (reward.doPickUp(Dungeon.hero!!)) {
            GLog.i(Hero.TXT_YOU_NOW_HAVE, reward.name())
        } else {
            Dungeon.level!!.drop(reward, imp.pos).sprite?.drop()
        }
        imp.flee()
        Imp.Quest.complete()
    }
    companion object {
        private const val TXT_MESSAGE =
            "Oh yes! You are my hero!\n" +
                    "Regarding your reward, I don't have cash with me right now, but I have something better for you. " +
                    "This is my family heirloom ring: my granddad took it off a dead paladin's finger."
        private const val TXT_REWARD = "Take the ring"
        private const val WIDTH = 120
        private const val BTN_HEIGHT = 20
        private const val GAP = 2
    }
}
