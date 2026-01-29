package com.watabou.pixeldungeon.actors
import android.util.SparseArray
import com.watabou.pixeldungeon.Dungeon
import com.watabou.pixeldungeon.Statistics
import com.watabou.pixeldungeon.actors.blobs.Blob
import com.watabou.pixeldungeon.actors.buffs.Buff
import com.watabou.pixeldungeon.actors.mobs.Mob
import com.watabou.pixeldungeon.levels.Level
import com.watabou.utils.Bundlable
import com.watabou.utils.Bundle
import java.util.*
abstract class Actor : Bundlable {
    private var time: Float = 0.0f
    private var id = 0
    protected abstract fun act(): Boolean
    open fun spend(time: Float) {
        this.time += time
    }
    open fun postpone(time: Float) {
        if (this.time < now + time) {
            this.time = now + time
        }
    }
    protected fun cooldown(): Float {
        return time - now
    }
    open fun diactivate() {
        time = Float.MAX_VALUE
    }
    protected open fun onAdd() {}
    protected open fun onRemove() {}
    override fun storeInBundle(bundle: Bundle) {
        bundle.put(TAG_TIME, time)
        bundle.put(TAG_ID, id)
    }
    override fun restoreFromBundle(bundle: Bundle) {
        time = bundle.getFloat(TAG_TIME)
        id = bundle.getInt(TAG_ID)
    }
    fun id(): Int {
        return if (id > 0) {
            id
        } else {
            var max = 0
            for (a in all) {
                if (a.id > max) {
                    max = a.id
                }
            }
            id = max + 1
            id
        }
    }
    open fun next() {
        if (current == this) {
            current = null
        }
    }
    companion object {
        const val TICK = 1f
        private const val TAG_TIME = "time"
        private const val TAG_ID = "id"
        var all = HashSet<Actor>()
        var current: Actor? = null
        var ids = SparseArray<Actor>()
        var now = 0f
        var chars = arrayOfNulls<Char>(Level.LENGTH)
        fun clear() {
            now = 0f
            chars.fill(null)
            all.clear()
            ids.clear()
        }
        fun fixTime() {
            if (Dungeon.hero != null && all.contains(Dungeon.hero as Actor)) {
                Statistics.duration += now.toLong()
            }
            var min = Float.MAX_VALUE
            for (a in all) {
                if (a.time < min) {
                    min = a.time
                }
            }
            for (a in all) {
                a.time -= min
            }
            now = 0f
        }
        fun init() {
            val currentHero = Dungeon.hero ?: return
            addDelayed(currentHero, -Float.MIN_VALUE)
            val currentLevel = Dungeon.level ?: return
            for (mob in currentLevel.mobs) {
                add(mob)
            }
            for (blob in currentLevel.blobs.values) {
                add(blob)
            }
            current = null
        }
        fun occupyCell(ch: Char) {
            chars[ch.pos] = ch
        }
        fun freeCell(pos: Int) {
            chars[pos] = null
        }
        fun process() {
            if (current != null) {
                return
            }
            var doNext: Boolean
            do {
                now = Float.MAX_VALUE
                current = null
                chars.fill(null)
                for (actor in all) {
                    if (actor.time < now) {
                        now = actor.time
                        current = actor
                    }
                    if (actor is Char) {
                        chars[actor.pos] = actor
                    }
                }
                val curr = current
                if (curr != null) {
                    if (curr is Char && curr.sprite?.isMoving == true) {
                        // If it's character's turn to act, but its sprite 
                        // is moving, wait till the movement is over
                        current = null
                        break
                    }
                    doNext = curr.act()
                    val hero = Dungeon.hero
                    if (doNext && (hero == null || !hero.isAlive)) {
                        doNext = false
                        current = null
                    }
                } else {
                    doNext = false
                }
            } while (doNext)
        }
        fun add(actor: Actor) {
            add(actor, now)
        }
        fun addDelayed(actor: Actor, delay: Float) {
            add(actor, now + delay)
        }
        private fun add(actor: Actor, time: Float) {
            if (all.contains(actor)) {
                return
            }
            if (actor.id > 0) {
                ids.put(actor.id, actor)
            }
            all.add(actor)
            actor.time += time
            actor.onAdd()
            if (actor is Char) {
                chars[actor.pos] = actor
                for (buff in actor.buffs()) {
                    all.add(buff)
                    buff.onAdd()
                }
            }
        }
        fun remove(actor: Actor?) {
            if (actor != null) {
                all.remove(actor)
                actor.onRemove()
                if (actor.id > 0) {
                    ids.remove(actor.id)
                }
            }
        }
        fun findChar(pos: Int): Char? {
            return chars[pos]
        }
        fun findById(id: Int): Actor? {
            return ids.get(id)
        }
        fun all(): HashSet<Actor> {
            return all
        }
    }
}
