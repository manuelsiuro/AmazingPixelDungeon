package com.watabou.pixeldungeon.ui
import com.watabou.noosa.BitmapTextMultiline
import com.watabou.noosa.ColorBlock
import com.watabou.noosa.Game
import com.watabou.noosa.ui.Component
import com.watabou.pixeldungeon.scenes.PixelScene
import com.watabou.pixeldungeon.sprites.CharSprite
import com.watabou.pixeldungeon.utils.GLog
import com.watabou.pixeldungeon.utils.Utils
import com.watabou.utils.Signal
import com.watabou.utils.Highlighter
import java.util.ArrayList
import java.util.regex.Pattern
import kotlin.math.max
import kotlin.math.min
class GameLog : Component(), Signal.Listener<String> {
    private var lastEntry: BitmapTextMultiline? = null
    private var lastColor: Int = 0
    private lateinit var bg: ColorBlock
    private var idleTime: Float = 0f
    init {
        GLog.update.add(this)
        recreateLines()
    }
    override fun createChildren() {
        bg = ColorBlock(1f, 1f, 0x000000)
        bg.visible = false
        addToBack(bg)
    }
    private fun recreateLines() {
        for (entry in entries) {
            val le = PixelScene.createMultiline(entry.text, 6f)
            lastEntry = le
            lastColor = entry.color
            le.hardlight(lastColor)
            add(le)
        }
    }
    fun newLine() {
        lastEntry = null
    }
    override fun onSignal(t: String?) {
        if (t == null) return
        idleTime = 0f
        var txt = t
        var color = CharSprite.DEFAULT
        if (txt.startsWith(GLog.POSITIVE)) {
            txt = txt.substring(GLog.POSITIVE.length)
            color = CharSprite.POSITIVE
        } else if (txt.startsWith(GLog.NEGATIVE)) {
            txt = txt.substring(GLog.NEGATIVE.length)
            color = CharSprite.NEGATIVE
        } else if (txt.startsWith(GLog.WARNING)) {
            txt = txt.substring(GLog.WARNING.length)
            color = CharSprite.WARNING
        } else if (txt.startsWith(GLog.HIGHLIGHT)) {
            txt = txt.substring(GLog.HIGHLIGHT.length)
            color = CharSprite.NEUTRAL
        }
        // Strip underscore emphasis markers — GameLog uses plain BitmapTextMultiline
        txt = Highlighter(txt).text

        txt = Utils.capitalize(txt) + if (PUNCTUATION.matcher(txt).matches()) "" else "."
        val le = lastEntry
        if (le != null && color == lastColor && le.nLines < MAX_LINES) {
            // Handle potential null from text()
            val lastMessage = le.text() ?: ""
            le.text(if (lastMessage.isEmpty()) txt else "$lastMessage $txt")
            le.measure()
            entries[entries.size - 1].text = le.text() ?: ""
        } else {
            val newEntry = PixelScene.createMultiline(txt, 6f)
            newEntry.hardlight(color)
            lastColor = color
            add(newEntry)
            lastEntry = newEntry
            entries.add(Entry(txt, color))
        }
        if (length > 0) {
            var nLines: Int
            do {
                nLines = 0
                for (i in 0 until length) {
                    val m = members[i]
                    if (m is BitmapTextMultiline) {
                        nLines += m.nLines
                    }
                }
                if (nLines > MAX_LINES) {
                    val toRemove = members.firstOrNull { it is BitmapTextMultiline }
                    if (toRemove != null) {
                        remove(toRemove)
                    }
                    if (entries.isNotEmpty()) {
                        entries.removeAt(0)
                    }
                }
            } while (nLines > MAX_LINES)
            if (entries.isEmpty()) {
                lastEntry = null
            }
        }
        layout()
    }
    override fun layout() {
        var pos = y
        val textEntries = mutableListOf<BitmapTextMultiline>()
        for (i in length - 1 downTo 0) {
            val entry = members[i]
            if (entry is BitmapTextMultiline) {
                entry.maxWidth = width.toInt()
                entry.measure()
                entry.x = x + PAD
                entry.y = pos - entry.height()
                pos -= entry.height()
                textEntries.add(0, entry)
            }
        }
        if (textEntries.isNotEmpty()) {
            val topY = textEntries.first().y
            bg.x = x
            bg.y = topY - PAD
            bg.size(width, y - topY + PAD * 2)
            bg.visible = true
        } else {
            bg.visible = false
        }
    }
    override fun update() {
        super.update()
        idleTime += Game.elapsed
        // Compute idle fade multiplier
        val multiplier = if (idleTime <= IDLE_DELAY) {
            1f
        } else {
            max(0f, 1f - (idleTime - IDLE_DELAY) / IDLE_FADE_DURATION)
        }
        // Collect text entries in display order (oldest first)
        val textEntries = mutableListOf<BitmapTextMultiline>()
        for (i in 0 until length) {
            val m = members[i]
            if (m is BitmapTextMultiline) {
                textEntries.add(m)
            }
        }
        val count = textEntries.size
        if (count > 0) {
            val lerpFactor = min(1f, Game.elapsed * FADE_SPEED)
            for ((index, entry) in textEntries.withIndex()) {
                // Oldest entry is index 0, newest is count-1
                val posAlpha = if (count == 1) 1f
                    else TEXT_MIN_ALPHA + (1f - TEXT_MIN_ALPHA) * index / (count - 1)
                val target = posAlpha * multiplier
                val current = entry.alpha()
                entry.alpha(current + (target - current) * lerpFactor)
            }
            // Lerp background alpha
            val bgTarget = BG_ALPHA * multiplier
            val bgCurrent = bg.alpha()
            bg.alpha(bgCurrent + (bgTarget - bgCurrent) * lerpFactor)
            bg.visible = multiplier > 0f
        } else {
            bg.visible = false
        }
    }
    override fun destroy() {
        GLog.update.remove(this)
        super.destroy()
    }
    private class Entry(var text: String, var color: Int)
    companion object {
        private const val MAX_LINES = 3
        private const val BG_ALPHA = 0.45f
        private const val TEXT_MIN_ALPHA = 0.4f
        private const val PAD = 3f
        private const val FADE_SPEED = 4f
        private const val IDLE_DELAY = 5f
        private const val IDLE_FADE_DURATION = 2f
        private val PUNCTUATION = Pattern.compile(".*[.,;?! ]$")
        private val entries = ArrayList<Entry>()
        fun wipe() {
            entries.clear()
        }
    }
}
