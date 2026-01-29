package com.watabou.pixeldungeon.ui
import com.watabou.noosa.BitmapText
import com.watabou.noosa.ui.Button
import com.watabou.pixeldungeon.Dungeon
import com.watabou.pixeldungeon.items.Item
import com.watabou.pixeldungeon.items.armor.Armor
import com.watabou.pixeldungeon.items.weapon.Weapon
import com.watabou.pixeldungeon.items.weapon.melee.MeleeWeapon
import com.watabou.pixeldungeon.scenes.PixelScene
import com.watabou.pixeldungeon.sprites.ItemSprite
import com.watabou.pixeldungeon.sprites.ItemSpriteSheet
import com.watabou.pixeldungeon.utils.Utils
open class ItemSlot : Button {
    protected lateinit var icon: ItemSprite
    protected lateinit var topLeft: BitmapText
    protected lateinit var topRight: BitmapText
    protected lateinit var bottomRight: BitmapText
    constructor() : super()
    constructor(item: Item) : this() {
        item(item)
    }
    override fun createChildren() {
        super.createChildren()
        icon = ItemSprite()
        add(icon)
        topLeft = BitmapText(PixelScene.font1x)
        add(topLeft)
        topRight = BitmapText(PixelScene.font1x)
        add(topRight)
        bottomRight = BitmapText(PixelScene.font1x)
        add(bottomRight)
    }
    override fun layout() {
        super.layout()
        icon.x = x + (width - icon.width) / 2
        icon.y = y + (height - icon.height) / 2
        if (this::topLeft.isInitialized) {
            topLeft.x = x
            topLeft.y = y
        }
        if (this::topRight.isInitialized) {
            topRight.x = x + (width - topRight.width())
            topRight.y = y
        }
        if (this::bottomRight.isInitialized) {
            bottomRight.x = x + (width - bottomRight.width())
            bottomRight.y = y + (height - bottomRight.height())
        }
    }
    open fun item(item: Item?) {
        if (item == null) {
            active = false
            icon.visible = false
            topLeft.visible = false
            topRight.visible = false
            bottomRight.visible = false
        } else {
            active = true
            icon.visible = true
            topLeft.visible = true
            topRight.visible = true
            bottomRight.visible = true
            icon.view(item.image(), item.glowing())
            topLeft.text(item.status())
            val isArmor = item is Armor
            val isWeapon = item is Weapon
            if (isArmor || isWeapon) {
                if (item.levelKnown || (isWeapon && item !is MeleeWeapon)) {
                    val str = if (isArmor) (item as Armor).STR else (item as Weapon).STR
                    topRight.text(Utils.format(TXT_STRENGTH, str))
                    if (str > (Dungeon.hero?.STR() ?: 0)) {
                        topRight.hardlight(DEGRADED)
                    } else {
                        topRight.resetColor()
                    }
                } else {
                    topRight.text(
                        Utils.format(
                            TXT_TYPICAL_STR, if (isArmor)
                                (item as Armor).typicalSTR()
                            else
                                (item as MeleeWeapon).typicalSTR()
                        )
                    )
                    topRight.hardlight(WARNING)
                }
                topRight.measure()
            } else {
                topRight.text(null)
            }
            val level = item.visiblyUpgraded()
            if (level != 0 || (item.cursed && item.cursedKnown)) {
                bottomRight.text(if (item.levelKnown) Utils.format(TXT_LEVEL, level) else TXT_CURSED)
                bottomRight.measure()
                bottomRight.hardlight(if (level > 0) (if (item.isBroken) WARNING else UPGRADED) else DEGRADED)
            } else {
                bottomRight.text(null)
            }
            layout()
        }
    }
    fun enable(value: Boolean) {
        active = value
        val alpha = if (value) ENABLED else DISABLED
        icon.alpha(alpha)
        topLeft.alpha(alpha)
        topRight.alpha(alpha)
        bottomRight.alpha(alpha)
    }
    fun showParams(value: Boolean) {
        if (value) {
            add(topRight)
            add(bottomRight)
        } else {
            remove(topRight)
            remove(bottomRight)
        }
    }
    companion object {
        const val DEGRADED = 0xFF4444
        const val UPGRADED = 0x44FF44
        const val WARNING = 0xFF8800
        private const val ENABLED = 1.0f
        private const val DISABLED = 0.3f
        private const val TXT_STRENGTH = ":%d"
        private const val TXT_TYPICAL_STR = "%d?"
        private const val TXT_LEVEL = "%+d"
        private const val TXT_CURSED = ""//"-";
        // Special "virtual items"
        val CHEST: Item = object : Item() {
            override fun image(): Int {
                return ItemSpriteSheet.CHEST
            }
        }
        val LOCKED_CHEST: Item = object : Item() {
            override fun image(): Int {
                return ItemSpriteSheet.LOCKED_CHEST
            }
        }
        val TOMB: Item = object : Item() {
            override fun image(): Int {
                return ItemSpriteSheet.TOMB
            }
        }
        val SKELETON: Item = object : Item() {
            override fun image(): Int {
                return ItemSpriteSheet.BONES
            }
        }
    }
}
