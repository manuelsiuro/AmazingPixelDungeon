package com.watabou.pixeldungeon.items.wands
import com.watabou.noosa.audio.Sample
import com.watabou.pixeldungeon.Assets
import com.watabou.pixeldungeon.Dungeon
import com.watabou.pixeldungeon.ResultDescriptions
import com.watabou.pixeldungeon.actors.Actor
import com.watabou.pixeldungeon.actors.blobs.Blob
import com.watabou.pixeldungeon.actors.blobs.Fire
import com.watabou.pixeldungeon.actors.buffs.Buffs
import com.watabou.pixeldungeon.actors.buffs.Burning
import com.watabou.pixeldungeon.effects.MagicMissile
import com.watabou.pixeldungeon.effects.particles.FlameParticle
import com.watabou.pixeldungeon.items.Item
import com.watabou.pixeldungeon.levels.Level
import com.watabou.pixeldungeon.mechanics.Ballistica
import com.watabou.pixeldungeon.scenes.GameScene
import com.watabou.pixeldungeon.utils.GLog
import com.watabou.pixeldungeon.utils.Utils
import com.watabou.utils.Callback
import com.watabou.utils.Random
class WandOfFirebolt : Wand() {
    init {
        name = "Wand of Firebolt"
    }
    override fun onZap(cell: Int) {
        val level = power()
        for (i in 1 until Ballistica.distance - 1) {
            val c = Ballistica.trace[i]
            if (Level.flamable[c]) {
                Blob.seed(c, 1, Fire::class.java)?.let { GameScene.add(it) }
            }
        }
        Blob.seed(cell, 1, Fire::class.java)?.let { GameScene.add(it) }
        val ch = Actor.findChar(cell)
        if (ch != null) {
            ch.damage(Random.Int(1, 8 + level * level), this)
            Buffs.affect(ch, Burning::class.java)?.reignite(ch)
            ch.sprite?.emitter()?.burst(FlameParticle.FACTORY, 5)
            if (ch == Item.curUser && !ch.isAlive) {
                Dungeon.fail(Utils.format(ResultDescriptions.WAND, name, Dungeon.depth))
                GLog.n("You killed yourself with your own Wand of Firebolt...")
            }
        }
    }
    override fun fx(cell: Int, callback: Callback) {
        val user = Item.curUser ?: return
        val parent = user.sprite?.parent ?: return
        MagicMissile.fire(parent, user.pos, cell, callback)
        Sample.play(Assets.SND_ZAP)
    }
    override fun desc(): String {
        return "This wand unleashes bursts of magical fire. It will ignite " +
                "flammable terrain, and will damage and burn a creature it hits."
    }
}
