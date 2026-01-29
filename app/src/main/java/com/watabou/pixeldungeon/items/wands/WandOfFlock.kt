package com.watabou.pixeldungeon.items.wands
import com.watabou.noosa.audio.Sample
import com.watabou.pixeldungeon.Assets
import com.watabou.pixeldungeon.Dungeon
import com.watabou.pixeldungeon.actors.Actor
import com.watabou.pixeldungeon.actors.Char
import com.watabou.pixeldungeon.actors.mobs.npcs.NPC
import com.watabou.pixeldungeon.effects.CellEmitter
import com.watabou.pixeldungeon.effects.MagicMissile
import com.watabou.pixeldungeon.effects.Speck
import com.watabou.pixeldungeon.items.Item
import com.watabou.pixeldungeon.levels.Level
import com.watabou.pixeldungeon.mechanics.Ballistica
import com.watabou.pixeldungeon.scenes.GameScene
import com.watabou.pixeldungeon.sprites.SheepSprite
import com.watabou.pixeldungeon.utils.BArray
import com.watabou.utils.Callback
import com.watabou.utils.PathFinder
import com.watabou.utils.Random
class WandOfFlock : Wand() {
    init {
        name = "Wand of Flock"
    }
    override fun onZap(cell: Int) {
        var c = cell
        val level = power()
        val n = level + 2
        if (Actor.findChar(c) != null && Ballistica.distance > 2) {
            c = Ballistica.trace[Ballistica.distance - 2]
        }
        val passable = BArray.or(Level.passable, Level.avoid, null)
        for (actor in Actor.all()) {
            if (actor is Char) {
                passable[actor.pos] = false
            }
        }
        PathFinder.buildDistanceMap(c, passable, n)
        var dist = 0
        val distance = PathFinder.distance ?: return
        if (Actor.findChar(c) != null) {
            distance[c] = Int.MAX_VALUE
            dist = 1
        }
        val lifespan = (level + 3).toFloat()
        var i = 0
        sheepLabel@ while (i < n) {
            do {
                for (j in 0 until Level.LENGTH) {
                    if (distance[j] == dist) {
                        val sheep = Sheep()
                        sheep.lifespan = lifespan
                        sheep.pos = j
                        GameScene.add(sheep)
                        Dungeon.level?.mobPress(sheep)
                        CellEmitter.get(j).burst(Speck.factory(Speck.WOOL), 4)
                        distance[j] = Int.MAX_VALUE
                        i++
                        continue@sheepLabel
                    }
                }
                dist++
            } while (dist < n)
            i++
        }
    }
    override fun fx(cell: Int, callback: Callback) {
        val user = Item.curUser ?: return
        val parent = user.sprite?.parent ?: return
        MagicMissile.wool(parent, user.pos, cell, callback)
        Sample.play(Assets.SND_ZAP)
    }
    override fun desc(): String {
        return "A flick of this wand summons a flock of magic sheep, creating temporary impenetrable obstacle."
    }
    class Sheep : NPC() {
        var lifespan: Float = 0f
        private var initialized = false
        init {
            name = "sheep"
            spriteClass = SheepSprite::class.java
        }
        override fun act(): Boolean {
            if (initialized) {
                HP = 0
                destroy()
                sprite?.die()
            } else {
                initialized = true
                spend(lifespan + Random.Float(2f))
            }
            return true
        }
        override fun damage(dmg: Int, src: Any?) {
        }
        override fun description(): String {
            return "This is a magic sheep. What's so magical about it? You can't kill it. " +
                    "It will stand there until it magcially fades away, all the while chewing cud with a blank stare."
        }
        override fun interact() {
            yell(Random.element(QUOTES))
        }
        companion object {
            private val QUOTES = arrayOf("Baa!", "Baa?", "Baa.", "Baa...")
        }
    }
}
