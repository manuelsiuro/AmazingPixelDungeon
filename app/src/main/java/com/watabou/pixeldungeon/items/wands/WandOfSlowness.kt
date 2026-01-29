package com.watabou.pixeldungeon.items.wands
import com.watabou.noosa.audio.Sample
import com.watabou.pixeldungeon.Assets
import com.watabou.pixeldungeon.actors.Actor
import com.watabou.pixeldungeon.actors.buffs.Buffs
import com.watabou.pixeldungeon.actors.buffs.Slow
import com.watabou.pixeldungeon.effects.MagicMissile
import com.watabou.pixeldungeon.items.Item
import com.watabou.pixeldungeon.utils.GLog
import com.watabou.utils.Callback
class WandOfSlowness : Wand() {
    init {
        name = "Wand of Slowness"
    }
    override fun onZap(cell: Int) {
        val ch = Actor.findChar(cell)
        if (ch != null) {
            Buffs.affect(ch, Slow::class.java, Slow.duration(ch) / 3 + power())
        } else {
            GLog.i("nothing happened")
        }
    }
    override fun fx(cell: Int, callback: Callback) {
        val user = Item.curUser ?: return
        val parent = user.sprite?.parent ?: return
        MagicMissile.slowness(parent, user.pos, cell, callback)
        Sample.play(Assets.SND_ZAP)
    }
    override fun desc(): String {
        return "This wand will cause a creature to move and attack " +
                "at half its ordinary speed until the effect ends"
    }
}
