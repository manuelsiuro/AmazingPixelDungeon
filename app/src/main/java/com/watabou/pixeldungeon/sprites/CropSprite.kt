package com.watabou.pixeldungeon.sprites

import com.watabou.noosa.Game
import com.watabou.noosa.Image
import com.watabou.noosa.TextureFilm
import com.watabou.pixeldungeon.Assets
import com.watabou.pixeldungeon.Dungeon
import com.watabou.pixeldungeon.DungeonTilemap
import com.watabou.pixeldungeon.farming.CropData
import com.watabou.pixeldungeon.farming.CropManager
import com.watabou.pixeldungeon.farming.CropType
import com.watabou.pixeldungeon.levels.Level

class CropSprite : Image {

    private enum class State {
        GROWING, NORMAL, WITHERING
    }

    private var state = State.NORMAL
    private var time: Float = 0f
    private var checkTimer: Float = 0f
    private var pos = -1
    private var lastStage = -1
    private var cropType: CropType? = null

    constructor() : super(Assets.CROPS) {
        if (frames == null) {
            frames = TextureFilm(Assets.CROPS, 16, 16)
        }
        origin.set(8f, 12f)
    }

    fun reset(crop: CropData) {
        revive()
        cropType = crop.cropType
        lastStage = crop.stage
        setFrame(crop.cropType, crop.stage)
        alpha(1f)
        pos = crop.pos
        x = (pos % Level.WIDTH) * DungeonTilemap.SIZE.toFloat()
        y = (pos / Level.WIDTH) * DungeonTilemap.SIZE.toFloat()
        state = State.GROWING
        time = DELAY
    }

    private fun setFrame(type: CropType, stage: Int) {
        // Map crop type + stage to a plant sprite frame
        // Each crop type uses a base frame offset; stages shift within it
        val baseFrame = when (type) {
            CropType.WHEAT -> 0
            CropType.CARROT -> 1
            CropType.POTATO -> 2
            CropType.MELON -> 3
        }
        // Use stage to pick different plant sprites as growth visual
        // stage 0 = seedling, 1 = sprout, 2 = vegetative, 3 = mature
        val frameIdx = baseFrame + stage * 4
        frames?.get(frameIdx)?.let { frame(it) }
    }

    fun updateStage(crop: CropData) {
        crop.updateStage(CropManager.currentTime())
        if (crop.stage != lastStage) {
            lastStage = crop.stage
            setFrame(crop.cropType, crop.stage)
        }
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
            State.NORMAL -> {
                checkTimer -= Game.elapsed
                if (checkTimer <= 0f) {
                    checkTimer = CHECK_INTERVAL
                    val level = Dungeon.level
                    if (level != null && pos >= 0) {
                        val crop = level.crops.get(pos)
                        if (crop != null) {
                            updateStage(crop)
                        }
                    }
                }
            }
        }
    }

    override fun kill() {
        state = State.WITHERING
        time = DELAY
    }

    companion object {
        private const val DELAY = 0.2f
        private const val CHECK_INTERVAL = 0.5f
        private var frames: TextureFilm? = null
    }
}
