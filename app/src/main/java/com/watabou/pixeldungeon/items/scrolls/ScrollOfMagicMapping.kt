package com.watabou.pixeldungeon.items.scrolls
import com.watabou.noosa.audio.Sample
import com.watabou.pixeldungeon.Assets
import com.watabou.pixeldungeon.Dungeon
import com.watabou.pixeldungeon.actors.buffs.Invisibility
import com.watabou.pixeldungeon.effects.CellEmitter
import com.watabou.pixeldungeon.effects.Speck
import com.watabou.pixeldungeon.effects.SpellSprite
import com.watabou.pixeldungeon.levels.Level
import com.watabou.pixeldungeon.levels.Terrain
import com.watabou.pixeldungeon.scenes.GameScene
import com.watabou.pixeldungeon.utils.GLog
class ScrollOfMagicMapping : Scroll() {
    init {
        name = "Scroll of Magic Mapping"
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
                        discover(i)
                        noticed = true
                    }
                }
            }
        }
        Dungeon.observe()
        GLog.i(TXT_LAYOUT)
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
        return "When this scroll is read, an image of crystal clarity will be etched into your memory, " +
                "alerting you to the precise layout of the level and revealing all hidden secrets. " +
                "The locations of items and creatures will remain unknown."
    }
    override fun price(): Int {
        return if (isKnown) 25 * quantity else super.price()
    }
    companion object {
        private const val TXT_LAYOUT = "You are now aware of the level layout."
        fun discover(cell: Int) {
            CellEmitter.get(cell).start(Speck.factory(Speck.DISCOVER), 0.1f, 4)
        }
    }
}
