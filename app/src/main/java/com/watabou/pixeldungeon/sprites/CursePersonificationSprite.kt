package com.watabou.pixeldungeon.sprites
import com.watabou.noosa.particles.Emitter
import com.watabou.pixeldungeon.actors.Char
import com.watabou.pixeldungeon.effects.particles.ShadowParticle
class CursePersonificationSprite : WraithSprite() {
    private var cloud: Emitter? = null
    override fun link(ch: Char) {
        super.link(ch)
        if (cloud == null) {
            cloud = emitter().also { it.pour(ShadowParticle.UP, 0.1f) }
        }
    }
    override fun update() {
        super.update()
        cloud?.let { it.visible = visible }
    }
    override fun kill() {
        super.kill()
        cloud?.on = false
    }
}
