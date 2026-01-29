package com.watabou.pixeldungeon.effects
import com.watabou.noosa.BitmapText
import com.watabou.noosa.Camera
import com.watabou.noosa.Game
import com.watabou.pixeldungeon.DungeonTilemap
import com.watabou.pixeldungeon.scenes.GameScene
import com.watabou.pixeldungeon.scenes.PixelScene
import com.watabou.utils.SparseArray
class FloatingText : BitmapText() {
    private var timeLeft: Float = 0f
    private var key: Int = -1
    private var cameraZoom: Float = -1f
    init {
        speed.y = -DISTANCE / LIFESPAN
    }
    override fun update() {
        super.update()
        if (timeLeft > 0) {
            timeLeft -= Game.elapsed
            if (timeLeft <= 0) {
                kill()
            } else {
                val p = timeLeft / LIFESPAN
                alpha(if (p > 0.5f) 1f else p * 2)
            }
        }
    }
    override fun kill() {
        if (key != -1) {
            stacks.get(key)?.remove(this)
            key = -1
        }
        super.kill()
    }
    override fun destroy() {
        kill()
        super.destroy()
    }
    fun reset(x: Float, y: Float, text: String, color: Int) {
        revive()
        val mainCamera = Camera.main ?: return
        if (cameraZoom != mainCamera.zoom) {
            cameraZoom = mainCamera.zoom
            PixelScene.chooseFont(9f, cameraZoom)
            font = PixelScene.font
            scale.set(PixelScene.scale)
        }
        text(text)
        hardlight(color)
        measure()
        this.x = PixelScene.align(x - width() / 2)
        this.y = y - height()
        timeLeft = LIFESPAN
    }
    companion object {
        private const val LIFESPAN = 1f
        private const val DISTANCE = DungeonTilemap.SIZE.toFloat()
        private val stacks = SparseArray<ArrayList<FloatingText>>()
        fun show(x: Float, y: Float, text: String, color: Int) {
            val status = GameScene.status() ?: return
            status.reset(x, y, text, color)
        }
        fun show(x: Float, y: Float, key: Int, text: String, color: Int) {
            val txt = GameScene.status() ?: return
            txt.reset(x, y, text, color)
            push(txt, key)
        }
        private fun push(txt: FloatingText, key: Int) {
            txt.key = key
            var stack = stacks.get(key)
            if (stack == null) {
                stack = ArrayList()
                stacks.put(key, stack)
            }
            if (stack.size > 0) {
                var below = txt
                var aboveIndex = stack.size - 1
                while (aboveIndex >= 0) {
                    val above = stack[aboveIndex]
                    if (above.y + above.height() > below.y) {
                        above.y = below.y - above.height()
                        below = above
                        aboveIndex--
                    } else {
                        break
                    }
                }
            }
            stack.add(txt)
        }
    }
}
