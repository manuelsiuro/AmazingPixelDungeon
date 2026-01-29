package com.watabou.pixeldungeon.items
import com.watabou.pixeldungeon.actors.buffs.Buffs
import com.watabou.pixeldungeon.actors.buffs.Light
import com.watabou.pixeldungeon.actors.hero.Hero
import com.watabou.pixeldungeon.effects.particles.FlameParticle
import com.watabou.pixeldungeon.sprites.ItemSpriteSheet
import java.util.ArrayList
class Torch : Item() {
    init {
        name = "torch"
        image = ItemSpriteSheet.TORCH
        stackable = true
        defaultAction = AC_LIGHT
    }
    override fun actions(hero: Hero): ArrayList<String> {
        val actions = super.actions(hero)
        actions.add(AC_LIGHT)
        return actions
    }
    override fun execute(hero: Hero, action: String) {
        if (action == AC_LIGHT) {
            hero.spend(TIME_TO_LIGHT)
            hero.busy()
            hero.sprite?.operate(hero.pos)
            detach(hero.belongings.backpack)
            Buffs.affect(hero, Light::class.java, Light.DURATION)
            val emitter = hero.sprite?.centerEmitter()
            emitter?.start(FlameParticle.FACTORY, 0.2f, 3)
        } else {
            super.execute(hero, action)
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
        return "It's an indispensable item in The Demon Halls, which are notorious for their poor ambient lighting."
    }
    companion object {
        const val AC_LIGHT = "LIGHT"
        const val TIME_TO_LIGHT = 1f
    }
}
