package com.watabou.pixeldungeon.sprites
import com.watabou.noosa.TextureFilm
import com.watabou.pixeldungeon.Assets
import com.watabou.pixeldungeon.actors.Char
class ElementalSprite : MobSprite() {
    init {
        texture(Assets.ELEMENTAL)
        val frames = TextureFilm(checkNotNull(texture) { "Texture must be set" }, 12, 14)
        idle = Animation(10, true)
        idle?.frames(frames, 0, 1, 2)
        run = Animation(12, true)
        run?.frames(frames, 0, 1, 3)
        attack = Animation(15, false)
        attack?.frames(frames, 4, 5, 6)
        die = Animation(15, false)
        die?.frames(frames, 7, 8, 9, 10, 11, 12, 13, 12)
        idle?.let { play(it) }
    }
    override fun link(ch: Char) {
        super.link(ch)
        add(State.BURNING)
    }
    override fun die() {
        super.die()
        remove(State.BURNING)
    }
    override fun blood(): Int = 0xFFFF7D13.toInt()
}
