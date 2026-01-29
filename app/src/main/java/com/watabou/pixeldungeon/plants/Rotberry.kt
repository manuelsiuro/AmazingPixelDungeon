package com.watabou.pixeldungeon.plants
import com.watabou.noosa.audio.Sample
import com.watabou.pixeldungeon.Assets
import com.watabou.pixeldungeon.Dungeon
import com.watabou.pixeldungeon.actors.Char
import com.watabou.pixeldungeon.actors.blobs.Blob
import com.watabou.pixeldungeon.actors.blobs.ToxicGas
import com.watabou.pixeldungeon.actors.buffs.Buff
import com.watabou.pixeldungeon.actors.buffs.Buffs
import com.watabou.pixeldungeon.actors.buffs.Roots
import com.watabou.pixeldungeon.effects.CellEmitter
import com.watabou.pixeldungeon.effects.Speck
import com.watabou.pixeldungeon.items.bags.Bag
import com.watabou.pixeldungeon.items.potions.PotionOfStrength
import com.watabou.pixeldungeon.scenes.GameScene
import com.watabou.pixeldungeon.sprites.ItemSpriteSheet
import com.watabou.pixeldungeon.utils.GLog
class Rotberry : Plant() {
    init {
        image = 7
        plantName = "Rotberry"
    }
    override fun activate(ch: Char?) {
        super.activate(ch)
        GameScene.add(Blob.seed(pos, 100, ToxicGas::class.java)!!)
        Dungeon.level!!.drop(Seed(), pos).sprite?.drop()
        if (ch != null) {
            Buffs.prolong(ch, Roots::class.java, Buff.TICK * 3f)
        }
    }
    override fun desc(): String {
        return TXT_DESC
    }
    class Seed : Plant.Seed() {
        init {
            plantName = "Rotberry"
            name = "seed of " + plantName
            image = ItemSpriteSheet.SEED_ROTBERRY
            plantClass = Rotberry::class.java
            alchemyClass = PotionOfStrength::class.java
        }
        override fun collect(container: Bag?): Boolean {
            if (super.collect(container)) {
                if (Dungeon.level != null) {
                    for (mob in Dungeon.level!!.mobs) {
                        mob.beckon(Dungeon.hero!!.pos)
                    }
                    GLog.w("The seed emits a roar that echoes throughout the dungeon!")
                    CellEmitter.center(Dungeon.hero!!.pos).start(Speck.factory(Speck.SCREAM), 0.3f, 3)
                    Sample.play(Assets.SND_CHALLENGE)
                }
                return true
            } else {
                return false
            }
        }
        override fun desc(): String {
            return TXT_DESC
        }
    }
    companion object {
        private const val TXT_DESC = "Berries of this shrub taste like sweet, sweet death."
    }
}
