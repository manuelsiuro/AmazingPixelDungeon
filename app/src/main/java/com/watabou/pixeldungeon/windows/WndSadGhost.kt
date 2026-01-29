package com.watabou.pixeldungeon.windows
import com.watabou.pixeldungeon.Dungeon
import com.watabou.pixeldungeon.actors.hero.Hero
import com.watabou.pixeldungeon.actors.mobs.npcs.Ghost
import com.watabou.pixeldungeon.items.Item
import com.watabou.pixeldungeon.utils.GLog
class WndSadGhost(private val ghost: Ghost, item: Item?, text: String) :
    WndQuest(ghost, text, TXT_WEAPON, TXT_ARMOR) {
    private val questItem: Item? = item
    override fun onSelect(index: Int) {
        questItem?.detach(Dungeon.hero!!.belongings.backpack)
        val reward = if (index == 0) Ghost.Quest.weapon!! else Ghost.Quest.armor!!
        if (reward.doPickUp(Dungeon.hero!!)) {
            GLog.i(Hero.TXT_YOU_NOW_HAVE, reward.name())
        } else {
            Dungeon.level!!.drop(reward, ghost.pos).sprite?.drop()
        }
        ghost.yell("Farewell, adventurer!")
        ghost.die(null)
        Ghost.Quest.complete()
    }
    companion object {
        private const val TXT_WEAPON = "Ghost's weapon"
        private const val TXT_ARMOR = "Ghost's armor"
    }
}
