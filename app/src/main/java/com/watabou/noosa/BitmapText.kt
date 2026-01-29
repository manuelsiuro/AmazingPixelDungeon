package com.watabou.noosa
import android.graphics.Bitmap
import android.graphics.RectF
import com.watabou.gltextures.SmartTexture
import com.watabou.gltextures.TextureCache
import com.watabou.glwrap.Matrix
import com.watabou.glwrap.Quad
import java.nio.FloatBuffer
open class BitmapText : Visual {
    protected var text: String? = null
    var font: Font? = null
    protected var vertices: FloatArray? = null
    protected var quads: FloatBuffer? = null
    var realLength: Int = 0
    protected var dirty: Boolean = true
    constructor() : this("", null)
    constructor(font: Font?) : this("", font)
    constructor(text: String, font: Font?) : super(0f, 0f, 0f, 0f) {
        this.text = text
        this.font = font
        vertices = FloatArray(16)
    }
    override fun destroy() {
        text = null
        font = null
        vertices = null
        quads = null
        super.destroy()
    }
    override fun updateMatrix() {
        // "origin" field is ignored
        Matrix.setIdentity(matrix)
        Matrix.translate(matrix, x, y)
        Matrix.translate(matrix, origin.x, origin.y)
        Matrix.scale(matrix, scale.x, scale.y)
        Matrix.rotate(matrix, angle)
        Matrix.translate(matrix, -origin.x, -origin.y)
        // Note: Original Java code had:
        // Matrix.setIdentity( matrix );
        // Matrix.translate( matrix, x, y );
        // Matrix.scale( matrix, scale.x, scale.y );
        // Matrix.rotate( matrix, angle );
        // But Visual.updateMatrix handles origin.
        // The original code explicitly commented "origin" field is ignored.
        // So I should stick to the original logic if possible, or adapt.
        // Let's stick to original logic for safety.
        Matrix.setIdentity(matrix)
        Matrix.translate(matrix, x, y)
        Matrix.scale(matrix, scale.x, scale.y)
        Matrix.rotate(matrix, angle)
    }
    override fun draw() {
        super.draw()
        val script = NoosaScript.get()
        font!!.texture.bind()
        if (dirty) {
            updateVertices()
        }
        script.camera(camera())
        script.uModel.valueM4(matrix)
        script.lighting(
            rm, gm, bm, am,
            ra, ga, ba, aa
        )
        script.drawQuadSet(quads!!, realLength)
    }
    open fun updateVertices() {
        width = 0f
        height = 0f
        if (text == null) {
            text = ""
        }
        quads = Quad.createSet(text!!.length)
        realLength = 0
        val length = text!!.length
        for (i in 0 until length) {
            val rect = font!![text!![i]]
            if (rect != null) {
                val w = font!!.width(rect)
                val h = font!!.height(rect)
                vertices!![0] = width
                vertices!![1] = 0f
                vertices!![2] = rect.left
                vertices!![3] = rect.top
                vertices!![4] = width + w
                vertices!![5] = 0f
                vertices!![6] = rect.right
                vertices!![7] = rect.top
                vertices!![8] = width + w
                vertices!![9] = h
                vertices!![10] = rect.right
                vertices!![11] = rect.bottom
                vertices!![12] = width
                vertices!![13] = h
                vertices!![14] = rect.left
                vertices!![15] = rect.bottom
                quads!!.put(vertices)
                realLength++
                width += w + font!!.tracking
                if (h > height) {
                    height = h
                }
            }
        }
        if (length > 0) {
            width -= font!!.tracking
        }
        dirty = false
    }
    open fun measure() {
        width = 0f
        height = 0f
        if (text == null) {
            text = ""
        }
        val length = text!!.length
        for (i in 0 until length) {
            val rect = font!![text!![i]]
            if (rect != null) {
                val w = font!!.width(rect)
                val h = font!!.height(rect)
                width += w + font!!.tracking
                if (h > height) {
                    height = h
                }
            }
        }
        if (length > 0) {
            width -= font!!.tracking
        }
    }
    open fun baseLine(): Float {
        return font!!.baseLine * scale.y
    }
    fun font(): Font? {
        return font
    }
    fun font(value: Font?) {
        font = value
    }
    fun text(): String? {
        return text
    }
    fun text(str: String?) {
        text = str
        dirty = true
    }
    open class Font : TextureFilm {
        var texture: SmartTexture
        var tracking: Float = 0f
        var baseLine: Float = 0f
        var autoUppercase: Boolean = false
        var lineHeight: Float = 0f
        protected constructor(tx: SmartTexture) : super(tx) {
            texture = tx
        }
        constructor(tx: SmartTexture, width: Int, chars: String) : this(tx, width, tx.height, chars)
        constructor(tx: SmartTexture, width: Int, height: Int, chars: String) : super(tx) {
            texture = tx
            autoUppercase = chars == LATIN_UPPER
            val length = chars.length
            val uw = width.toFloat() / tx.width
            val vh = height.toFloat() / tx.height
            var left = 0f
            var top = 0f
            var bottom = vh
            for (i in 0 until length) {
                val rect = RectF(left, top, left + uw, bottom)
                add(chars[i], rect)
                left += uw
                if (left >= 1f) {
                    left = 0f
                    top = bottom
                    bottom += vh
                }
            }
            baseLine = height.toFloat()
            lineHeight = baseLine
        }
        protected fun splitBy(bitmap: Bitmap, height: Int, color: Int, chars: String) {
            autoUppercase = chars == LATIN_UPPER
            val length = chars.length
            val width = bitmap.width
            val vHeight = height.toFloat() / bitmap.height
            var pos = 0
            var found: Boolean
            // spaceMeasuring
            loop@ for (k in 0 until width) {
                for (j in 0 until height) {
                    if (bitmap.getPixel(k, j) != color) {
                        pos = k
                        break@loop
                    }
                }
                pos = k + 1
            }
            // If not found, pos is width.
            // Wait, original logic:
            /*
			spaceMeasuring:
			for (pos=0; pos <  width; pos++) {
				for (int j=0; j < height; j++) {
					if (bitmap.getPixel( pos, j ) != color) {
						break spaceMeasuring;
					}
				}
			}
            */
            // If it breaks, pos is at the non-matching pixel.
            // If it finishes loop (all pixels are color), pos is width.
            add(' ', RectF(0f, 0f, pos.toFloat() / width, vHeight))
            for (i in 0 until length) {
                val ch = chars[i]
                if (ch == ' ') {
                    continue
                } else {
                    var separator = pos
                    do {
                        if (++separator >= width) {
                            break
                        }
                        found = (0 until height).all { j -> bitmap.getPixel(separator, j) == color }
                    } while (!found)
                    add(ch, RectF(pos.toFloat() / width, 0f, separator.toFloat() / width, vHeight))
                    pos = separator + 1
                }
            }
            baseLine = height(frames[chars[0]]!!)
            lineHeight = baseLine
        }
        operator fun get(ch: Char): RectF? {
            return super.get(if (autoUppercase) Character.toUpperCase(ch) else ch)
        }
        companion object {
            const val LATIN_UPPER = " !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ"
            const val LATIN_FULL =
                " !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\u007F"
            fun colorMarked(bmp: Bitmap, color: Int, chars: String): Font {
                val font = Font(TextureCache.get(bmp))
                font.splitBy(bmp, bmp.height, color, chars)
                return font
            }
            fun colorMarked(bmp: Bitmap, height: Int, color: Int, chars: String): Font {
                val font = Font(TextureCache.get(bmp))
                font.splitBy(bmp, height, color, chars)
                return font
            }
        }
    }
}
