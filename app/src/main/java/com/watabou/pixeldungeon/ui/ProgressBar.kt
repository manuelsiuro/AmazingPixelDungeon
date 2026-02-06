package com.watabou.pixeldungeon.ui

import com.watabou.noosa.BitmapText
import com.watabou.noosa.ColorBlock
import com.watabou.noosa.ui.Component
import com.watabou.pixeldungeon.scenes.PixelScene

class ProgressBar : Component() {

    private lateinit var bg: ColorBlock
    private lateinit var fill: ColorBlock
    private lateinit var label: BitmapText
    private var level: Float = 0f

    override fun createChildren() {
        bg = ColorBlock(1f, 1f, COLOR_BG)
        add(bg)

        fill = ColorBlock(1f, 1f, COLOR_FILL)
        add(fill)

        label = PixelScene.createText(6f)
        add(label)
    }

    override fun layout() {
        bg.x = x
        bg.y = y
        bg.size(width, height)

        fill.x = x
        fill.y = y
        fill.size(width * level, height)

        label.measure()
        label.x = x + (width - label.width()) / 2
        label.y = y + (height - label.baseLine()) / 2
    }

    fun progress(value: Float) {
        level = value.coerceIn(0f, 1f)
        fill.size(width * level, height)

        val pct = (level * 100).toInt()
        label.text("$pct%")
        label.measure()
        label.x = x + (width - label.width()) / 2
    }

    companion object {
        private const val COLOR_BG = 0xFF4C2B19.toInt()
        private const val COLOR_FILL = 0xFF40B040.toInt()
    }
}
