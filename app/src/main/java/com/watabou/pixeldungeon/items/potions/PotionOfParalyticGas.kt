package com.watabou.pixeldungeon.items.potions
import com.watabou.noosa.audio.Sample
import com.watabou.pixeldungeon.Assets
import com.watabou.pixeldungeon.Dungeon
import com.watabou.pixeldungeon.actors.blobs.Blob
import com.watabou.pixeldungeon.actors.blobs.ParalyticGas
import com.watabou.pixeldungeon.scenes.GameScene
class PotionOfParalyticGas : Potion() {
    init {
        name = "Potion of Paralytic Gas"
    }
    override fun shatter(cell: Int) {
        if (Dungeon.visible[cell]) {
            setKnown()
            splash(cell)
            Sample.play(Assets.SND_SHATTER)
        }
        Blob.seed(cell, 1000, ParalyticGas::class.java)?.let { GameScene.add(it) }
    }
    override fun desc(): String {
        return "Upon exposure to open air, the liquid in this flask will vaporize " +
                "into a numbing yellow haze. Anyone who inhales the cloud will be paralyzed " +
                "instantly, unable to move for some time after the cloud dissipates. This " +
                "item can be thrown at distant enemies to catch them within the effect of the gas."
    }
    override fun price(): Int {
        return if (isKnown) 40 * quantity else super.price()
    }
}
