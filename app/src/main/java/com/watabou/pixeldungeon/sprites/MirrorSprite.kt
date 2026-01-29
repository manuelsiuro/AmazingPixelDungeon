package com.watabou.pixeldungeon.sprites
import com.watabou.noosa.TextureFilm
import com.watabou.pixeldungeon.Dungeon
import com.watabou.pixeldungeon.actors.Char
import com.watabou.pixeldungeon.actors.mobs.npcs.MirrorImage
class MirrorSprite : MobSprite() {
    init {
        val hero = Dungeon.hero
        val sheet = hero?.heroClass?.spritesheet()
        if (sheet != null) {
            texture(sheet)
        }
        updateArmor(0)
        idle()
    }
    override fun link(ch: Char) {
        super.link(ch)
        updateArmor((ch as MirrorImage).tier)
    }
    fun updateArmor(tier: Int) {
        val film = TextureFilm(HeroSprite.tiers(), tier, FRAME_WIDTH, FRAME_HEIGHT)
        idle = Animation(1, true)
        idle?.frames(film, 0, 0, 0, 1, 0, 0, 1, 1)
        run = Animation(20, true)
        run?.frames(film, 2, 3, 4, 5, 6, 7)
        die = Animation(20, false)
        die?.frames(film, 0)
        attack = Animation(15, false)
        attack?.frames(film, 13, 14, 15, 0)
        idle()
    }
    companion object {
        private const val FRAME_WIDTH = 12
        private const val FRAME_HEIGHT = 15
    }
}
