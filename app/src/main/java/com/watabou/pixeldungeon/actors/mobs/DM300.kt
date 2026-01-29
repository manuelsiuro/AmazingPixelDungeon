package com.watabou.pixeldungeon.actors.mobs
import com.watabou.noosa.Camera
import com.watabou.noosa.audio.Sample
import com.watabou.pixeldungeon.Assets
import com.watabou.pixeldungeon.Badges
import com.watabou.pixeldungeon.Dungeon
import com.watabou.pixeldungeon.Statistics
import com.watabou.pixeldungeon.actors.Actor
import com.watabou.pixeldungeon.actors.Char
import com.watabou.pixeldungeon.actors.blobs.Blob
import com.watabou.pixeldungeon.actors.blobs.ToxicGas
import com.watabou.pixeldungeon.actors.buffs.Buffs
import com.watabou.pixeldungeon.actors.buffs.Paralysis
import com.watabou.pixeldungeon.effects.CellEmitter
import com.watabou.pixeldungeon.effects.Speck
import com.watabou.pixeldungeon.effects.particles.ElmoParticle
import com.watabou.pixeldungeon.items.keys.SkeletonKey
import com.watabou.pixeldungeon.items.rings.RingOfThorns
import com.watabou.pixeldungeon.items.scrolls.ScrollOfPsionicBlast
import com.watabou.pixeldungeon.items.weapon.enchantments.Death
import com.watabou.pixeldungeon.levels.Level
import com.watabou.pixeldungeon.levels.Terrain
import com.watabou.pixeldungeon.scenes.GameScene
import com.watabou.pixeldungeon.sprites.DM300Sprite
import com.watabou.pixeldungeon.utils.GLog
import com.watabou.utils.Random
import java.util.*
class DM300 : Mob() {
    init {
        name = if (Dungeon.depth == Statistics.deepestFloor) "DM-300" else "DM-350"
        spriteClass = DM300Sprite::class.java
        HT = 200
        HP = HT
        EXP = 30
        defenseSkill = 18
        loot = RingOfThorns().random()
        lootChance = 0.333f
    }
    override fun damageRoll(): Int {
        return Random.NormalIntRange(18, 24)
    }
    override fun attackSkill(target: Char?): Int {
        return 28
    }
    override fun dr(): Int {
        return 10
    }
    override fun act(): Boolean {
        GameScene.add(Blob.seed(pos, 30, ToxicGas::class.java) as Blob)
        return super.act()
    }
    override fun move(step: Int) {
        super.move(step)
        val level = Dungeon.level
        if (level?.map?.get(step) == Terrain.INACTIVE_TRAP && HP < HT) {
            HP += Random.Int(1, HT - HP)
            sprite?.emitter()?.burst(ElmoParticle.FACTORY, 5)
            val hero = Dungeon.hero
            if (Dungeon.visible[step] && hero?.isAlive == true) {
                GLog.n("DM-300 repairs itself!")
            }
        }
        val cells = intArrayOf(
            step - 1, step + 1, step - Level.WIDTH, step + Level.WIDTH,
            step - 1 - Level.WIDTH,
            step - 1 + Level.WIDTH,
            step + 1 - Level.WIDTH,
            step + 1 + Level.WIDTH
        )
        val cell = cells[Random.Int(cells.size)]
        if (Dungeon.visible[cell]) {
            CellEmitter.get(cell).start(Speck.factory(Speck.ROCK), 0.07f, 10)
            Camera.main?.shake(3f, 0.7f)
            Sample.play(Assets.SND_ROCKS)
            if (Level.water[cell]) {
                GameScene.ripple(cell)
            } else if (level?.map?.get(cell) == Terrain.EMPTY) {
                Level.set(cell, Terrain.EMPTY_DECO)
                GameScene.updateMap(cell)
            }
        }
        val ch = Actor.findChar(cell)
        if (ch != null && ch !== this) {
            Buffs.prolong(ch, Paralysis::class.java, 2f)
        }
    }
    override fun die(src: Any?) {
        super.die(src)
        GameScene.bossSlain()
        Dungeon.level?.drop(SkeletonKey(), pos)?.sprite?.drop()
        Badges.validateBossSlain()
        yell("Mission failed. Shutting down.")
    }
    override fun notice() {
        super.notice()
        yell("Unauthorised personnel detected.")
    }
    override fun description(): String {
        return "This machine was created by the Dwarves several centuries ago. Later, Dwarves started to replace machines with golems, elementals and even demons. Eventually it led their civilization to the decline. The DM-300 and similar machines were typically used for construction and mining, and in some cases, for city defense."
    }
    override fun resistances(): HashSet<Class<*>> {
        return RESISTANCES
    }
    override fun immunities(): HashSet<Class<*>> {
        return IMMUNITIES
    }
    companion object {
        private val RESISTANCES = hashSetOf<Class<*>>(Death::class.java, ScrollOfPsionicBlast::class.java)
        private val IMMUNITIES = hashSetOf<Class<*>>(ToxicGas::class.java)
    }
}
