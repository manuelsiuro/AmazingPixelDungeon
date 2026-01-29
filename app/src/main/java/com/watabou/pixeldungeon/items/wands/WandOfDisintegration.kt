package com.watabou.pixeldungeon.items.wands
import com.watabou.pixeldungeon.Dungeon
import com.watabou.pixeldungeon.DungeonTilemap
import com.watabou.pixeldungeon.actors.Actor
import com.watabou.pixeldungeon.actors.Char
import com.watabou.pixeldungeon.effects.CellEmitter
import com.watabou.pixeldungeon.effects.DeathRay
import com.watabou.pixeldungeon.effects.particles.PurpleParticle
import com.watabou.pixeldungeon.items.Item
import com.watabou.pixeldungeon.levels.Level
import com.watabou.pixeldungeon.levels.Terrain
import com.watabou.pixeldungeon.mechanics.Ballistica
import com.watabou.pixeldungeon.scenes.GameScene
import com.watabou.utils.Callback
import com.watabou.utils.Random
import java.util.ArrayList
import kotlin.math.min
class WandOfDisintegration : Wand() {
    init {
        name = "Wand of Disintegration"
        hitChars = false
    }
    override fun onZap(cell: Int) {
        var terrainAffected = false
        val level = power()
        val maxDistance = distance()
        Ballistica.distance = min(Ballistica.distance, maxDistance)
        val chars = ArrayList<Char>()
        for (i in 1 until Ballistica.distance) {
            val c = Ballistica.trace[i]
            val ch = Actor.findChar(c)
            if (ch != null) {
                chars.add(ch)
            }
            val dungeonLevel = Dungeon.level ?: return
            val terr = dungeonLevel.map[c]
            if (terr == Terrain.DOOR || terr == Terrain.SIGN) {
                dungeonLevel.destroy(c)
                GameScene.updateMap(c)
                terrainAffected = true
            } else if (terr == Terrain.HIGH_GRASS) {
                Level.set(c, Terrain.GRASS)
                GameScene.updateMap(c)
                terrainAffected = true
            }
            CellEmitter.center(c).burst(PurpleParticle.BURST, Random.IntRange(1, 2))
        }
        if (terrainAffected) {
            Dungeon.observe()
        }
        val lvl = level + chars.size
        val dmgMin = lvl
        val dmgMax = 8 + lvl * lvl / 3
        for (ch in chars) {
            ch.damage(Random.NormalIntRange(dmgMin, dmgMax), this)
            ch.sprite?.centerEmitter()?.burst(PurpleParticle.BURST, Random.IntRange(1, 2))
            ch.sprite?.flash()
        }
    }
    private fun distance(): Int {
        return level() + 4
    }
    override fun fx(cell: Int, callback: Callback) {
        // We override logic, but we need to compute cell used for DeathRay
        val targetCell = Ballistica.trace[min(Ballistica.distance, distance()) - 1]
        val user = Item.curUser
        val sprite = user?.sprite
        val parent = sprite?.parent
        if (parent != null) {
            parent.add(DeathRay(sprite.center(), DungeonTilemap.tileCenterToWorld(targetCell)))
        }
        callback.call()
    }
    override fun desc(): String {
        return "This wand emits a beam of destructive energy, which pierces all creatures in its way. " +
                "The more targets it hits, the more damage it inflicts to each of them."
    }
}
