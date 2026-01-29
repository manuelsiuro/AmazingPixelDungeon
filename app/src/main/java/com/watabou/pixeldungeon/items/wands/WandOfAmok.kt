package com.watabou.pixeldungeon.items.wands
import com.watabou.noosa.audio.Sample
import com.watabou.pixeldungeon.Assets
import com.watabou.pixeldungeon.Dungeon
import com.watabou.pixeldungeon.actors.Actor
import com.watabou.pixeldungeon.actors.buffs.Amok
import com.watabou.pixeldungeon.actors.buffs.Buffs
import com.watabou.pixeldungeon.actors.buffs.Vertigo
import com.watabou.pixeldungeon.effects.MagicMissile
import com.watabou.pixeldungeon.items.Item
import com.watabou.pixeldungeon.utils.GLog
import com.watabou.utils.Callback
class WandOfAmok : Wand() {
    init {
        name = "Wand of Amok"
    }
    override fun onZap(cell: Int) {
        val ch = Actor.findChar(cell)
        if (ch != null) {
            if (ch == Dungeon.hero) {
                Buffs.affect(ch, Vertigo::class.java, Vertigo.duration(ch))
            } else {
                Buffs.affect(ch, Amok::class.java, 3f + power())
            }
        } else {
            GLog.i("nothing happened")
        }
    }
    override fun fx(cell: Int, callback: Callback) {
        val user = Item.curUser ?: return
        val parent = user.sprite?.parent ?: return
        MagicMissile.purpleLight(parent, user.pos, cell, callback)
        Sample.play(Assets.SND_ZAP)
    }
    override fun desc(): String {
        return "The purple light from this wand will make the target run amok " +
                "attacking random creatures in its vicinity."
    }
}
