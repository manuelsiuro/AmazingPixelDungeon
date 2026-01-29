package com.watabou.pixeldungeon.items.wands
import com.watabou.noosa.audio.Sample
import com.watabou.pixeldungeon.Assets
import com.watabou.pixeldungeon.actors.Actor
import com.watabou.pixeldungeon.actors.buffs.Buffs
import com.watabou.pixeldungeon.actors.buffs.Poison
import com.watabou.pixeldungeon.effects.MagicMissile
import com.watabou.pixeldungeon.items.Item
import com.watabou.pixeldungeon.utils.GLog
import com.watabou.utils.Callback
class WandOfPoison : Wand() {
    init {
        name = "Wand of Poison"
    }
    override fun onZap(cell: Int) {
        val ch = Actor.findChar(cell)
        if (ch != null) {
            Buffs.affect(ch, Poison::class.java)?.set(Poison.durationFactor(ch) * (5 + power()))
        } else {
            GLog.i("nothing happened")
        }
    }
    override fun fx(cell: Int, callback: Callback) {
        val user = Item.curUser ?: return
        val parent = user.sprite?.parent ?: return
        MagicMissile.poison(parent, user.pos, cell, callback)
        Sample.play(Assets.SND_ZAP)
    }
    override fun desc(): String {
        return "The vile blast of this twisted bit of wood will imbue its target " +
                "with a deadly venom. A creature that is poisoned will suffer periodic " +
                "damage until the effect ends. The duration of the effect increases " +
                "with the level of the staff."
    }
}
