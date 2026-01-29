package com.watabou.pixeldungeon.items.potions
import com.watabou.noosa.audio.Sample
import com.watabou.pixeldungeon.Assets
import com.watabou.pixeldungeon.Dungeon
import com.watabou.pixeldungeon.actors.blobs.Blob
import com.watabou.pixeldungeon.actors.blobs.ToxicGas
import com.watabou.pixeldungeon.scenes.GameScene
class PotionOfToxicGas : Potion() {
    init {
        name = "Potion of Toxic Gas"
    }
    override fun shatter(cell: Int) {
        if (Dungeon.visible[cell]) {
            setKnown()
            splash(cell)
            Sample.play(Assets.SND_SHATTER)
        }
        Blob.seed(cell, 1000, ToxicGas::class.java)?.let { GameScene.add(it) }
    }
    override fun desc(): String {
        return "Uncorking or shattering this pressurized glass will cause " +
                "its contents to explode into a deadly cloud of toxic green gas. " +
                "You might choose to fling this potion at distant enemies " +
                "instead of uncorking it by hand."
    }
    override fun price(): Int {
        return if (isKnown) 40 * quantity else super.price()
    }
}
