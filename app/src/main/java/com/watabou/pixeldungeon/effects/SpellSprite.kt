package com.watabou.pixeldungeon.effects
import com.watabou.noosa.Game
import com.watabou.noosa.Image
import com.watabou.noosa.TextureFilm
import com.watabou.pixeldungeon.Assets
import com.watabou.pixeldungeon.actors.Char
import com.watabou.pixeldungeon.scenes.GameScene
class SpellSprite : Image(Assets.SPELL_ICONS) {
    private lateinit var target: Char
    private var phase: Phase = Phase.FADE_IN
    private var duration: Float = 0f
    private var passed: Float = 0f
    init {
        if (film == null) {
            film = TextureFilm(requireNotNull(texture) { "SpellSprite texture must not be null" }, SIZE)
        }
    }
    fun reset(index: Int) {
        frame(requireNotNull(requireNotNull(film).get(index)))
        origin.set(width / 2, height / 2)
        phase = Phase.FADE_IN
        duration = FADE_IN_TIME
        passed = 0f
    }
    override fun update() {
        super.update()
        val s = target.sprite
        if (s != null) {
            x = s.center().x - SIZE / 2
            y = s.y - SIZE.toFloat()
        }
        when (phase) {
            Phase.FADE_IN -> {
                alpha(passed / duration)
                scale.set(passed / duration)
            }
            Phase.STATIC -> {
            }
            Phase.FADE_OUT -> alpha(1 - passed / duration)
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
    override fun kill() {
        super.kill()
        all.remove(target)
    }
    private enum class Phase {
        FADE_IN, STATIC, FADE_OUT
    }
    companion object {
        const val FOOD = 0
        const val MAP = 1
        const val CHARGE = 2
        const val MASTERY = 3
        private const val SIZE = 16
        private const val FADE_IN_TIME = 0.2f
        private const val STATIC_TIME = 0.8f
        private const val FADE_OUT_TIME = 0.4f
        private var film: TextureFilm? = null
        private val all = HashMap<Char, SpellSprite>()
        fun show(ch: Char, index: Int) {
            if (ch.sprite?.visible != true) {
                return
            }
            val old = all[ch]
            old?.kill()
            val sprite = GameScene.spellSprite()
            sprite.revive()
            sprite.reset(index)
            sprite.target = ch
            all[ch] = sprite
        }
    }
}
