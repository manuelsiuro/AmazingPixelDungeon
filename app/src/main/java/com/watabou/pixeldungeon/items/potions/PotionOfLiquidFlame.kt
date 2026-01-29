package com.watabou.pixeldungeon.items.potions
import com.watabou.noosa.audio.Sample
import com.watabou.pixeldungeon.Assets
import com.watabou.pixeldungeon.Dungeon
import com.watabou.pixeldungeon.actors.blobs.Blob
import com.watabou.pixeldungeon.actors.blobs.Fire
import com.watabou.pixeldungeon.scenes.GameScene
class PotionOfLiquidFlame : Potion() {
    init {
        name = "Potion of Liquid Flame"
    }
    override fun shatter(cell: Int) {
        if (Dungeon.visible[cell]) {
            setKnown()
            splash(cell)
            Sample.play(Assets.SND_SHATTER)
        }
        Blob.seed(cell, 2, Fire::class.java)?.let { GameScene.add(it) }
    }
    override fun desc(): String {
        return "This flask contains an unstable compound which will burst " +
                "violently into flame upon exposure to open air."
    }
    override fun price(): Int {
        return if (isKnown) 40 * quantity else super.price()
    }
}
