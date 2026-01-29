package com.watabou.noosa.tweeners
import com.watabou.noosa.Camera
import com.watabou.utils.PointF
open class CameraScrollTweener(camera: Camera, pos: PointF, time: Float) : Tweener(camera, time) {
    var scrollTarget: Camera = camera
    var start: PointF = camera.scroll
    var end: PointF = pos
    override fun updateValues(progress: Float) {
        scrollTarget.scroll = PointF.inter(start, end, progress)
    }
}
