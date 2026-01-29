package com.watabou.pixeldungeon.scenes
import com.watabou.gltextures.Gradient
import com.watabou.gltextures.SmartTexture
import com.watabou.glwrap.Matrix
import com.watabou.glwrap.Quad
import com.watabou.input.Touchscreen
import com.watabou.noosa.Camera
import com.watabou.noosa.ColorBlock
import com.watabou.noosa.Game
import com.watabou.noosa.Group
import com.watabou.noosa.Image
import com.watabou.noosa.MovieClip
import com.watabou.noosa.NoosaScript
import com.watabou.noosa.TextureFilm
import com.watabou.noosa.TouchArea
import com.watabou.noosa.Visual
import com.watabou.noosa.audio.Music
import com.watabou.pixeldungeon.Assets
import com.watabou.pixeldungeon.Badges
import com.watabou.pixeldungeon.Dungeon
import com.watabou.pixeldungeon.actors.hero.HeroClass
import com.watabou.pixeldungeon.ui.Archs
import com.watabou.pixeldungeon.ui.RedButton
import com.watabou.utils.Point
import com.watabou.utils.Random
import java.nio.FloatBuffer
class SurfaceScene : PixelScene() {
    private var viewport: Camera? = null
    override fun create() {
        super.create()
        Music.play(Assets.HAPPY, true)
        Music.volume(1f)
        PixelScene.uiCamera.visible = false
        val w = Camera.main!!.width
        val h = Camera.main!!.height
        val archs = Archs()
        archs.reversed = true
        archs.setSize(w.toFloat(), h.toFloat())
        add(archs)
        val vx = align((w - SKY_WIDTH) / 2f)
        val vy = align((h - SKY_HEIGHT - BUTTON_HEIGHT) / 2f)
        val s = Camera.main!!.cameraToScreen(vx, vy)
        viewport = Camera(s.x.toInt(), s.y.toInt(), SKY_WIDTH, SKY_HEIGHT, PixelScene.defaultZoom)
        Camera.add(viewport!!)
        val window = Group()
        window.camera = viewport
        add(window)
        val dayTime = !Dungeon.nightMode
        val sky = Sky(dayTime)
        sky.scale.set(SKY_WIDTH.toFloat(), SKY_HEIGHT.toFloat())
        window.add(sky)
        if (!dayTime) {
            for (i in 0 until NSTARS) {
                val size = Random.Float()
                val star = ColorBlock(size, size, 0xFFFFFFFF.toInt())
                star.x = Random.Float(SKY_WIDTH.toFloat()) - size / 2
                star.y = Random.Float(SKY_HEIGHT.toFloat()) - size / 2
                star.am = size * (1 - star.y / SKY_HEIGHT)
                window.add(star)
            }
        }
        val range = SKY_HEIGHT * 2 / 3f
        for (i in 0 until NCLOUDS) {
            val cloud = Cloud((NCLOUDS - 1 - i) * (range / NCLOUDS) + Random.Float(range / NCLOUDS), dayTime)
            window.add(cloud)
        }
        val nPatches = (sky.width() / GrassPatch.WIDTH + 1).toInt()
        for (i in 0 until nPatches * 4) {
            val patch = GrassPatch((i - 0.75f) * GrassPatch.WIDTH / 4, (SKY_HEIGHT + 1).toFloat(), dayTime)
            patch.brightness(if (dayTime) 0.7f else 0.4f)
            window.add(patch)
        }
        val a = Avatar(Dungeon.hero!!.heroClass)
        a.x = PixelScene.align((SKY_WIDTH - a.width) / 2)
        a.y = SKY_HEIGHT - a.height
        window.add(a)
        val pet = Pet()
        pet.bm = 1.2f
        pet.gm = 1.2f
        pet.rm = 1.2f
        pet.x = SKY_WIDTH / 2f + 2
        pet.y = SKY_HEIGHT - pet.height
        window.add(pet)
        window.add(object : TouchArea(sky) {
            override fun onClick(touch: Touchscreen.Touch) {
                pet.jump()
            }
        })
        for (i in 0 until nPatches) {
            val patch = GrassPatch((i - 0.5f) * GrassPatch.WIDTH, SKY_HEIGHT.toFloat(), dayTime)
            patch.brightness(if (dayTime) 1.0f else 0.8f)
            window.add(patch)
        }
        val frame = Image(Assets.SURFACE)
        frame.frame(0, 0, FRAME_WIDTH, FRAME_HEIGHT)
        frame.x = vx - FRAME_MARGIN_X
        frame.y = vy - FRAME_MARGIN_TOP
        add(frame)
        if (dayTime) {
            a.brightness(1.2f)
            pet.brightness(1.2f)
        } else {
            frame.hardlight(0xDDEEFF)
        }
        val gameOver = object : RedButton("Game Over") {
            override fun onClick() {
                Game.switchScene(TitleScene::class.java)
            }
        }
        gameOver.setSize((SKY_WIDTH - FRAME_MARGIN_X * 2).toFloat(), BUTTON_HEIGHT.toFloat())
        gameOver.setPos(frame.x + FRAME_MARGIN_X * 2, frame.y + frame.height + 4)
        add(gameOver)
        Badges.validateHappyEnd()
        fadeIn()
    }
    override fun destroy() {
        Badges.saveGlobal()
        Camera.remove(viewport!!)
        super.destroy()
    }
    override fun onBackPressed() {
    }
    private class Sky(dayTime: Boolean) : Visual(0f, 0f, 1f, 1f) {
        private val texture: SmartTexture
        private val verticesBuffer: FloatBuffer
        init {
            texture = Gradient(if (dayTime) day else night)
            val vertices = FloatArray(16)
            verticesBuffer = Quad.create()
            vertices[2] = 0.25f
            vertices[6] = 0.25f
            vertices[10] = 0.75f
            vertices[14] = 0.75f
            vertices[3] = 0f
            vertices[7] = 1f
            vertices[11] = 1f
            vertices[15] = 0f
            vertices[0] = 0f
            vertices[1] = 0f
            vertices[4] = 1f
            vertices[5] = 0f
            vertices[8] = 1f
            vertices[9] = 1f
            vertices[12] = 0f
            vertices[13] = 1f
            verticesBuffer.position(0)
            verticesBuffer.put(vertices)
        }
        override fun draw() {
            super.draw()
            val script = NoosaScript.get()
            texture.bind()
            script.camera(camera())
            script.uModel.valueM4(matrix)
            script.lighting(
                rm, gm, bm, am,
                ra, ga, ba, aa
            )
            script.drawQuad(verticesBuffer)
        }
        companion object {
            private val day = intArrayOf(0xFF4488FF.toInt(), 0xFFCCEEFF.toInt())
            private val night = intArrayOf(0xFF001155.toInt(), 0xFF335980.toInt())
        }
    }
    private class Cloud(y: Float, dayTime: Boolean) : Image(Assets.SURFACE) {
        init {
            var index: Int
            do {
                index = Random.Int(3)
            } while (index == lastIndex)
            when (index) {
                0 -> frame(88, 0, 49, 20)
                1 -> frame(88, 20, 49, 22)
                2 -> frame(88, 42, 50, 18)
            }
            lastIndex = index
            this.y = y
            scale.set(1 - y / SKY_HEIGHT)
            x = Random.Float(SKY_WIDTH + width()) - width()
            speed.x = scale.x * if (dayTime) +8 else -8
            if (dayTime) {
                tint(0xCCEEFF, 1 - scale.y)
            } else {
                rm = +3.0f
                bm = +3.0f
                gm = +3.0f
                ra = -2.1f
                ba = -2.1f
                ga = -2.1f
            }
        }
        override fun update() {
            super.update()
            if (speed.x > 0 && x > SKY_WIDTH) {
                x = -width()
            } else if (speed.x < 0 && x < -width()) {
                x = SKY_WIDTH.toFloat()
            }
        }
        companion object {
            private var lastIndex = -1
        }
    }
    private class Avatar(cl: HeroClass) : Image(Assets.AVATARS) {
        init {
            // Note: texture is nullable property in Image (it seems Image.kt defined it as nullable or platform type? OpenGlWrappers/Image.java? No Image.java was Noosa)
            // If texture is null, TextureFilm constructor fails?
            // Assets.AVATARS sets the texture key. Image constructor loads it.
            // If it's loaded, texture should be set.
            frame(TextureFilm(texture!!, WIDTH, HEIGHT).get(cl.ordinal)!!)
        }
        companion object {
            private const val WIDTH = 24
            private const val HEIGHT = 28
        }
    }
    private class Pet : MovieClip(Assets.PET), MovieClip.Listener {
        private val idle: Animation
        private val jump: Animation
        init {
            val frames = TextureFilm(texture!!, 16, 16)
            idle = Animation(2, true)
            idle.frames(frames, 0, 0, 0, 0, 0, 0, 1)
            jump = Animation(10, false)
            jump.frames(frames, 2, 3, 4, 5, 6)
            listener = this
            play(idle)
        }
        fun jump() {
            play(jump)
        }
        override fun onComplete(anim: Animation) {
            if (anim === jump) {
                play(idle)
            }
        }
    }
    private class GrassPatch(tx: Float, ty: Float, private val forward: Boolean) : Image(Assets.SURFACE) {
        private val tx: Float = tx
        private val ty: Float = ty
        private var a = Random.Float(5f).toDouble()
        // renamed to avoid conflict with Visual.angle
        private var ang: Double = 0.toDouble()
        init {
            frame(88 + Random.Int(4) * WIDTH, 60, WIDTH, HEIGHT)
        }
        override fun update() {
            super.update()
            a += (Game.elapsed * 5).toDouble()
            ang = (2 + Math.cos(a)) * if (forward) +0.2 else -0.2
            scale.y = Math.cos(ang).toFloat()
            x = tx + Math.tan(ang).toFloat() * width
            y = ty - scale.y * height
        }
        override fun updateMatrix() {
            super.updateMatrix()
            Matrix.skewX(matrix, (ang / Matrix.G2RAD).toFloat())
        }
        companion object {
            const val WIDTH = 16
            const val HEIGHT = 14
        }
    }
    companion object {
        private const val FRAME_WIDTH = 88
        private const val FRAME_HEIGHT = 125
        private const val FRAME_MARGIN_TOP = 9
        private const val FRAME_MARGIN_X = 4
        private const val BUTTON_HEIGHT = 20
        private const val SKY_WIDTH = 80
        private const val SKY_HEIGHT = 112
        private const val NSTARS = 100
        private const val NCLOUDS = 5
    }
}
