package com.watabou.pixeldungeon.effects
import com.watabou.noosa.Game
import com.watabou.noosa.Gizmo
import com.watabou.noosa.audio.Sample
import com.watabou.pixeldungeon.Assets
import com.watabou.pixeldungeon.sprites.CharSprite
class IceBlock(private val target: CharSprite) : Gizmo() {
    private var phase: Float = 0f
    override fun update() {
        super.update()
        phase += Game.elapsed * 2
        if (phase < 1) {
            target.tint(0.83f, 1.17f, 1.33f, phase * 0.6f)
        } else {
            target.tint(0.83f, 1.17f, 1.33f, 0.6f)
        }
    }
    fun melt() {
        target.resetColor()
        killAndErase()
        if (visible) {
            Splash.at(target.center(), 0xFFB2D6FF.toInt(), 5)
            Sample.play(Assets.SND_SHATTER)
        }
    }
    companion object {
        fun freeze(sprite: CharSprite): IceBlock {
            val iceBlock = IceBlock(sprite)
            val parent = requireNotNull(sprite.parent) { "sprite.parent must not be null" }
            parent.add(iceBlock)
            return iceBlock
        }
    }
}
