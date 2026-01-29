package com.watabou.pixeldungeon.sprites
import com.watabou.noosa.TextureFilm
import com.watabou.pixeldungeon.Assets
import com.watabou.utils.Random
class SheepSprite : MobSprite() {
    init {
        texture(Assets.SHEEP)
        val frames = TextureFilm(checkNotNull(texture) { "Texture must be set" }, 16, 15)
        idle = Animation(8, true)
        idle?.frames(frames, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 2, 3, 0)
        run = idle?.clone()
        attack = idle?.clone()
        die = Animation(20, false)
        die?.frames(frames, 0)
        idle?.let { play(it) }
        curAnim?.frames?.let { curFrame = Random.Int(it.size) }
    }
}
