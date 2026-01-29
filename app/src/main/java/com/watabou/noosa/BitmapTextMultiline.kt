package com.watabou.noosa
import com.watabou.glwrap.Quad
import com.watabou.utils.PointF
import java.util.ArrayList
import java.util.regex.Pattern
class BitmapTextMultiline : BitmapText {
    var maxWidth: Int = Int.MAX_VALUE
    protected var spaceSize: Float = 0f
    var nLines: Int = 0
    var mask: BooleanArray? = null
    constructor(font: Font) : this("", font)
    constructor(text: String, font: Font) : super(text, font) {
        // Warning: font might be null if called from super constructor before initialization? No.
        // But get(' ') might return null if font doesn't have space.
        val spaceRect = font.get(' ')
        spaceSize = if (spaceRect != null) font.width(spaceRect) else 0f
    }
    override fun updateVertices() {
        if (text == null) {
            text = ""
        }
        quads = Quad.createSet(text!!.length)
        realLength = 0
        // This object controls lines breaking
        val writer = SymbolWriter()
        // Word size
        val metrics = PointF()
        val paragraphs = PARAGRAPH.split(text!!)
        // Current character (used in masking)
        var pos = 0
        for (i in paragraphs.indices) {
            val words = WORD.split(paragraphs[i])
            for (j in words.indices) {
                val word = words[j]
                if (word.isEmpty()) {
                    // This case is possible when there are
                    // several spaces coming along
                    continue
                }
                getWordMetrics(word, metrics)
                writer.addSymbol(metrics.x, metrics.y)
                val length = word.length
                var shift = 0f    // Position in pixels relative to the beginning of the word
                for (k in 0 until length) {
                    val rect = font!![word[k]]
                    if (rect != null) {
                        val w = font!!.width(rect)
                        val h = font!!.height(rect)
                        if (mask == null || mask!![pos]) {
                            vertices!![0] = writer.x + shift
                            vertices!![1] = writer.y
                            vertices!![2] = rect.left
                            vertices!![3] = rect.top
                            vertices!![4] = writer.x + shift + w
                            vertices!![5] = writer.y
                            vertices!![6] = rect.right
                            vertices!![7] = rect.top
                            vertices!![8] = writer.x + shift + w
                            vertices!![9] = writer.y + h
                            vertices!![10] = rect.right
                            vertices!![11] = rect.bottom
                            vertices!![12] = writer.x + shift
                            vertices!![13] = writer.y + h
                            vertices!![14] = rect.left
                            vertices!![15] = rect.bottom
                            quads!!.put(vertices)
                            realLength++
                        }
                        shift += w + font!!.tracking
                        pos++
                    }
                }
                writer.addSpace(spaceSize)
            }
            writer.newLine(0f, font!!.lineHeight)
        }
        nLines = writer.nLines()
        dirty = false
    }
    private fun getWordMetrics(word: String, metrics: PointF) {
        var w = 0f
        var h = 0f
        val length = word.length
        for (i in 0 until length) {
            val rect = font!![word[i]]
            if (rect != null) {
                w += font!!.width(rect) + if (w > 0) font!!.tracking else 0f
                h = Math.max(h, font!!.height(rect))
            }
        }
        metrics.set(w, h)
    }
    override fun measure() {
        val writer = SymbolWriter()
        val metrics = PointF()
        if (text == null) text = ""
        val paragraphs = PARAGRAPH.split(text!!)
        for (i in paragraphs.indices) {
            val words = WORD.split(paragraphs[i])
            for (j in words.indices) {
                if (j > 0) {
                    writer.addSpace(spaceSize)
                }
                val word = words[j]
                if (word.isEmpty()) {
                    continue
                }
                getWordMetrics(word, metrics)
                writer.addSymbol(metrics.x, metrics.y)
            }
            writer.newLine(0f, font!!.lineHeight)
        }
        width = writer.width
        height = writer.height
        nLines = writer.nLines()
    }
    override fun baseLine(): Float {
        return (height - font!!.lineHeight + font!!.baseLine) * scale.y
    }
    private inner class SymbolWriter {
        var width = 0f
        var height = 0f
        var nLines = 0
        var lineWidth = 0f
        var lineHeight = 0f
        var x = 0f
        var y = 0f
        fun addSymbol(w: Float, h: Float) {
            if (lineWidth > 0 && lineWidth + font!!.tracking + w > maxWidth / scale.x) {
                newLine(w, h)
            } else {
                x = lineWidth
                lineWidth += (if (lineWidth > 0) font!!.tracking else 0f) + w
                if (h > lineHeight) {
                    lineHeight = h
                }
            }
        }
        fun addSpace(w: Float) {
            if (lineWidth > 0 && lineWidth + font!!.tracking + w > maxWidth / scale.x) {
                newLine(0f, 0f)
            } else {
                x = lineWidth
                lineWidth += (if (lineWidth > 0) font!!.tracking else 0f) + w
            }
        }
        fun newLine(w: Float, h: Float) {
            height += lineHeight
            if (width < lineWidth) {
                width = lineWidth
            }
            lineWidth = w
            lineHeight = h
            x = 0f
            y = height
            nLines++
        }
        fun nLines(): Int {
            return if (x == 0f) nLines else nLines + 1
        }
    }
    inner class LineSplitter {
        private var lines: ArrayList<BitmapText>? = null
        private var curLine: StringBuilder? = null
        private var curLineWidth: Float = 0f
        private val metrics = PointF()
        private fun newLine(str: String, width: Float) {
            val txt = BitmapText(curLine!!.toString(), font)
            txt.scale.set(scale.x)
            lines!!.add(txt)
            curLine = StringBuilder(str)
            curLineWidth = width
        }
        private fun append(str: String, width: Float) {
            curLineWidth += (if (curLineWidth > 0) font!!.tracking else 0f) + width
            curLine!!.append(str)
        }
        fun split(): ArrayList<BitmapText> {
            lines = ArrayList()
            curLine = StringBuilder()
            curLineWidth = 0f
            if (text == null) text = ""
            val paragraphs = PARAGRAPH.split(text!!)
            for (i in paragraphs.indices) {
                val words = WORD.split(paragraphs[i])
                for (j in words.indices) {
                    val word = words[j]
                    if (word.isEmpty()) {
                        continue
                    }
                    getWordMetrics(word, metrics)
                    if (curLineWidth > 0 && curLineWidth + font!!.tracking + metrics.x > maxWidth / scale.x) {
                        newLine(word, metrics.x)
                    } else {
                        append(word, metrics.x)
                    }
                    if (curLineWidth > 0 && curLineWidth + font!!.tracking + spaceSize > maxWidth / scale.x) {
                        newLine("", 0f)
                    } else {
                        append(" ", spaceSize)
                    }
                }
                newLine("", 0f)
            }
            return lines!!
        }
    }
    companion object {
        protected val PARAGRAPH: Pattern = Pattern.compile("\n")
        protected val WORD: Pattern = Pattern.compile("\\s+")
    }
}
