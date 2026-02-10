package com.watabou.pixeldungeon.items.food
import com.watabou.pixeldungeon.actors.buffs.Buffs
import com.watabou.pixeldungeon.actors.buffs.Poison
import com.watabou.pixeldungeon.actors.hero.Hero
import com.watabou.pixeldungeon.sprites.ItemSpriteSheet
import com.watabou.pixeldungeon.utils.GLog
import com.watabou.utils.Random
class MushroomSoup : Food() {
    init {
        name = "mushroom soup"
        image = ItemSpriteSheet.MUSHROOM_SOUP
        energy = 260f
        message = "That soup was earthy and warm."
    }
    override fun execute(hero: Hero, action: String) {
        super.execute(hero, action)
        if (action == AC_EAT) {
            if (Random.Int(4) == 0) {
                GLog.w("You feel queasy... that mushroom was not edible!")
                Buffs.affect(hero, Poison::class.java)?.set(Poison.durationFactor(hero) * hero.HT / 10)
            }
        }
    }
    override fun info(): String {
        return "A steaming bowl of mushroom soup. Most dungeon mushrooms are harmless... most."
    }
    override fun price(): Int {
        return 8 * quantity
    }
}
