package com.watabou.pixeldungeon.actors.blobs
import com.watabou.pixeldungeon.Dungeon
import com.watabou.pixeldungeon.PixelDungeon
import com.watabou.pixeldungeon.actors.Actor
import com.watabou.pixeldungeon.effects.BlobEmitter
import com.watabou.pixeldungeon.levels.Level
import com.watabou.pixeldungeon.utils.BArray
import com.watabou.utils.Bundle
import java.util.Arrays
open class Blob : Actor() {
    var volume = 0
    var cur: IntArray
    protected var off: IntArray
    var emitter: BlobEmitter? = null
    init {
        cur = IntArray(LENGTH)
        off = IntArray(LENGTH)
        volume = 0
    }
    override fun storeInBundle(bundle: Bundle) {
        super.storeInBundle(bundle)
        if (volume > 0) {
            var start = 0
            while (start < LENGTH) {
                if (cur[start] > 0) {
                    break
                }
                start++
            }
            var end = LENGTH - 1
            while (end > start) {
                if (cur[end] > 0) {
                    break
                }
                end--
            }
            bundle.put(START, start)
            bundle.put(CUR, trim(start, end + 1))
        }
    }
    private fun trim(start: Int, end: Int): IntArray {
        val len = end - start
        val copy = IntArray(len)
        System.arraycopy(cur, start, copy, 0, len)
        return copy
    }
    override fun restoreFromBundle(bundle: Bundle) {
        super.restoreFromBundle(bundle)
        val data = bundle.getIntArray(CUR)
        if (data != null) {
            val start = bundle.getInt(START)
            for (i in data.indices) {
                cur[i + start] = data[i]
                volume += data[i]
            }
        }
        if (Level.resizingNeeded) {
            val cur = IntArray(Level.LENGTH)
            Arrays.fill(cur, 0)
            val loadedMapSize = Level.loadedMapSize
            for (i in 0 until loadedMapSize) {
                System.arraycopy(this.cur, i * loadedMapSize, cur, i * Level.WIDTH, loadedMapSize)
            }
            this.cur = cur
        }
    }
    override fun act(): Boolean {
        spend(TICK)
        if (volume > 0) {
            volume = 0
            evolve()
            val tmp = off
            off = cur
            cur = tmp
        }
        return true
    }
    open fun use(emitter: BlobEmitter) {
        this.emitter = emitter
    }
    protected open fun evolve() {
        val notBlocking = BArray.not(Level.solid, null)
        for (i in 1 until HEIGHT - 1) {
            val from = i * WIDTH + 1
            val to = from + WIDTH - 2
            for (pos in from until to) {
                if (notBlocking[pos]) {
                    var count = 1
                    var sum = cur[pos]
                    if (notBlocking[pos - 1]) {
                        sum += cur[pos - 1]
                        count++
                    }
                    if (notBlocking[pos + 1]) {
                        sum += cur[pos + 1]
                        count++
                    }
                    if (notBlocking[pos - WIDTH]) {
                        sum += cur[pos - WIDTH]
                        count++
                    }
                    if (notBlocking[pos + WIDTH]) {
                        sum += cur[pos + WIDTH]
                        count++
                    }
                    val value = if (sum >= count) (sum / count) -1 else 0
                    off[pos] = value
                    volume += value
                } else {
                    off[pos] = 0
                }
            }
        }
    }
    open fun seed(cell: Int, amount: Int) {
        cur[cell] += amount
        volume += amount
    }
    fun clear(cell: Int) {
        volume -= cur[cell]
        cur[cell] = 0
    }
    open fun tileDesc(): String? {
        return null
    }
    companion object {
        const val WIDTH = Level.WIDTH
        const val HEIGHT = Level.HEIGHT
        const val LENGTH = Level.LENGTH
        private const val CUR = "cur"
        private const val START = "start"
        @Suppress("UNCHECKED_CAST")
        fun <T : Blob> seed(cell: Int, amount: Int, type: Class<T>): T? {
            return try {
                val currentLevel = Dungeon.level ?: return null
                var gas = currentLevel.blobs[type] as T?
                if (gas == null) {
                    gas = type.getDeclaredConstructor().newInstance()
                    currentLevel.blobs[type] = gas
                }
                gas?.seed(cell, amount)
                gas
            } catch (e: Exception) {
                PixelDungeon.reportException(e)
                null
            }
        }
    }
}
