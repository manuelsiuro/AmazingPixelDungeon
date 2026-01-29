package com.watabou.pixeldungeon
import com.watabou.noosa.NinePatch
object Chrome {
    enum class Type {
        TOAST,
        TOAST_TR,
        WINDOW,
        BUTTON,
        TAG,
        SCROLL,
        TAB_SET,
        TAB_SELECTED,
        TAB_UNSELECTED
    }
    fun get(type: Type): NinePatch? {
        when (type) {
            Type.WINDOW -> return NinePatch(Assets.CHROME, 0, 0, 22, 22, 7)
            Type.TOAST -> return NinePatch(Assets.CHROME, 22, 0, 18, 18, 5)
            Type.TOAST_TR -> return NinePatch(Assets.CHROME, 40, 0, 18, 18, 5)
            Type.BUTTON -> return NinePatch(Assets.CHROME, 58, 0, 6, 6, 2)
            Type.TAG -> return NinePatch(Assets.CHROME, 22, 18, 16, 14, 3)
            Type.SCROLL -> return NinePatch(Assets.CHROME, 32, 32, 32, 32, 5, 11, 5, 11)
            Type.TAB_SET -> return NinePatch(Assets.CHROME, 64, 0, 22, 22, 7, 7, 7, 7)
            Type.TAB_SELECTED -> return NinePatch(Assets.CHROME, 64, 22, 10, 14, 4, 7, 4, 6)
            Type.TAB_UNSELECTED -> return NinePatch(Assets.CHROME, 74, 22, 10, 14, 4, 7, 4, 6)
            else -> return null
        }
    }
}
