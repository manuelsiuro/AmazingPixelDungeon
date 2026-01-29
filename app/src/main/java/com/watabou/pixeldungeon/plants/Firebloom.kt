package com.watabou.pixeldungeon.plants
import com.watabou.pixeldungeon.Dungeon
import com.watabou.pixeldungeon.actors.Char
import com.watabou.pixeldungeon.actors.blobs.Blob
import com.watabou.pixeldungeon.actors.blobs.Fire
import com.watabou.pixeldungeon.effects.CellEmitter
import com.watabou.pixeldungeon.effects.particles.FlameParticle
import com.watabou.pixeldungeon.items.potions.PotionOfLiquidFlame
import com.watabou.pixeldungeon.scenes.GameScene
import com.watabou.pixeldungeon.sprites.ItemSpriteSheet
class Firebloom : Plant() {
    init {
        image = 0
        plantName = "Firebloom"
    }
    override fun activate(ch: Char?) {
        super.activate(ch)
        GameScene.add(Blob.seed(pos, 2, Fire::class.java)!!)
        if (Dungeon.visible[pos]) {
            CellEmitter.get(pos).burst(FlameParticle.FACTORY, 5)
        }
    }
    override fun desc(): String {
        return TXT_DESC
    }
    class Seed : Plant.Seed() {
        init {
            plantName = "Firebloom"
            name = "seed of " + plantName
            image = ItemSpriteSheet.SEED_FIREBLOOM
            plantClass = Firebloom::class.java
            alchemyClass = PotionOfLiquidFlame::class.java
        }
        override fun desc(): String {
            return TXT_DESC
        }
    }
    companion object {
        private const val TXT_DESC = "When something touches a Firebloom, it bursts into flames."
    }
}
