package com.watabou.pixeldungeon.levels
import com.watabou.utils.Random
object Patch {
    private var cur = BooleanArray(Level.LENGTH)
    private var off = BooleanArray(Level.LENGTH)
    fun generate(seed: Float, nGen: Int): BooleanArray {
        val w = Level.WIDTH
        val h = Level.HEIGHT
        for (i in 0 until Level.LENGTH) {
            off[i] = Random.Float() < seed
        }
        for (i in 0 until nGen) {
            for (y in 1 until h - 1) {
                for (x in 1 until w - 1) {
                    val pos = x + y * w
                    var count = 0
                    if (off[pos - w - 1]) {
                        count++
                    }
                    if (off[pos - w]) {
                        count++
                    }
                    if (off[pos - w + 1]) {
                        count++
                    }
                    if (off[pos - 1]) {
                        count++
                    }
                    if (off[pos + 1]) {
                        count++
                    }
                    if (off[pos + w - 1]) {
                        count++
                    }
                    if (off[pos + w]) {
                        count++
                    }
                    if (off[pos + w + 1]) {
                        count++
                    }
                    if (!off[pos] && count >= 5) {
                        cur[pos] = true
                    } else if (off[pos] && count >= 4) {
                        cur[pos] = true
                    } else {
                        cur[pos] = false
                    }
                }
            }
            val tmp = cur
            cur = off
            off = tmp
        }
        return off
    }
}
