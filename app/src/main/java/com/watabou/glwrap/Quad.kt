package com.watabou.glwrap
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.ShortBuffer
object Quad {
    val VALUES = shortArrayOf(0, 1, 2, 0, 2, 3)
    const val SIZE = 6
    private var indices: ShortBuffer? = null
    private var indexSize = 0
    fun create(): FloatBuffer {
        return ByteBuffer.allocateDirect(16 * Float.SIZE_BYTES)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
    }
    fun createSet(size: Int): FloatBuffer {
        return ByteBuffer.allocateDirect(size * 16 * Float.SIZE_BYTES)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
    }
    fun getIndices(size: Int): ShortBuffer? {
        if (size > indexSize) {
            // TODO: Optimize it!
            indexSize = size
            indices = ByteBuffer.allocateDirect(size * SIZE * Short.SIZE_BYTES)
                .order(ByteOrder.nativeOrder())
                .asShortBuffer()
            val values = ShortArray(size * 6)
            var pos = 0
            val limit = size * 4
            var ofs = 0
            while (ofs < limit) {
                values[pos++] = (ofs + 0).toShort()
                values[pos++] = (ofs + 1).toShort()
                values[pos++] = (ofs + 2).toShort()
                values[pos++] = (ofs + 0).toShort()
                values[pos++] = (ofs + 2).toShort()
                values[pos++] = (ofs + 3).toShort()
                ofs += 4
            }
            indices!!.put(values)
            indices!!.position(0)
        }
        return indices
    }
    fun fill(
        v: FloatArray,
        x1: Float, x2: Float, y1: Float, y2: Float,
        u1: Float, u2: Float, v1: Float, v2: Float
    ) {
        v[0] = x1
        v[1] = y1
        v[2] = u1
        v[3] = v1
        v[4] = x2
        v[5] = y1
        v[6] = u2
        v[7] = v1
        v[8] = x2
        v[9] = y2
        v[10] = u2
        v[11] = v2
        v[12] = x1
        v[13] = y2
        v[14] = u1
        v[15] = v2
    }
    fun fillXY(v: FloatArray, x1: Float, x2: Float, y1: Float, y2: Float) {
        v[0] = x1
        v[1] = y1
        v[4] = x2
        v[5] = y1
        v[8] = x2
        v[9] = y2
        v[12] = x1
        v[13] = y2
    }
    fun fillUV(v: FloatArray, u1: Float, u2: Float, v1: Float, v2: Float) {
        v[2] = u1
        v[3] = v1
        v[6] = u2
        v[7] = v1
        v[10] = u2
        v[11] = v2
        v[14] = u1
        v[15] = v2
    }
}
