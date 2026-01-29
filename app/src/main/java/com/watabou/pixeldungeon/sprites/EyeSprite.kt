package com.watabou.pixeldungeon.sprites
import com.watabou.noosa.TextureFilm
import com.watabou.pixeldungeon.Assets
import com.watabou.pixeldungeon.Dungeon
import com.watabou.pixeldungeon.DungeonTilemap
import com.watabou.pixeldungeon.effects.DeathRay
class EyeSprite : MobSprite() {
    private var attackPos: Int = 0
    init {
        texture(Assets.EYE)
        val frames = TextureFilm(checkNotNull(texture) { "Texture must be set" }, 16, 18)
        idle = Animation(8, true)
        idle?.frames(frames, 0, 1, 2)
        run = Animation(12, true)
        run?.frames(frames, 5, 6)
        attack = Animation(8, false)
        attack?.frames(frames, 4, 3)
        die = Animation(8, false)
        die?.frames(frames, 7, 8, 9)
        idle?.let { play(it) }
    }
    override fun attack(cell: Int) {
        attackPos = cell
        super.attack(cell)
    }
    override fun onComplete(anim: Animation) {
        super.onComplete(anim)
        if (anim == attack) {
            val ch = ch ?: return
            if (Dungeon.visible[ch.pos] || Dungeon.visible[attackPos]) {
                parent?.add(DeathRay(center(), DungeonTilemap.tileCenterToWorld(attackPos)))
            }
        }
    }
}
