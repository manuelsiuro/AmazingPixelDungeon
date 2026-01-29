package com.watabou.pixeldungeon.items.wands
import com.watabou.noosa.audio.Sample
import com.watabou.noosa.tweeners.AlphaTweener
import com.watabou.pixeldungeon.Assets
import com.watabou.pixeldungeon.Dungeon
import com.watabou.pixeldungeon.actors.Actor
import com.watabou.pixeldungeon.actors.Char
import com.watabou.pixeldungeon.effects.MagicMissile
import com.watabou.pixeldungeon.effects.Speck
import com.watabou.pixeldungeon.items.Item
import com.watabou.pixeldungeon.mechanics.Ballistica
import com.watabou.utils.Callback
class WandOfBlink : Wand() {
    init {
        name = "Wand of Blink"
    }
    override fun onZap(cell: Int) {
        var targetCell = cell
        val level = power()
        if (Ballistica.distance > level + 4) {
            targetCell = Ballistica.trace[level + 3]
        } else if (Actor.findChar(targetCell) != null && Ballistica.distance > 1) {
            targetCell = Ballistica.trace[Ballistica.distance - 2]
        }
        Item.curUser?.sprite?.visible = true
        Dungeon.hero?.let { appear(it, targetCell) }
        Dungeon.observe()
    }
    override fun fx(cell: Int, callback: Callback) {
        val user = Item.curUser ?: return
        val parent = user.sprite?.parent ?: return
        MagicMissile.whiteLight(parent, user.pos, cell, callback)
        Sample.play(Assets.SND_ZAP)
        user.sprite?.visible = false
    }
    override fun desc(): String {
        return "This wand will allow you to teleport in the chosen direction. " +
                "Creatures and inanimate obstructions will block the teleportation."
    }
    companion object {
        fun appear(ch: Char, pos: Int) {
            ch.sprite?.interruptMotion()
            ch.move(pos)
            ch.sprite?.place(pos)
            if (ch.invisible == 0) {
                ch.sprite?.alpha(0f)
                ch.sprite?.let { sprite ->
                    sprite.parent?.add(AlphaTweener(sprite, 1f, 0.4f))
                }
            }
            ch.sprite?.emitter()?.start(Speck.factory(Speck.LIGHT), 0.2f, 3)
            Sample.play(Assets.SND_TELEPORT)
        }
    }
}
