package com.watabou.pixeldungeon.levels.traps
import com.watabou.noosa.audio.Sample
import com.watabou.pixeldungeon.Assets
import com.watabou.pixeldungeon.Dungeon
import com.watabou.pixeldungeon.actors.Char
import com.watabou.pixeldungeon.effects.CellEmitter
import com.watabou.pixeldungeon.effects.Speck
import com.watabou.pixeldungeon.utils.GLog
object AlarmTrap {
    fun trigger(pos: Int, ch: Char?) {
        val level = Dungeon.level ?: return
        for (mob in level.mobs) {
            if (mob !== ch) {
                mob.beckon(pos)
            }
        }
        if (Dungeon.visible[pos]) {
            GLog.w("The trap emits a piercing sound that echoes throughout the dungeon!")
            CellEmitter.center(pos).start(Speck.factory(Speck.SCREAM), 0.3f, 3)
        }
        Sample.play(Assets.SND_ALERT)
    }
}
