package com.watabou.pixeldungeon.actors.blobs
import com.watabou.noosa.audio.Sample
import com.watabou.pixeldungeon.Assets
import com.watabou.pixeldungeon.Dungeon
import com.watabou.pixeldungeon.Journal
import com.watabou.pixeldungeon.Journal.Feature
import com.watabou.pixeldungeon.actors.buffs.Hunger
import com.watabou.pixeldungeon.actors.hero.Hero
import com.watabou.pixeldungeon.effects.BlobEmitter
import com.watabou.pixeldungeon.effects.CellEmitter
import com.watabou.pixeldungeon.effects.Speck
import com.watabou.pixeldungeon.effects.particles.ShaftParticle
import com.watabou.pixeldungeon.items.DewVial
import com.watabou.pixeldungeon.items.Item
import com.watabou.pixeldungeon.items.potions.PotionOfHealing
import com.watabou.pixeldungeon.utils.GLog
class WaterOfHealth : WellWater() {
    override fun affectHero(hero: Hero): Boolean {
        Sample.play(Assets.SND_DRINK)
        PotionOfHealing.heal(hero)
        hero.belongings.uncurseEquipped()
        hero.buff(Hunger::class.java)?.satisfy(Hunger.STARVING)
        CellEmitter.get(pos).start(ShaftParticle.FACTORY, 0.2f, 3)
        Dungeon.hero!!.interrupt()
        GLog.p(TXT_PROCCED)
        Journal.remove(Feature.WELL_OF_HEALTH)
        return true
    }
    override fun affectItem(item: Item): Item? {
        if (item is DewVial && !item.isFull) {
            item.fill()
            Journal.remove(Feature.WELL_OF_HEALTH)
            return item
        }
        return null
    }
    override fun use(emitter: BlobEmitter) {
        super.use(emitter)
        emitter.start(Speck.factory(Speck.HEALING), 0.5f, 0)
    }
    override fun tileDesc(): String {
        return "Power of health radiates from the water of this well. " +
                "Take a sip from it to heal your wounds and satisfy hunger."
    }
    companion object {
        private const val TXT_PROCCED = "As you take a sip, you feel your wounds heal completely."
    }
}
