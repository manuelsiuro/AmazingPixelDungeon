package com.watabou.pixeldungeon.ui
import com.watabou.noosa.ColorBlock
import com.watabou.noosa.ui.Component
class HealthBar : Component() {
    private lateinit var hpBg: ColorBlock
    private lateinit var hpLvl: ColorBlock
    private var level: Float = 0f
    override fun createChildren() {
        hpBg = ColorBlock(1f, 1f, COLOR_BG)
        add(hpBg)
        hpLvl = ColorBlock(1f, 1f, COLOR_LVL)
        add(hpLvl)
        height = HEIGHT.toFloat()
    }
    override fun layout() {
        hpLvl.x = x
        hpBg.x = hpLvl.x
        hpLvl.y = y
        hpBg.y = hpLvl.y
        hpBg.size(width, HEIGHT.toFloat())
        hpLvl.size(width * level, HEIGHT.toFloat())
        height = HEIGHT.toFloat()
    }
    fun level(value: Float) {
        level = value
        layout()
    }
    companion object {
        private const val COLOR_BG = 0xFFCC0000.toInt()
        private const val COLOR_LVL = 0xFF00EE00.toInt()
        private const val HEIGHT = 2
    }
}
