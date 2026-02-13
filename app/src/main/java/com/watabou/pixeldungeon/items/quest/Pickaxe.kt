package com.watabou.pixeldungeon.items.quest
import com.watabou.noosa.audio.Sample
import com.watabou.pixeldungeon.Assets
import com.watabou.pixeldungeon.Dungeon
import com.watabou.pixeldungeon.actors.Char
import com.watabou.pixeldungeon.actors.buffs.Hunger
import com.watabou.pixeldungeon.actors.hero.Hero
import com.watabou.pixeldungeon.actors.mobs.Bat
import com.watabou.pixeldungeon.effects.CellEmitter
import com.watabou.pixeldungeon.effects.Speck
import com.watabou.pixeldungeon.items.weapon.Weapon
import com.watabou.pixeldungeon.levels.Level
import com.watabou.pixeldungeon.levels.Terrain
import com.watabou.pixeldungeon.levels.features.HarvestableWall
import com.watabou.pixeldungeon.scenes.GameScene
import com.watabou.pixeldungeon.sprites.ItemSprite
import com.watabou.pixeldungeon.sprites.ItemSpriteSheet
import com.watabou.pixeldungeon.ui.BuffIndicator
import com.watabou.pixeldungeon.utils.GLog
import com.watabou.utils.Bundle
import com.watabou.utils.Callback
class Pickaxe : Weapon() {
    var bloodStained = false
    init {
        name = "pickaxe"
        image = ItemSpriteSheet.PICKAXE
        unique = true
        defaultAction = AC_MINE
        STR = 14
    }
    override fun min(): Int {
        return 3
    }
    override fun max(): Int {
        return 12
    }
    override fun actions(hero: Hero): ArrayList<String> {
        val actions = super.actions(hero)
        actions.add(AC_MINE)
        return actions
    }
    override fun execute(hero: Hero, action: String) {
        if (action == AC_MINE) {
            val level = Dungeon.level ?: return

            // Check for dark gold vein first (quest mining on depths 11-15)
            if (Dungeon.depth in 11..15) {
                for (i in Level.NEIGHBOURS8.indices) {
                    val pos = hero.pos + Level.NEIGHBOURS8[i]
                    if (level.map[pos] == Terrain.WALL_DECO) {
                        hero.spend(TIME_TO_MINE)
                        hero.busy()
                        hero.sprite?.attack(pos, object : Callback {
                            override fun call() {
                                CellEmitter.center(pos).burst(Speck.factory(Speck.STAR), 7)
                                Sample.play(Assets.SND_EVOKE)
                                Level.set(pos, Terrain.WALL)
                                GameScene.updateMap(pos)
                                val gold = DarkGold()
                                val currentHero = Dungeon.hero
                                if (currentHero != null && gold.doPickUp(currentHero)) {
                                    GLog.i(Hero.TXT_YOU_NOW_HAVE, gold.name())
                                } else {
                                    Dungeon.level?.drop(gold, hero.pos)?.sprite?.drop()
                                }
                                val hunger = hero.buff(Hunger::class.java)
                                if (hunger != null && !hunger.isStarving) {
                                    hunger.satisfy(-Hunger.STARVING / 10f)
                                    BuffIndicator.refreshHero()
                                }
                                hero.onOperateComplete()
                            }
                        })
                        return
                    }
                }
            }

            // Check for harvestable walls (crafting material mining)
            for (i in Level.NEIGHBOURS8.indices) {
                val pos = hero.pos + Level.NEIGHBOURS8[i]
                if (level.map[pos] == Terrain.WALL && level.harvestable[pos]) {
                    hero.spend(TIME_TO_MINE)
                    hero.busy()
                    hero.sprite?.attack(pos, object : Callback {
                        override fun call() {
                            HarvestableWall.mine(level, pos, hero)
                            val hunger = hero.buff(Hunger::class.java)
                            if (hunger != null && !hunger.isStarving) {
                                hunger.satisfy(-Hunger.STARVING / 10f)
                                BuffIndicator.refreshHero()
                            }
                            hero.onOperateComplete()
                        }
                    })
                    return
                }
            }

            GLog.w(TXT_NO_VEIN)
        } else {
            super.execute(hero, action)
        }
    }
    override val isUpgradable: Boolean
        get() = false
    override val isIdentified: Boolean
        get() = true
    override fun proc(attacker: Char, defender: Char, damage: Int) {
        if (!bloodStained && defender is Bat && defender.HP <= damage) {
            bloodStained = true
            updateQuickslot()
        }
    }
    override fun storeInBundle(bundle: Bundle) {
        super.storeInBundle(bundle)
        bundle.put(BLOODSTAINED, bloodStained)
    }
    override fun restoreFromBundle(bundle: Bundle) {
        super.restoreFromBundle(bundle)
        bloodStained = bundle.getBoolean(BLOODSTAINED)
    }
    override fun glowing(): ItemSprite.Glowing? {
        return if (bloodStained) BLOODY else null
    }
    override fun info(): String {
        return "This is a large and sturdy tool for breaking rocks. Probably it can be used as a weapon."
    }
    companion object {
        const val AC_MINE = "MINE"
        const val TIME_TO_MINE = 2f
        private const val TXT_NO_VEIN = "There is nothing to mine nearby."
        private val BLOODY = ItemSprite.Glowing(0x550000)
        private const val BLOODSTAINED = "bloodStained"
    }
}
