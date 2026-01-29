package com.watabou.pixeldungeon.actors.blobs
import com.watabou.noosa.audio.Sample
import com.watabou.pixeldungeon.Assets
import com.watabou.pixeldungeon.Badges
import com.watabou.pixeldungeon.Dungeon
import com.watabou.pixeldungeon.DungeonTilemap
import com.watabou.pixeldungeon.Journal
import com.watabou.pixeldungeon.Journal.Feature
import com.watabou.pixeldungeon.actors.buffs.Awareness
import com.watabou.pixeldungeon.actors.buffs.Buff
import com.watabou.pixeldungeon.actors.buffs.Buffs
import com.watabou.pixeldungeon.actors.hero.Hero
import com.watabou.pixeldungeon.effects.BlobEmitter
import com.watabou.pixeldungeon.effects.Identification
import com.watabou.pixeldungeon.effects.Speck
import com.watabou.pixeldungeon.items.Item
import com.watabou.pixeldungeon.levels.Level
import com.watabou.pixeldungeon.levels.Terrain
import com.watabou.pixeldungeon.scenes.GameScene
import com.watabou.pixeldungeon.utils.GLog
class WaterOfAwareness : WellWater() {
    override fun affectHero(hero: Hero): Boolean {
        Sample.play(Assets.SND_DRINK)
        emitter!!.parent!!.add(Identification(DungeonTilemap.tileCenterToWorld(pos)))
        hero.belongings.observe()
        for (i in 0 until Level.LENGTH) {
            val terr = Dungeon.level!!.map[i]
            if ((Terrain.flags[terr] and Terrain.SECRET) != 0) {
                Level.set(i, Terrain.discover(terr))
                GameScene.updateMap(i)
                if (Dungeon.visible[i]) {
                    GameScene.discoverTile(i, terr)
                }
            }
        }
        Buffs.affect(hero, Awareness::class.java)?.spend(Awareness.DURATION)
        Dungeon.observe()
        Dungeon.hero!!.interrupt()
        GLog.p(TXT_PROCCED)
        Journal.remove(Feature.WELL_OF_AWARENESS)
        return true
    }
    override fun affectItem(item: Item): Item? {
        if (item.isIdentified) {
            return null
        } else {
            item.identify()
            Badges.validateItemLevelAquired(item)
            emitter!!.parent!!.add(Identification(DungeonTilemap.tileCenterToWorld(pos)))
            Journal.remove(Feature.WELL_OF_AWARENESS)
            return item
        }
    }
    override fun use(emitter: BlobEmitter) {
        super.use(emitter)
        emitter.pour(Speck.factory(Speck.QUESTION), 0.3f)
    }
    override fun tileDesc(): String {
        return "Power of knowledge radiates from the water of this well. " +
                "Take a sip from it to reveal all secrets of equipped items."
    }
    companion object {
        private const val TXT_PROCCED =
            "As you take a sip, you feel the knowledge pours into your mind. " +
                    "Now you know everything about your equipped items. Also you sense " +
                    "all items on the level and know all its secrets."
    }
}
