package com.watabou.pixeldungeon.effects
import com.watabou.noosa.Game
import com.watabou.noosa.Group
import com.watabou.noosa.Image
import com.watabou.pixeldungeon.Dungeon
import com.watabou.pixeldungeon.DungeonTilemap
import com.watabou.pixeldungeon.actors.Char
import com.watabou.pixeldungeon.levels.Level
class Wound : Image(Effects.get(Effects.Type.WOUND)) {
    private var time: Float = 0f
    init {
        origin.set(width / 2, height / 2)
    }
    fun reset(p: Int) {
        revive()
        x = (p % Level.WIDTH) * DungeonTilemap.SIZE + (DungeonTilemap.SIZE - width) / 2
        y = (p / Level.WIDTH) * DungeonTilemap.SIZE + (DungeonTilemap.SIZE - height) / 2
        time = TIME_TO_FADE
    }
    override fun update() {
        super.update()
        time -= Game.elapsed
        if (time <= 0) {
            kill()
        } else {
            val p = time / TIME_TO_FADE
            alpha(p)
            scale.x = 1 + p
        }
    }
    companion object {
        private const val TIME_TO_FADE = 0.8f
        @JvmOverloads
        fun hit(ch: Char, angle: Float = 0f) {
            val sprite = ch.sprite ?: return
            val parent = sprite.parent ?: return
            val w = parent.recycle(Wound::class.java) as Wound
            parent.bringToFront(w)
            w.reset(ch.pos)
            w.angle = angle
        }
        @JvmOverloads
        fun hit(pos: Int, angle: Float = 0f) {
            val hero = Dungeon.hero ?: return
            val sprite = hero.sprite ?: return
            val parent = sprite.parent ?: return
            val w = parent.recycle(Wound::class.java) as Wound
            parent.bringToFront(w)
            w.reset(pos)
            w.angle = angle
        }
    }
}
