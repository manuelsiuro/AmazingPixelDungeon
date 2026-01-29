package com.watabou.pixeldungeon.items
import com.watabou.noosa.audio.Sample
import com.watabou.pixeldungeon.Assets
import com.watabou.pixeldungeon.Dungeon
import com.watabou.pixeldungeon.actors.hero.Hero
import com.watabou.pixeldungeon.actors.hero.HeroClass
import com.watabou.pixeldungeon.effects.Speck
import com.watabou.pixeldungeon.sprites.CharSprite
import com.watabou.pixeldungeon.sprites.ItemSpriteSheet
import kotlin.math.min
class Dewdrop : Item() {
    init {
        name = "dewdrop"
        image = ItemSpriteSheet.DEWDROP
        stackable = true
    }
    override fun doPickUp(hero: Hero): Boolean {
        val vial = hero.belongings.getItem(DewVial::class.java)
        if (hero.HP < hero.HT || vial == null || vial.isFull) {
            var value = 1 + (Dungeon.depth - 1) / 5
            if (hero.heroClass == HeroClass.HUNTRESS) {
                value++
            }
            val effect = min(hero.HT - hero.HP, value * quantity)
            if (effect > 0) {
                hero.HP += effect
                hero.sprite?.emitter()?.burst(Speck.factory(Speck.HEALING), 1)
                hero.sprite?.showStatus(CharSprite.POSITIVE, TXT_VALUE, effect)
            }
        } else {
            vial.collectDew(this)
        }
        Sample.play(Assets.SND_DEWDROP)
        hero.spendAndNext(TIME_TO_PICK_UP)
        return true
    }
    override fun info(): String {
        return "A crystal clear dewdrop."
    }
    companion object {
        private const val TXT_VALUE = "%+dHP"
    }
}
