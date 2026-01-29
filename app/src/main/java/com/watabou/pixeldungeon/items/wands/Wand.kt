package com.watabou.pixeldungeon.items.wands
import com.watabou.noosa.audio.Sample
import com.watabou.pixeldungeon.Assets
import com.watabou.pixeldungeon.Badges
import com.watabou.pixeldungeon.Dungeon
import com.watabou.pixeldungeon.actors.Actor
import com.watabou.pixeldungeon.actors.Char
import com.watabou.pixeldungeon.actors.buffs.Buff
import com.watabou.pixeldungeon.actors.buffs.Invisibility
import com.watabou.pixeldungeon.actors.hero.Hero
import com.watabou.pixeldungeon.actors.hero.HeroClass
import com.watabou.pixeldungeon.effects.MagicMissile
import com.watabou.pixeldungeon.items.Item
import com.watabou.pixeldungeon.items.ItemStatusHandler
import com.watabou.pixeldungeon.items.KindOfWeapon
import com.watabou.pixeldungeon.items.bags.Bag
import com.watabou.pixeldungeon.items.rings.RingOfPower
import com.watabou.pixeldungeon.mechanics.Ballistica
import com.watabou.pixeldungeon.scenes.CellSelector
import com.watabou.pixeldungeon.scenes.GameScene
import com.watabou.pixeldungeon.sprites.ItemSpriteSheet
import com.watabou.pixeldungeon.ui.QuickSlot
import com.watabou.pixeldungeon.utils.GLog
import com.watabou.utils.Bundle
import com.watabou.utils.Callback
import com.watabou.utils.Random
import java.util.ArrayList
import kotlin.math.max
import kotlin.math.sqrt
abstract class Wand : KindOfWeapon() {
    var maxCharges: Int = initialCharges()
    var curCharges: Int = maxCharges
    protected var charger: Charger? = null
    private var curChargeKnown = false
    private var usagesToKnow = USAGES_TO_KNOW
    protected var hitChars: Boolean = true
    private var wood: String? = null
    init {
        defaultAction = AC_ZAP
        try {
            image = handler?.image(this) ?: 0
            wood = handler?.label(this)
        } catch (e: Exception) {
            // Wand of Magic Missile
        }
    }
    override fun actions(hero: Hero): ArrayList<String> {
        val actions = super.actions(hero)
        if (curCharges > 0 || !curChargeKnown) {
            actions.add(AC_ZAP)
        }
        if (hero.heroClass != HeroClass.MAGE) {
            actions.remove(AC_EQUIP)
            actions.remove(AC_UNEQUIP)
        }
        return actions
    }
    override fun doUnequip(hero: Hero, collect: Boolean, single: Boolean): Boolean {
        onDetach()
        return super.doUnequip(hero, collect, single)
    }
    override fun activate(hero: Hero) {
        charge(hero)
    }
    override fun execute(hero: Hero, action: String) {
        if (action == AC_ZAP) {
            Item.curUser = hero
            Item.curItem = this
            GameScene.selectCell(zapper)
        } else {
            super.execute(hero, action)
        }
    }
    protected abstract fun onZap(cell: Int)
    override fun collect(container: Bag?): Boolean {
        if (super.collect(container)) {
            container?.owner?.let { charge(it) }
            return true
        } else {
            return false
        }
    }
    fun charge(owner: Char) {
        if (charger == null) {
            charger = Charger()
        }
        charger?.attachTo(owner)
    }
    override fun onDetach() {
        stopCharging()
    }
    fun stopCharging() {
        charger?.detach()
        charger = null
    }
    fun power(): Int {
        val eLevel = effectiveLevel()
        val c = charger
        return if (c != null) {
            val t = c.target
            val power = t?.buff(RingOfPower.Power::class.java)
            if (power == null) eLevel else max(eLevel + power.level, 0)
        } else {
            eLevel
        }
    }
    protected open fun isKnown(): Boolean {
        return handler?.isKnown(this) == true
    }
    open fun setKnown() {
        if (!isKnown()) {
            handler?.know(this)
        }
        Badges.validateAllWandsIdentified()
    }
    override fun identify(): Item {
        setKnown()
        curChargeKnown = true
        super.identify()
        updateQuickslot()
        return this
    }
    override fun toString(): String {
        val sb = StringBuilder(super.toString())
        val status = status()
        if (status != null) {
            sb.append(" ($status)")
        }
        if (isBroken) {
            sb.insert(0, "broken ")
        }
        return sb.toString()
    }
    override fun name(): String {
        return if (isKnown()) name else "$wood wand"
    }
    override fun info(): String {
        val info = StringBuilder(if (isKnown()) desc() else String.format(TXT_WOOD, wood))
        val hero = Dungeon.hero
        if (hero != null && hero.heroClass == HeroClass.MAGE) {
            info.append("\n\n")
            if (levelKnown) {
                val min = min()
                info.append(String.format(TXT_DAMAGE, min + (max() - min) / 2))
            } else {
                info.append(String.format(TXT_WEAPON))
            }
        }
        return info.toString()
    }
    override val isIdentified: Boolean
        get() = super.isIdentified && isKnown() && curChargeKnown
    override fun status(): String? {
        return if (levelKnown) {
            (if (curChargeKnown) curCharges else "?").toString() + "/" + maxCharges
        } else {
            null
        }
    }
    override fun upgrade(): Item {
        super.upgrade()
        updateLevel()
        curCharges = Math.min(curCharges + 1, maxCharges)
        updateQuickslot()
        return this
    }
    override fun degrade(): Item {
        super.degrade()
        updateLevel()
        updateQuickslot()
        return this
    }
    override fun maxDurability(lvl: Int): Int {
        return 6 * (if (lvl < 16) 16 - lvl else 1)
    }
    protected fun updateLevel() {
        maxCharges = Math.min(initialCharges() + level(), 9)
        curCharges = Math.min(curCharges, maxCharges)
    }
    protected open fun initialCharges(): Int {
        return 2
    }
    override fun min(): Int {
        val tier = 1 + effectiveLevel() / 3
        return tier
    }
    override fun max(): Int {
        val level = effectiveLevel()
        val tier = 1 + level / 3
        return (tier * tier - tier + 10) / 2 + level
    }
    protected open fun fx(cell: Int, callback: Callback) {
        val user = Item.curUser ?: return
        val parent = user.sprite?.parent ?: return
        MagicMissile.blueLight(parent, user.pos, cell, callback)
        Sample.play(Assets.SND_ZAP)
    }
    protected fun wandUsed() {
        curCharges--
        if (!isIdentified && --usagesToKnow <= 0) {
            identify()
            GLog.w(TXT_IDENTIFY, name())
        } else {
            updateQuickslot()
        }
        use()
        Item.curUser?.spendAndNext(TIME_TO_ZAP)
    }
    override fun random(): Item {
        if (Random.Float() < 0.5f) {
            upgrade()
            if (Random.Float() < 0.15f) {
                upgrade()
            }
        }
        return this
    }
    override fun price(): Int {
        return considerState(50)
    }
    override fun storeInBundle(bundle: Bundle) {
        super.storeInBundle(bundle)
        bundle.put(UNFAMILIRIARITY, usagesToKnow)
        bundle.put(MAX_CHARGES, maxCharges)
        bundle.put(CUR_CHARGES, curCharges)
        bundle.put(CUR_CHARGE_KNOWN, curChargeKnown)
    }
    override fun restoreFromBundle(bundle: Bundle) {
        super.restoreFromBundle(bundle)
        usagesToKnow = bundle.getInt(UNFAMILIRIARITY)
        if (usagesToKnow == 0) {
            usagesToKnow = USAGES_TO_KNOW
        }
        maxCharges = bundle.getInt(MAX_CHARGES)
        curCharges = bundle.getInt(CUR_CHARGES)
        curChargeKnown = bundle.getBoolean(CUR_CHARGE_KNOWN)
    }
    protected inner class Charger : Buff() {
        override fun attachTo(target: Char): Boolean {
            super.attachTo(target)
            delay()
            return true
        }
        override fun act(): Boolean {
            if (curCharges < maxCharges) {
                curCharges++
                updateQuickslot()
            }
            delay()
            return true
        }
        fun delay() {
            val t = target
            val time2charge = if (t is Hero && t.heroClass == HeroClass.MAGE)
                CHARGE_DELAY / sqrt((1 + effectiveLevel()).toDouble()).toFloat()
            else
                CHARGE_DELAY
            spend(time2charge)
        }
    }
    companion object {
        private const val USAGES_TO_KNOW = 40
        const val AC_ZAP = "ZAP"
        private const val TXT_WOOD = "This thin %s wand is warm to the touch. Who knows what it will do when used?"
        private const val TXT_DAMAGE = "When this wand is used as a melee weapon, its average damage is %d points per hit."
        private const val TXT_WEAPON = "You can use this wand as a melee weapon."
        private const val TXT_FIZZLES = "your wand fizzles; it must be out of charges for now"
        private const val TXT_SELF_TARGET = "You can't target yourself"
        private const val TXT_IDENTIFY = "You are now familiar enough with your %s."
        private const val TIME_TO_ZAP = 1f
        private const val CHARGE_DELAY = 40f
        private const val UNFAMILIRIARITY = "unfamiliarity"
        private const val MAX_CHARGES = "maxCharges"
        private const val CUR_CHARGES = "curCharges"
        private const val CUR_CHARGE_KNOWN = "curChargeKnown"
        private val wands = arrayOf(
            WandOfTeleportation::class.java,
            WandOfSlowness::class.java,
            WandOfFirebolt::class.java,
            WandOfPoison::class.java,
            WandOfRegrowth::class.java,
            WandOfBlink::class.java,
            WandOfLightning::class.java,
            WandOfAmok::class.java,
            WandOfReach::class.java,
            WandOfFlock::class.java,
            WandOfDisintegration::class.java,
            WandOfAvalanche::class.java
        )
        private val woods = arrayOf(
            "holly", "yew", "ebony", "cherry", "teak", "rowan", "willow", "mahogany", "bamboo", "purpleheart", "oak", "birch"
        )
        private val images = arrayOf(
            ItemSpriteSheet.WAND_HOLLY,
            ItemSpriteSheet.WAND_YEW,
            ItemSpriteSheet.WAND_EBONY,
            ItemSpriteSheet.WAND_CHERRY,
            ItemSpriteSheet.WAND_TEAK,
            ItemSpriteSheet.WAND_ROWAN,
            ItemSpriteSheet.WAND_WILLOW,
            ItemSpriteSheet.WAND_MAHOGANY,
            ItemSpriteSheet.WAND_BAMBOO,
            ItemSpriteSheet.WAND_PURPLEHEART,
            ItemSpriteSheet.WAND_OAK,
            ItemSpriteSheet.WAND_BIRCH
        )
        private var handler: ItemStatusHandler<Wand>? = null
        fun initWoods() {
            handler = ItemStatusHandler(wands, woods, images)
        }
        fun save(bundle: Bundle) {
            handler?.save(bundle)
        }
        fun restore(bundle: Bundle) {
            handler = ItemStatusHandler(wands, woods, images, bundle)
        }
        fun allKnown(): Boolean {
            return handler?.known()?.size == wands.size
        }
        protected var zapper: CellSelector.Listener = object : CellSelector.Listener {
            override fun onSelect(cell: Int?) {
                if (cell != null) {
                    val user = Item.curUser ?: return
                    if (cell == user.pos) {
                        GLog.i(TXT_SELF_TARGET)
                        return
                    }
                    val curWand = Item.curItem as Wand
                    curWand.setKnown()
                    val targetCell = Ballistica.cast(user.pos, cell, true, curWand.hitChars)
                    user.sprite?.zap(targetCell)
                    val char = Actor.findChar(targetCell)
                    if (char != null) {
                        Item.curItem?.let { QuickSlot.target(it, char) }
                    }
                    if (curWand.curCharges > 0) {
                        user.busy()
                        curWand.fx(targetCell, object : Callback {
                            override fun call() {
                                curWand.onZap(targetCell)
                                curWand.wandUsed()
                            }
                        })
                        Invisibility.dispel()
                    } else {
                        user.spendAndNext(TIME_TO_ZAP)
                        GLog.w(TXT_FIZZLES)
                        curWand.levelKnown = true
                        curWand.updateQuickslot()
                    }
                }
            }
            override fun prompt(): String {
                return "Choose direction to zap"
            }
        }
    }
}
