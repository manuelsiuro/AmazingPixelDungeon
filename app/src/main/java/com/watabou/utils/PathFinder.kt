package com.watabou.utils
import java.util.Arrays
import java.util.LinkedList
object PathFinder {
    var distance: IntArray? = null
    private var goals: BooleanArray? = null
    private var queue: IntArray? = null
    private var size = 0
    private var dir: IntArray? = null
    fun setMapSize(width: Int, height: Int) {
        val size = width * height
        if (PathFinder.size != size) {
            PathFinder.size = size
            distance = IntArray(size)
            goals = BooleanArray(size)
            queue = IntArray(size)
            dir = intArrayOf(-1, +1, -width, +width, -width - 1, -width + 1, +width - 1, +width + 1)
        }
    }
    fun find(from: Int, to: Int, passable: BooleanArray): Path? {
        if (!buildDistanceMap(from, to, passable)) {
            return null
        }
        val result = Path()
        var s = from
        // From the starting position we are moving downwards,
        // until we reach the ending point
        do {
            var minD = distance!![s]
            var mins = s
            for (i in dir!!.indices) {
                val n = s + dir!![i]
                val thisD = distance!![n]
                if (thisD < minD) {
                    minD = thisD
                    mins = n
                }
            }
            s = mins
            result.add(s)
        } while (s != to)
        return result
    }
    fun getStep(from: Int, to: Int, passable: BooleanArray): Int {
        if (!buildDistanceMap(from, to, passable)) {
            return -1
        }
        // From the starting position we are making one step downwards
        var minD = distance!![from]
        var best = from
        var step: Int
        var stepD: Int
        for (i in dir!!.indices) {
            step = from + dir!![i]
            stepD = distance!![step]
            if (stepD < minD) {
                minD = stepD
                best = step
            }
        }
        return best
    }
    fun getStepBack(cur: Int, from: Int, passable: BooleanArray): Int {
        val d = buildEscapeDistanceMap(cur, from, 2f, passable)
        for (i in 0 until size) {
            goals!![i] = distance!![i] == d
        }
        if (!buildDistanceMap(cur, goals!!, passable)) {
            return -1
        }
        val s = cur
        // From the starting position we are making one step downwards
        var minD = distance!![s]
        var mins = s
        for (i in dir!!.indices) {
            val n = s + dir!![i]
            val thisD = distance!![n]
            if (thisD < minD) {
                minD = thisD
                mins = n
            }
        }
        return mins
    }
    private fun buildDistanceMap(from: Int, to: Int, passable: BooleanArray): Boolean {
        if (from == to) {
            return false
        }
        val dist = distance ?: return false
        Arrays.fill(dist, Integer.MAX_VALUE)
        var pathFound = false
        var head = 0
        var tail = 0
        // Add to queue
        queue!![tail++] = to
        distance!![to] = 0
        while (head < tail) {
            // Remove from queue
            val step = queue!![head++]
            if (step == from) {
                pathFound = true
                break
            }
            val nextDistance = distance!![step] + 1
            for (i in dir!!.indices) {
                val n = step + dir!![i]
                if (n == from || (n >= 0 && n < size && passable[n] && (distance!![n] > nextDistance))) {
                    // Add to queue
                    queue!![tail++] = n
                    distance!![n] = nextDistance
                }
            }
        }
        return pathFound
    }
    fun buildDistanceMap(to: Int, passable: BooleanArray, limit: Int) {
        Arrays.fill(distance!!, Integer.MAX_VALUE)
        var head = 0
        var tail = 0
        // Add to queue
        queue!![tail++] = to
        distance!![to] = 0
        while (head < tail) {
            // Remove from queue
            val step = queue!![head++]
            val nextDistance = distance!![step] + 1
            if (nextDistance > limit) {
                return
            }
            for (i in dir!!.indices) {
                val n = step + dir!![i]
                if (n >= 0 && n < size && passable[n] && (distance!![n] > nextDistance)) {
                    // Add to queue
                    queue!![tail++] = n
                    distance!![n] = nextDistance
                }
            }
        }
    }
    private fun buildDistanceMap(from: Int, to: BooleanArray, passable: BooleanArray): Boolean {
        if (to[from]) {
            return false
        }
        Arrays.fill(distance!!, Integer.MAX_VALUE)
        var pathFound = false
        var head = 0
        var tail = 0
        // Add to queue
        for (i in 0 until size) {
            if (to[i]) {
                queue!![tail++] = i
                distance!![i] = 0
            }
        }
        while (head < tail) {
            // Remove from queue
            val step = queue!![head++]
            if (step == from) {
                pathFound = true
                break
            }
            val nextDistance = distance!![step] + 1
            for (i in dir!!.indices) {
                val n = step + dir!![i]
                if (n == from || (n >= 0 && n < size && passable[n] && (distance!![n] > nextDistance))) {
                    // Add to queue
                    queue!![tail++] = n
                    distance!![n] = nextDistance
                }
            }
        }
        return pathFound
    }
    private fun buildEscapeDistanceMap(cur: Int, from: Int, factor: Float, passable: BooleanArray): Int {
        Arrays.fill(distance!!, Integer.MAX_VALUE)
        var destDist = Integer.MAX_VALUE
        var head = 0
        var tail = 0
        // Add to queue
        queue!![tail++] = from
        distance!![from] = 0
        var dist = 0
        while (head < tail) {
            // Remove from queue
            val step = queue!![head++]
            dist = distance!![step]
            if (dist > destDist) {
                return destDist
            }
            if (step == cur) {
                destDist = (dist * factor).toInt() + 1
            }
            val nextDistance = dist + 1
            for (i in dir!!.indices) {
                val n = step + dir!![i]
                if (n >= 0 && n < size && passable[n] && distance!![n] > nextDistance) {
                    // Add to queue
                    queue!![tail++] = n
                    distance!![n] = nextDistance
                }
            }
        }
        return dist
    }
    class Path : LinkedList<Int>()
}
