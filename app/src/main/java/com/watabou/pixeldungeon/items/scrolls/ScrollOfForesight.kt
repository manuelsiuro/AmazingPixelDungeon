package com.watabou.pixeldungeon.items.scrolls

import com.watabou.noosa.audio.Sample
import com.watabou.pixeldungeon.Assets
import com.watabou.pixeldungeon.Dungeon
import com.watabou.pixeldungeon.actors.buffs.Awareness
import com.watabou.pixeldungeon.actors.buffs.Buffs
import com.watabou.pixeldungeon.actors.buffs.Invisibility
import com.watabou.pixeldungeon.effects.CellEmitter
import com.watabou.pixeldungeon.effects.Speck
import com.watabou.pixeldungeon.effects.SpellSprite
import com.watabou.pixeldungeon.levels.Level
import com.watabou.pixeldungeon.levels.Terrain
import com.watabou.pixeldungeon.scenes.GameScene
import com.watabou.pixeldungeon.utils.GLog

class ScrollOfForesight : Scroll() {

    init {
        name = "Scroll of Foresight"
    }

    override fun doRead() {
        val level = Dungeon.level ?: return
        val length = Level.LENGTH
        val map = level.map
        val mapped = level.mapped
        val discoverable = Level.discoverable

        var noticed = false

        for (i in 0 until length) {
            val terr = map[i]
            if (discoverable[i]) {
                mapped[i] = true
                if ((Terrain.flags[terr] and Terrain.SECRET) != 0) {
                    Level.set(i, Terrain.discover(terr))
                    GameScene.updateMap(i)
                    if (Dungeon.visible[i]) {
                        GameScene.discoverTile(i, terr)
                        ScrollOfMagicMapping.discover(i)
                        noticed = true
                    }
                }
            }
        }

        // Grant awareness buff
        val hero = curUser
        if (hero != null) {
            Buffs.prolong(hero, Awareness::class.java, 50f)
        }

        Dungeon.observe()
        GLog.i("Your senses sharpen. You are aware of everything on this floor.")

        if (noticed) {
            Sample.play(Assets.SND_SECRET)
        }

        curUser?.let { SpellSprite.show(it, SpellSprite.MAP) }
        Sample.play(Assets.SND_READ)
        Invisibility.dispel()
        setKnown()
        readAnimation()
    }

    override fun desc(): String {
        return "This scroll grants the reader extraordinary perception. It reveals the complete " +
                "layout of the current floor, all hidden traps and doors, and grants a lingering " +
                "awareness that detects unseen threats."
    }

    override fun price(): Int {
        return if (isKnown) 30 * quantity else super.price()
    }
}
