package com.watabou.pixeldungeon.sprites
import com.watabou.noosa.Game
class DiscardedItemSprite : ItemSprite() {
    init {
        originToCenter()
        angularSpeed = 720f
    }
    override fun drop() {
        scale.set(1f)
        am = 1f
    }
    override fun update() {
        super.update()
        scale.set(scale.x * 0.9f)
        am -= Game.elapsed
        if (am <= 0) {
            remove()
        }
    }
}
