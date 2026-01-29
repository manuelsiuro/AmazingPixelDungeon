package com.watabou.pixeldungeon.sprites
import android.graphics.RectF
import com.watabou.gltextures.SmartTexture
import com.watabou.gltextures.TextureCache
import com.watabou.noosa.Camera
import com.watabou.noosa.Image
import com.watabou.noosa.TextureFilm
import com.watabou.pixeldungeon.Assets
import com.watabou.pixeldungeon.Dungeon
import com.watabou.pixeldungeon.actors.hero.Hero
import com.watabou.pixeldungeon.actors.hero.HeroClass
import com.watabou.utils.Callback
class HeroSprite : CharSprite() {
    private lateinit var fly: Animation
    private lateinit var read: Animation
    init {
        val hero = Dungeon.hero
        if (hero != null) {
            link(hero)
            hero.heroClass.spritesheet()?.let { texture(it) }
            updateArmor()
            idle()
        }
    }
    fun updateArmor() {
        val hero = ch as Hero
        val film = TextureFilm(tiers(), hero.tier(), FRAME_WIDTH, FRAME_HEIGHT)
        idle = Animation(1, true)
        idle?.frames(film, 0, 0, 0, 1, 0, 0, 1, 1)
        run = Animation(RUN_FRAMERATE, true)
        run?.frames(film, 2, 3, 4, 5, 6, 7)
        die = Animation(20, false)
        die?.frames(film, 8, 9, 10, 11, 12, 11)
        attack = Animation(15, false)
        attack?.frames(film, 13, 14, 15, 0)
        zap = attack?.clone()
        operate = Animation(8, false)
        operate?.frames(film, 16, 17, 16, 17)
        fly = Animation(1, true)
        fly.frames(film, 18)
        read = Animation(20, false)
        read.frames(film, 19, 20, 20, 20, 20, 20, 20, 20, 20, 19)
    }
    override fun place(cell: Int) {
        super.place(cell)
        Camera.main?.target = this
    }
    override fun move(from: Int, to: Int) {
        super.move(from, to)
        val currentCh = ch
        if (currentCh != null && currentCh.flying) {
            play(fly)
        }
        Camera.main?.target = this
    }
    override fun jump(from: Int, to: Int, callback: Callback?) {
        super.jump(from, to, callback)
        play(fly)
    }
    fun read() {
        animCallback = object : Callback {
            override fun call() {
                idle()
                ch?.onOperateComplete()
            }
        }
        play(read)
    }
    override fun update() {
        sleeping = (ch as Hero).restoreHealth
        super.update()
    }
    fun sprint(on: Boolean): Boolean {
        run?.delay = if (on) 0.625f / RUN_FRAMERATE else 1f / RUN_FRAMERATE
        return on
    }
    companion object {
        private const val FRAME_WIDTH = 12
        private const val FRAME_HEIGHT = 15
        private const val RUN_FRAMERATE = 20
        private var tiers: TextureFilm? = null
        fun tiers(): TextureFilm {
            var currentTiers = tiers
            if (currentTiers == null) {
                val texture: SmartTexture = TextureCache.get(Assets.ROGUE)
                currentTiers = TextureFilm(texture, texture.width, FRAME_HEIGHT)
                tiers = currentTiers
            }
            return currentTiers
        }
        fun avatar(cl: HeroClass, armorTier: Int): Image {
            val patch: RectF = tiers()[armorTier] ?: RectF()
            val spritesheet = cl.spritesheet() ?: Assets.ROGUE
            val avatar = Image(spritesheet)
            val frame: RectF = avatar.texture?.uvRect(1, 0, FRAME_WIDTH, FRAME_HEIGHT) ?: RectF()
            frame.offset(patch.left, patch.top)
            avatar.frame(frame)
            return avatar
        }
    }
}
