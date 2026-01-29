package com.watabou.pixeldungeon.sprites
import com.watabou.noosa.tweeners.PosTweener
import com.watabou.noosa.tweeners.Tweener
import com.watabou.pixeldungeon.DungeonTilemap
import com.watabou.pixeldungeon.items.Item
import com.watabou.utils.Callback
import com.watabou.utils.PointF
import kotlin.math.atan2
class MissileSprite : ItemSprite(), Tweener.Listener {
    private var callback: Callback? = null
    init {
        originToCenter()
    }
    fun reset(from: Int, to: Int, item: Item?, listener: Callback?) {
        if (item == null) {
            reset(from, to, 0, null, listener)
        } else {
            reset(from, to, item.image(), item.glowing(), listener)
        }
    }
    fun reset(from: Int, to: Int, image: Int, glowing: Glowing?, listener: Callback?) {
        revive()
        view(image, glowing)
        this.callback = listener
        point(DungeonTilemap.tileToWorld(from))
        val dest = DungeonTilemap.tileToWorld(to)
        val d = PointF.diff(dest, point())
        speed.set(d).normalize().scale(SPEED)
        if (image == 31 || image == 108 || image == 109 || image == 110) {
            angularSpeed = 0f
            angle = 135 - (atan2(d.x.toDouble(), d.y.toDouble()) / Math.PI * 180).toFloat()
        } else {
            angularSpeed = if (image == 15 || image == 106) 1440f else 720f
        }
        val tweener = PosTweener(this, dest, d.length() / SPEED)
        tweener.listener = this
        parent?.add(tweener)
    }
    override fun onComplete(tweener: Tweener) {
        kill()
        callback?.call()
    }
    companion object {
        private const val SPEED = 240f
    }
}
