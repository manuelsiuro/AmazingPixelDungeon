package com.watabou.pixeldungeon.windows
import com.watabou.pixeldungeon.Dungeon
import com.watabou.pixeldungeon.actors.hero.Hero
import com.watabou.pixeldungeon.actors.mobs.npcs.Wandmaker
import com.watabou.pixeldungeon.items.Item
import com.watabou.pixeldungeon.utils.GLog
import com.watabou.pixeldungeon.utils.Utils
class WndWandmaker(private val wandmaker: Wandmaker, item: Item) :
    WndQuest(wandmaker, TXT_MESSAGE, TXT_BATTLE, TXT_NON_BATTLE) {
    private val questItem: Item = item
    override fun onSelect(index: Int) {
        questItem.detach(Dungeon.hero!!.belongings.backpack)
        val reward = if (index == 0) Wandmaker.Quest.wand1!! else Wandmaker.Quest.wand2!!
        reward.identify()
        if (reward.doPickUp(Dungeon.hero!!)) {
            GLog.i(Hero.TXT_YOU_NOW_HAVE, reward.name())
        } else {
            Dungeon.level!!.drop(reward, wandmaker.pos).sprite?.drop()
        }
        wandmaker.yell(Utils.format(TXT_FARAWELL, Dungeon.hero!!.className()))
        wandmaker.destroy()
        wandmaker.sprite?.die()
        Wandmaker.Quest.complete()
    }
    companion object {
        private const val TXT_MESSAGE =
            "Oh, I see you have succeeded! I do hope it hasn't troubled you too much. " +
                    "As I promised, you can choose one of my high quality wands."
        private const val TXT_BATTLE = "Battle wand"
        private const val TXT_NON_BATTLE = "Non-battle wand"
        private const val TXT_FARAWELL = "Good luck in your quest, %s!"
    }
}
