package com.watabou.pixeldungeon.items.scrolls
import com.watabou.pixeldungeon.Badges
import com.watabou.pixeldungeon.actors.buffs.Blindness
import com.watabou.pixeldungeon.actors.hero.Hero
import com.watabou.pixeldungeon.items.Item
import com.watabou.pixeldungeon.items.ItemStatusHandler
import com.watabou.pixeldungeon.sprites.HeroSprite
import com.watabou.pixeldungeon.sprites.ItemSpriteSheet
import com.watabou.pixeldungeon.utils.GLog
import com.watabou.utils.Bundle
import java.util.ArrayList
import java.util.HashSet
abstract class Scroll : Item() {
    private var rune: String? = null
    init {
        stackable = true
        defaultAction = AC_READ
        image = handler?.image(this) ?: 0
        rune = handler?.label(this)
    }
    override fun restoreFromBundle(bundle: Bundle) {
        super.restoreFromBundle(bundle)
        // Ensure handler is initialized if restoring? 
        // Actually restore calls handler.image/label in init block. 
        // But handler is static.
    }
    override fun actions(hero: Hero): ArrayList<String> {
        val actions = super.actions(hero)
        actions.add(AC_READ)
        return actions
    }
    override fun execute(hero: Hero, action: String) {
        if (action == AC_READ) {
            if (hero.buff(Blindness::class.java) != null) {
                GLog.w(TXT_BLINDED)
            } else {
                curUser = hero
                curItem = detach(hero.belongings.backpack)
                doRead()
            }
        } else {
            super.execute(hero, action)
        }
    }
    protected abstract fun doRead()
    fun readAnimation() {
        curUser?.let {
            it.spend(TIME_TO_READ)
            it.busy()
            (it.sprite as HeroSprite).read()
        }
    }
    val isKnown: Boolean
        get() = handler?.isKnown(this) == true
    fun setKnown() {
        if (!isKnown) {
            handler?.know(this)
        }
        Badges.validateAllScrollsIdentified()
    }
    override fun identify(): Item {
        setKnown()
        return super.identify()
    }
    override fun name(): String {
        return if (isKnown) name else "scroll \"$rune\""
    }
    override fun info(): String {
        return if (isKnown)
            desc()
        else
            "This parchment is covered with indecipherable writing, and bears a title " +
                    "of rune $rune. Who knows what it will do when read aloud?"
    }
    override val isUpgradable: Boolean
        get() = false
    override val isIdentified: Boolean
        get() = isKnown
    override fun price(): Int {
        return 15 * quantity
    }
    companion object {
        private const val TXT_BLINDED = "You can't read a scroll while blinded"
        const val AC_READ = "READ"
        const val TIME_TO_READ = 1f
        private val scrolls = arrayOf(
            ScrollOfIdentify::class.java,
            ScrollOfMagicMapping::class.java,
            ScrollOfRecharging::class.java,
            ScrollOfRemoveCurse::class.java,
            ScrollOfTeleportation::class.java,
            ScrollOfChallenge::class.java,
            ScrollOfTerror::class.java,
            ScrollOfLullaby::class.java,
            ScrollOfPsionicBlast::class.java,
            ScrollOfMirrorImage::class.java,
            ScrollOfUpgrade::class.java,
            ScrollOfEnchantment::class.java
        )
        private val runes = arrayOf(
            "KAUNAN",
            "SOWILO",
            "LAGUZ",
            "YNGVI",
            "GYFU",
            "RAIDO",
            "ISAZ",
            "MANNAZ",
            "NAUDIZ",
            "BERKANAN",
            "ODAL",
            "TIWAZ"
        )
        private val images = arrayOf(
            ItemSpriteSheet.SCROLL_KAUNAN,
            ItemSpriteSheet.SCROLL_SOWILO,
            ItemSpriteSheet.SCROLL_LAGUZ,
            ItemSpriteSheet.SCROLL_YNGVI,
            ItemSpriteSheet.SCROLL_GYFU,
            ItemSpriteSheet.SCROLL_RAIDO,
            ItemSpriteSheet.SCROLL_ISAZ,
            ItemSpriteSheet.SCROLL_MANNAZ,
            ItemSpriteSheet.SCROLL_NAUDIZ,
            ItemSpriteSheet.SCROLL_BERKANAN,
            ItemSpriteSheet.SCROLL_ODAL,
            ItemSpriteSheet.SCROLL_TIWAZ
        )
        private var handler: ItemStatusHandler<Scroll>? = null
        fun initLabels() {
            handler = ItemStatusHandler(scrolls, runes, images)
        }
        fun save(bundle: Bundle) {
            handler?.save(bundle)
        }
        fun restore(bundle: Bundle) {
            handler = ItemStatusHandler(scrolls, runes, images, bundle)
        }
        fun getKnown(): HashSet<Class<out Scroll>> {
            return handler?.known() ?: HashSet()
        }
        fun getUnknown(): HashSet<Class<out Scroll>> {
            return handler?.unknown() ?: HashSet()
        }
        fun allKnown(): Boolean {
            return handler?.known()?.size == scrolls.size
        }
    }
}
