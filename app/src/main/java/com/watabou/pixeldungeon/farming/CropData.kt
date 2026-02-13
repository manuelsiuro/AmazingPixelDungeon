package com.watabou.pixeldungeon.farming

import com.watabou.utils.Bundlable
import com.watabou.utils.Bundle

class CropData : Bundlable {

    var pos: Int = 0
    var cropType: CropType = CropType.WHEAT
    var plantedAt: Float = 0f
    var stage: Int = 0
    var hydrated: Boolean = false

    constructor()

    constructor(pos: Int, cropType: CropType, plantedAt: Float, hydrated: Boolean) {
        this.pos = pos
        this.cropType = cropType
        this.plantedAt = plantedAt
        this.hydrated = hydrated
        this.stage = 0
    }

    fun updateStage(currentTime: Float) {
        val elapsed = currentTime - plantedAt
        val multiplier = if (hydrated) 1.5f else 1.0f
        val effectiveAge = elapsed * multiplier
        val growthTime = cropType.growthTime.toFloat()

        stage = when {
            effectiveAge >= growthTime -> 3
            effectiveAge >= growthTime * 0.55f -> 2
            effectiveAge >= growthTime * 0.25f -> 1
            else -> 0
        }
    }

    val isMature: Boolean
        get() = stage >= 3

    override fun storeInBundle(bundle: Bundle) {
        bundle.put(POS, pos)
        bundle.put(CROP_TYPE, cropType.name)
        bundle.put(PLANTED_AT, plantedAt)
        bundle.put(STAGE, stage)
        bundle.put(HYDRATED, hydrated)
    }

    override fun restoreFromBundle(bundle: Bundle) {
        pos = bundle.getInt(POS)
        cropType = try {
            CropType.valueOf(bundle.getString(CROP_TYPE))
        } catch (e: Exception) {
            CropType.WHEAT
        }
        plantedAt = bundle.getFloat(PLANTED_AT)
        stage = bundle.getInt(STAGE)
        hydrated = bundle.getBoolean(HYDRATED)
    }

    companion object {
        private const val POS = "pos"
        private const val CROP_TYPE = "cropType"
        private const val PLANTED_AT = "plantedAt"
        private const val STAGE = "stage"
        private const val HYDRATED = "hydrated"
    }
}
