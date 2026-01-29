package com.watabou.pixeldungeon.sprites
import com.watabou.noosa.particles.Emitter
import com.watabou.pixeldungeon.actors.Char
import com.watabou.pixeldungeon.effects.Speck
class FetidRatSprite : RatSprite() {
    private var cloud: Emitter? = null
    override fun link(ch: Char) {
        super.link(ch)
        if (cloud == null) {
            cloud = emitter()
            cloud?.pour(Speck.factory(Speck.PARALYSIS), 0.7f)
        }
    }
    override fun update() {
        super.update()
        cloud?.let { it.visible = visible }
    }
    override fun die() {
        super.die()
        cloud?.let { it.on = false }
    }
}
