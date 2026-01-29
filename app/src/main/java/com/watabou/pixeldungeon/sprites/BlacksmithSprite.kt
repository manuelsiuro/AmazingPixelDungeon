package com.watabou.pixeldungeon.sprites
import com.watabou.noosa.TextureFilm
import com.watabou.noosa.audio.Sample
import com.watabou.noosa.particles.Emitter
import com.watabou.pixeldungeon.Assets
import com.watabou.pixeldungeon.Dungeon
import com.watabou.pixeldungeon.actors.Char
import com.watabou.pixeldungeon.effects.Speck
import com.watabou.pixeldungeon.levels.Level
class BlacksmithSprite : MobSprite() {
    private var emitter: Emitter? = null
    init {
        texture(Assets.TROLL)
        val frames = TextureFilm(checkNotNull(texture) { "Texture must be set" }, 13, 16)
        idle = Animation(15, true)
        idle?.frames(frames, 0, 0, 0, 0, 0, 0, 0, 1, 2, 2, 2, 3)
        run = Animation(20, true)
        run?.frames(frames, 0)
        die = Animation(20, false)
        die?.frames(frames, 0)
        idle?.let { play(it) }
    }
    override fun link(ch: Char) {
        super.link(ch)
        val newEmitter = Emitter()
        newEmitter.autoKill = false
        newEmitter.pos(x + 7, y + 12)
        emitter = newEmitter
        parent?.add(newEmitter)
    }
    override fun update() {
        super.update()
        emitter?.visible = visible
    }
    override fun onComplete(anim: Animation) {
        super.onComplete(anim)
        val currentEmitter = emitter
        val currentCh = ch
        if (visible && currentEmitter != null && anim === idle && currentCh != null) {
            currentEmitter.burst(Speck.factory(Speck.FORGE), 3)
            val hero = Dungeon.hero ?: return
            val volume = 0.2f / Level.distance(currentCh.pos, hero.pos)
            Sample.play(Assets.SND_EVOKE, volume, volume, 0.8f)
        }
    }
}
