package com.watabou.pixeldungeon.sprites
import com.watabou.noosa.TextureFilm
import com.watabou.noosa.particles.PixelParticle
import com.watabou.pixeldungeon.Assets
class ShopkeeperSprite : MobSprite() {
    private var coin: PixelParticle? = null
    init {
        texture(Assets.KEEPER)
        val film = TextureFilm(checkNotNull(texture) { "Texture must be set" }, 14, 14)
        idle = Animation(10, true)
        idle?.frames(film, 1, 1, 1, 1, 1, 0, 0, 0, 0)
        die = Animation(20, false)
        die?.frames(film, 0)
        run = idle?.clone()
        attack = idle?.clone()
        idle()
    }
    override fun onComplete(anim: Animation) {
        super.onComplete(anim)
        if (visible && anim === idle) {
            val currentCoin = coin ?: PixelParticle().also {
                coin = it
                parent?.add(it)
            }
            currentCoin.reset(x + if (flipHorizontal) 0f else 13f, y + 7, 0xFFFF00, 1f, 0.5f)
            currentCoin.speed.y = -40f
            currentCoin.acc.y = 160f
        }
    }
}
