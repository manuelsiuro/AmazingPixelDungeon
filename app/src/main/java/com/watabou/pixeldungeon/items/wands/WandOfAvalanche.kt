package com.watabou.pixeldungeon.items.wands
import com.watabou.noosa.Camera
import com.watabou.noosa.audio.Sample
import com.watabou.pixeldungeon.Assets
import com.watabou.pixeldungeon.Dungeon
import com.watabou.pixeldungeon.ResultDescriptions
import com.watabou.pixeldungeon.actors.Actor
import com.watabou.pixeldungeon.actors.buffs.Buffs
import com.watabou.pixeldungeon.actors.buffs.Paralysis
import com.watabou.pixeldungeon.actors.mobs.Mob
import com.watabou.pixeldungeon.effects.CellEmitter
import com.watabou.pixeldungeon.effects.MagicMissile
import com.watabou.pixeldungeon.effects.Speck
import com.watabou.pixeldungeon.items.Item
import com.watabou.pixeldungeon.levels.Level
import com.watabou.pixeldungeon.mechanics.Ballistica
import com.watabou.pixeldungeon.scenes.GameScene
import com.watabou.pixeldungeon.utils.BArray
import com.watabou.pixeldungeon.utils.GLog
import com.watabou.pixeldungeon.utils.Utils
import com.watabou.utils.Callback
import com.watabou.utils.PathFinder
import com.watabou.utils.Random
import kotlin.math.min
class WandOfAvalanche : Wand() {
    init {
        name = "Wand of Avalanche"
        hitChars = false
    }
    override fun onZap(cell: Int) {
        Sample.play(Assets.SND_ROCKS)
        val level = power()
        Ballistica.distance = min(Ballistica.distance, 8 + level)
        val size = 1 + level / 3
        PathFinder.buildDistanceMap(cell, BArray.not(Level.solid, null), size)
        val distance = PathFinder.distance ?: return
        var shake = 0
        for (i in 0 until Level.LENGTH) {
            val d = distance[i]
            if (d < Int.MAX_VALUE) {
                val ch = Actor.findChar(i)
                if (ch != null) {
                    ch.sprite?.flash()
                    ch.damage(Random.Int(2, 6 + (size - d) * 2), this)
                    if (ch.isAlive && Random.Int(2 + d) == 0) {
                        Buffs.prolong(ch, Paralysis::class.java, Random.IntRange(2, 6).toFloat())
                    }
                }
                if (ch != null && ch.isAlive) {
                    if (ch is Mob) {
                        Dungeon.level?.mobPress(ch)
                    } else {
                        Dungeon.level?.press(i, ch)
                    }
                } else {
                    Dungeon.level?.press(i, null)
                }
                if (Dungeon.visible[i]) {
                    CellEmitter.get(i).start(Speck.factory(Speck.ROCK), 0.07f, 3 + (size - d))
                    if (Level.water[i]) {
                        GameScene.ripple(i)
                    }
                    if (shake < size - d) {
                        shake = size - d
                    }
                }
            }
            Camera.main?.shake(3f, 0.07f * (3 + shake))
        }
        if (Item.curUser?.isAlive == false) {
            Dungeon.fail(Utils.format(ResultDescriptions.WAND, name, Dungeon.depth))
            GLog.n("You killed yourself with your own Wand of Avalanche...")
        }
    }
    override fun fx(cell: Int, callback: Callback) {
        val user = Item.curUser ?: return
        val parent = user.sprite?.parent ?: return
        MagicMissile.earth(parent, user.pos, cell, callback)
        Sample.play(Assets.SND_ZAP)
    }
    override fun desc(): String {
        return "When a discharge of this wand hits a wall (or any other solid obstacle) it causes " +
                "an avalanche of stones, damaging and stunning all creatures in the affected area."
    }
}
