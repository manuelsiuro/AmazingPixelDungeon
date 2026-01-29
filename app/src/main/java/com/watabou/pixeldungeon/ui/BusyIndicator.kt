package com.watabou.pixeldungeon.ui
import com.watabou.noosa.Image
import com.watabou.pixeldungeon.Dungeon
class BusyIndicator : Image() {
    init {
        copy(Icons.BUSY.get())
        origin.set(width / 2, height / 2)
        angularSpeed = 720f
    }
    override fun update() {
        super.update()
        val hero = Dungeon.hero ?: return
        visible = hero.isAlive && !hero.ready
    }
}
