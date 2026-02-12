package com.watabou.pixeldungeon.ui
import com.watabou.noosa.BitmapTextMultiline
import com.watabou.noosa.ui.Component
import com.watabou.pixeldungeon.scenes.PixelScene
import com.watabou.pixeldungeon.sprites.CharSprite
import com.watabou.pixeldungeon.utils.GLog
import com.watabou.pixeldungeon.utils.Utils
import com.watabou.utils.Signal
import com.watabou.utils.Highlighter
import java.util.ArrayList
import java.util.regex.Pattern
class GameLog : Component(), Signal.Listener<String> {
    private var lastEntry: BitmapTextMultiline? = null
    private var lastColor: Int = 0
    init {
        GLog.update.add(this)
        recreateLines()
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
                    val toRemove = members[0]
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
        // length is protected property of Group
        for (i in length - 1 downTo 0) {
            val entry = members[i]
            if (entry is BitmapTextMultiline) {
                entry.maxWidth = width.toInt()
                entry.measure()
                entry.x = x
                entry.y = pos - entry.height()
                pos -= entry.height()
            }
        }
    }
    override fun destroy() {
        GLog.update.remove(this)
        super.destroy()
    }
    private class Entry(var text: String, var color: Int)
    companion object {
        private const val MAX_LINES = 3
        private val PUNCTUATION = Pattern.compile(".*[.,;?! ]$")
        private val entries = ArrayList<Entry>()
        fun wipe() {
            entries.clear()
        }
    }
}
