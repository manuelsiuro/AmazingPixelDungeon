package com.watabou.pixeldungeon.plants
import com.watabou.pixeldungeon.actors.Char
import com.watabou.pixeldungeon.actors.blobs.Blob
import com.watabou.pixeldungeon.actors.blobs.ConfusionGas
import com.watabou.pixeldungeon.items.potions.PotionOfInvisibility
import com.watabou.pixeldungeon.scenes.GameScene
import com.watabou.pixeldungeon.sprites.ItemSpriteSheet
class Dreamweed : Plant() {
    init {
        image = 3
        plantName = "Dreamweed"
    }
    override fun activate(ch: Char?) {
        super.activate(ch)
        if (ch != null) {
            GameScene.add(Blob.seed(pos, 400, ConfusionGas::class.java)!!)
        }
    }
    override fun desc(): String {
        return TXT_DESC
    }
    class Seed : Plant.Seed() {
        init {
            plantName = "Dreamweed"
            name = "seed of " + plantName
            image = ItemSpriteSheet.SEED_DREAMWEED
            plantClass = Dreamweed::class.java
            alchemyClass = PotionOfInvisibility::class.java
        }
        override fun desc(): String {
            return TXT_DESC
        }
    }
    companion object {
        private const val TXT_DESC = "Upon touching a Dreamweed it secretes a glittering cloud of confusing gas."
    }
}
