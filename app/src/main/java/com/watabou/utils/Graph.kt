package com.watabou.utils
import java.util.ArrayList
import java.util.LinkedList
object Graph {
    fun <T : Node> setPrice(nodes: List<T>, value: Int) {
        for (node in nodes) {
            node.price(value)
        }
    }
    fun <T : Node> buildDistanceMap(nodes: Collection<T>, focus: Node) {
        for (node in nodes) {
            node.distance(Integer.MAX_VALUE)
        }
        val queue = LinkedList<Node>()
        focus.distance(0)
        queue.add(focus)
        while (!queue.isEmpty()) {
            val node = queue.poll() ?: continue
            val distance = node.distance()
            val price = node.price()
            for (edge in node.edges()) {
                if (edge.distance() > distance + price) {
                    queue.add(edge)
                    edge.distance(distance + price)
                }
            }
        }
    }
    @Suppress("UNCHECKED_CAST", "UNUSED_PARAMETER")
    fun <T : Node> buildPath(nodes: Collection<T>, from: T, to: T): List<T>? {
        val path = ArrayList<T>()
        var room: T = from
        while (room !== to) {
            var min = room.distance()
            var next: T? = null
            val edges = room.edges()
            for (edge in edges) {
                val distance = edge.distance()
                if (distance < min) {
                    min = distance
                    next = edge as T
                }
            }
            if (next == null) {
                return null
            }
            path.add(next)
            room = next
        }
        return path
    }
    interface Node {
        fun distance(): Int
        fun distance(value: Int)
        fun price(): Int
        fun price(value: Int)
        fun edges(): Collection<@JvmWildcard Node>
    }
}
