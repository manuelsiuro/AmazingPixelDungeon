package com.watabou.pixeldungeon.effects
import com.watabou.noosa.Game
import com.watabou.pixeldungeon.actors.Char
import com.watabou.pixeldungeon.items.Item
import com.watabou.pixeldungeon.sprites.ItemSprite
class Enchanting(item: Item) : ItemSprite(item.image(), null) {
    private enum class Phase {
        FADE_IN, STATIC, FADE_OUT
    }
    private var color: Int = item.glowing()?.color ?: 0
    private var target: Char? = null
    private var phase: Phase
    private var duration: Float
    private var passed: Float
    init {
        originToCenter()
        phase = Phase.FADE_IN
        duration = FADE_IN_TIME
        passed = 0f
    }
    override fun update() {
        super.update()
        target?.let { t ->
            val s = t.sprite
            if (s != null) {
                x = s.center().x - SIZE / 2
                y = s.y - SIZE
            }
        }
        when (phase) {
            Phase.FADE_IN -> {
                alpha(passed / duration * ALPHA)
                scale.set(passed / duration)
            }
            Phase.STATIC -> tint(color, passed / duration * 0.8f)
            Phase.FADE_OUT -> {
                alpha((1 - passed / duration) * ALPHA)
                scale.set(1 + passed / duration)
            }
        }
        passed += Game.elapsed
        if (passed > duration) {
            when (phase) {
                Phase.FADE_IN -> {
                    phase = Phase.STATIC
                    duration = STATIC_TIME
                }
                Phase.STATIC -> {
                    phase = Phase.FADE_OUT
                    duration = FADE_OUT_TIME
                }
                Phase.FADE_OUT -> kill()
            }
            passed = 0f
        }
    }
    companion object {
        private const val SIZE = 16
        private const val FADE_IN_TIME = 0.2f
        private const val STATIC_TIME = 1.0f
        private const val FADE_OUT_TIME = 0.4f
        private const val ALPHA = 0.6f
        fun show(ch: Char, item: Item) {
            val charSprite = ch.sprite
            if (charSprite?.visible != true) {
                return
            }
            val parent = charSprite.parent ?: return
            val sprite = Enchanting(item)
            sprite.target = ch
            parent.add(sprite)
        }
    }
}
