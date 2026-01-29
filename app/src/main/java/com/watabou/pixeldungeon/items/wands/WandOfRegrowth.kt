package com.watabou.pixeldungeon.items.wands
import com.watabou.noosa.audio.Sample
import com.watabou.pixeldungeon.Assets
import com.watabou.pixeldungeon.Dungeon
import com.watabou.pixeldungeon.actors.blobs.Blob
import com.watabou.pixeldungeon.actors.blobs.Regrowth
import com.watabou.pixeldungeon.effects.MagicMissile
import com.watabou.pixeldungeon.items.Item
import com.watabou.pixeldungeon.levels.Level
import com.watabou.pixeldungeon.levels.Terrain
import com.watabou.pixeldungeon.mechanics.Ballistica
import com.watabou.pixeldungeon.scenes.GameScene
import com.watabou.pixeldungeon.utils.GLog
import com.watabou.utils.Callback
class WandOfRegrowth : Wand() {
    init {
        name = "Wand of Regrowth"
    }
    override fun onZap(cell: Int) {
        val dungeonLevel = Dungeon.level ?: return
        for (i in 1 until Ballistica.distance - 1) {
            val p = Ballistica.trace[i]
            val c = dungeonLevel.map[p]
            if (c == Terrain.EMPTY ||
                c == Terrain.EMBERS ||
                c == Terrain.EMPTY_DECO
            ) {
                Level.set(p, Terrain.GRASS)
                GameScene.updateMap(p)
                if (Dungeon.visible[p]) {
                    GameScene.discoverTile(p, c)
                }
            }
        }
        val c = dungeonLevel.map[cell]
        if (c == Terrain.EMPTY ||
            c == Terrain.EMBERS ||
            c == Terrain.EMPTY_DECO ||
            c == Terrain.GRASS ||
            c == Terrain.HIGH_GRASS
        ) {
            Blob.seed(cell, (power() + 2) * 20, Regrowth::class.java)?.let { GameScene.add(it) }
        } else {
            GLog.i("nothing happened")
        }
    }
    override fun fx(cell: Int, callback: Callback) {
        val user = Item.curUser ?: return
        val parent = user.sprite?.parent ?: return
        MagicMissile.foliage(parent, user.pos, cell, callback)
        Sample.play(Assets.SND_ZAP)
    }
    override fun desc(): String {
        return "\"When life ceases new life always begins to grow... The eternal cycle always remains!\""
    }
}
