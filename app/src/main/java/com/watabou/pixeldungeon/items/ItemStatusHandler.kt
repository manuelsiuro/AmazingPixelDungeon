package com.watabou.pixeldungeon.items
import com.watabou.utils.Bundle
import com.watabou.utils.Random
import java.util.ArrayList
import java.util.Arrays
import java.util.HashMap
import java.util.HashSet
class ItemStatusHandler<T : Item> {
    private val items: Array<Class<out T>>
    private val images: HashMap<Class<out T>, Int>
    private val labels: HashMap<Class<out T>, String>
    private val known: HashSet<Class<out T>>
    constructor(items: Array<Class<out T>>, allLabels: Array<String>, allImages: Array<Int>) {
        this.items = items
        this.images = HashMap()
        this.labels = HashMap()
        known = HashSet()
        val labelsLeft = ArrayList(Arrays.asList(*allLabels))
        val imagesLeft = ArrayList(Arrays.asList(*allImages))
        for (i in items.indices) {
            val item = items[i]
            val index = Random.Int(labelsLeft.size)
            labels[item] = labelsLeft[index]
            labelsLeft.removeAt(index)
            images[item] = imagesLeft[index]
            imagesLeft.removeAt(index)
        }
    }
    constructor(items: Array<Class<out T>>, labels: Array<String>, images: Array<Int>, bundle: Bundle) {
        this.items = items
        this.images = HashMap()
        this.labels = HashMap()
        known = HashSet()
        restore(bundle, labels, images)
    }
    fun save(bundle: Bundle) {
        for (i in items.indices) {
            val itemName = items[i].toString()
            images[items[i]]?.let { bundle.put(itemName + PFX_IMAGE, it) }
            labels[items[i]]?.let { bundle.put(itemName + PFX_LABEL, it) }
            bundle.put(itemName + PFX_KNOWN, known.contains(items[i]))
        }
    }
    private fun restore(bundle: Bundle, allLabels: Array<String>, allImages: Array<Int>) {
        val labelsLeft = ArrayList(Arrays.asList(*allLabels))
        val imagesLeft = ArrayList(Arrays.asList(*allImages))
        for (i in items.indices) {
            val item = items[i]
            val itemName = item.toString()
            if (bundle.contains(itemName + PFX_LABEL)) {
                val label = bundle.getString(itemName + PFX_LABEL)
                labels[item] = label
                labelsLeft.remove(label)
                val image = bundle.getInt(itemName + PFX_IMAGE)
                images[item] = image
                imagesLeft.remove(image)
                if (bundle.getBoolean(itemName + PFX_KNOWN)) {
                    known.add(item)
                }
            } else {
                val index = Random.Int(labelsLeft.size)
                labels[item] = labelsLeft[index]
                labelsLeft.removeAt(index)
                images[item] = imagesLeft[index]
                imagesLeft.removeAt(index)
            }
        }
    }
    fun image(item: T): Int {
        return images[item.javaClass] ?: 0
    }
    fun label(item: T): String? {
        return labels[item.javaClass]
    }
    fun isKnown(item: T): Boolean {
        return known.contains(item.javaClass)
    }
    fun know(item: T) {
        known.add(item.javaClass)
        if (known.size == items.size - 1) {
            for (i in items.indices) {
                if (!known.contains(items[i])) {
                    known.add(items[i])
                    break
                }
            }
        }
    }
    fun known(): HashSet<Class<out T>> {
        return known
    }
    fun unknown(): HashSet<Class<out T>> {
        val result = HashSet<Class<out T>>()
        for (i in items) {
            if (!known.contains(i)) {
                result.add(i)
            }
        }
        return result
    }
    companion object {
        private const val PFX_IMAGE = "_image"
        private const val PFX_LABEL = "_label"
        private const val PFX_KNOWN = "_known"
    }
}
