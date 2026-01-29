package com.watabou.utils
import java.util.regex.Pattern
class Highlighter(text: String) {
    var text: String
    var mask: BooleanArray
    init {
        val stripped = STRIPPER.matcher(text).replaceAll("")
        mask = BooleanArray(stripped.length)
        val m = HIGHLIGHTER.matcher(stripped)
        var pos = 0
        var lastMatch = 0
        while (m.find()) {
            pos += (m.start() - lastMatch)
            val groupLen = m.group(1)?.length ?: 0
            for (i in pos until pos + groupLen) {
                mask[i] = true
            }
            pos += groupLen
            lastMatch = m.end()
        }
        m.reset(text)
        val sb = StringBuffer()
        while (m.find()) {
            m.appendReplacement(sb, m.group(1) ?: "")
        }
        m.appendTail(sb)
        this.text = sb.toString()
    }
    fun inverted(): BooleanArray {
        val result = BooleanArray(mask.size)
        for (i in result.indices) {
            result[i] = !mask[i]
        }
        return result
    }
    fun isHighlighted(): Boolean {
        for (b in mask) {
            if (b) {
                return true
            }
        }
        return false
    }
    companion object {
        private val HIGHLIGHTER = Pattern.compile("_(.*?)_")
        private val STRIPPER = Pattern.compile("[ \n]")
    }
}
