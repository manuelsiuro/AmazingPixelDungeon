package com.watabou.pixeldungeon.items.rings
import com.watabou.pixeldungeon.Badges
import com.watabou.pixeldungeon.Dungeon
import com.watabou.pixeldungeon.PixelDungeon
import com.watabou.pixeldungeon.actors.Char
import com.watabou.pixeldungeon.actors.buffs.Buff
import com.watabou.pixeldungeon.actors.hero.Hero
import com.watabou.pixeldungeon.actors.hero.HeroClass
import com.watabou.pixeldungeon.items.EquipableItem
import com.watabou.pixeldungeon.items.Item
import com.watabou.pixeldungeon.items.ItemStatusHandler
import com.watabou.pixeldungeon.sprites.ItemSpriteSheet
import com.watabou.pixeldungeon.utils.GLog
import com.watabou.pixeldungeon.utils.Utils
import com.watabou.pixeldungeon.windows.WndOptions
import com.watabou.utils.Bundle
import com.watabou.utils.Random
import java.util.ArrayList
open class Ring : EquipableItem() {
    protected var buff: Buff? = null
    private var gem: String? = null
    private var ticksToKnow = TICKS_TO_KNOW
    init {
        syncGem()
    }
    fun syncGem() {
        image = handler?.image(this) ?: 0
        gem = handler?.label(this)
    }
    override fun actions(hero: Hero): ArrayList<String> {
        val actions = super.actions(hero)
        actions.add(if (isEquipped(hero)) AC_UNEQUIP else AC_EQUIP)
        return actions
    }
    override fun doEquip(hero: Hero): Boolean {
        if (hero.belongings.ring1 != null && hero.belongings.ring2 != null) {
            val r1 = hero.belongings.ring1 ?: return false
            val r2 = hero.belongings.ring2 ?: return false
            val scene = com.watabou.noosa.Game.scene() ?: return false
            scene.add(
                object : WndOptions(
                    TXT_UNEQUIP_TITLE, TXT_UNEQUIP_MESSAGE,
                    Utils.capitalize(r1.toString()),
                    Utils.capitalize(r2.toString())
                ) {
                    override fun onSelect(index: Int) {
                        detach(hero.belongings.backpack)
                        val equipped = if (index == 0) r1 else r2
                        if (equipped.doUnequip(hero, true, false)) {
                            doEquip(hero)
                        } else {
                            collect(hero.belongings.backpack)
                        }
                    }
                })
            return false
        } else {
            if (hero.belongings.ring1 == null) {
                hero.belongings.ring1 = this
            } else {
                hero.belongings.ring2 = this
            }
            detach(hero.belongings.backpack)
            activate(hero)
            cursedKnown = true
            if (cursed) {
                equipCursed(hero)
                GLog.n("your $this tightens around your finger painfully")
            }
            hero.spendAndNext(TIME_TO_EQUIP)
            return true
        }
    }
    open fun activate(ch: Char) {
        buff = buff()
        buff?.attachTo(ch)
    }
    override fun doUnequip(hero: Hero, collect: Boolean, single: Boolean): Boolean {
        if (super.doUnequip(hero, collect, single)) {
            if (hero.belongings.ring1 === this) {
                hero.belongings.ring1 = null
            } else {
                hero.belongings.ring2 = null
            }
            buff?.let { hero.remove(it) }
            buff = null
            return true
        } else {
            return false
        }
    }
    override fun isEquipped(hero: Hero): Boolean {
        return hero.belongings.ring1 === this || hero.belongings.ring2 === this
    }
    override fun effectiveLevel(): Int {
        return if (isBroken) 1 else level()
    }
    private fun renewBuff() {
        buff?.let { currentBuff ->
            val owner = currentBuff.target
            currentBuff.detach()
            buff = buff()
            owner?.let { o -> buff?.attachTo(o) }
        }
    }
    override fun getBroken() {
        renewBuff()
        super.getBroken()
    }
    override fun fix() {
        super.fix()
        renewBuff()
    }
    override fun maxDurability(lvl: Int): Int {
        if (lvl <= 1) {
            return Int.MAX_VALUE
        } else {
            return 100 * (if (lvl < 16) 16 - lvl else 1)
        }
    }
    open fun isKnown(): Boolean {
        return handler?.isKnown(this) ?: false
    }
    open fun setKnown() {
        if (!isKnown()) {
            handler?.know(this)
        }
        Badges.validateAllRingsIdentified()
    }
    override fun toString(): String {
        return if (levelKnown && isBroken)
            "broken " + super.toString()
        else
            super.toString()
    }
    override fun name(): String {
        return if (isKnown()) name else "$gem ring"
    }
    override fun desc(): String {
        return "This metal band is adorned with a large $gem gem " +
                "that glitters in the darkness. Who knows what effect it has when worn?"
    }
    override fun info(): String {
        val hero = Dungeon.hero
        if (hero != null && isEquipped(hero)) {
            return desc() + "\n\n" + "The " + name() + " is on your finger" +
                    (if (cursed) ", and because it is cursed, you are powerless to remove it." else ".")
        } else if (cursed && cursedKnown) {
            return desc() + "\n\nYou can feel a malevolent magic lurking within the " + name() + "."
        } else {
            return desc()
        }
    }
    override val isIdentified: Boolean
        get() = super.isIdentified && isKnown()
    override fun identify(): Item {
        setKnown()
        return super.identify()
    }
    override fun random(): Item {
        val lvl = Random.Int(1, 3)
        if (Random.Float() < 0.3f) {
            degrade(lvl)
            cursed = true
        } else {
            upgrade(lvl)
        }
        return this
    }
    override fun price(): Int {
        return considerState(80)
    }
    protected open fun buff(): RingBuff? {
        return null
    }
    override fun storeInBundle(bundle: Bundle) {
        super.storeInBundle(bundle)
        bundle.put(UNFAMILIRIARITY, ticksToKnow)
    }
    override fun restoreFromBundle(bundle: Bundle) {
        super.restoreFromBundle(bundle)
        ticksToKnow = bundle.getInt(UNFAMILIRIARITY)
        if (ticksToKnow == 0) {
            ticksToKnow = TICKS_TO_KNOW
        }
    }
    open inner class RingBuff : Buff() {
        var level: Int = 0
        init {
            level = this@Ring.effectiveLevel()
        }
        override fun attachTo(target: Char): Boolean {
            if (target is Hero && target.heroClass == HeroClass.ROGUE && !isKnown()) {
                setKnown()
                GLog.i(TXT_KNOWN, name())
                Badges.validateItemLevelAquired(this@Ring)
            }
            return super.attachTo(target)
        }
        override fun act(): Boolean {
            if (!isIdentified && --ticksToKnow <= 0) {
                val gemName = name()
                identify()
                GLog.w(TXT_IDENTIFY, gemName, this@Ring.toString())
                Badges.validateItemLevelAquired(this@Ring)
            }
            use()
            spend(TICK)
            return true
        }
    }
    companion object {
        private const val TICKS_TO_KNOW = 200
        private const val TIME_TO_EQUIP = 1f
        private const val TXT_IDENTIFY = "you are now familiar enough with your %s to identify it. It is %s."
        private const val TXT_UNEQUIP_TITLE = "Unequip one ring"
        private const val TXT_UNEQUIP_MESSAGE = "You can only wear two rings at a time. " + "Unequip one of your equipped rings."
        private const val UNFAMILIRIARITY = "unfamiliarity"
        private const val TXT_KNOWN = "This is a %s"
        private val rings = arrayOf(
            RingOfMending::class.java,
            RingOfDetection::class.java,
            RingOfShadows::class.java,
            RingOfPower::class.java,
            RingOfHerbalism::class.java,
            RingOfAccuracy::class.java,
            RingOfEvasion::class.java,
            RingOfSatiety::class.java,
            RingOfHaste::class.java,
            RingOfHaggler::class.java,
            RingOfElements::class.java,
            RingOfThorns::class.java
        )
        private val gems = arrayOf(
            "diamond",
            "opal",
            "garnet",
            "ruby",
            "amethyst",
            "topaz",
            "onyx",
            "tourmaline",
            "emerald",
            "sapphire",
            "quartz",
            "agate"
        )
        private val images = arrayOf(
            ItemSpriteSheet.RING_DIAMOND,
            ItemSpriteSheet.RING_OPAL,
            ItemSpriteSheet.RING_GARNET,
            ItemSpriteSheet.RING_RUBY,
            ItemSpriteSheet.RING_AMETHYST,
            ItemSpriteSheet.RING_TOPAZ,
            ItemSpriteSheet.RING_ONYX,
            ItemSpriteSheet.RING_TOURMALINE,
            ItemSpriteSheet.RING_EMERALD,
            ItemSpriteSheet.RING_SAPPHIRE,
            ItemSpriteSheet.RING_QUARTZ,
            ItemSpriteSheet.RING_AGATE
        )
        private var handler: ItemStatusHandler<Ring>? = null
        fun initGems() {
            handler = ItemStatusHandler(rings, gems, images)
        }
        fun save(bundle: Bundle) {
            handler?.save(bundle)
        }
        fun restore(bundle: Bundle) {
            handler = ItemStatusHandler(rings, gems, images, bundle)
        }
        fun allKnown(): Boolean {
            return handler?.known()?.size == rings.size - 2
        }
    }
}
