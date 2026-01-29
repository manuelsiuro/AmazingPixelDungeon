package com.watabou.noosa.tweeners
import com.watabou.noosa.Visual
import com.watabou.utils.PointF
open class PosTweener(visual: Visual, pos: PointF, time: Float) : Tweener(visual, time) {
    var visual: Visual = visual
    var start: PointF = visual.point()
    var end: PointF = pos
    override fun updateValues(progress: Float) {
        visual.point(PointF.inter(start, end, progress))
    }
}
