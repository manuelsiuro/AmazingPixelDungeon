package com.watabou.pixeldungeon.items.wands
import com.watabou.noosa.audio.Sample
import com.watabou.pixeldungeon.Assets
import com.watabou.pixeldungeon.Dungeon
import com.watabou.pixeldungeon.actors.Actor
import com.watabou.pixeldungeon.actors.Char
import com.watabou.pixeldungeon.effects.MagicMissile
import com.watabou.pixeldungeon.items.Item
import com.watabou.pixeldungeon.items.scrolls.ScrollOfTeleportation
import com.watabou.pixeldungeon.utils.GLog
import com.watabou.utils.Callback
class WandOfTeleportation : Wand() {
    init {
        name = "Wand of Teleportation"
    }
    override fun onZap(cell: Int) {
        val ch = Actor.findChar(cell)
        val user = Item.curUser
        if (ch === user) {
            setKnown()
            user?.let { ScrollOfTeleportation.teleportHero(it) }
        } else if (ch != null) {
            var count = 10
            var pos: Int
            do {
                pos = Dungeon.level?.randomRespawnCell() ?: -1
                if (count-- <= 0) {
                    break
                }
            } while (pos == -1)
            if (pos == -1) {
                GLog.w(ScrollOfTeleportation.TXT_NO_TELEPORT)
            } else {
                ch.pos = pos
                ch.sprite?.place(ch.pos)
                ch.sprite?.visible = Dungeon.visible[pos]
                val userName = Item.curUser?.name ?: "Someone"
                GLog.i("$userName teleported " + ch.name + " to somewhere")
            }
        } else {
            GLog.i("nothing happened")
        }
    }
    override fun fx(cell: Int, callback: Callback) {
        val user = Item.curUser ?: return
        val parent = user.sprite?.parent ?: return
        MagicMissile.coldLight(parent, user.pos, cell, callback)
        Sample.play(Assets.SND_ZAP)
    }
    override fun desc(): String {
        return "A blast from this wand will teleport a creature against " +
                "its will to a random place on the current level."
    }
}
