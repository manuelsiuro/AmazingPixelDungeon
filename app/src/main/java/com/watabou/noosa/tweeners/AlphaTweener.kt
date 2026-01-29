package com.watabou.noosa.tweeners
import com.watabou.noosa.Visual
open class AlphaTweener(image: Visual, alpha: Float, time: Float) : Tweener(image, time) {
    var image: Visual = image
    var start: Float = image.alpha()
    var delta: Float = alpha - start
    override fun updateValues(progress: Float) {
        image.alpha(start + delta * progress)
    }
}
