package com.watabou.pixeldungeon.plants
import com.watabou.noosa.audio.Sample
import com.watabou.pixeldungeon.Assets
import com.watabou.pixeldungeon.Dungeon
import com.watabou.pixeldungeon.actors.Char
import com.watabou.pixeldungeon.actors.buffs.Barkskin
import com.watabou.pixeldungeon.actors.buffs.Buff
import com.watabou.pixeldungeon.actors.buffs.Buffs
import com.watabou.pixeldungeon.actors.hero.Hero
import com.watabou.pixeldungeon.actors.hero.HeroSubClass
import com.watabou.pixeldungeon.effects.CellEmitter
import com.watabou.pixeldungeon.effects.particles.LeafParticle
import com.watabou.pixeldungeon.items.Dewdrop
import com.watabou.pixeldungeon.items.Generator
import com.watabou.pixeldungeon.items.Item
import com.watabou.pixeldungeon.levels.Level
import com.watabou.pixeldungeon.levels.Terrain
import com.watabou.pixeldungeon.sprites.PlantSprite
import com.watabou.pixeldungeon.utils.Utils
import com.watabou.utils.Bundle
import com.watabou.utils.Bundlable
import com.watabou.utils.Random
import java.util.ArrayList
open class Plant : Bundlable {
    var plantName: String? = null
    var image: Int = 0
    var pos: Int = 0
    var sprite: PlantSprite? = null
    open fun activate(ch: Char?) {
        if (ch is Hero && ch.subClass == HeroSubClass.WARDEN) {
            Buffs.affect(ch, Barkskin::class.java)?.level(ch.HT / 3)
        }
        wither()
    }
    open fun wither() {
        val currentLevel = Dungeon.level ?: return
        currentLevel.uproot(pos)
        sprite?.kill()
        if (Dungeon.visible[pos]) {
            CellEmitter.get(pos).burst(LeafParticle.GENERAL, 6)
        }
        if (Dungeon.hero?.subClass == HeroSubClass.WARDEN) {
            if (Random.Int(5) == 0) {
                val seed = Generator.random(Generator.Category.SEED)
                if (seed != null) {
                    currentLevel.drop(seed, pos).sprite?.drop()
                }
            }
            if (Random.Int(5) == 0) {
                currentLevel.drop(Dewdrop(), pos).sprite?.drop()
            }
        }
    }
    override fun restoreFromBundle(bundle: Bundle) {
        pos = bundle.getInt(POS)
    }
    override fun storeInBundle(bundle: Bundle) {
        bundle.put(POS, pos)
    }
    open fun desc(): String? {
        return null
    }
    open class Seed : Item() {
            protected var plantClass: Class<out Plant>? = null
            protected var plantName: String? = null
            var alchemyClass: Class<out Item>? = null
        init {
            stackable = true
            defaultAction = AC_THROW
        }
        override fun actions(hero: Hero): ArrayList<String> {
            val actions = super.actions(hero)
            actions.add(AC_PLANT)
            return actions
        }
        override fun onThrow(cell: Int) {
            val currentLevel = Dungeon.level ?: return
            if (currentLevel.map[cell] == Terrain.ALCHEMY || Level.pit[cell]) {
                super.onThrow(cell)
            } else {
                currentLevel.plant(this, cell)
            }
        }
        override fun execute(hero: Hero, action: String) {
            if (action == AC_PLANT) {
                hero.spend(TIME_TO_PLANT)
                hero.busy()
                (detach(hero.belongings.backpack) as Seed).onThrow(hero.pos)
                hero.sprite?.operate(hero.pos)
            } else {
                super.execute(hero, action)
            }
        }
        fun couch(pos: Int): Plant? {
            try {
                if (Dungeon.visible[pos]) {
                    Sample.play(Assets.SND_PLANT)
                }
                val pc = plantClass ?: return null
                val plant = pc.getDeclaredConstructor().newInstance()
                plant.pos = pos
                return plant
            } catch (e: Exception) {
                return null
            }
        }
        override val isUpgradable: Boolean
            get() = false
        override val isIdentified: Boolean
            get() = true
        override fun price(): Int {
            return 10 * quantity
        }
        override fun info(): String {
            return String.format(TXT_INFO, Utils.indefinite(plantName ?: "plant"), desc())
        }
        companion object {
            const val AC_PLANT = "PLANT"
            private const val TXT_INFO = "Throw this seed to the place where you want to grow %s.\n\n%s"
            private const val TIME_TO_PLANT = 1f
        }
    }
    companion object {
        private const val POS = "pos"
    }
}
