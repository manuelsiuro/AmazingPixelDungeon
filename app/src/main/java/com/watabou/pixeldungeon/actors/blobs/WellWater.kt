package com.watabou.pixeldungeon.actors.blobs
import com.watabou.pixeldungeon.Dungeon
import com.watabou.pixeldungeon.Journal
import com.watabou.pixeldungeon.Journal.Feature
import com.watabou.pixeldungeon.actors.hero.Hero
import com.watabou.pixeldungeon.items.Heap
import com.watabou.pixeldungeon.items.Item
import com.watabou.pixeldungeon.levels.Level
import com.watabou.pixeldungeon.levels.Terrain
import com.watabou.pixeldungeon.scenes.GameScene
import com.watabou.utils.Bundle
import com.watabou.utils.Random
open class WellWater : Blob() {
    protected var pos: Int = 0
    override fun restoreFromBundle(bundle: Bundle) {
        super.restoreFromBundle(bundle)
        for (i in 0 until LENGTH) {
            if (cur[i] > 0) {
                pos = i
                break
            }
        }
    }
    override fun evolve() {
        volume = cur[pos].also { off[pos] = it }
        if (Dungeon.visible[pos]) {
            when (this) {
                is WaterOfAwareness -> Journal.add(Feature.WELL_OF_AWARENESS)
                is WaterOfHealth -> Journal.add(Feature.WELL_OF_HEALTH)
                is WaterOfTransmutation -> Journal.add(Feature.WELL_OF_TRANSMUTATION)
            }
        }
    }
    protected open fun affect(): Boolean {
        var heap: Heap?
        if (pos == Dungeon.hero!!.pos && affectHero(Dungeon.hero!!)) {
            volume = 0
            off[pos] = 0
            cur[pos] = 0
            return true
        } else {
            heap = Dungeon.level!!.heaps.get(pos)
            if (heap != null) {
                val oldItem = heap.peek()
                if (oldItem != null) {
                    val newItem = affectItem(oldItem)
                    if (newItem != null) {
                        if (newItem === oldItem) {
                            // Nothing to do
                        } else if (oldItem.quantity() > 1) {
                            oldItem.quantity(oldItem.quantity() - 1)
                            heap.drop(newItem)
                        } else {
                            heap.replace(oldItem, newItem)
                        }
                        heap.sprite?.link()
                        volume = 0
                        off[pos] = 0
                        cur[pos] = 0
                        return true
                    } else {
                        var newPlace: Int
                        do {
                            newPlace = pos + Level.NEIGHBOURS8[Random.Int(8)]
                        } while (!Level.passable[newPlace] && !Level.avoid[newPlace])
                        Dungeon.level!!.drop(heap.pickUp(), newPlace).sprite?.drop(pos)
                        return false
                    }
                } else {
                    return false
                }
            } else {
                return false
            }
        }
    }
    protected open fun affectHero(hero: Hero): Boolean {
        return false
    }
    protected open fun affectItem(item: Item): Item? {
        return null
    }
    override fun seed(cell: Int, amount: Int) {
        cur[pos] = 0
        pos = cell
        volume = amount
        cur[pos] = amount
    }
    companion object {
        fun affectCell(cell: Int) {
            val waters = arrayOf(
                WaterOfHealth::class.java,
                WaterOfAwareness::class.java,
                WaterOfTransmutation::class.java
            )
            for (waterClass in waters) {
                val water = Dungeon.level!!.blobs[waterClass] as WellWater?
                if (water != null &&
                    water.volume > 0 &&
                    water.pos == cell &&
                    water.affect()
                ) {
                    Level.set(cell, Terrain.EMPTY_WELL)
                    GameScene.updateMap(cell)
                    return
                }
            }
        }
    }
}
