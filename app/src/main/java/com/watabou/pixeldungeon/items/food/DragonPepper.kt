package com.watabou.pixeldungeon.items.food
import com.watabou.pixeldungeon.actors.buffs.Buffs
import com.watabou.pixeldungeon.actors.buffs.Burning
import com.watabou.pixeldungeon.actors.hero.Hero
import com.watabou.pixeldungeon.sprites.ItemSpriteSheet
import com.watabou.pixeldungeon.utils.GLog
class DragonPepper : Food() {
    init {
        name = "dragon pepper"
        image = ItemSpriteSheet.DRAGON_PEPPER
        energy = 80f
        message = "Your mouth is on fire!"
    }
    override fun execute(hero: Hero, action: String) {
        super.execute(hero, action)
        if (action == AC_EAT) {
            GLog.w("Flames erupt from your mouth!")
            Buffs.affect(hero, Burning::class.java)?.reignite(hero)
        }
    }
    override fun info(): String {
        return "A tiny crimson pepper that radiates heat. Eating it will satisfy " +
                "hunger, but the burning sensation is very real."
    }
    override fun price(): Int {
        return 10 * quantity
    }
}
