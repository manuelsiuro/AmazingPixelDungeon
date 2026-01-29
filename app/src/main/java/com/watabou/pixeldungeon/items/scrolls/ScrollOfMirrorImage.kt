package com.watabou.pixeldungeon.items.scrolls
import com.watabou.noosa.audio.Sample
import com.watabou.pixeldungeon.Assets
import com.watabou.pixeldungeon.actors.Actor
import com.watabou.pixeldungeon.actors.buffs.Invisibility
import com.watabou.pixeldungeon.actors.mobs.npcs.MirrorImage
import com.watabou.pixeldungeon.items.wands.WandOfBlink
import com.watabou.pixeldungeon.levels.Level
import com.watabou.pixeldungeon.scenes.GameScene
import com.watabou.utils.Random
import java.util.ArrayList
class ScrollOfMirrorImage : Scroll() {
    init {
        name = "Scroll of Mirror Image"
    }
    override fun doRead() {
        val hero = curUser ?: return
        val respawnPoints = ArrayList<Int>()
        for (i in Level.NEIGHBOURS8.indices) {
            val p = hero.pos + Level.NEIGHBOURS8[i]
            if (Actor.findChar(p) == null && (Level.passable[p] || Level.avoid[p])) {
                respawnPoints.add(p)
            }
        }
        var nImages = NIMAGES
        while (nImages > 0 && respawnPoints.size > 0) {
            val index = Random.index(respawnPoints)
            val mob = MirrorImage()
            mob.duplicate(hero)
            GameScene.add(mob)
            WandOfBlink.appear(mob, respawnPoints[index])
            respawnPoints.removeAt(index)
            nImages--
        }
        if (nImages < NIMAGES) {
            setKnown()
        }
        Sample.play(Assets.SND_READ)
        Invisibility.dispel()
        readAnimation()
    }
    override fun desc(): String {
        return "The incantation on this scroll will create illusionary twins of the reader, which will chase his enemies."
    }
    companion object {
        private const val NIMAGES = 3
    }
}
