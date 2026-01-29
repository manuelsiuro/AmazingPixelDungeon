package com.watabou.pixeldungeon.ui
import com.watabou.noosa.Image
import com.watabou.pixeldungeon.Dungeon
import com.watabou.pixeldungeon.scenes.PixelScene
class ResumeButton : Tag(0xCDD5C0) {
    private lateinit var icon: Image
    init {
        setSize(24f, 22f)
        visible = false
    }
    override fun createChildren() {
        super.createChildren()
        icon = Icons.get(Icons.RESUME)
        add(icon)
    }
    override fun layout() {
        super.layout()
        icon.x = PixelScene.align(PixelScene.uiCamera, x + 1 + (width - icon.width) / 2)
        icon.y = PixelScene.align(PixelScene.uiCamera, y + (height - icon.height) / 2)
    }
    override fun update() {
        val prevVisible = visible
        if (Dungeon.hero != null) {
            visible = (Dungeon.hero?.lastAction != null)
        } else {
            visible = false
        }
        if (visible && !prevVisible) {
            flash()
        }
        super.update()
    }
    override fun onClick() {
        Dungeon.hero?.resume()
    }
}
