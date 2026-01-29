package com.watabou.pixeldungeon.effects
import com.watabou.noosa.Game
import com.watabou.noosa.Image
import com.watabou.pixeldungeon.scenes.GameScene
import com.watabou.pixeldungeon.sprites.CharSprite
import com.watabou.pixeldungeon.ui.Icons
import com.watabou.utils.Random
open class EmoIcon(protected var owner: CharSprite) : Image() {
    protected var maxSize: Float = 2f
    protected var timeScale: Float = 1f
    protected var growing: Boolean = true
    init {
        GameScene.add(this)
    }
    override fun update() {
        super.update()
        if (visible) {
            if (growing) {
                scale.set(scale.x + Game.elapsed * timeScale)
                if (scale.x > maxSize) {
                    growing = false
                }
            } else {
                scale.set(scale.x - Game.elapsed * timeScale)
                if (scale.x < 1) {
                    growing = true
                }
            }
            x = owner.x + owner.width - width / 2
            y = owner.y - height
        }
    }
    class Sleep(owner: CharSprite) : EmoIcon(owner) {
        init {
            copy(Icons.get(Icons.SLEEP))
            maxSize = 1.2f
            timeScale = 0.5f
            origin.set(width / 2, height / 2)
            scale.set(Random.Float(1f, maxSize))
        }
    }
    class Alert(owner: CharSprite) : EmoIcon(owner) {
        init {
            copy(Icons.get(Icons.ALERT))
            maxSize = 1.3f
            timeScale = 2f
            origin.set(2.5f, height - 2.5f)
            scale.set(Random.Float(1f, maxSize))
        }
    }
}
