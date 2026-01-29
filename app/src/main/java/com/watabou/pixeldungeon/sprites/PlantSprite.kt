package com.watabou.pixeldungeon.sprites
import com.watabou.noosa.Game
import com.watabou.noosa.Image
import com.watabou.noosa.TextureFilm
import com.watabou.pixeldungeon.Assets
import com.watabou.pixeldungeon.Dungeon
import com.watabou.pixeldungeon.DungeonTilemap
import com.watabou.pixeldungeon.levels.Level
import com.watabou.pixeldungeon.plants.Plant
class PlantSprite : Image {
    private enum class State {
        GROWING, NORMAL, WITHERING
    }
    private var state = State.NORMAL
    private var time: Float = 0f
    private var pos = -1
    constructor() : super(Assets.PLANTS) {
        if (frames == null) {
            frames = TextureFilm(Assets.PLANTS, 16, 16)
        }
        origin.set(8f, 12f)
    }
    constructor(image: Int) : this() {
        reset(image)
    }
    fun reset(plant: Plant) {
        revive()
        reset(plant.image)
        alpha(1f)
        pos = plant.pos
        x = (pos % Level.WIDTH) * DungeonTilemap.SIZE.toFloat()
        y = (pos / Level.WIDTH) * DungeonTilemap.SIZE.toFloat()
        state = State.GROWING
        time = DELAY
    }
    fun reset(image: Int) {
        frames?.get(image)?.let { frame(it) }
    }
    override fun update() {
        super.update()
        visible = pos == -1 || Dungeon.visible[pos]
        when (state) {
            State.GROWING -> {
                time -= Game.elapsed
                if (time <= 0) {
                    state = State.NORMAL
                    scale.set(1f)
                } else {
                    scale.set(1 - time / DELAY)
                }
            }
            State.WITHERING -> {
                time -= Game.elapsed
                if (time <= 0) {
                    super.kill()
                } else {
                    alpha(time / DELAY)
                }
            }
            State.NORMAL -> { }
        }
    }
    override fun kill() {
        state = State.WITHERING
        time = DELAY
    }
    companion object {
        private const val DELAY = 0.2f
        private var frames: TextureFilm? = null
    }
}
