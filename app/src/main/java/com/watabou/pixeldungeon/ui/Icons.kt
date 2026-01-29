package com.watabou.pixeldungeon.ui
import com.watabou.noosa.Image
import com.watabou.pixeldungeon.Assets
import com.watabou.pixeldungeon.actors.hero.HeroClass
enum class Icons {
    SKULL,
    BUSY,
    COMPASS,
    PREFS,
    WARNING,
    TARGET,
    WATA,
    WARRIOR,
    MAGE,
    ROGUE,
    HUNTRESS,
    CLOSE,
    DEPTH,
    SLEEP,
    ALERT,
    SUPPORT,
    SUPPORTED,
    BACKPACK,
    SEED_POUCH,
    SCROLL_HOLDER,
    WAND_HOLSTER,
    KEYRING,
    CHECKED,
    UNCHECKED,
    EXIT,
    CHALLENGE_OFF,
    CHALLENGE_ON,
    RESUME;
    fun get(): Image {
        return get(this)
    }
    companion object {
        fun get(type: Icons): Image {
            val icon = Image(Assets.ICONS)
            val tex = icon.texture ?: return icon
            when (type) {
                SKULL -> icon.frame(tex.uvRect(0, 0, 8, 8))
                BUSY -> icon.frame(tex.uvRect(8, 0, 16, 8))
                COMPASS -> icon.frame(tex.uvRect(0, 8, 7, 13))
                PREFS -> icon.frame(tex.uvRect(30, 0, 46, 16))
                WARNING -> icon.frame(tex.uvRect(46, 0, 58, 12))
                TARGET -> icon.frame(tex.uvRect(0, 13, 16, 29))
                WATA -> icon.frame(tex.uvRect(30, 16, 45, 26))
                WARRIOR -> icon.frame(tex.uvRect(0, 29, 16, 45))
                MAGE -> icon.frame(tex.uvRect(16, 29, 32, 45))
                ROGUE -> icon.frame(tex.uvRect(32, 29, 48, 45))
                HUNTRESS -> icon.frame(tex.uvRect(48, 29, 64, 45))
                CLOSE -> icon.frame(tex.uvRect(0, 45, 13, 58))
                DEPTH -> icon.frame(tex.uvRect(45, 12, 54, 20))
                SLEEP -> icon.frame(tex.uvRect(13, 45, 22, 53))
                ALERT -> icon.frame(tex.uvRect(22, 45, 30, 53))
                SUPPORT -> icon.frame(tex.uvRect(30, 45, 46, 61))
                SUPPORTED -> icon.frame(tex.uvRect(46, 45, 62, 61))
                BACKPACK -> icon.frame(tex.uvRect(58, 0, 68, 10))
                SCROLL_HOLDER -> icon.frame(tex.uvRect(68, 0, 78, 10))
                SEED_POUCH -> icon.frame(tex.uvRect(78, 0, 88, 10))
                WAND_HOLSTER -> icon.frame(tex.uvRect(88, 0, 98, 10))
                KEYRING -> icon.frame(tex.uvRect(64, 29, 74, 39))
                CHECKED -> icon.frame(tex.uvRect(54, 12, 66, 24))
                UNCHECKED -> icon.frame(tex.uvRect(66, 12, 78, 24))
                EXIT -> icon.frame(tex.uvRect(98, 0, 114, 16))
                CHALLENGE_OFF -> icon.frame(tex.uvRect(78, 16, 102, 40))
                CHALLENGE_ON -> icon.frame(tex.uvRect(102, 16, 126, 40))
                RESUME -> icon.frame(tex.uvRect(114, 0, 126, 11))
            }
            return icon
        }
        fun get(cl: HeroClass): Image? {
            return when (cl) {
                HeroClass.WARRIOR -> get(WARRIOR)
                HeroClass.MAGE -> get(MAGE)
                HeroClass.ROGUE -> get(ROGUE)
                HeroClass.HUNTRESS -> get(HUNTRESS)
            }
        }
    }
}
