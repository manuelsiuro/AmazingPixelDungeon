package com.watabou.pixeldungeon.items.potions
import com.watabou.noosa.audio.Sample
import com.watabou.pixeldungeon.Assets
import com.watabou.pixeldungeon.Badges
import com.watabou.pixeldungeon.Dungeon
import com.watabou.pixeldungeon.actors.hero.Hero
import com.watabou.pixeldungeon.effects.Splash
import com.watabou.pixeldungeon.items.Item
import com.watabou.pixeldungeon.items.ItemStatusHandler
import com.watabou.pixeldungeon.levels.Level
import com.watabou.pixeldungeon.levels.Terrain
import com.watabou.pixeldungeon.scenes.GameScene
import com.watabou.pixeldungeon.sprites.ItemSprite
import com.watabou.pixeldungeon.sprites.ItemSpriteSheet
import com.watabou.pixeldungeon.utils.GLog
import com.watabou.pixeldungeon.windows.WndOptions
import com.watabou.utils.Bundle
import java.util.ArrayList
import java.util.HashSet
open class Potion : Item() {
    private var color: String? = null
    init {
        stackable = true
        defaultAction = AC_DRINK
        image = handler?.image(this) ?: 0
        color = handler?.label(this)
    }
    override fun actions(hero: Hero): ArrayList<String> {
        val actions = super.actions(hero)
        actions.add(AC_DRINK)
        return actions
    }
    override fun execute(hero: Hero, action: String) {
        if (action == AC_DRINK) {
            if (isKnown && (
                        this is PotionOfLiquidFlame ||
                                this is PotionOfToxicGas ||
                                this is PotionOfParalyticGas)
            ) {
                GameScene.show(
                    object : WndOptions(TXT_HARMFUL, TXT_R_U_SURE_DRINK, TXT_YES, TXT_NO) {
                        override fun onSelect(index: Int) {
                            if (index == 0) {
                                drink(hero)
                            }
                        }
                    }
                )
            } else {
                drink(hero)
            }
        } else {
            super.execute(hero, action)
        }
    }
    override fun doThrow(hero: Hero) {
        if (isKnown && (
                    this is PotionOfExperience ||
                            this is PotionOfHealing ||
                            this is PotionOfLevitation ||
                            this is PotionOfMindVision ||
                            this is PotionOfStrength ||
                            this is PotionOfInvisibility ||
                            this is PotionOfMight)
        ) {
            GameScene.show(
                object : WndOptions(TXT_BENEFICIAL, TXT_R_U_SURE_THROW, TXT_YES, TXT_NO) {
                    override fun onSelect(index: Int) {
                        if (index == 0) {
                            super@Potion.doThrow(hero)
                        }
                    }
                }
            )
        } else {
            super.doThrow(hero)
        }
    }
    protected open fun drink(hero: Hero) {
        detach(hero.belongings.backpack)
        hero.spend(TIME_TO_DRINK)
        hero.busy()
        onThrow(hero.pos)
        Sample.play(Assets.SND_DRINK)
        hero.sprite?.operate(hero.pos)
    }
    override fun onThrow(cell: Int) {
        val hero = Dungeon.hero
        if (hero != null && hero.pos == cell) {
            apply(hero)
        } else if (Dungeon.level?.map?.get(cell) == Terrain.WELL || Level.pit[cell]) {
            super.onThrow(cell)
        } else {
            shatter(cell)
        }
    }
    open fun apply(hero: Hero) {
        shatter(hero.pos)
    }
    open fun shatter(cell: Int) {
        if (Dungeon.visible[cell]) {
            GLog.i("The flask shatters and ${color()} liquid splashes harmlessly")
            Sample.play(Assets.SND_SHATTER)
            splash(cell)
        }
    }
    val isKnown: Boolean
        get() = handler?.isKnown(this) == true
    fun setKnown() {
        if (!isKnown) {
            handler?.know(this)
        }
        Badges.validateAllPotionsIdentified()
    }
    override fun identify(): Item {
        setKnown()
        return this
    }
    protected fun color(): String? {
        return color
    }
    override fun name(): String {
        return if (isKnown) name else "$color potion"
    }
    override fun info(): String {
        return if (isKnown)
            desc()
        else
            "This flask contains a swirling $color liquid. " +
                    "Who knows what it will do when drunk or thrown?"
    }
    override val isIdentified: Boolean
        get() = isKnown
    override val isUpgradable: Boolean
        get() = false
    override fun price(): Int {
        return 20 * quantity
    }
    protected open fun splash(cell: Int) {
        val color = ItemSprite.pick(image, 8, 10)
        Splash.at(cell, color, 5)
    }
    companion object {
        const val AC_DRINK = "DRINK"
        private const val TXT_HARMFUL = "Harmful potion!"
        private const val TXT_BENEFICIAL = "Beneficial potion"
        private const val TXT_YES = "Yes, I know what I'm doing"
        private const val TXT_NO = "No, I changed my mind"
        private const val TXT_R_U_SURE_DRINK =
            "Are you sure you want to drink it? In most cases you should throw such potions at your enemies."
        private const val TXT_R_U_SURE_THROW =
            "Are you sure you want to throw it? In most cases it makes sense to drink it."
        private const val TIME_TO_DRINK = 1f
        private val potions = arrayOf(
            PotionOfHealing::class.java,
            PotionOfExperience::class.java,
            PotionOfToxicGas::class.java,
            PotionOfLiquidFlame::class.java,
            PotionOfStrength::class.java,
            PotionOfParalyticGas::class.java,
            PotionOfLevitation::class.java,
            PotionOfMindVision::class.java,
            PotionOfPurity::class.java,
            PotionOfInvisibility::class.java,
            PotionOfMight::class.java,
            PotionOfFrost::class.java
        )
        private val colors = arrayOf(
            "turquoise", "crimson", "azure", "jade", "golden", "magenta",
            "charcoal", "ivory", "amber", "bistre", "indigo", "silver"
        )
        private val images = arrayOf(
            ItemSpriteSheet.POTION_TURQUOISE,
            ItemSpriteSheet.POTION_CRIMSON,
            ItemSpriteSheet.POTION_AZURE,
            ItemSpriteSheet.POTION_JADE,
            ItemSpriteSheet.POTION_GOLDEN,
            ItemSpriteSheet.POTION_MAGENTA,
            ItemSpriteSheet.POTION_CHARCOAL,
            ItemSpriteSheet.POTION_IVORY,
            ItemSpriteSheet.POTION_AMBER,
            ItemSpriteSheet.POTION_BISTRE,
            ItemSpriteSheet.POTION_INDIGO,
            ItemSpriteSheet.POTION_SILVER
        )
        private var handler: ItemStatusHandler<Potion>? = null
        fun initColors() {
            handler = ItemStatusHandler(potions, colors, images)
        }
        fun save(bundle: Bundle) {
            handler?.save(bundle)
        }
        fun restore(bundle: Bundle) {
            handler = ItemStatusHandler(potions, colors, images, bundle)
        }
        fun getKnown(): HashSet<Class<out Potion>> {
            return handler?.known() ?: HashSet()
        }
        fun getUnknown(): HashSet<Class<out Potion>> {
            return handler?.unknown() ?: HashSet()
        }
        fun allKnown(): Boolean {
            return handler?.known()?.size == potions.size
        }
    }
}
