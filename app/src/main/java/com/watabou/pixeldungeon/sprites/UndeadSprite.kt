package com.watabou.pixeldungeon.sprites
import com.watabou.noosa.TextureFilm
import com.watabou.pixeldungeon.Assets
import com.watabou.pixeldungeon.Dungeon
import com.watabou.pixeldungeon.effects.Speck
class UndeadSprite : MobSprite() {
    init {
        texture(Assets.UNDEAD)
        val frames = TextureFilm(checkNotNull(texture) { "Texture must be set" }, 12, 16)
        idle = Animation(12, true)
        idle?.frames(frames, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 2, 3)
        run = Animation(15, true)
        run?.frames(frames, 4, 5, 6, 7, 8, 9)
        attack = Animation(15, false)
        attack?.frames(frames, 14, 15, 16)
        die = Animation(12, false)
        die?.frames(frames, 10, 11, 12, 13)
        idle?.let { play(it) }
    }
    override fun die() {
        super.die()
        val currentCh = ch ?: return
        if (Dungeon.visible[currentCh.pos]) {
            emitter().burst(Speck.factory(Speck.BONE), 3)
        }
    }
    override fun blood(): Int = 0xFFcccccc.toInt()
}
