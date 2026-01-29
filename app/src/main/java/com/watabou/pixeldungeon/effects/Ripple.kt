package com.watabou.pixeldungeon.effects
import com.watabou.noosa.Game
import com.watabou.noosa.Image
import com.watabou.pixeldungeon.DungeonTilemap
import com.watabou.pixeldungeon.levels.Level
class Ripple : Image(Effects.get(Effects.Type.RIPPLE)) {
    private var time: Float = 0f
    fun reset(p: Int) {
        revive()
        x = (p % Level.WIDTH) * DungeonTilemap.SIZE.toFloat()
        y = (p / Level.WIDTH) * DungeonTilemap.SIZE.toFloat()
        origin.set(width / 2, height / 2)
        scale.set(0f)
        time = TIME_TO_FADE
    }
    override fun update() {
        super.update()
        time -= Game.elapsed
        if (time <= 0) {
            kill()
        } else {
            val p = time / TIME_TO_FADE
            scale.set(1 - p)
            alpha(p)
        }
    }
    companion object {
        private const val TIME_TO_FADE = 0.5f
    }
}
