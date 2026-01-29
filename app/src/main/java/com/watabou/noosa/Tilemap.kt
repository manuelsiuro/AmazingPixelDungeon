package com.watabou.noosa
import android.graphics.RectF
import com.watabou.gltextures.SmartTexture
import com.watabou.gltextures.TextureCache
import com.watabou.glwrap.Quad
import com.watabou.utils.Rect
import java.nio.FloatBuffer
open class Tilemap : Visual {
    protected var texture: SmartTexture? = null
    protected var tileset: TextureFilm? = null
    protected var data: IntArray? = null
    protected var mapWidth: Int = 0
    protected var mapHeight: Int = 0
    protected var size: Int = 0
    private var cellW: Float = 0f
    private var cellH: Float = 0f
    protected var vertices: FloatArray
    protected var quads: FloatBuffer? = null
    var updated: Rect
    constructor(tx: Any, tileset: TextureFilm) : super(0f, 0f, 0f, 0f) {
        this.texture = TextureCache.get(tx)
        this.tileset = tileset
        val r = tileset.get(0)!!
        cellW = tileset.width(r)
        cellH = tileset.height(r)
        vertices = FloatArray(16)
        updated = Rect()
    }
    fun map(data: IntArray, cols: Int) {
        this.data = data
        mapWidth = cols
        mapHeight = data.size / cols
        size = mapWidth * mapHeight
        width = cellW * mapWidth
        height = cellH * mapHeight
        quads = Quad.createSet(size)
        updated.set(0, 0, mapWidth, mapHeight)
    }
    protected fun updateVertices() {
        var y1 = cellH * updated.top
        var y2 = y1 + cellH
        for (i in updated.top until updated.bottom) {
            var x1 = cellW * updated.left
            var x2 = x1 + cellW
            var pos = i * mapWidth + updated.left
            quads!!.position(16 * pos)
            for (j in updated.left until updated.right) {
                val uv = tileset!![data!![pos++]]!!
                vertices[0] = x1
                vertices[1] = y1
                vertices[2] = uv.left
                vertices[3] = uv.top
                vertices[4] = x2
                vertices[5] = y1
                vertices[6] = uv.right
                vertices[7] = uv.top
                vertices[8] = x2
                vertices[9] = y2
                vertices[10] = uv.right
                vertices[11] = uv.bottom
                vertices[12] = x1
                vertices[13] = y2
                vertices[14] = uv.left
                vertices[15] = uv.bottom
                quads!!.put(vertices)
                x1 = x2
                x2 += cellW
            }
            y1 = y2
            y2 += cellH
        }
        updated.setEmpty()
    }
    override fun draw() {
        super.draw()
        val script = NoosaScript.get()
        texture!!.bind()
        script.uModel.valueM4(matrix)
        script.lighting(
            rm, gm, bm, am,
            ra, ga, ba, aa
        )
        if (!updated.isEmpty()) {
            updateVertices()
        }
        script.camera(camera())
        script.drawQuadSet(quads!!, size)
    }
}
