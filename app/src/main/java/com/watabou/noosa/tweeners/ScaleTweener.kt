package com.watabou.noosa.tweeners
import com.watabou.noosa.Visual
import com.watabou.utils.PointF
open class ScaleTweener(visual: Visual, scale: PointF, time: Float) : Tweener(visual, time) {
    var visual: Visual = visual
    var start: PointF = visual.scale
    var end: PointF = scale
    override fun updateValues(progress: Float) {
        visual.scale = PointF.inter(start, end, progress)
    }
}
