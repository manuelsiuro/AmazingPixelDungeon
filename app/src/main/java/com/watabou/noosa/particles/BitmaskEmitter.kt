package com.watabou.noosa.particles
import android.graphics.RectF
import com.watabou.gltextures.SmartTexture
import com.watabou.noosa.Image
import com.watabou.utils.Random
class BitmaskEmitter(target: Image) : Emitter() {
    // DON'T USE WITH COMPLETELY TRANSPARENT IMAGES!!!
    private var map: SmartTexture
    private var mapW: Int = 0
    private var mapH: Int = 0
    init {
        super.target = target
        map = target.texture as SmartTexture
        mapW = map.bitmap!!.width
        mapH = map.bitmap!!.height
    }
    override fun emit(index: Int) {
        val targetImage = target as Image
        val frame = targetImage.frame()
        val ofsX = frame.left * mapW
        val ofsY = frame.top * mapH
        var x: Float
        var y: Float
        do {
            x = Random.Float(frame.width()) * mapW
            y = Random.Float(frame.height()) * mapH
        } while (map.bitmap!!.getPixel((x + ofsX).toInt(), (y + ofsY).toInt()) and 0x000000FF == 0)
        factory!!.emit(
            this, index,
            target!!.x + x * target!!.scale.x,
            target!!.y + y * target!!.scale.y
        )
    }
}
