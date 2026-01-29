package com.watabou.pixeldungeon
import android.graphics.Bitmap
import com.watabou.gltextures.SmartTexture
import com.watabou.gltextures.TextureCache
import com.watabou.glwrap.Texture
import com.watabou.noosa.Image
import com.watabou.pixeldungeon.scenes.GameScene
import java.util.Arrays
class FogOfWar(mapWidth: Int, mapHeight: Int) : Image() {
    private var pixels: IntArray? = null
    private val pWidth: Int
    private val pHeight: Int
    private var width2: Int
    private var height2: Int
    init {
        pWidth = mapWidth + 1
        pHeight = mapHeight + 1
        width2 = 1
        while (width2 < pWidth) {
            width2 = width2 shl 1
        }
        height2 = 1
        while (height2 < pHeight) {
            height2 = height2 shl 1
        }
        val size = DungeonTilemap.SIZE.toFloat()
        width = width2 * size
        height = height2 * size
        texture(FogTexture())
        scale.set(
            DungeonTilemap.SIZE.toFloat(),
            DungeonTilemap.SIZE.toFloat()
        )
        y = -size / 2
        x = y
    }
    fun updateVisibility(visible: BooleanArray, visited: BooleanArray, mapped: BooleanArray) {
        if (pixels == null) {
            pixels = IntArray(width2 * height2)
        }
        Arrays.fill(pixels!!, INVISIBLE)
        for (i in 1 until pHeight - 1) {
            var pos = (pWidth - 1) * i
            for (j in 1 until pWidth - 1) {
                pos++
                var c = INVISIBLE
                if (visible[pos] && visible[pos - (pWidth - 1)] &&
                    visible[pos - 1] && visible[pos - (pWidth - 1) - 1]
                ) {
                    c = VISIBLE
                } else if (visited[pos] && visited[pos - (pWidth - 1)] &&
                    visited[pos - 1] && visited[pos - (pWidth - 1) - 1]
                ) {
                    c = VISITED
                } else if (mapped[pos] && mapped[pos - (pWidth - 1)] &&
                    mapped[pos - 1] && mapped[pos - (pWidth - 1) - 1]
                ) {
                    c = MAPPED
                }
                pixels!![i * width2 + j] = c
            }
        }
        (texture as FogTexture).pixels(width2, height2, pixels!!)
    }
    private inner class FogTexture : SmartTexture(
        Bitmap.createBitmap(
            width2,
            height2,
            Bitmap.Config.ARGB_8888
        )
    ) {
        init {
            filter(Texture.LINEAR, Texture.LINEAR)
            TextureCache.add(FogOfWar::class.java, this)
        }
        override fun reload() {
            super.reload()
            GameScene.afterObserve()
        }
    }
    companion object {
        private const val VISIBLE = 0x00000000
        private const val VISITED = 0xcc111111.toInt()
        private const val MAPPED = 0xcc442211.toInt()
        private const val INVISIBLE = 0xFF000000.toInt()
    }
}
