package com.watabou.pixeldungeon.effects
import com.watabou.noosa.particles.Emitter
import com.watabou.noosa.particles.PixelParticle
import com.watabou.pixeldungeon.DungeonTilemap
import com.watabou.pixeldungeon.scenes.GameScene
import com.watabou.utils.PointF
import com.watabou.utils.Random
object Splash {
    fun at(cell: Int, color: Int, n: Int) {
        at(DungeonTilemap.tileCenterToWorld(cell), color, n)
    }
    fun at(p: PointF, color: Int, n: Int) {
        if (n <= 0) {
            return
        }
        val emitter = requireNotNull(GameScene.emitter()) { "GameScene.emitter() must not be null" }
        emitter.pos(p)
        FACTORY.color = color
        FACTORY.dir = -3.1415926f / 2
        FACTORY.cone = 3.1415926f
        emitter.burst(FACTORY, n)
    }
    fun at(p: PointF, dir: Float, cone: Float, color: Int, n: Int) {
        if (n <= 0) {
            return
        }
        val emitter = requireNotNull(GameScene.emitter()) { "GameScene.emitter() must not be null" }
        emitter.pos(p)
        FACTORY.color = color
        FACTORY.dir = dir
        FACTORY.cone = cone
        emitter.burst(FACTORY, n)
    }
    private val FACTORY = SplashFactory()
    private class SplashFactory : Emitter.Factory() {
        var color: Int = 0
        var dir: Float = 0f
        var cone: Float = 0f
        override fun emit(emitter: Emitter, index: Int, x: Float, y: Float) {
            val p = emitter.recycle(PixelParticle.Shrinking::class.java) as PixelParticle
            p.reset(x, y, color, 4f, Random.Float(0.5f, 1.0f))
            p.speed.polar(Random.Float(dir - cone / 2, dir + cone / 2), Random.Float(40f, 80f))
            p.acc.set(0f, 100f)
        }
    }
}
