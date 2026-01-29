package com.watabou.pixeldungeon.ui
import com.watabou.gltextures.TextureCache
import com.watabou.noosa.Image
import com.watabou.noosa.ui.Component
import com.watabou.pixeldungeon.actors.Char
import com.watabou.pixeldungeon.sprites.CharSprite
class HealthIndicator : Component() {
    private var target: Char? = null
    private lateinit var bg: Image
    private lateinit var level: Image
    init {
        instance = this
    }
    override fun createChildren() {
        bg = Image(TextureCache.createSolid(0xFFcc0000.toInt()))
        bg.scale.y = HEIGHT
        add(bg)
        level = Image(TextureCache.createSolid(0xFF00cc00.toInt()))
        level.scale.y = HEIGHT
        add(level)
    }
    override fun update() {
        super.update()
        val t = target
        val sprite = t?.sprite
        if (t != null && t.isAlive && sprite?.isVisible() == true) {
            bg.scale.x = sprite.width
            level.scale.x = sprite.width * t.HP / t.HT
            level.x = sprite.x
            bg.x = level.x
            level.y = sprite.y - HEIGHT - 1
            bg.y = level.y
            visible = true
        } else {
            visible = false
        }
    }
    fun target(ch: Char?) {
        if (ch != null && ch.isAlive) {
            target = ch
        } else {
            target = null
        }
    }
    fun target(): Char? {
        return target
    }
    companion object {
        const val HEIGHT= 2f
        var instance: HealthIndicator? = null
    }
}
