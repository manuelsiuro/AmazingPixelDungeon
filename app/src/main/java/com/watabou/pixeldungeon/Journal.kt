package com.watabou.pixeldungeon
import com.watabou.utils.Bundle
import com.watabou.utils.Bundlable
import java.util.ArrayList
object Journal {
    enum class Feature(val desc: String) {
        WELL_OF_HEALTH("Well of Health"),
        WELL_OF_AWARENESS("Well of Awareness"),
        WELL_OF_TRANSMUTATION("Well of Transmutation"),
        SACRIFICIAL_FIRE("Sacrificial chamber"),
        ALCHEMY("Alchemy pot"),
        GARDEN("Garden"),
        STATUE("Animated statue"),
        GHOST("Sad ghost"),
        WANDMAKER("Old wandmaker"),
        TROLL("Troll blacksmith"),
        IMP("Ambitious imp")
    }
    class Record : Comparable<Record>, Bundlable {
        var feature: Feature? = null
        var depth: Int = 0
        constructor()
        constructor(feature: Feature, depth: Int) {
            this.feature = feature
            this.depth = depth
        }
        override fun compareTo(other: Record): Int {
            return other.depth - depth
        }
        override fun restoreFromBundle(bundle: Bundle) {
            feature = Feature.valueOf(bundle.getString(FEATURE))
            depth = bundle.getInt(DEPTH)
        }
        override fun storeInBundle(bundle: Bundle) {
            bundle.put(FEATURE, feature.toString())
            bundle.put(DEPTH, depth)
        }
        companion object {
            private const val FEATURE = "feature"
            private const val DEPTH = "depth"
        }
    }
    var records: ArrayList<Record> = ArrayList()
    fun reset() {
        records = ArrayList()
    }
    private const val JOURNAL = "journal"
    fun storeInBundle(bundle: Bundle) {
        bundle.put(JOURNAL, records)
    }
    fun restoreFromBundle(bundle: Bundle) {
        records = ArrayList()
        for (rec in bundle.getCollection(JOURNAL)) {
            records.add(rec as Record)
        }
    }
    fun add(feature: Feature) {
        val size = records.size
        for (i in 0 until size) {
            val rec = records[i]
            if (rec.feature == feature && rec.depth == Dungeon.depth) {
                return
            }
        }
        records.add(Record(feature, Dungeon.depth))
    }
    fun remove(feature: Feature) {
        val size = records.size
        for (i in 0 until size) {
            val rec = records[i]
            if (rec.feature == feature && rec.depth == Dungeon.depth) {
                records.removeAt(i)
                return
            }
        }
    }
}
