package com.watabou.pixeldungeon.items.wands
import com.watabou.noosa.Camera
import com.watabou.pixeldungeon.Dungeon
import com.watabou.pixeldungeon.ResultDescriptions
import com.watabou.pixeldungeon.actors.Actor
import com.watabou.pixeldungeon.actors.Char
import com.watabou.pixeldungeon.effects.CellEmitter
import com.watabou.pixeldungeon.effects.Lightning
import com.watabou.pixeldungeon.effects.particles.SparkParticle
import com.watabou.pixeldungeon.items.Item
import com.watabou.pixeldungeon.levels.Level
import com.watabou.pixeldungeon.levels.traps.LightningTrap
import com.watabou.pixeldungeon.utils.GLog
import com.watabou.pixeldungeon.utils.Utils
import com.watabou.utils.Callback
import com.watabou.utils.Random
import java.util.ArrayList
import java.util.HashSet
class WandOfLightning : Wand() {
    private val affected = ArrayList<Char>()
    private val points = IntArray(20)
    private var nPoints: Int = 0
    init {
        name = "Wand of Lightning"
    }
    override fun onZap(cell: Int) {
        // Everything is processed in fx() method
        if (Item.curUser?.isAlive == false) {
            Dungeon.fail(Utils.format(ResultDescriptions.WAND, name, Dungeon.depth))
            GLog.n("You killed yourself with your own Wand of Lightning...")
        }
    }
    private fun hit(ch: Char, damage: Int) {
        if (damage < 1) {
            return
        }
        if (ch === Dungeon.hero) {
            Camera.main?.shake(2f, 0.3f)
        }
        affected.add(ch)
        ch.damage(if (Level.water[ch.pos] && !ch.flying) (damage * 2) else damage, LightningTrap.LIGHTNING)
        ch.sprite?.centerEmitter()?.burst(SparkParticle.FACTORY, 3)
        ch.sprite?.flash()
        points[nPoints++] = ch.pos
        val ns = HashSet<Char>()
        for (i in Level.NEIGHBOURS8.indices) {
            val n = Actor.findChar(ch.pos + Level.NEIGHBOURS8[i])
            if (n != null && !affected.contains(n)) {
                ns.add(n)
            }
        }
        if (ns.size > 0) {
            Random.element(ns)?.let { hit(it, Random.Int(damage / 2, damage)) }
        }
    }
    override fun fx(cell: Int, callback: Callback) {
        val user = Item.curUser ?: return
        nPoints = 0
        points[nPoints++] = user.pos
        val ch = Actor.findChar(cell)
        if (ch != null) {
            affected.clear()
            val lvl = power()
            hit(ch, Random.Int(5 + lvl / 2, 10 + lvl))
        } else {
            points[nPoints++] = cell
            CellEmitter.center(cell).burst(SparkParticle.FACTORY, 3)
        }
        user.sprite?.parent?.add(Lightning(points, nPoints, callback))
    }
    override fun desc(): String {
        return "This wand conjures forth deadly arcs of electricity, which deal damage " +
                "to several creatures standing close to each other."
    }
}
