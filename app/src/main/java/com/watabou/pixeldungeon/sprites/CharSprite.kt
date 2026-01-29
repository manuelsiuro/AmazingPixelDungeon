package com.watabou.pixeldungeon.sprites
import com.watabou.noosa.Game
import com.watabou.noosa.MovieClip
import com.watabou.noosa.Visual
import com.watabou.noosa.audio.Sample
import com.watabou.noosa.particles.Emitter
import com.watabou.noosa.tweeners.PosTweener
import com.watabou.noosa.tweeners.Tweener
import com.watabou.pixeldungeon.Assets
import com.watabou.pixeldungeon.DungeonTilemap
import com.watabou.pixeldungeon.actors.Char
import com.watabou.pixeldungeon.effects.EmoIcon
import com.watabou.pixeldungeon.effects.FloatingText
import com.watabou.pixeldungeon.effects.IceBlock
import com.watabou.pixeldungeon.effects.Speck
import com.watabou.pixeldungeon.effects.Splash
import com.watabou.pixeldungeon.effects.TorchHalo
import com.watabou.pixeldungeon.effects.particles.FlameParticle
import com.watabou.pixeldungeon.items.potions.PotionOfInvisibility
import com.watabou.pixeldungeon.levels.Level
import com.watabou.pixeldungeon.scenes.GameScene
import com.watabou.pixeldungeon.utils.Utils
import com.watabou.utils.Callback
import com.watabou.utils.PointF
import com.watabou.utils.Random
import kotlin.math.min
import kotlin.math.sqrt
open class CharSprite : MovieClip(), Tweener.Listener, MovieClip.Listener {
    enum class State {
        BURNING, LEVITATING, INVISIBLE, PARALYSED, FROZEN, ILLUMINATED
    }
    protected var idle: Animation? = null
    protected var run: Animation? = null
    protected var attack: Animation? = null
    protected var operate: Animation? = null
    protected var zap: Animation? = null
    protected var die: Animation? = null
    protected var animCallback: Callback? = null
    protected var motion: Tweener? = null
    protected var burning: Emitter? = null
    protected var levitation: Emitter? = null
    protected var iceBlock: IceBlock? = null
    protected var halo: TorchHalo? = null
    protected var emo: EmoIcon? = null
    private var jumpTweener: Tweener? = null
    private var jumpCallback: Callback? = null
    private var flashTime = 0f
    protected var sleeping = false
    var ch: Char? = null
    var isMoving = false
    init {
        listener = this
    }
    open fun link(ch: Char) {
        this.ch = ch
        ch.sprite = this
        place(ch.pos)
        turnTo(ch.pos, Random.Int(Level.LENGTH))
        ch.updateSpriteState()
    }
    fun worldToCamera(cell: Int): PointF {
        val csize = DungeonTilemap.SIZE
        return PointF(
            (cell % Level.WIDTH + 0.5f) * csize - width * 0.5f,
            (cell / Level.WIDTH + 1.0f) * csize - height
        )
    }
    open fun place(cell: Int) {
        point(worldToCamera(cell))
    }
    fun showStatus(color: Int, text: String, vararg args: Any) {
        if (visible) {
            val formattedText = if (args.isNotEmpty()) {
                Utils.format(text, *args)
            } else {
                text
            }
            val currentCh = ch
            if (currentCh != null) {
                FloatingText.show(x + width * 0.5f, y, currentCh.pos, formattedText, color)
            } else {
                FloatingText.show(x + width * 0.5f, y, formattedText, color)
            }
        }
    }
    open fun idle() {
        play(idle, false)
    }
    open fun move(from: Int, to: Int) {
        play(run, false)
        val posTweener = PosTweener(this, worldToCamera(to), MOVE_INTERVAL)
        posTweener.listener = this
        motion = posTweener
        parent?.add(posTweener)
        isMoving = true
        turnTo(from, to)
        val currentCh = ch
        if (visible && Level.water[from] && currentCh != null && !currentCh.flying) {
            GameScene.ripple(from)
        }
        currentCh?.onMotionComplete()
    }
    fun interruptMotion() {
        motion?.let { onComplete(it) }
    }
    open fun attack(cell: Int) {
        val currentCh = ch ?: return
        turnTo(currentCh.pos, cell)
        play(attack, false)
    }
    open fun attack(cell: Int, callback: Callback?) {
        animCallback = callback
        val currentCh = ch ?: return
        turnTo(currentCh.pos, cell)
        play(attack, false)
    }
    open fun operate(cell: Int) {
        val currentCh = ch ?: return
        turnTo(currentCh.pos, cell)
        play(operate, false)
    }
    open fun zap(cell: Int) {
        val currentCh = ch ?: return
        turnTo(currentCh.pos, cell)
        play(zap, false)
    }
    fun turnTo(from: Int, to: Int) {
        val fx = from % Level.WIDTH
        val tx = to % Level.WIDTH
        if (tx > fx) {
            flipHorizontal = false
        } else if (tx < fx) {
            flipHorizontal = true
        }
    }
    open fun jump(from: Int, to: Int, callback: Callback?) {
        jumpCallback = callback
        val distance = Level.distance(from, to)
        val tweener = JumpTweener(this, worldToCamera(to), (distance * 4).toFloat(), distance * 0.1f)
        tweener.listener = this
        jumpTweener = tweener
        parent?.add(tweener)
        turnTo(from, to)
    }
    open fun die() {
        sleeping = false
        play(die, false)
        emo?.killAndErase()
    }
    open fun emitter(): Emitter {
        val emitter = checkNotNull(GameScene.emitter()) { "GameScene.emitter() returned null" }
        emitter.pos(this)
        return emitter
    }
    fun centerEmitter(): Emitter {
        val emitter = checkNotNull(GameScene.emitter()) { "GameScene.emitter() returned null" }
        emitter.pos(center())
        return emitter
    }
    fun bottomEmitter(): Emitter {
        val emitter = checkNotNull(GameScene.emitter()) { "GameScene.emitter() returned null" }
        emitter.pos(x, y + height, width, 0f)
        return emitter
    }
    fun burst(color: Int, n: Int) {
        if (visible) {
            Splash.at(center(), color, n)
        }
    }
    fun bloodBurstA(from: PointF?, damage: Int) {
        if (visible && from != null) {
            val c = center()
            val currentCh = ch ?: return
            val n = min(9.0 * sqrt(damage.toDouble() / currentCh.HT), 9.0).toInt()
            Splash.at(c, PointF.angle(from, c), 3.1415926f / 2, blood(), n)
        }
    }
    open fun blood(): Int {
        return 0xFFBB0000.toInt()
    }
    fun flash() {
        ra = 1f
        ba = 1f
        ga = 1f
        flashTime = FLASH_INTERVAL
    }
    open fun add(state: State) {
        when (state) {
            State.BURNING -> {
                burning = emitter()
                burning?.pour(FlameParticle.FACTORY, 0.06f)
                if (visible) {
                    Sample.play(Assets.SND_BURNING)
                }
            }
            State.LEVITATING -> {
                levitation = emitter()
                levitation?.pour(Speck.factory(Speck.JET), 0.02f)
            }
            State.INVISIBLE -> {
                ch?.let { PotionOfInvisibility.melt(it) }
            }
            State.PARALYSED -> {
                paused = true
            }
            State.FROZEN -> {
                iceBlock = IceBlock.freeze(this)
                paused = true
            }
            State.ILLUMINATED -> {
                val h = TorchHalo(this)
                halo = h
                GameScene.effect(h)
            }
        }
    }
    open fun remove(state: State) {
        when (state) {
            State.BURNING -> {
                burning?.let {
                    it.on = false
                    burning = null
                }
            }
            State.LEVITATING -> {
                levitation?.let {
                    it.on = false
                    levitation = null
                }
            }
            State.INVISIBLE -> {
                alpha(1f)
            }
            State.PARALYSED -> {
                paused = false
            }
            State.FROZEN -> {
                iceBlock?.melt()
                iceBlock = null
                paused = false
            }
            State.ILLUMINATED -> {
                halo?.putOut()
            }
        }
    }
    override fun update() {
        super.update()
        if (paused) {
            curAnim?.let { anim ->
                listener?.onComplete(anim)
            }
        }
        if (flashTime > 0) {
            flashTime -= Game.elapsed
            if (flashTime <= 0) {
                resetColor()
            }
        }
        burning?.visible = visible
        levitation?.visible = visible
        iceBlock?.visible = visible
        if (sleeping) {
            showSleep()
        } else {
            hideSleep()
        }
        emo?.visible = visible
    }
    fun showSleep() {
        if (emo !is EmoIcon.Sleep) {
            emo?.killAndErase()
            emo = EmoIcon.Sleep(this)
        }
    }
    fun hideSleep() {
        if (emo is EmoIcon.Sleep) {
            emo?.killAndErase()
            emo = null
        }
    }
    fun showAlert() {
        if (emo !is EmoIcon.Alert) {
            emo?.killAndErase()
            emo = EmoIcon.Alert(this)
        }
    }
    fun hideAlert() {
        if (emo is EmoIcon.Alert) {
            emo?.killAndErase()
            emo = null
        }
    }
    override fun kill() {
        super.kill()
        emo?.killAndErase()
        emo = null
    }
    override fun onComplete(tweener: Tweener) {
        if (tweener === jumpTweener) {
            val currentCh = ch
            if (visible && currentCh != null && Level.water[currentCh.pos] && !currentCh.flying) {
                GameScene.ripple(currentCh.pos)
            }
            jumpCallback?.call()
        } else if (tweener === motion) {
            isMoving = false
            motion?.killAndErase()
            motion = null
        }
    }
    override fun onComplete(anim: Animation) {
        val callback = animCallback
        if (callback != null) {
            callback.call()
            animCallback = null
        } else {
            if (anim === attack) {
                idle()
                ch?.onAttackComplete()
            } else if (anim === operate) {
                idle()
                ch?.onOperateComplete()
            }
        }
    }
    private class JumpTweener(
        val visual: Visual,
        val end: PointF,
        val height: Float,
        time: Float
    ) : Tweener(visual, time) {
        val start: PointF = visual.point()
        override fun updateValues(progress: Float) {
            visual.point(PointF.inter(start, end, progress).offset(0f, -height * 4 * progress * (1 - progress)))
        }
    }
    companion object {
        const val DEFAULT = 0xFFFFFF
        const val POSITIVE = 0x00FF00
        const val NEGATIVE = 0xFF0000
        const val WARNING = 0xFF8800
        const val NEUTRAL = 0xFFFF00
        private const val MOVE_INTERVAL = 0.1f
        private const val FLASH_INTERVAL = 0.05f
    }
}
