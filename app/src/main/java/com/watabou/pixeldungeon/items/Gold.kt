package com.watabou.pixeldungeon.items
import com.watabou.noosa.audio.Sample
import com.watabou.pixeldungeon.Assets
import com.watabou.pixeldungeon.Badges
import com.watabou.pixeldungeon.Dungeon
import com.watabou.pixeldungeon.Statistics
import com.watabou.pixeldungeon.actors.hero.Hero
import com.watabou.pixeldungeon.scenes.GameScene
import com.watabou.pixeldungeon.sprites.CharSprite
import com.watabou.pixeldungeon.sprites.ItemSpriteSheet
import com.watabou.pixeldungeon.utils.Utils
import com.watabou.utils.Bundle
import com.watabou.utils.Random
import java.util.ArrayList
class Gold : Item {
    constructor() : this(1)
    constructor(value: Int) {
        this.quantity = value
        name = "gold"
        image = ItemSpriteSheet.GOLD
        stackable = true
    }
    override fun actions(hero: Hero): ArrayList<String> {
        return ArrayList()
    }
    override fun doPickUp(hero: Hero): Boolean {
        Dungeon.gold += quantity
        Statistics.goldCollected += quantity
        Badges.validateGoldCollected()
        GameScene.pickUp(this)
        hero.sprite?.showStatus(CharSprite.NEUTRAL, TXT_VALUE, quantity)
        hero.spendAndNext(TIME_TO_PICK_UP)
        Sample.play(Assets.SND_GOLD, 1f, 1f, Random.Float(0.9f, 1.1f))
        return true
    }
    override val isUpgradable: Boolean
        get() = false
    override val isIdentified: Boolean
        get() = true
    override fun info(): String {
        return when (quantity) {
            0 -> TXT_COLLECT
            1 -> TXT_INFO_1
            else -> Utils.format(TXT_INFO, quantity)
        }
    }
    override fun random(): Item {
        quantity = Random.Int(20 + Dungeon.depth * 10, 40 + Dungeon.depth * 20)
        return this
    }
    override fun storeInBundle(bundle: Bundle) {
        super.storeInBundle(bundle)
        bundle.put(VALUE, quantity)
    }
    override fun restoreFromBundle(bundle: Bundle) {
        super.restoreFromBundle(bundle)
        quantity = bundle.getInt(VALUE)
    }
    companion object {
        private const val TXT_COLLECT = "Collect gold coins to spend them later in a shop."
        private const val TXT_INFO = "A pile of %d gold coins. $TXT_COLLECT"
        private const val TXT_INFO_1 = "One gold coin. $TXT_COLLECT"
        private const val TXT_VALUE = "%+d"
        private const val VALUE = "value"
    }
}
