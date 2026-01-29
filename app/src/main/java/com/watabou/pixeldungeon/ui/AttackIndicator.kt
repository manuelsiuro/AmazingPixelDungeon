package com.watabou.pixeldungeon.ui
import com.watabou.pixeldungeon.Dungeon
import com.watabou.pixeldungeon.actors.Char
import com.watabou.pixeldungeon.actors.mobs.Mob
import com.watabou.pixeldungeon.levels.Level
import com.watabou.pixeldungeon.scenes.PixelScene
import com.watabou.pixeldungeon.sprites.CharSprite
import com.watabou.utils.Random
import java.util.ArrayList
class AttackIndicator : Tag(DangerIndicator.COLOR) {
    private var sprite: CharSprite? = null
    private val candidates = ArrayList<Mob>()
    private var enabled = true
    init {
        instance = this
        setSize(24f, 24f)
        visible(false)
        enable(false)
    }
    override fun createChildren() {
        super.createChildren()
    }
    override fun layout() {
        super.layout()
        val s = sprite
        if (s != null) {
            s.x = x + (width - s.width()) / 2
            s.y = y + (height - s.height()) / 2
            PixelScene.align(s)
        }
    }
    override fun update() {
        super.update()
        val hero = Dungeon.hero ?: return
        if (hero.isAlive) {
            if (!hero.ready) {
                enable(false)
            }
        } else {
            visible(false)
            enable(false)
        }
    }
    private fun checkEnemies() {
        val hero = Dungeon.hero ?: return
        val heroPos = hero.pos
        candidates.clear()
        val v = hero.visibleEnemies()
        for (i in 0 until v) {
            val mob = hero.visibleEnemy(i)
            if (Level.adjacent(heroPos, mob.pos)) {
                candidates.add(mob)
            }
        }
        if (!candidates.contains(lastTarget)) {
            if (candidates.isEmpty()) {
                lastTarget = null
            } else {
                lastTarget = Random.element(candidates)
                updateImage()
                flash()
            }
        } else {
            val background = bg
            if (background != null && !background.visible) {
                flash()
            }
        }
        visible(lastTarget != null)
        enable(bg?.visible == true)
    }
    private fun updateImage() {
        sprite?.killAndErase()
        sprite = null
        try {
            val target = lastTarget ?: return
            val s = target.spriteClass?.getDeclaredConstructor()?.newInstance() ?: return
            s.idle()
            s.paused = true
            sprite = s
            add(s)
            s.x = x + (width - s.width()) / 2 + 1
            s.y = y + (height - s.height()) / 2
            PixelScene.align(s)
        } catch (e: Exception) {
        }
    }
    private fun enable(value: Boolean) {
        enabled = value
        sprite?.alpha(if (value) ENABLED else DISABLED)
    }
    private fun visible(value: Boolean) {
        bg?.visible = value
        val s = sprite
        if (s != null) {
            s.visible = value
        }
    }
    override fun onClick() {
        val target = lastTarget
        if (enabled && target != null) {
            Dungeon.hero?.handle(target.pos)
        }
    }
    companion object {
        private const val ENABLED = 1.0f
        private const val DISABLED = 0.3f
        private var instance: AttackIndicator? = null
        private var lastTarget: Mob? = null
        fun target(target: Char) {
            lastTarget = target as Mob
            instance?.updateImage()
            HealthIndicator.instance?.target(target)
        }
        fun updateState() {
            instance?.checkEnemies()
        }
    }
}
