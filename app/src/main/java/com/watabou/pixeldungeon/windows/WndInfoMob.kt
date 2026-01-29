package com.watabou.pixeldungeon.windows
import com.watabou.noosa.BitmapText
import com.watabou.noosa.ui.Component
import com.watabou.pixeldungeon.actors.mobs.Mob
import com.watabou.pixeldungeon.scenes.PixelScene
import com.watabou.pixeldungeon.sprites.CharSprite
import com.watabou.pixeldungeon.ui.BuffIndicator
import com.watabou.pixeldungeon.ui.HealthBar
import com.watabou.pixeldungeon.ui.Window
import com.watabou.pixeldungeon.utils.Utils
import kotlin.math.max
class WndInfoMob(mob: Mob) : WndTitledMessage(MobTitle(mob), desc(mob)) {
    companion object {
        private fun desc(mob: Mob): String {
            val builder = StringBuilder(mob.description())
            builder.append("\n\n" + mob.state.status() + ".")
            return builder.toString()
        }
    }
    private class MobTitle(mob: Mob) : Component() {
        private val image: CharSprite
        private val name: BitmapText
        private val health: HealthBar
        private val buffs: BuffIndicator
        init {
            name = PixelScene.createText(Utils.capitalize(mob.name), 9f)
            name.hardlight(Window.TITLE_COLOR)
            name.measure()
            add(name)
            image = mob.sprite!!
            add(image)
            health = HealthBar()
            health.level(mob.HP.toFloat() / mob.HT)
            add(health)
            buffs = BuffIndicator(mob)
            add(buffs)
        }
        override fun layout() {
            image.x = 0f
            image.y = max(0f, name.height() + GAP + health.height() - image.height)
            name.x = image.width + GAP
            name.y = image.height - health.height() - GAP - name.baseLine()
            val w = width - image.width - GAP
            health.setRect(image.width + GAP, image.height - health.height(), w, health.height())
            buffs.setPos(
                name.x + name.width() + GAP,
                name.y + name.baseLine() - BuffIndicator.SIZE
            )
            height = health.bottom()
        }
        companion object {
            private const val GAP = 2f
        }
    }
}
