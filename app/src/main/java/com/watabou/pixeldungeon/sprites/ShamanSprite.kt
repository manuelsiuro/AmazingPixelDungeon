package com.watabou.pixeldungeon.sprites
import com.watabou.noosa.TextureFilm
import com.watabou.pixeldungeon.Assets
import com.watabou.pixeldungeon.actors.mobs.Shaman
import com.watabou.pixeldungeon.effects.Lightning
class ShamanSprite : MobSprite() {
    private val points = IntArray(2)
    init {
        texture(Assets.SHAMAN)
        val frames = TextureFilm(checkNotNull(texture) { "Texture must be set" }, 12, 15)
        idle = Animation(2, true)
        idle?.frames(frames, 0, 0, 0, 1, 0, 0, 1, 1)
        run = Animation(12, true)
        run?.frames(frames, 4, 5, 6, 7)
        attack = Animation(12, false)
        attack?.frames(frames, 2, 3, 0)
        zap = attack?.clone()
        die = Animation(12, false)
        die?.frames(frames, 8, 9, 10)
        idle?.let { play(it) }
    }
    override fun zap(cell: Int) {
        val currentCh = ch ?: return
        points[0] = currentCh.pos
        points[1] = cell
        parent?.add(Lightning(points, 2, currentCh as Shaman))
        turnTo(currentCh.pos, cell)
        zap?.let { play(it) }
    }
}
