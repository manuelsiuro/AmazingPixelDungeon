package com.watabou.pixeldungeon.sprites

import com.watabou.noosa.TextureFilm
import com.watabou.pixeldungeon.Assets
import com.watabou.pixeldungeon.actors.mobs.npcs.AiNpc

class AiNpcSprite : MobSprite() {

    private var variant = 0

    init {
        texture(Assets.AI_NPC)
        setupVariant(0)
    }

    fun setupVariant(variant: Int) {
        this.variant = variant
        val tex = checkNotNull(texture) { "Texture must be set" }
        val film = TextureFilm(tex, 12, 13)
        val offset = variant * 21

        idle = Animation(1, true)
        idle?.frames(film, offset + 0, offset + 0, offset + 0, offset + 1, offset + 0, offset + 0, offset + 0, offset + 0, offset + 1)

        run = Animation(15, true)
        run?.frames(film, offset + 0, offset + 0, offset + 2, offset + 3, offset + 3, offset + 4)

        die = Animation(10, false)
        die?.frames(film, offset + 5, offset + 6, offset + 7, offset + 8, offset + 9)

        attack = Animation(12, false)
        attack?.frames(film, offset + 10, offset + 11, offset + 12, offset + 0)

        idle()
    }

    override fun link(ch: com.watabou.pixeldungeon.actors.Char) {
        super.link(ch)
        if (ch is AiNpc) {
            setupVariant(ch.variant)
        }
    }
}
